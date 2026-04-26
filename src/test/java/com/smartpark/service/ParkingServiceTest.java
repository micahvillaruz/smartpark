package com.smartpark.service;

import com.smartpark.dto.CheckInRequest;
import com.smartpark.dto.CheckOutRequest;
import com.smartpark.dto.ParkingSessionResponse;
import com.smartpark.entity.ParkingLot;
import com.smartpark.entity.ParkingSession;
import com.smartpark.entity.Vehicle;
import com.smartpark.entity.VehicleType;
import com.smartpark.repository.ParkingLotRepository;
import com.smartpark.repository.ParkingSessionRepository;
import com.smartpark.repository.VehicleRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ParkingServiceTest {

    @Mock
    private ParkingLotRepository parkingLotRepository;

    @Mock
    private VehicleRepository vehicleRepository;

    @Mock
    private ParkingSessionRepository parkingSessionRepository;

    @InjectMocks
    private ParkingService parkingService;

    private ParkingLot lot;
    private Vehicle vehicle;

    @BeforeEach
    void setUp() {
        lot = new ParkingLot("LOT-001", "Downtown", 10, 0, new BigDecimal("0.5000"));
        vehicle = new Vehicle("ABC-1234", VehicleType.CAR, "Juan Dela Cruz");
    }

    @Test
    void calculateCost_roundsPartialMinuteUp() {
        LocalDateTime checkIn = LocalDateTime.of(2026, 4, 27, 10, 0, 0);
        LocalDateTime checkOut = checkIn.plusSeconds(90); // 1.5 minutes → 2 minutes charged

        BigDecimal cost = parkingService.calculateCost(checkIn, checkOut, new BigDecimal("0.5000"));

        // 2 minutes * 0.5 = 1.00
        assertEquals(0, new BigDecimal("1.00").compareTo(cost),
                "Expected 1.00, got " + cost);
    }

    @Test
    void calculateCost_chargesAtLeastOneMinute_forShortStays() {
        LocalDateTime checkIn = LocalDateTime.of(2026, 4, 27, 10, 0, 0);
        LocalDateTime checkOut = checkIn.plusSeconds(15); // 15 seconds → still charges 1 minute

        BigDecimal cost = parkingService.calculateCost(checkIn, checkOut, new BigDecimal("0.5000"));

        assertEquals(0, new BigDecimal("0.50").compareTo(cost));
    }

    @Test
    void checkIn_succeeds_whenLotHasSpaceAndVehicleNotParked() {
        CheckInRequest request = new CheckInRequest();
        request.setLicensePlate("ABC-1234");
        request.setLotId("LOT-001");

        when(vehicleRepository.findById("ABC-1234")).thenReturn(Optional.of(vehicle));
        when(parkingLotRepository.findById("LOT-001")).thenReturn(Optional.of(lot));
        when(parkingSessionRepository.findByVehicleLicensePlateAndActiveTrue("ABC-1234"))
                .thenReturn(Optional.empty());
        when(parkingSessionRepository.save(any(ParkingSession.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        ParkingSessionResponse response = parkingService.checkIn(request);

        assertNotNull(response);
        assertEquals("ABC-1234", response.getLicensePlate());
        assertEquals("LOT-001", response.getLotId());
        assertTrue(response.isActive());
        assertEquals(1, lot.getOccupiedSpaces(), "Lot occupancy should increment");
        verify(parkingLotRepository).save(lot);
    }

    @Test
    void checkIn_throws_whenVehicleAlreadyParked() {
        CheckInRequest request = new CheckInRequest();
        request.setLicensePlate("ABC-1234");
        request.setLotId("LOT-001");

        ParkingSession existing = new ParkingSession(vehicle, lot, LocalDateTime.now());

        when(vehicleRepository.findById("ABC-1234")).thenReturn(Optional.of(vehicle));
        when(parkingLotRepository.findById("LOT-001")).thenReturn(Optional.of(lot));
        when(parkingSessionRepository.findByVehicleLicensePlateAndActiveTrue("ABC-1234"))
                .thenReturn(Optional.of(existing));

        IllegalStateException ex = assertThrows(IllegalStateException.class,
                () -> parkingService.checkIn(request));
        assertTrue(ex.getMessage().contains("already parked"));
        verify(parkingSessionRepository, never()).save(any());
    }

    @Test
    void checkIn_throws_whenLotIsFull() {
        lot.setCapacity(1);
        lot.setOccupiedSpaces(1); // already at capacity

        CheckInRequest request = new CheckInRequest();
        request.setLicensePlate("ABC-1234");
        request.setLotId("LOT-001");

        when(vehicleRepository.findById("ABC-1234")).thenReturn(Optional.of(vehicle));
        when(parkingLotRepository.findById("LOT-001")).thenReturn(Optional.of(lot));
        when(parkingSessionRepository.findByVehicleLicensePlateAndActiveTrue("ABC-1234"))
                .thenReturn(Optional.empty());

        IllegalStateException ex = assertThrows(IllegalStateException.class,
                () -> parkingService.checkIn(request));
        assertTrue(ex.getMessage().contains("full"));
    }

    @Test
    void checkIn_throws_whenVehicleNotFound() {
        CheckInRequest request = new CheckInRequest();
        request.setLicensePlate("UNKNOWN");
        request.setLotId("LOT-001");

        when(vehicleRepository.findById("UNKNOWN")).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> parkingService.checkIn(request));
    }

    @Test
    void checkOut_calculatesCostAndDecrementsLot() {
        lot.setOccupiedSpaces(1);
        ParkingSession session = new ParkingSession(vehicle, lot, LocalDateTime.now().minusMinutes(10));

        CheckOutRequest request = new CheckOutRequest();
        request.setLicensePlate("ABC-1234");

        when(parkingSessionRepository.findByVehicleLicensePlateAndActiveTrue("ABC-1234"))
                .thenReturn(Optional.of(session));
        when(parkingSessionRepository.save(any(ParkingSession.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        ParkingSessionResponse response = parkingService.checkOut(request);

        assertFalse(response.isActive());
        assertNotNull(response.getCost());
        assertNotNull(response.getCheckOutTime());
        assertEquals(0, lot.getOccupiedSpaces(), "Lot occupancy should decrement");
    }

    @Test
    void checkOut_throws_whenNoActiveSession() {
        CheckOutRequest request = new CheckOutRequest();
        request.setLicensePlate("ABC-1234");

        when(parkingSessionRepository.findByVehicleLicensePlateAndActiveTrue("ABC-1234"))
                .thenReturn(Optional.empty());

        assertThrows(IllegalStateException.class, () -> parkingService.checkOut(request));
    }

    @Test
    void autoCheckOut_marksSessionAsAutoCheckedOut() {
        lot.setOccupiedSpaces(1);
        ParkingSession session = new ParkingSession(vehicle, lot, LocalDateTime.now().minusMinutes(20));

        when(parkingSessionRepository.save(any(ParkingSession.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        ParkingSessionResponse response = parkingService.autoCheckOut(session);

        assertFalse(response.isActive());
        assertTrue(response.isAutoCheckedOut(), "Session should be marked as auto-checked-out");
        assertNotNull(response.getCost());
        assertEquals(0, lot.getOccupiedSpaces());
    }
}