-- Preloaded parking lots
INSERT INTO parking_lot (lot_id, location, capacity, occupied_spaces, cost_per_minute) VALUES
('LOT-001', 'Downtown Plaza',     20, 0, 0.5000),
('LOT-002', 'Airport Terminal A', 50, 0, 1.0000),
('LOT-003', 'Mall of Asia',       30, 0, 0.7500),
('LOT-004', 'BGC Central',        15, 0, 1.2500);

-- Preloaded vehicles
INSERT INTO vehicle (license_plate, type, owner_name) VALUES
('ABC-1234', 'CAR',        'Juan Dela Cruz'),
('XYZ-9876', 'CAR',        'Maria Santos'),
('MOT-0001', 'MOTORCYCLE', 'Pedro Reyes'),
('TRK-5555', 'TRUCK',      'Logistics Corp Driver'),
('CAR-2222', 'CAR',        'Ana Mercado');