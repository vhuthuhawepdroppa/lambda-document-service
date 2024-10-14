package za.co.droppa.model;


/**
 * @author Nicolas
 * @date 2020/08/08
 */
public enum TransportMode {
    AIR("Air"),
    WATER("Water"),
    ROAD("Railways, road, etc.");


    private final String description;

    TransportMode(String description) {
        this.description = description;
    }

    public String description() { return description; }

    public static TransportMode findByName(String name) {
        final String province = name.replace("-", "_").replace(" ", "_").toUpperCase();
        for (TransportMode v : values()) {
            if (v.name().equals(province)) {
                return v;
            }
        }
        return null;
    }
}
