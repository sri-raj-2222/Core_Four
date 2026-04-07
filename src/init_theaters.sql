DROP TABLE IF EXISTS theaters;

CREATE TABLE theaters (
    id VARCHAR(50) PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    location VARCHAR(255),
    num_screens INT DEFAULT 1,
    capacity INT DEFAULT 0
);

INSERT INTO
    theaters (
        id,
        name,
        location,
        num_screens,
        capacity
    )
VALUES (
        'TH001',
        'Grand Galaxy Cinema',
        'Downtown City Center',
        4,
        1200
    );