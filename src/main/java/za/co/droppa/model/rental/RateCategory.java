package za.co.droppa.model.rental;

/**
 * @author Nicolas
 * @date 2021/06/07
 */
public enum RateCategory {
    /*ONE_TON_FRIDGE, TWO_HALF_TON_FRIDGE,
    FOUR_TON_FRIDGE, EIGHT_TON_FRIDGE,MINI_VAN,
    ONE_TON, ONE_HALF_TON,TWO_HALF_TON,FOUR_TON,SIX_TON,EIGHT_TON;*/

    HALF_TON_LDV_WITH_CANOPY("1/2-Ton LDV with canopy "),
    HALF_TON_PANELVAN("1/2-Ton Panelvan(Caddy/Partner/Fiat)"),
    ONE_TON_LDV_WITH_CANOPY("1-Ton LDV with canopy"),
    ONE_TON_FRIDGE("1 Ton Fridge"),
    TWO_TON_PANELVAN("2-Ton Panelvan"),
    TWO_TON_FRIDGE_BODY("2-Ton fridge body"),
    FOUR_TON_ENCLOSED_VAN_BODY("4-Ton enclosed van body"),
    FOUR_TON_FRIDGE_BODY("4-Ton fridge body"),
    EIGHT_TON_ENCLOSED_VAN_BODY_DROP_SIDE("8-Ton enclosed van body / drop side"),
    EIGHT_TON_FRIDGE_BODY("8-Ton fridge body"),
    EIGHT_TON_TAUTLINER("8-Ton tautliner"),
    TWELVE_TON_CURTAIN_SIDE("12 Ton Curtain Side"),
    FOURTEEN_TON_FRIDGE("14 Ton Fridge"),
    TRUCK_TRACTOR_AND_FRIDGE_TRAILER("Truck Tractor and Fridge trailer"),
    TRUCK_TRACTOR_AND_SIX_BY_TWELVE_TRAILER("Truck Tractor and 6/12m Trailer");
    private String description;

   RateCategory (String description) {
        this.description = description;
    }
    public static RateCategory findByName(String name) {
        final String filter = name.replace("-", "_").
                replace(" ", "_").toUpperCase();
        for (RateCategory v : values()) {
            if (v.name().equals(filter)) {
                return v;
            }
        }
        return null;
    }
}
