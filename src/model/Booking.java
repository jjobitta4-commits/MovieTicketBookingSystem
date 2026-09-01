package model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class Booking {
    private int id;
    private int userId;
    private int showId;
    private int seatsBooked;
    private BigDecimal totalAmount;
    private LocalDateTime bookingDate;
    private String status;

    private String userName;
    private String movieTitle;
    private String theatreName;
    private java.time.LocalDate showDate;
    private java.time.LocalTime showTime;

    public Booking() {}

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }
    public int getShowId() { return showId; }
    public void setShowId(int showId) { this.showId = showId; }
    public int getSeatsBooked() { return seatsBooked; }
    public void setSeatsBooked(int seatsBooked) { this.seatsBooked = seatsBooked; }
    public BigDecimal getTotalAmount() { return totalAmount; }
    public void setTotalAmount(BigDecimal totalAmount) { this.totalAmount = totalAmount; }
    public LocalDateTime getBookingDate() { return bookingDate; }
    public void setBookingDate(LocalDateTime bookingDate) { this.bookingDate = bookingDate; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getUserName() { return userName; }
    public void setUserName(String userName) { this.userName = userName; }
    public String getMovieTitle() { return movieTitle; }
    public void setMovieTitle(String movieTitle) { this.movieTitle = movieTitle; }
    public String getTheatreName() { return theatreName; }
    public void setTheatreName(String theatreName) { this.theatreName = theatreName; }
    public java.time.LocalDate getShowDate() { return showDate; }
    public void setShowDate(java.time.LocalDate showDate) { this.showDate = showDate; }
    public java.time.LocalTime getShowTime() { return showTime; }
    public void setShowTime(java.time.LocalTime showTime) { this.showTime = showTime; }
}
