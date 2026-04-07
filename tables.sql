CREATE TABLE USERS(
    AGE INT,
    NAME VARCHAR(50),
    PHONE_NUMBER BIGINT UNIQUE,
    EMAIL VARCHAR(50) UNIQUE,
    PASSWORD VARCHAR(20)
);
-- TRUNCATE table users;
-- DROP TABLE USERS;
SELECT * FROM users;

INSERT INTO users VALUES(19,"raj",9876543567,"rajuuu@gmail.com","123");
INSERT INTO admin_data values(18,"raj",91822,"raj@gmail.com","123");
SELECT * FROM admin_data;
-- desc admin_data;
CREATE TABLE ADMIN_DATA(
    AGE INT,
    NAME VARCHAR(50),
    PHONE_NUMBER INT UNIQUE,
    EMAIL VARCHAR(50) UNIQUE,
    PASSWORD VARCHAR(20)
);
UPDATE admin_data 
set email="admin@gmail.com"
where email="raj@gmail.com";
use movies_booking;
-- DROP TABLE IF EXISTS MOVIES;
CREATE TABLE MOVIES (
    MOVIE_ID INT PRIMARY KEY AUTO_INCREMENT,
    MOVIE_NAME VARCHAR(100) NOT NULL,
    MOVIE_DURATION INT,           
    MOVIE_GENRE VARCHAR(50),      
    MOVIE_LANGUAGE VARCHAR(50),
    POSTER_PATH VARCHAR(500)      
);
INSERT INTO MOVIES (MOVIE_NAME, MOVIE_DURATION, MOVIE_GENRE, MOVIE_LANGUAGE, POSTER_PATH)
VALUES ('Kung Fu Panda', 95, 'Animation', 'English', 'C:/Users/hp/OneDrive/Pictures/panda.jpg');

-- drop table movies;
-- drop table shows;
-- drop table theaters;
-- drop table seats;
CREATE  TABLE SHOWS(
    show_id INT PRIMARY KEY AUTO_INCREMENT,
    movie_ID INT,
    theater_id INT,
    show_time DATETIME NOT NULL,
    price DECIMAL(10,2),
    FOREIGN KEY (movie_id) REFERENCES movies(movie_id),
    FOREIGN KEY (theater_id) REFERENCES theaters(theater_id)
);
-- Creating 3 different shows for Movie ID 1 (Panda)
INSERT INTO SHOWS (movie_ID, theater_id, show_time, price) VALUES 
(1, 1, '2026-02-24 10:30:00', 250.00), 
(1, 2, '2026-02-24 14:00:00', 200.00), 
(1, 1, '2026-02-24 18:30:00', 300.00); 

-- Creating shows for Movie ID 2 (The Batman)
INSERT INTO SHOWS (movie_ID, theater_id, show_time, price) VALUES 
(2, 3, '2026-02-24 21:00:00', 350.00),
(2, 4, '2026-02-24 13:15:00', 180.00);
CREATE TABLE theaters (
    theater_id INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(100),
    location VARCHAR(100)
);
INSERT INTO theaters (name, location) VALUES 
('PVR Cinemas', 'Hyderabad - Banjara Hills'),
('Inox Leisure', 'Hyderabad - Gachibowli'),
('Cinepolis', 'Hyderabad - Kukatpally'),
('Asian Cinemas', 'Hyderabad - Uppal');

CREATE TABLE seats (
    seat_id INT PRIMARY KEY AUTO_INCREMENT,
    show_id INT,
    seat_number VARCHAR(10),
    status VARCHAR(10),
    FOREIGN KEY (show_id) REFERENCES shows(show_id)
);
-- use database movies_booking;
show databases;

show tables;
desc users;
use movies_booking;
SELECT * FROM USERS;
SELECT * FROM ADMIN_DATA;
SELECT * FROM MOVIES;
SELECT * FROM SHOWS;
SELECT * FROM THEATERS;
SELECT * FROM SEATS;


-- Missing tables for revenue and booking calculations
CREATE TABLE IF NOT EXISTS bookings (
    booking_id INT PRIMARY KEY AUTO_INCREMENT,
    user_email VARCHAR(50),
    show_id INT,
    seat_numbers VARCHAR(100),
    total_amount DECIMAL(10,2),
    booking_date DATETIME DEFAULT CURRENT_TIMESTAMP,
    status VARCHAR(20) DEFAULT 'CONFIRMED',
    FOREIGN KEY (user_email) REFERENCES users(EMAIL),
    FOREIGN KEY (show_id) REFERENCES shows(show_id)
);

CREATE TABLE IF NOT EXISTS payments (
    payment_id INT PRIMARY KEY AUTO_INCREMENT,
    booking_id INT,
    amount DECIMAL(10,2),
    payment_date DATETIME DEFAULT CURRENT_TIMESTAMP,
    status VARCHAR(20) DEFAULT 'SUCCESS',
    FOREIGN KEY (booking_id) REFERENCES bookings(booking_id)
);

