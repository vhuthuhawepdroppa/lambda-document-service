package za.co.droppa.model.geo;

public class Location {

    private String[] coordinates;

    private String address;

    public Location() { }

    public String[] getCoordinates() { return coordinates; }

    public void setCoordinates(String[] coordinates) { this.coordinates = coordinates; }

    public String getAddress() { return address; }

    public void setAddress(String address) { this.address = address; }
}
