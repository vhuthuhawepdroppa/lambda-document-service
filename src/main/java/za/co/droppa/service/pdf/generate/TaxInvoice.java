package za.co.droppa.service.pdf.generate;

import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import za.co.droppa.dto.VatInvoiceDetailsDTO;
import za.co.droppa.model.*;
import za.co.droppa.model.rental.RentalTruck;
import za.co.droppa.util.DateUtil;
import za.co.droppa.util.FontUtil;
import za.co.droppa.util.PDFUtil;

import java.io.ByteArrayInputStream;
import java.time.LocalDateTime;
import java.util.Map;

/**
 * @author Vhuthuhawe
 * @date 2024/08/03
 */
public class TaxInvoice extends AbstractPdfDocument {
    public TaxInvoice(String base64String,String name,String heading, String waybill) throws DocumentException {
        super(base64String,name,heading,waybill);
    }

    /**
     * Builds a PDF document based on the provided parameters and returns it as a ByteArrayInputStream.
     *
     * @param params a map of parameters including invoice type, information object, VAT number, and bank account details.
     * @return a ByteArrayInputStream containing the generated PDF document.
     */
    @Override
    public ByteArrayInputStream buildDocument(Map<String,Object> params) {

        Object invoiceType = params.get("invoiceType");
        Object infoObj = params.get("infoObj");
        String vatNo = (String) params.get("vatNo");
        BankAccount bank = (BankAccount) params.get("bank");

        try {

            open();


            if(invoiceType instanceof RentalTruck) {
                RentalTruck rentalTruck = (RentalTruck) invoiceType;

                if (bank != null)
                    PDFUtil.addBankingDetailsAbsolute(writer, bank);

                Person person = (Person) infoObj;
                document.add(clientInformationTableTaxInvoiceTruck(rentalTruck, person, vatNo));

                document.add(taxInvoiceRetailDetails(rentalTruck));

            } else if (invoiceType instanceof Booking) {
                Booking booking = (Booking) invoiceType;


                if (infoObj instanceof Person) {
                    Person person = (Person) infoObj;
                    document.add(clientInformationTableTaxInvoiceNormal(booking, person));
                }

                if (bank != null)
                    PDFUtil.addBankingDetailsAbsolute(writer, bank);

                if(infoObj instanceof VatInvoiceDetailsDTO){
                    VatInvoiceDetailsDTO vatInvoice = (VatInvoiceDetailsDTO) infoObj;
                    document.add(clientInformationTableTaxInvoiceNormalVat(booking, vatInvoice));
                }

                document.add(taxInvoiceNormalDetails(booking));
            }
            else if(invoiceType instanceof BucketBooking){
                BucketBooking booking = (BucketBooking) invoiceType;


                if (bank != null)
                    PDFUtil.addBankingDetailsAbsolute(writer, bank);

                document.add(clientInformationTableTaxInvoiceRetail(booking,booking.getRetail(),vatNo));

                document.add(taxInvoiceRetailDetails(booking));
            }


            close();
        } catch (DocumentException e) {
            System.out.println(e.getMessage());
        }

        return new ByteArrayInputStream(out.toByteArray());
    }

    /**
     * Creates a PDF table containing client information for a truck rental invoice.
     *
     * @param rentalTruck the RentalTruck object containing rental details.
     * @param person the Person object containing client details.
     * @param vatNo the VAT number to be included in the table.
     * @return a PdfPTable with client information for the truck rental invoice.
     */
    private PdfPTable clientInformationTableTaxInvoiceTruck (RentalTruck rentalTruck, Person person, String vatNo) {

        String refNo = "INV"+rentalTruck.getTrackNo().replaceAll("[^\\d.]", "");

        PdfPTable clientInformationTable = new PdfPTable(3);
        clientInformationTable.setWidthPercentage(100);

        clientInformationTable.addCell(PDFUtil.createCell("Client Information", 3, PDFUtil.lightGray, 10, false, 2, Element.ALIGN_LEFT)).setPaddingLeft(10);


        PdfPTable leftClientInformationTable = new PdfPTable(2);
        PDFUtil.setColumnWidths(leftClientInformationTable,new float[]{1.5f,2f});
        leftClientInformationTable.addCell(PDFUtil.createCell("To:", FontUtil.small, false, 1));
        leftClientInformationTable.addCell(PDFUtil.createCell(person.getFirstName()+" "+person.getSurname(), FontUtil.small, false, 1));

        leftClientInformationTable.addCell(PDFUtil.createCell("Email:", FontUtil.small, false, 1));
        leftClientInformationTable.addCell(PDFUtil.createCell(person.getEmail(), FontUtil.small, false, 1));

        leftClientInformationTable.addCell(PDFUtil.createCell("REF:", FontUtil.small, false, 1));
        leftClientInformationTable.addCell(PDFUtil.createCell(refNo, FontUtil.small, false, 1));

        leftClientInformationTable.addCell(PDFUtil.createCell("Tel:", FontUtil.small, false, 1));
        leftClientInformationTable.addCell(PDFUtil.createCell(person.getMobile(), FontUtil.small, false, 1));

        leftClientInformationTable.addCell(PDFUtil.createCell("VAT:", FontUtil.small, false, 1));
        leftClientInformationTable.addCell(PDFUtil.createCell(vatNo, FontUtil.small, false, 1));


        clientInformationTable.addCell(PDFUtil.toPDFTableCell(leftClientInformationTable, 1));


        PdfPTable emptyMiddleTable = new PdfPTable(1);
        PDFUtil.addEmptyCells(emptyMiddleTable,4);

        clientInformationTable.addCell(PDFUtil.toPDFTableCell(emptyMiddleTable,1));

        PdfPTable rightClientInformationTable = new PdfPTable(2);
        PDFUtil.setColumnWidths(rightClientInformationTable,new float[]{1.5f,2f});
        rightClientInformationTable.addCell(PDFUtil.createCell("Issue Date:", FontUtil.small, false, 1));
        rightClientInformationTable.addCell(PDFUtil.createCell(DateUtil.toDate(LocalDateTime.now()), FontUtil.small, false, 1));

        rightClientInformationTable.addCell(PDFUtil.createCell("Period:", FontUtil.small, false, 1));
        rightClientInformationTable.addCell(PDFUtil.createCell(DateUtil.toDate(rentalTruck.getRentalCreationDate()), FontUtil.small, false, 1));

        clientInformationTable.addCell(PDFUtil.toPDFTableCell(rightClientInformationTable, 1));

        return clientInformationTable;
    }

    /**
     * Creates a PDF table containing retail details for a bucket booking invoice.
     *
     * @param booking the BucketBooking object containing booking details.
     * @return an Element representing the retail details section of the invoice.
     */
    private Element taxInvoiceRetailDetails(BucketBooking booking) {

        PdfPTable retailNDetails = new PdfPTable(5);

        PDFUtil.setColumnWidths(retailNDetails, new float[]{1f, 4f, 2f, 2f, 2.5f});

        retailNDetails.setWidthPercentage(100);

        retailNDetails.addCell(PDFUtil.createCell("ID", 1, PDFUtil.lightGray, 10, false, 2, Element.ALIGN_CENTER)).setPaddingLeft(10);
        retailNDetails.addCell(PDFUtil.createCell("DESCRIPTION", 1, PDFUtil.lightGray, 10, false, 2, Element.ALIGN_CENTER)).setPaddingLeft(10);
        retailNDetails.addCell(PDFUtil.createCell("DATE", 1, PDFUtil.lightGray, 10, false, 2, Element.ALIGN_CENTER)).setPaddingLeft(10);
        retailNDetails.addCell(PDFUtil.createCell("QTY", 1, PDFUtil.lightGray, 10, false, 2, Element.ALIGN_CENTER)).setPaddingLeft(10);
        retailNDetails.addCell(PDFUtil.createCell("TOTAL", 1, PDFUtil.lightGray, 10, false, 2, Element.ALIGN_LEFT)).setPaddingLeft(10);


        double totalPrice = 0.00;
        double subPrice = 0.00;
        double totalSubPrice = 0.00;

        for (int i = 0; i < booking.getBookings().size(); i++) {
            Booking currentBooking = booking.getBookings().get(i);
            int quantity = 1;


//            double curSubPrice = currentBooking.getPrice() / configuration().reverseVat();
            double curSubPrice = currentBooking.getPrice() / 2;
            subPrice+=curSubPrice;

            String index = (i < 9) ? "0" + (i + 1) : String.valueOf(i + 1);

            if ((i + 1) % 2 != 0) {

                retailNDetails.addCell(PDFUtil.createCell(index, 1, 7, false, 1, Element.ALIGN_CENTER, 0, 10, 10));
                retailNDetails.addCell(PDFUtil.createCell("Droppa delivery reference : " + currentBooking.getTrackNo(), 1, 7, false, 1, Element.ALIGN_CENTER, 0, 10, 10));
                retailNDetails.addCell(PDFUtil.createCell(DateUtil.toDate(booking.getBookingCreatedDate()), 1, 7, false, 1, Element.ALIGN_CENTER, 0, 10, 10));
                retailNDetails.addCell(PDFUtil.createCell(quantity + "", 1, 7, false, 2, Element.ALIGN_CENTER, 0, 10, 10));
                retailNDetails.addCell(PDFUtil.createCell("R" + money.format(curSubPrice), 1, 7, false, 2, Element.ALIGN_LEFT, 0, 10, 10));
            } else {

                retailNDetails.addCell(PDFUtil.createCell(index, 1, 7, false, 1, Element.ALIGN_CENTER, 0, 10, 10)).setBackgroundColor(PDFUtil.lighterGray);
                retailNDetails.addCell(PDFUtil.createCell("Droppa delivery reference : " + currentBooking.getTrackNo(), 1, 7, false, 1, Element.ALIGN_CENTER, 0, 10, 10)).setBackgroundColor(PDFUtil.lighterGray);
                retailNDetails.addCell(PDFUtil.createCell(DateUtil.toDate(booking.getBookingCreatedDate()), 1, 7, false, 1, Element.ALIGN_CENTER, 0, 10, 10)).setBackgroundColor(PDFUtil.lighterGray);
                retailNDetails.addCell(PDFUtil.createCell(quantity + "", 1, 7, false, 2, Element.ALIGN_CENTER, 0, 10, 10)).setBackgroundColor(PDFUtil.lighterGray);
                retailNDetails.addCell(PDFUtil.createCell("R" + money.format(curSubPrice), 1, 7, false, 2, Element.ALIGN_LEFT, 0, 10, 10)).setBackgroundColor(PDFUtil.lighterGray);
            }

            totalSubPrice += curSubPrice;
            totalPrice += currentBooking.getPrice();
        }




        double vatAmount = totalPrice - totalSubPrice;

        PDFUtil.addEmptyCells(retailNDetails, 3);
        retailNDetails.addCell(PDFUtil.createCell("Sub Total", 1, 9, false, 1, Element.ALIGN_LEFT, -1, 8, 8));
        PdfPCell subTotalCell = PDFUtil.createCell("R" + money.format(subPrice), 1, 9, false, 3, Element.ALIGN_LEFT, -1, 8, 8);
        subTotalCell.setBackgroundColor(PDFUtil.lighterGray);
        retailNDetails.addCell(subTotalCell);

        PDFUtil.addEmptyCells(retailNDetails, 3);
        retailNDetails.addCell(PDFUtil.createCell("VAT 15%", 1, 9, false, 1, Element.ALIGN_LEFT, -1, 8, 8));
        PdfPCell vatAmountCell = PDFUtil.createCell("R" + money.format(vatAmount), 1, 9, false, 3, Element.ALIGN_LEFT, -1, 8, 8);
        vatAmountCell.setBackgroundColor(PDFUtil.lighterGray);
        retailNDetails.addCell(vatAmountCell);


        PDFUtil.addEmptyCells(retailNDetails, 3);
        retailNDetails.addCell(PDFUtil.createCell("Total", 1, 9, false, 3, Element.ALIGN_LEFT, -1, 8, 8));
        PdfPCell totalCell = PDFUtil.createCell("R" + money.format(totalPrice), 1, 12, false, 3, Element.ALIGN_LEFT, -1, 8, 8);
        totalCell.setBackgroundColor(PDFUtil.lightBlue);
        retailNDetails.addCell(totalCell);

        return retailNDetails;
    }

    /**
     * Creates a PDF table containing retail details for a booking invoice.
     *
     * @param booking the Booking object containing booking details.
     * @return an Element representing the retail details section of the invoice.
     */
    private Element taxInvoiceRetailDetails(Booking booking) {

        return null;


    }

    /**
     * Creates a PDF table for displaying retail details on a tax invoice for a rental truck.
     *
     * This method generates a table that includes details of the rental truck, such as description, date, quantity,
     * and total price. It also calculates and displays the subtotal, VAT, and total amount. The table is formatted
     * with alternating row colors for better readability.
     *
     * @param rentalTruck The RentalTruck object containing details about the rental, such as the total cost, track number,
     *                    and rental creation date.
     * @return A PdfPTable instance containing the formatted retail details for the tax invoice, including item details,
     *         subtotal, VAT, and total amount.
     */
    private PdfPTable taxInvoiceRetailDetails(RentalTruck rentalTruck) {
        PdfPTable retailDetailsTable = new PdfPTable(5);

        PDFUtil.setColumnWidths(retailDetailsTable,new float[]{1f, 4f,2f,2f,2.5f});

        retailDetailsTable.setWidthPercentage(100);

        retailDetailsTable.addCell(PDFUtil.createCell("ID", 1, PDFUtil.lightGray, 10, false, 2, Element.ALIGN_CENTER)).setPaddingLeft(10);
        retailDetailsTable.addCell(PDFUtil.createCell("DESCRIPTION", 1, PDFUtil.lightGray, 10, false, 2, Element.ALIGN_CENTER)).setPaddingLeft(10);
        retailDetailsTable.addCell(PDFUtil.createCell("DATE", 1, PDFUtil.lightGray, 10, false, 2, Element.ALIGN_CENTER)).setPaddingLeft(10);
        retailDetailsTable.addCell(PDFUtil.createCell("QTY", 1, PDFUtil.lightGray, 10, false, 2, Element.ALIGN_CENTER)).setPaddingLeft(10);
        retailDetailsTable.addCell(PDFUtil.createCell("TOTAL", 1, PDFUtil.lightGray, 10, false, 2, Element.ALIGN_LEFT)).setPaddingLeft(10);

        double totalPrice = 0.00;
        double subTotal = 0.00;
        for(int i=0;i<1;i++) {

            int quantity = 1;
//            subTotal = rentalTruck.getTotal() / configuration().reverseVat();
            subTotal = rentalTruck.getTotal() / 2;

            String index = (i < 9) ? "0" + (i + 1) : String.valueOf(i + 1);

            if ((i + 1) % 2 != 0) {
//                subTotal = rentalTruck.getTotal() / configuration().reverseVat();
                subTotal = rentalTruck.getTotal() / 2;

                retailDetailsTable.addCell(PDFUtil.createCell(index, 1, 7, false, 1, Element.ALIGN_CENTER, 0, 10, 10));
                retailDetailsTable.addCell(PDFUtil.createCell("Droppa rental reference: " + rentalTruck.getTrackNo(), 1, 7, false, 1, Element.ALIGN_CENTER, 0, 10, 10));
                retailDetailsTable.addCell(PDFUtil.createCell(DateUtil.toDate(rentalTruck.getRentalCreationDate()), 1, 7, false, 1, Element.ALIGN_CENTER, 0, 10, 10));
                retailDetailsTable.addCell(PDFUtil.createCell(quantity+"", 1, 7, false, 2, Element.ALIGN_CENTER, 0, 10, 10));
                retailDetailsTable.addCell(PDFUtil.createCell("R" + money.format(subTotal), 1, 7, false, 2, Element.ALIGN_LEFT, 0, 10, 10));
            }
            else {

                retailDetailsTable.addCell(PDFUtil.createCell(index, 1, 7, false, 1, Element.ALIGN_CENTER, 0, 10, 10)).setBackgroundColor(PDFUtil.lighterGray);
                retailDetailsTable.addCell(PDFUtil.createCell("Droppa rental reference: " + rentalTruck.getTrackNo(), 1, 7, false, 1, Element.ALIGN_CENTER, 0, 10, 10)).setBackgroundColor(PDFUtil.lighterGray);
                retailDetailsTable.addCell(PDFUtil.createCell(DateUtil.toDate(rentalTruck.getRentalCreationDate()), 1, 7, false, 1, Element.ALIGN_CENTER, 0, 10, 10)).setBackgroundColor(PDFUtil.lighterGray);
                retailDetailsTable.addCell(PDFUtil.createCell(quantity+"", 1, 7, false, 2, Element.ALIGN_CENTER, 0, 10, 10)).setBackgroundColor(PDFUtil.lighterGray);
                retailDetailsTable.addCell(PDFUtil.createCell("R" + money.format(subTotal), 1, 7, false, 2, Element.ALIGN_LEFT, 0, 10, 10)).setBackgroundColor(PDFUtil.lighterGray);
            }

            totalPrice += rentalTruck.getTotal();
        }


        double vatAmount = totalPrice - subTotal;

        PDFUtil.addEmptyCells(retailDetailsTable,3);
        retailDetailsTable.addCell(PDFUtil.createCell("Sub Total",1, 9, false, 1, Element.ALIGN_LEFT,-1,8,8));
        PdfPCell subTotalCell = PDFUtil.createCell("R" + money.format(subTotal),1, 9, false, 3, Element.ALIGN_LEFT,-1,8,8);
        subTotalCell.setBackgroundColor(PDFUtil.lighterGray);
        retailDetailsTable.addCell(subTotalCell);

        PDFUtil.addEmptyCells(retailDetailsTable,3);
        retailDetailsTable.addCell(PDFUtil.createCell("VAT 15%",1, 9, false, 1, Element.ALIGN_LEFT,-1,8,8));
        PdfPCell vatAmountCell = PDFUtil.createCell("R" + money.format(vatAmount),1, 9, false, 3, Element.ALIGN_LEFT,-1,8,8);
        vatAmountCell.setBackgroundColor(PDFUtil.lighterGray);
        retailDetailsTable.addCell(vatAmountCell);


        PDFUtil.addEmptyCells(retailDetailsTable,3);
        retailDetailsTable.addCell(PDFUtil.createCell("Total",1, 9, false, 3, Element.ALIGN_LEFT,-1,8,8));
        PdfPCell totalCell= PDFUtil.createCell("R" + money.format(totalPrice),1, 12, false, 3, Element.ALIGN_LEFT,-1,8,8);
        totalCell.setBackgroundColor(PDFUtil.lightBlue);
        retailDetailsTable.addCell(totalCell);

        return retailDetailsTable;
    }

    /**
     * Creates a PDF table for displaying client information on a tax invoice for a retail transaction.
     *
     * This method generates a table that includes client information such as the client's name, email, reference number,
     * telephone number, VAT number, and physical address. It also includes invoice-related details such as the issue date
     * and period. The table is divided into left and right sections for better organization.
     *
     * @param booking The BucketBooking object containing details about the booking, including the track number and booking date.
     * @param retail The Retail object containing details about the retail client, including name, contact information, and address.
     * @param vatNo The VAT number of the client or the business.
     * @return A PdfPTable instance containing the formatted client information for the tax invoice, including client details
     *         and invoice-related information.
     */
    private PdfPTable clientInformationTableTaxInvoiceRetail (BucketBooking booking, Retail retail, String vatNo) {

        PdfPTable clientInformationTable = new PdfPTable(3);
        clientInformationTable.setWidthPercentage(100);

        clientInformationTable.addCell(PDFUtil.createCell("Client Information", 3, PDFUtil.lightGray, 10, false, 2,Element.ALIGN_LEFT)).setPaddingLeft(10);


        PdfPTable leftClientInformationTable = new PdfPTable(2);
        PDFUtil.setColumnWidths(leftClientInformationTable,new float[]{1.5f,2f});
        leftClientInformationTable.addCell(PDFUtil.createCell("To:", FontUtil.small, false, 1));
        leftClientInformationTable.addCell(PDFUtil.createCell(retail.getName(), FontUtil.small, false, 1));

        leftClientInformationTable.addCell(PDFUtil.createCell("Email:", FontUtil.small, false, 1));
        leftClientInformationTable.addCell(PDFUtil.createCell(retail.getOwner().getEmail(), FontUtil.small, false, 1));

        leftClientInformationTable.addCell(PDFUtil.createCell("REF:", FontUtil.small, false, 1));
        leftClientInformationTable.addCell(PDFUtil.createCell(booking.getTrackNo(), FontUtil.small, false, 1));

        leftClientInformationTable.addCell(PDFUtil.createCell("Tel:", FontUtil.small, false, 1));
        leftClientInformationTable.addCell(PDFUtil.createCell(retail.getOwner().getMobile(), FontUtil.small, false, 1));

        leftClientInformationTable.addCell(PDFUtil.createCell("VAT:", FontUtil.small, false, 1));
        leftClientInformationTable.addCell(PDFUtil.createCell(vatNo, FontUtil.small, false, 1));

        leftClientInformationTable.addCell(PDFUtil.createCell("Physical Address :", FontUtil.small, false, 1));
        leftClientInformationTable.addCell(PDFUtil.createCell(retail.getAddress().getAddressLine1(), FontUtil.small, false, 1));


        clientInformationTable.addCell(PDFUtil.toPDFTableCell(leftClientInformationTable, 1));


        PdfPTable emptyMiddleTable = new PdfPTable(1);
        PDFUtil.addEmptyCells(emptyMiddleTable,4);

        clientInformationTable.addCell(PDFUtil.toPDFTableCell(emptyMiddleTable,1));

        PdfPTable rightClientInformationTable = new PdfPTable(2);
        PDFUtil.setColumnWidths(rightClientInformationTable,new float[]{1.5f,2f});
        rightClientInformationTable.addCell(PDFUtil.createCell("Issue Date:", FontUtil.small, false, 1));
        rightClientInformationTable.addCell(PDFUtil.createCell(DateUtil.toDate(booking.getBookingCreatedDate()), FontUtil.small, false, 1));

        rightClientInformationTable.addCell(PDFUtil.createCell("Period:", FontUtil.small, false, 1));
        rightClientInformationTable.addCell(PDFUtil.createCell("", FontUtil.small, false, 1));

        clientInformationTable.addCell(PDFUtil.toPDFTableCell(rightClientInformationTable, 1));

        return clientInformationTable;
    }


    /**
     * Creates a PDF table for displaying client information on a standard tax invoice.
     *
     * This method generates a table that includes client details and invoice information, formatted into
     * a PDF layout. The table is divided into three main sections: client information on the left,
     * an empty space in the middle, and invoice details on the right.
     *
     * @param booking The Booking object containing the booking details such as the track number and pickup address.
     * @param person The Person object containing client details such as name, email, and mobile number.
     * @return A PdfPTable instance containing the formatted client and invoice information for the tax invoice.
     */
    private PdfPTable clientInformationTableTaxInvoiceNormal (Booking booking,Person person) {

        String refNo = "INV"+booking.getTrackNo().replaceAll("[^\\d.]", "");


        PdfPTable clientInformationTable = new PdfPTable(3);
        clientInformationTable.setWidthPercentage(100);

        clientInformationTable.addCell(PDFUtil.createCell("Client Information", 3, PDFUtil.lightGray, 10, false, 2,Element.ALIGN_LEFT)).setPaddingLeft(10);


        PdfPTable leftClientInformationTable = new PdfPTable(2);
        PDFUtil.setColumnWidths(leftClientInformationTable,new float[]{1.5f,2f});
        leftClientInformationTable.addCell(PDFUtil.createCell("To:", FontUtil.small, false, 1));
        leftClientInformationTable.addCell(PDFUtil.createCell(person.getFirstName()+" "+person.getSurname(), FontUtil.small, false, 1));

        leftClientInformationTable.addCell(PDFUtil.createCell("Email:", FontUtil.small, false, 1));
        leftClientInformationTable.addCell(PDFUtil.createCell(person.getEmail(), FontUtil.small, false, 1));

        leftClientInformationTable.addCell(PDFUtil.createCell("Tel:", FontUtil.small, false, 1));
        leftClientInformationTable.addCell(PDFUtil.createCell(person.getMobile(), FontUtil.small, false, 1));

        leftClientInformationTable.addCell(PDFUtil.createCell("Physical Address:", FontUtil.small, false, 1));
        leftClientInformationTable.addCell(PDFUtil.createCell(booking.getPickUpAddress() , FontUtil.small, false, 1));


        clientInformationTable.addCell(PDFUtil.toPDFTableCell(leftClientInformationTable, 1));


        PdfPTable emptyMiddleTable = new PdfPTable(1);
        PDFUtil.addEmptyCells(emptyMiddleTable,4);

        clientInformationTable.addCell(PDFUtil.toPDFTableCell(emptyMiddleTable,1));

        PdfPTable rightClientInformationTable = new PdfPTable(2);
        PDFUtil.setColumnWidths(rightClientInformationTable,new float[]{1.5f,2f});


        rightClientInformationTable.addCell(PDFUtil.createCell("Invoice No:", FontUtil.small, false, 1));
        rightClientInformationTable.addCell(PDFUtil.createCell(refNo, FontUtil.small, false, 1));

        rightClientInformationTable.addCell(PDFUtil.createCell("Issue Date:", FontUtil.small, false, 1));
        rightClientInformationTable.addCell(PDFUtil.createCell(DateUtil.toDate(booking.getBookingCreatedDate()), FontUtil.small, false, 1));


        clientInformationTable.addCell(PDFUtil.toPDFTableCell(rightClientInformationTable, 1));

        return clientInformationTable;
    }


    /**
     * Creates a PDF table for displaying client information on a tax invoice, including VAT details.
     *
     * @param booking The Booking object containing the booking details for the invoice.
     * @param vatInvoiceDetailsDTO The VatInvoiceDetailsDTO object containing client information and VAT details.
     * @return A PdfPTable instance containing the client information formatted for a tax invoice.
     */
    private PdfPTable clientInformationTableTaxInvoiceNormalVat (Booking booking,VatInvoiceDetailsDTO vatInvoiceDetailsDTO) {

        String refNo = "INV"+booking.getTrackNo().replaceAll("[^\\d.]", "");


        PdfPTable clientInformationTable = new PdfPTable(3);
        clientInformationTable.setWidthPercentage(100);

        clientInformationTable.addCell(PDFUtil.createCell("Client Information", 3, PDFUtil.lightGray, 10, false, 2,Element.ALIGN_LEFT)).setPaddingLeft(10);


        PdfPTable leftClientInformationTable = new PdfPTable(2);
        PDFUtil.setColumnWidths(leftClientInformationTable,new float[]{1.5f,2f});

        leftClientInformationTable.addCell(PDFUtil.createCell("Company Name:", FontUtil.small, false, 1));
        leftClientInformationTable.addCell(PDFUtil.createCell(vatInvoiceDetailsDTO.companyName, FontUtil.small, false, 1));

        leftClientInformationTable.addCell(PDFUtil.createCell("VAT", FontUtil.small, false, 1));
        leftClientInformationTable.addCell(PDFUtil.createCell(vatInvoiceDetailsDTO.vatNo, FontUtil.small, false, 1));

        leftClientInformationTable.addCell(PDFUtil.createCell("To:", FontUtil.small, false, 1));
        leftClientInformationTable.addCell(PDFUtil.createCell(vatInvoiceDetailsDTO.toNames, FontUtil.small, false, 1));

        leftClientInformationTable.addCell(PDFUtil.createCell("Email:", FontUtil.small, false, 1));
        leftClientInformationTable.addCell(PDFUtil.createCell(vatInvoiceDetailsDTO.toEmail, FontUtil.small, false, 1));

        leftClientInformationTable.addCell(PDFUtil.createCell("REF:", FontUtil.small, false, 1));
        leftClientInformationTable.addCell(PDFUtil.createCell(vatInvoiceDetailsDTO.trackNo, FontUtil.small, false, 1));

        leftClientInformationTable.addCell(PDFUtil.createCell("Tel:", FontUtil.small, false, 1));
        leftClientInformationTable.addCell(PDFUtil.createCell(vatInvoiceDetailsDTO.toPhone, FontUtil.small, false, 1));

        leftClientInformationTable.addCell(PDFUtil.createCell("Physical Address:", FontUtil.small, false, 1));
        leftClientInformationTable.addCell(PDFUtil.createCell(vatInvoiceDetailsDTO.address , FontUtil.small, false, 1));


        clientInformationTable.addCell(PDFUtil.toPDFTableCell(leftClientInformationTable, 1));


        PdfPTable emptyMiddleTable = new PdfPTable(1);
        PDFUtil.addEmptyCells(emptyMiddleTable,4);

        clientInformationTable.addCell(PDFUtil.toPDFTableCell(emptyMiddleTable,1));

        PdfPTable rightClientInformationTable = new PdfPTable(2);
        PDFUtil.setColumnWidths(rightClientInformationTable,new float[]{1.5f,2f});


        rightClientInformationTable.addCell(PDFUtil.createCell("Invoice No:", FontUtil.small, false, 1));
        rightClientInformationTable.addCell(PDFUtil.createCell(refNo, FontUtil.small, false, 1));

        rightClientInformationTable.addCell(PDFUtil.createCell("Issue Date:", FontUtil.small, false, 1));
        rightClientInformationTable.addCell(PDFUtil.createCell(DateUtil.toDate(booking.getBookingCreatedDate()), FontUtil.small, false, 1));


        clientInformationTable.addCell(PDFUtil.toPDFTableCell(rightClientInformationTable, 1));

        return clientInformationTable;
    }

    /**
     * Generates a PDF table for normal booking details in a tax invoice.
     *
     * @param booking The Booking object containing the details to be included in the table.
     * @return A PdfPTable instance containing the normal booking details formatted for a tax invoice.
     */
    private Element taxInvoiceNormalDetails(Booking booking) {

        PdfPTable retailNDetails = new PdfPTable(5);

        PDFUtil.setColumnWidths(retailNDetails, new float[]{1f, 4f, 2f, 2f, 2.5f});

        retailNDetails.setWidthPercentage(100);

        retailNDetails.addCell(PDFUtil.createCell("ID", 1, PDFUtil.lightGray, 10, false, 2, Element.ALIGN_CENTER)).setPaddingLeft(10);
        retailNDetails.addCell(PDFUtil.createCell("DESCRIPTION", 1, PDFUtil.lightGray, 10, false, 2, Element.ALIGN_CENTER)).setPaddingLeft(10);
        retailNDetails.addCell(PDFUtil.createCell("DATE", 1, PDFUtil.lightGray, 10, false, 2, Element.ALIGN_CENTER)).setPaddingLeft(10);
        retailNDetails.addCell(PDFUtil.createCell("QTY", 1, PDFUtil.lightGray, 10, false, 2, Element.ALIGN_CENTER)).setPaddingLeft(10);
        retailNDetails.addCell(PDFUtil.createCell("TOTAL", 1, PDFUtil.lightGray, 10, false, 2, Element.ALIGN_LEFT)).setPaddingLeft(10);


        double totalPrice = 0.00;

        double subTotal = 0.00;
        for (int i = 0; i < 1; i++) {

            int quantity = 1;
//            subTotal = booking.getPrice() / configuration().reverseVat();
            subTotal = booking.getPrice() / 2;
            String index = (i < 9) ? "0" + (i + 1) : String.valueOf(i + 1);


            if ((i + 1) % 2 != 0) {

                retailNDetails.addCell(PDFUtil.createCell(index, 1, 7, false, 1, Element.ALIGN_CENTER, 0, 10, 10));
                retailNDetails.addCell(PDFUtil.createCell("Droppa delivery reference : " + booking.getTrackNo(), 1, 7, false, 1, Element.ALIGN_CENTER, 0, 10, 10));
                retailNDetails.addCell(PDFUtil.createCell(DateUtil.toDate(booking.getBookingCreatedDate()), 1, 7, false, 1, Element.ALIGN_CENTER, 0, 10, 10));
                retailNDetails.addCell(PDFUtil.createCell(quantity + "", 1, 7, false, 2, Element.ALIGN_CENTER, 0, 10, 10));
                retailNDetails.addCell(PDFUtil.createCell("R" + money.format(subTotal), 1, 7, false, 2, Element.ALIGN_LEFT, 0, 10, 10));
            } else {

                retailNDetails.addCell(PDFUtil.createCell(index, 1, 7, false, 1, Element.ALIGN_CENTER, 0, 10, 10)).setBackgroundColor(PDFUtil.lighterGray);
                retailNDetails.addCell(PDFUtil.createCell("Droppa delivery reference : " + booking.getTrackNo(), 1, 7, false, 1, Element.ALIGN_CENTER, 0, 10, 10)).setBackgroundColor(PDFUtil.lighterGray);
                retailNDetails.addCell(PDFUtil.createCell(DateUtil.toDate(booking.getBookingCreatedDate()), 1, 7, false, 1, Element.ALIGN_CENTER, 0, 10, 10)).setBackgroundColor(PDFUtil.lighterGray);
                retailNDetails.addCell(PDFUtil.createCell(quantity + "", 1, 7, false, 2, Element.ALIGN_CENTER, 0, 10, 10)).setBackgroundColor(PDFUtil.lighterGray);
                retailNDetails.addCell(PDFUtil.createCell("R" + money.format(subTotal), 1, 7, false, 2, Element.ALIGN_LEFT, 0, 10, 10)).setBackgroundColor(PDFUtil.lighterGray);
            }

            totalPrice += booking.getPrice();
        }


        double vatAmount = totalPrice - subTotal;

        PDFUtil.addEmptyCells(retailNDetails, 3);
        retailNDetails.addCell(PDFUtil.createCell("Sub Total", 1, 9, false, 1, Element.ALIGN_LEFT, -1, 8, 8));
        PdfPCell subTotalCell = PDFUtil.createCell("R" + money.format(subTotal), 1, 9, false, 3, Element.ALIGN_LEFT, -1, 8, 8);
        subTotalCell.setBackgroundColor(PDFUtil.lighterGray);
        retailNDetails.addCell(subTotalCell);

        PDFUtil.addEmptyCells(retailNDetails, 3);
        retailNDetails.addCell(PDFUtil.createCell("VAT 15%", 1, 9, false, 1, Element.ALIGN_LEFT, -1, 8, 8));
        PdfPCell vatAmountCell = PDFUtil.createCell("R" + money.format(vatAmount), 1, 9, false, 3, Element.ALIGN_LEFT, -1, 8, 8);
        vatAmountCell.setBackgroundColor(PDFUtil.lighterGray);
        retailNDetails.addCell(vatAmountCell);


        PDFUtil.addEmptyCells(retailNDetails, 3);
        retailNDetails.addCell(PDFUtil.createCell("Total", 1, 9, false, 3, Element.ALIGN_LEFT, -1, 8, 8));
        PdfPCell totalCell = PDFUtil.createCell("R" + money.format(totalPrice), 1, 12, false, 3, Element.ALIGN_LEFT, -1, 8, 8);
        totalCell.setBackgroundColor(PDFUtil.lightBlue);
        retailNDetails.addCell(totalCell);

        return retailNDetails;
    }
}

