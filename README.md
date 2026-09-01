# CineBook - Movie Ticket Booking System

A desktop movie ticket booking application built with **Java Swing** (pure
Java2D styling, no CSS/HTML) and **MySQL** for storage.

## Features

### Admin
- Login (default: `admin` / `admin123`)
- Add Movies
- Manage Theatres (add / delete)
- Manage Shows (schedule a movie in a theatre with date, time, price)
- View Bookings (every customer booking, across all shows)

### Customer
- Register / Login
- Search Movies (by title / genre / language) & see all showtimes
- Book Tickets (seat-count aware, prevents overbooking via a DB transaction)
- Cancel Tickets (auto-restores the seats to the show)
- Download Ticket (saves a formatted e-ticket as a `.txt` file anywhere on disk)

## Tables
`admin`, `users`, `movies`, `theatres`, `shows`, `bookings` - see
[`sql/schema.sql`](sql/schema.sql).

## Project layout

```
MovieTicketBookingSystem/
├── sql/schema.sql              Database schema + sample data
├── lib/                        Put mysql-connector-j-x.x.x.jar here
├── src/
│   ├── main/Main.java          Entry point
│   ├── db/DBConnection.java    JDBC connection settings (edit host/user/pass)
│   ├── util/PasswordUtil.java  SHA-256 password hashing
│   ├── model/                  Admin, User, Movie, Theatre, Show, Booking
│   ├── dao/                    One DAO per table - all SQL lives here
│   └── ui/
│       ├── AppFrame.java       Root window / CardLayout navigation
│       ├── LoginPanel.java     Combined Admin/Customer login
│       ├── RegisterPanel.java  Customer sign-up
│       ├── common/             Shared theme, buttons, sidebar (no CSS)
│       ├── admin/              Admin dashboard screens
│       └── customer/           Customer dashboard screens
└── README.md
```

## Setup

### 1. Create the database

```bash
mysql -u root -p < sql/schema.sql
```

This creates the `movie_ticket_booking` database, all 6 tables, a default
admin account, and a couple of sample movies/theatres/shows so you have data
to test with immediately.

### 2. Get the MySQL JDBC driver

Download **mysql-connector-j** (the MySQL Connector/J `.jar`) from
https://dev.mysql.com/downloads/connector/j/ and place it inside `lib/`.
(It isn't bundled here since it's a third-party binary.)

### 3. Configure the connection

Edit `src/db/DBConnection.java` if your MySQL isn't the default
`localhost:3306` / user `root` / password `root`:

```java
private static final String USER = "root";
private static final String PASSWORD = "root";
```

### 4. Compile & run

From the project root, with the connector jar in `lib/`:

**Linux / macOS**
```bash
mkdir -p build
javac -cp "lib/*" -d build $(find src -name "*.java")
java -cp "build:lib/*" main.Main
```

**Windows (PowerShell / cmd)**
```bat
mkdir build
javac -cp "lib\*" -d build (Get-ChildItem -Recurse -Filter *.java src | % FullName)
java -cp "build;lib\*" main.Main
```

Or simply import the `src` folder as a Java project in IntelliJ IDEA /
Eclipse / NetBeans, add the connector jar to the module's classpath, and run
`main.Main`.

## Notes

- Passwords are stored as SHA-256 hashes (`util.PasswordUtil`) - never in
  plain text.
- Booking and cancellation both run inside a DB transaction
  (`Connection.setAutoCommit(false)` + row locking) so two customers can't
  overbook the same show.
- The whole UI is plain Swing painted with Java2D (`ui/common/RoundedButton`,
  `ui/common/Sidebar`, `ui/common/UITheme`) - there is no HTML/CSS anywhere in
  the project.
