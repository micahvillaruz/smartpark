package com.smartpark.scheduler;

import com.smartpark.entity.ParkingSession;
import com.smartpark.repository.ParkingSessionRepository;
import com.smartpark.service.ParkingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
public class AutoCheckoutScheduler {

    private static final Logger log = LoggerFactory.getLogger(AutoCheckoutScheduler.class);

    private final ParkingSessionRepository parkingSessionRepository;
    private final ParkingService parkingService;
    private final long thresholdMinutes;

    public AutoCheckoutScheduler(ParkingSessionRepository parkingSessionRepository,
                                 ParkingService parkingService,
                                 @Value("${app.parking.auto-checkout-minutes}") long thresholdMinutes) {
        this.parkingSessionRepository = parkingSessionRepository;
        this.parkingService = parkingService;
        this.thresholdMinutes = thresholdMinutes;
    }

    /**
     * Runs every 30 seconds. Finds active sessions older than the configured threshold
     * (default 15 minutes) and auto-checks them out.
     */
    @Scheduled(fixedRateString = "${app.parking.scheduler-rate-ms:30000}")
    public void autoCheckoutExpiredSessions() {
        LocalDateTime threshold = LocalDateTime.now().minusMinutes(thresholdMinutes);
        List<ParkingSession> expired = parkingSessionRepository.findActiveSessionsOlderThan(threshold);

        if (expired.isEmpty()) {
            return;
        }

        log.info("Auto-checkout scheduler: found {} session(s) over {} minutes", expired.size(), thresholdMinutes);

        for (ParkingSession session : expired) {
            try {
                parkingService.autoCheckOut(session);
                log.info("Auto-checked-out vehicle {} from lot {}",
                        session.getVehicle().getLicensePlate(),
                        session.getParkingLot().getLotId());
            } catch (Exception e) {
                // Don't let one bad session kill the whole batch
                log.error("Failed to auto-checkout session {}: {}", session.getId(), e.getMessage());
            }
        }
    }
}