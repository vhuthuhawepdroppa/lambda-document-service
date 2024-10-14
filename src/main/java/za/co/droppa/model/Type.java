package za.co.droppa.model;

import java.util.Arrays;
import java.util.List;

public enum Type {
    FLASH("Same Day"),
    DEDICATED("Normal removal"),
    DEDICATED_CAMPOS("Campos dedicated"),
    EXPRESS_COURIER("PickUp Following day"),
    DASH("Dash"),
    BUDGET_COURIER("2-5 days."),
    ECONOMY_COURIER("COURIER_GUY Delivered within 2-3 days"),
    CG_LSX("COURIER_GUY SAME DAY LOCAL EXPRESS"),
    CG_LSE("COURIER_GUY SAME DAY LOCAL ECONOMY"),
    CG_OVNR("COURIER_GUY Expect delivery between 2 - 3 business days."),
    CG_ECOR("COURIER_GUY Economy Regional  Expect delivery between 3 - 5 business days."),
    CG_OVN("COURIER_GUY Overnight Expect delivery between 1 - 2 business days"),
    DSV_ECO("DSV ECONOMY"), DSV_EXP("DSV_EXPRESS"),
    DPD_ECON("DPD ECONOMY"),
    DPD_ROAD("DPD ROAD"),
    DPD_ONX("DPD OVERNIGHT LATEST 11:00"),
    DPD_ONX1("DPD OVERNIGHT LATEST 18:30"),
    DPD_SDX("DPD LASER SAME DAY LOCAL EXPRESS"),

    BRIMA_A12("BRIMA Overnight Air 2"),
    BRIMA_A9("BRIMA Overnight Air"),
    BRIMA_G17("BRIMA Overnight 2"),
    BRIMA_G24("BRIMA Roadfreight"),

    F_FREIGHT_ECO("First Freight Economy Road"),
    F_FREIGHT_LOC("First Freight Local Economy"),
    F_FREIGHT_LSD("First Freight Local Same Day"),
    F_FREIGHT_ONX("First Freight Overnight"),
    ICP("Skynet International booking");

    private String description;

    Type(String description) {
        this.description = description;
    }

    public String description() {
        return description;
    }

    public static Type findByName(String name) {
        final String type = name.replace("-", "_")
                .replace(" ", "_")
                .toUpperCase();

        for (Type v : values()) {
            if (v.name().equals(type)) {
                return v;
            }
        }
        return null;
    }

    public static int typeServiceLevel(Type type) {

        //return 1 for economy
        if (type == ECONOMY_COURIER || type == BUDGET_COURIER || type == CG_ECOR ||
                type == DPD_ECON || type == DSV_ECO)
            return 1;

            //return 2 for express
        else if (type == EXPRESS_COURIER || type == DPD_ONX || type == DPD_ONX1 || type == DSV_EXP || type == CG_OVN || type == CG_OVNR)
            return 2;

            //return 3 for same day
        else if (type.equals(DPD_SDX) || type.equals(CG_LSX) || type.equals(CG_LSE))
            return 3;

        return 0;
    }

    public static String companyName(Type type) {

        if (type == null)
            return "DROPPA";

        switch (type) {
            case ECONOMY_COURIER:
            case CG_LSX:
            case CG_LSE:
            case CG_ECOR:
            case CG_OVN:
            case CG_OVNR:
                return "COURIER_GUY";
            case BUDGET_COURIER:
            case EXPRESS_COURIER:
                return "SKYNET";
            case DASH:
                return "UBER";
            case DSV_ECO:
            case DSV_EXP:
                return "DSV";
            case DPD_SDX:
            case DPD_ECON:
            case DPD_ONX:
            case DPD_ONX1:
                return "DPD_LASER";
            case BRIMA_A12:
            case BRIMA_A9:
            case BRIMA_G17:
            case BRIMA_G24:
                return "BRIMA";
            case F_FREIGHT_ECO:
            case F_FREIGHT_ONX:
            case F_FREIGHT_LOC:
            case F_FREIGHT_LSD:
                return "FIRST_FREIGHT";
            case DEDICATED_CAMPOS:
                return "CAMPOS";
            default:
                return "DROPPA";
        }
    }

    public static List<Type> couriers() {
        return Arrays.asList(
                Type.EXPRESS_COURIER,
                Type.DASH,
                Type.BUDGET_COURIER,
                Type.ECONOMY_COURIER,
                Type.CG_LSX,
                Type.CG_LSE,
                Type.CG_OVNR,
                Type.CG_ECOR,
                Type.CG_OVN,
                Type.DSV_ECO,
                Type.DSV_EXP,
                Type.DPD_ECON,
                Type.DPD_ROAD,
                Type.DPD_ONX,
                Type.DPD_ONX1,
                Type.DPD_SDX,
                Type.BRIMA_A12,
                Type.BRIMA_A9,
                Type.BRIMA_G17,
                Type.BRIMA_G24,
                Type.F_FREIGHT_ECO,
                Type.F_FREIGHT_LOC,
                Type.F_FREIGHT_LSD,
                Type.F_FREIGHT_ONX,
                Type.DEDICATED_CAMPOS
        );
    }
}
