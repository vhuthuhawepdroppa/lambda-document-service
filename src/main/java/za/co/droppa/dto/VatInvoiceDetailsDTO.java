package za.co.droppa.dto;

import lombok.Data;

@Data
public class VatInvoiceDetailsDTO {

    public String companyName;

    public String address;

    public String vatNo;

    public String toNames;

    public String toEmail;

    public String toPhone;

    public String trackNo;
}
