package za.co.droppa.dto;

import lombok.Data;

import java.util.List;

@Data
public class BillDTO {
	
	private String generatedDate;
	
	private String reference;
	
	private String recipientName;
	
	private String recipientEmail;
	
	private String discountAmt;
	
	private String salesTax;
	
	private List<QuoteInvoiceDTO> listOfInvoice;
	
	private String actualRecipient;
	
	private String recipientPhone;
	
}
