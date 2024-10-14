package za.co.droppa.model.geo;

/**
 * @author Nicolas
 * @date 2020/08/20
 */
public enum PointOfInterestType {
    AIRPORT("Airport"),
    SHESHA("Shesha"),
    CITY_CENTERS("City centers");

    private String description;

    PointOfInterestType(String description) {
        this.description = description;
    }

    public String description() { return description; }

    public static PointOfInterestType findByName(String name) {
        final String province = name.replace("-", "_").replace(" ", "_").toUpperCase();
        for (PointOfInterestType v : values()) {
            if (v.name().equals(province)) {
                return v;
            }
        }
        return null;
    }
}
