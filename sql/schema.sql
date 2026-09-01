-- ============================================================
--  Movie Ticket Booking System - Database Schema
--  Database: MySQL 8.x
-- ============================================================

CREATE DATABASE IF NOT EXISTS movie_ticket_booking;
USE movie_ticket_booking;

-- --------------------------------------------------------------
-- Table: admin
-- --------------------------------------------------------------
CREATE TABLE IF NOT EXISTS admin (
    id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(256) NOT NULL
);

-- Default admin -> username: admin | password: admin123
INSERT INTO admin (username, password)
VALUES ('admin', '240be518fabd2724ddb6f04eeb1da5967448d7e831c08c8fa822809f74c720a9')
ON DUPLICATE KEY UPDATE username = username;

-- --------------------------------------------------------------
-- Table: users  (customers)
-- --------------------------------------------------------------
CREATE TABLE IF NOT EXISTS users (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    email VARCHAR(100) NOT NULL UNIQUE,
    password VARCHAR(256) NOT NULL,
    phone VARCHAR(20),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- --------------------------------------------------------------
-- Table: movies
-- --------------------------------------------------------------
CREATE TABLE IF NOT EXISTS movies (
    id INT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(150) NOT NULL,
    genre VARCHAR(80),
    language VARCHAR(50),
    duration_minutes INT,
    description TEXT,
    release_date DATE,
    rating VARCHAR(10)
);

-- --------------------------------------------------------------
-- Table: theatres
-- --------------------------------------------------------------
CREATE TABLE IF NOT EXISTS theatres (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(120) NOT NULL,
    location VARCHAR(150),
    total_seats INT NOT NULL DEFAULT 100
);

-- --------------------------------------------------------------
-- Table: shows  (a movie playing in a theatre at a date/time)
-- --------------------------------------------------------------
CREATE TABLE IF NOT EXISTS shows (
    id INT AUTO_INCREMENT PRIMARY KEY,
    movie_id INT NOT NULL,
    theatre_id INT NOT NULL,
    show_date DATE NOT NULL,
    show_time TIME NOT NULL,
    price DECIMAL(8,2) NOT NULL DEFAULT 150.00,
    available_seats INT NOT NULL,
    FOREIGN KEY (movie_id) REFERENCES movies(id) ON DELETE CASCADE,
    FOREIGN KEY (theatre_id) REFERENCES theatres(id) ON DELETE CASCADE
);

-- --------------------------------------------------------------
-- Table: bookings
-- --------------------------------------------------------------
CREATE TABLE IF NOT EXISTS bookings (
    id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,
    show_id INT NOT NULL,
    seats_booked INT NOT NULL,
    total_amount DECIMAL(10,2) NOT NULL,
    booking_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    status VARCHAR(20) NOT NULL DEFAULT 'CONFIRMED',
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (show_id) REFERENCES shows(id) ON DELETE CASCADE
);

-- --------------------------------------------------------------
-- Sample data (optional - comment out if not needed)
-- --------------------------------------------------------------
INSERT INTO theatres (name, location, total_seats) VALUES
('PVR Cinemas', 'City Center Mall', 120),
('INOX Multiplex', 'Downtown Plaza', 100);

INSERT INTO movies (title, genre, language, duration_minutes, description, release_date, rating) VALUES
('The Last Horizon', 'Sci-Fi', 'English', 148, 'A crew races against time to save Earth.', '2026-06-01', 'UA'),
('Monsoon Melodies', 'Drama/Musical', 'Hindi', 132, 'A heartfelt story set against the monsoon.', '2026-05-15', 'U');

INSERT INTO shows (movie_id, theatre_id, show_date, show_time, price, available_seats) VALUES
(1, 1, CURDATE(), '18:30:00', 220.00, 120),
(2, 2, CURDATE(), '21:00:00', 180.00, 100);
