package za.co.droppa.dto;

import lombok.Data;

@Data
public class QuoteInvoiceDTO {

	private int quantity;

	private String description;

	private String price;

	private String paymentType;

	private String delivaryDate;
}
