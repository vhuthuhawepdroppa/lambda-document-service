package za.co.droppa.model;
/**
 * Developer: Nicolas Tebogo
 * Date: 18/09/2018
 */
public enum Province {

	GAUTENG("Gauteng"),
    LIMPOPO("Limpopo"),
    KWA_ZULU_NATAL("Kwa-Zulu Natal"),
    NORTHERN_PROVINCE("Northern Cape"),
    NORTH_WEST("North West"),
    FREE_STATE("Free State"),
    WESTERN_CAPE("Western Cape"),
    MPUMALANGA("Mpumalanga"),
    NORTHERN_CAPE("Northern Cape"),
	EASTERN_CAPE("Eastern Cape"),
	UNKNOWN("Unknow Province");

    private String description;

    Province(String description) {
        this.description = description;
    }

    public String description() { return description; }



    
    public static Province findByName(String name) {
        String normalizedInput = name.trim()
                .replaceAll("\\s+", "_")
                .replace("-", "_")
                .toUpperCase();

        // Iterate over the enum values to find a match
        for (Province v : values()) {
            if (v.name().equals(normalizedInput)) {
                return v;
            }
        }

        for (Province v : values()) {
            if (v.description.toUpperCase().trim().equals(normalizedInput)) {
                return v;
            }
        }


        return name.equalsIgnoreCase("KwaZulu-Natal") || name.equalsIgnoreCase("Kwazulu Natal") ? KWA_ZULU_NATAL: null;
    }

    public static Province findByCode(String provinceCode){
        switch (provinceCode) {
            case "GP":
                return Province.GAUTENG;
            case "WC":
                return Province.WESTERN_CAPE;
            case "KZN":
                return Province.KWA_ZULU_NATAL;
            case "EC":
                return Province.EASTERN_CAPE;
            case "FS":
                return Province.FREE_STATE;
            case "LP":
                return Province.LIMPOPO;
            case "MP":
                return Province.MPUMALANGA;
            case "NW":
                return Province.NORTH_WEST;
            case "NC":
                return Province.NORTHERN_CAPE;
            default:
                return Province.UNKNOWN;
        }
    }

    public static String findCodeByProvince(Province province){
        switch (province) {
            case GAUTENG:
                return "GP";
            case WESTERN_CAPE:
                return "WC";
            case KWA_ZULU_NATAL:
                return "KZN";
            case EASTERN_CAPE:
                return "EC";
            case FREE_STATE:
                return "FS";
            case LIMPOPO:
                return "LP";
            case MPUMALANGA:
                return "MP";
            case NORTH_WEST:
                return "NW";
            case NORTHERN_CAPE:
                return "NC";
            default:
                return "UNKNOWN";
        }
    }

}
