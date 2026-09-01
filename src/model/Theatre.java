package model;

public class Theatre {
    private int id;
    private String name;
    private String location;
    private int totalSeats;

    public Theatre() {}

    public Theatre(int id, String name, String location, int totalSeats) {
        this.id = id;
        this.name = name;
        this.location = location;
        this.totalSeats = totalSeats;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }
    public int getTotalSeats() { return totalSeats; }
    public void setTotalSeats(int totalSeats) { this.totalSeats = totalSeats; }

    @Override
    public String toString() { return name + " - " + location; }
}
