package za.co.droppa.model;

public enum BookingPaymentType {
	
	
    ONLINE_PAY("Online Payment"),
    VOUCHER("Voucher"),
    RETAIL_PAY("Retail Payment"),
    WALLET_PAY("Wallet Payment"),
    INVOICE("Invoice"),
    VODAPAY("Voda Pay"),
    CASH_PAY("Cash Payment");

    private String description;

    BookingPaymentType(String description) {
        this.description = description;
    }

    public String description() { return description; }
    
    public static BookingPaymentType findByName(String name) {
    	;
        for (BookingPaymentType v : values()) {
            if (v.name().equals(name.toUpperCase())) {
                return v;
            }
        }
        return null;
    }

}
