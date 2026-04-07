-- Use the database
CREATE DATABASE IF NOT EXISTS movies_booking;

USE movies_booking;

-- Admin Table (Matches requested fields and security improvements)
CREATE TABLE IF NOT EXISTS admin (
    id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(100) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    last_login DATETIME,
    failed_attempts INT DEFAULT 0
);

-- Users Table
CREATE TABLE IF NOT EXISTS users (
    id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(100) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    phone VARCHAR(15),
    status VARCHAR(20) DEFAULT 'ACTIVE'
);

-- Theater Managers Table
CREATE TABLE IF NOT EXISTS theater_managers (
    id INT AUTO_INCREMENT PRIMARY KEY,
    theater_id VARCHAR(100) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    status VARCHAR(20) DEFAULT 'ACTIVE',
    name VARCHAR(100) DEFAULT 'Unnamed',
    num_screens INT DEFAULT 1
);

select * from theater_managers;
truncate table theater_managers;
use movies_booking;

INSERT INTO
    theater_managers (
        id,
        theater_id,
        password,
        status,
        name,
        num_screens
    )
VALUES (
        1,
        "TH001",
        "123",
        "ACTIVE",
        "Default Theater",
        3
    )
ON DUPLICATE KEY UPDATE
    theater_id = theater_id;

-- Theaters View (Replaces old table)
CREATE VIEW theaters AS
SELECT
    id AS theater_id,
    name,
    'Unknown' AS location,
    num_screens,
    0 AS capacity
FROM theater_managers;

use movies_booking;

-- Screens Table (to manage total seats and capacity)
CREATE TABLE IF NOT EXISTS screens (
    id INT AUTO_INCREMENT PRIMARY KEY,
    screen_number VARCHAR(10) UNIQUE NOT NULL,
    capacity INT NOT NULL
);

-- Disabled Seats Table
CREATE TABLE IF NOT EXISTS disabled_seats (
    id INT AUTO_INCREMENT PRIMARY KEY,
    screen_id INT,
    seat_number VARCHAR(10),
    FOREIGN KEY (screen_id) REFERENCES screens (id) ON DELETE CASCADE
);

-- Movies Table
CREATE TABLE IF NOT EXISTS movies (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    genre VARCHAR(100),
    duration INT, -- in minutes
    language VARCHAR(50),
    release_date DATE,
    poster_image VARCHAR(255),
    status VARCHAR(50) DEFAULT 'Now Showing'
);

-- Shows Table
CREATE TABLE IF NOT EXISTS shows (
    id INT AUTO_INCREMENT PRIMARY KEY,
    movie_id INT,
    screen_id INT,
    show_time TIME,
    show_date DATE,
    ticket_price DECIMAL(10, 2),
    FOREIGN KEY (movie_id) REFERENCES movies (id) ON DELETE CASCADE,
    FOREIGN KEY (screen_id) REFERENCES screens (id) ON DELETE CASCADE
);

-- Bookings Table
CREATE TABLE IF NOT EXISTS bookings (
    id VARCHAR(50) PRIMARY KEY, -- e.g. BOK-1234
    user_id INT,
    show_id INT,
    seat_numbers VARCHAR(255), -- e.g. "A1,A2,A3"
    total_amount DECIMAL(10, 2),
    booking_date DATETIME DEFAULT CURRENT_TIMESTAMP,
    status VARCHAR(50) DEFAULT 'CONFIRMED',
    FOREIGN KEY (user_id) REFERENCES users (id),
    FOREIGN KEY (show_id) REFERENCES shows (id)
);
select * from bookings;
use movies_booking;

-- Payments Table
CREATE TABLE IF NOT EXISTS payments (
    id INT AUTO_INCREMENT PRIMARY KEY,
    booking_id VARCHAR(50),
    amount DECIMAL(10, 2),
    payment_date DATETIME DEFAULT CURRENT_TIMESTAMP,
    status VARCHAR(50) DEFAULT 'SUCCESS',
    FOREIGN KEY (booking_id) REFERENCES bookings (id) ON DELETE CASCADE
);

-- Insert Default Admin
INSERT INTO
    admin (username, password)
SELECT *
FROM (
        SELECT 'admin' AS username, 'admin123' AS password
    ) AS tmp
WHERE
    NOT EXISTS (
        SELECT username
        FROM admin
        WHERE
            username = 'admin'
    )
LIMIT 1;

-- Insert Default Theater Manager
INSERT INTO
    theater_managers (theater_id, password)
SELECT *
FROM (
        SELECT 'TM001' AS theater_id, 'tm123' AS password
    ) AS tmp
WHERE
    NOT EXISTS (
        SELECT theater_id
        FROM theater_managers
        WHERE
            theater_id = 'TM001'
    )
LIMIT 1;

-- Insert Default Theater Data
INSERT INTO
    theaters (
        id,
        name,
        location,
        num_screens,
        capacity
    )
SELECT *
FROM (
        SELECT
            'TH001' AS id, 'Grand Galaxy Cinema' AS name, 'Downtown City Center' AS location, 4 AS num_screens, 1200 AS capacity
    ) AS tmp
WHERE
    NOT EXISTS (
        SELECT id
        FROM theaters
        WHERE
            id = 'TH001'
    )
LIMIT 1;