package za.co.droppa.model.rental;

/**
 * @author Nicolas
 * @date 2021/06/07
 */
public enum RateUnit {
    DAILY,MONTHLY,WEEKLY;

    public static RateUnit findByName(String name) {
        final String filter = name.toUpperCase();
        for (RateUnit v : values()) {
            if (v.name().equals(filter)) {
                return v;
            }
        }
        return null;
    }
}
