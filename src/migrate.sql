ALTER TABLE theater_managers
ADD COLUMN name VARCHAR(100) DEFAULT 'Unnamed';

ALTER TABLE theater_managers ADD COLUMN num_screens INT DEFAULT 1;

UPDATE theater_managers tm
JOIN theaters t ON tm.id = t.theater_id
SET
    tm.name = t.name,
    tm.num_screens = t.num_screens;

DROP TABLE theaters;

CREATE VIEW theaters AS
SELECT
    id AS theater_id,
    name,
    'Unknown' AS location,
    num_screens,
    0 AS capacity
FROM theater_managers;

CREATE TABLE IF NOT EXISTS disabled_seats (
    id INT AUTO_INCREMENT PRIMARY KEY,
    screen_id INT,
    seat_number VARCHAR(10),
    FOREIGN KEY (screen_id) REFERENCES screens (id) ON DELETE CASCADE
);