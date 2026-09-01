package model;

import java.time.LocalDate;

public class Movie {
    private int id;
    private String title;
    private String genre;
    private String language;
    private int durationMinutes;
    private String description;
    private LocalDate releaseDate;
    private String rating;

    public Movie() {}

    public Movie(int id, String title, String genre, String language, int durationMinutes,
                 String description, LocalDate releaseDate, String rating) {
        this.id = id;
        this.title = title;
        this.genre = genre;
        this.language = language;
        this.durationMinutes = durationMinutes;
        this.description = description;
        this.releaseDate = releaseDate;
        this.rating = rating;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getGenre() { return genre; }
    public void setGenre(String genre) { this.genre = genre; }
    public String getLanguage() { return language; }
    public void setLanguage(String language) { this.language = language; }
    public int getDurationMinutes() { return durationMinutes; }
    public void setDurationMinutes(int durationMinutes) { this.durationMinutes = durationMinutes; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public LocalDate getReleaseDate() { return releaseDate; }
    public void setReleaseDate(LocalDate releaseDate) { this.releaseDate = releaseDate; }
    public String getRating() { return rating; }
    public void setRating(String rating) { this.rating = rating; }

    @Override
    public String toString() { return title; }
}
