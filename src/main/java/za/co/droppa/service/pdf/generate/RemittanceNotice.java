package za.co.droppa.service.pdf.generate;

import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import za.co.droppa.model.BankAccount;
import za.co.droppa.model.Booking;
import za.co.droppa.model.Person;
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
public class RemittanceNotice extends AbstractPdfDocument {
    public RemittanceNotice(String base64String,String name,String heading, String waybill) throws DocumentException {
        super(base64String,name,heading,waybill);
    }

    @Override
    public ByteArrayInputStream buildDocument(Map<String, Object> params)  {

        BankAccount bank = (BankAccount) params.get("bank");
        Booking booking = (Booking) params.get("booking");
        Person person = (Person) params.get("person");

        try {

            open();

            PDFUtil.addBankingDetailsAbsolute(writer, bank);

            document.setMargins(document.leftMargin(), document.rightMargin(), document.topMargin(), document.bottomMargin() + 120);



            document.add(clientInformationRemittanceTable(booking,person));
            document.add(PDFUtil.blankLine(-15));

            document.add(remittanceNoticeDetails(booking));
            document.add(PDFUtil.blankLine(0));

            close();
        } catch (DocumentException e) {
            System.out.printf("unable to generate remittance notice, {%s}%n",e.getMessage());
        }

        return new ByteArrayInputStream(out.toByteArray());
    }


    /**
     * Creates a PDF table containing client and booking information for a remittance.
     *
     * @param booking an instance of Booking containing the booking details.
     * @param person an instance of Person containing the client's details.
     * @return a PdfPTable object representing the client and booking information section.
     */
    private PdfPTable clientInformationRemittanceTable(Booking booking, Person person) {
        PdfPTable clientInformationTable = new PdfPTable(3);
        clientInformationTable.setWidthPercentage(100);

        clientInformationTable.addCell(PDFUtil.createCell("Client Information", 3, PDFUtil.lightGray, 10, false, 2, Element.ALIGN_LEFT)).setPaddingLeft(10);


        PdfPTable leftClientInformationTable = new PdfPTable(2);
        PDFUtil.setColumnWidths(leftClientInformationTable,new float[]{1,2});

        leftClientInformationTable.addCell(PDFUtil.createCell("Full Names:", FontUtil.small, false, 1));
        leftClientInformationTable.addCell(PDFUtil.createCell(person.getFirstName()+" "+person.getSurname(), FontUtil.small, false, 1));


        leftClientInformationTable.addCell(PDFUtil.createCell("Email:", FontUtil.small, false, 1));
        leftClientInformationTable.addCell(PDFUtil.createCell(person.getEmail(), FontUtil.small, false, 1));

        leftClientInformationTable.addCell(PDFUtil.createCell("REF:", FontUtil.small, false, 1));
        leftClientInformationTable.addCell(PDFUtil.createCell(booking.getTrackNo(), FontUtil.small, false, 1));

        leftClientInformationTable.addCell(PDFUtil.createCell("Tel:", FontUtil.small, false, 1));
        leftClientInformationTable.addCell(PDFUtil.createCell(person.getMobile(), FontUtil.small, false, 1));


        clientInformationTable.addCell(PDFUtil.toPDFTableCell(leftClientInformationTable, 1));


        PdfPTable emptyMiddleTable = new PdfPTable(1);
        PDFUtil.addEmptyCells(emptyMiddleTable,4);
        clientInformationTable.addCell(PDFUtil.toPDFTableCell(emptyMiddleTable,1));

        PdfPTable rightClientInformationTable = new PdfPTable(2);
        PDFUtil.setColumnWidths(rightClientInformationTable,new float[]{1,2});
        rightClientInformationTable.addCell(PDFUtil.createCell("Issue Date:", FontUtil.small, false, 1));
        rightClientInformationTable.addCell(PDFUtil.createCell(DateUtil.toDate(LocalDateTime.now()), FontUtil.small, false, 1));


        clientInformationTable.addCell(PDFUtil.toPDFTableCell(rightClientInformationTable, 1));

        return clientInformationTable;
    }


    /**
     * Creates a PDF table containing the details of a remittance notice for a booking.
     *
     * @param booking an instance of Booking containing the booking details.
     * @return a PdfPTable object representing the remittance details section.
     */
    private PdfPTable remittanceNoticeDetails(Booking booking) {


        PdfPTable remittanceDetailsTable = new PdfPTable(6);

        PDFUtil.setColumnWidths(remittanceDetailsTable,new float[]{1.2f, 5f,4f,3f,3f,3f});

        remittanceDetailsTable.setWidthPercentage(100);

        remittanceDetailsTable.addCell(PDFUtil.createCell("ID", 1, PDFUtil.lightGray, 10, false, 2, Element.ALIGN_CENTER)).setPaddingLeft(10);
        remittanceDetailsTable.addCell(PDFUtil.createCell("DESCRIPTION", 1, PDFUtil.lightGray, 10, false, 2, Element.ALIGN_LEFT)).setPaddingLeft(10);
        remittanceDetailsTable.addCell(PDFUtil.createCell("PAYMENT TYPE", 1, PDFUtil.lightGray, 10, false, 2, Element.ALIGN_CENTER));
        remittanceDetailsTable.addCell(PDFUtil.createCell("PRICE", 1, PDFUtil.lightGray, 10, false, 2, Element.ALIGN_CENTER));
        remittanceDetailsTable.addCell(PDFUtil.createCell("SERVICE FEE", 1, PDFUtil.lightGray, 10, false, 2, Element.ALIGN_CENTER));
        remittanceDetailsTable.addCell(PDFUtil.createCell("TOTAL", 1, PDFUtil.lightGray, 10, false, 2, Element.ALIGN_LEFT));


        double singlePrice = 0.0;
        double netAmount = 0.0;

        for(int i=0;i<1;i++) {
//            double serviceAmt = booking.getPrice() * configuration().serviceFeePercent();
            double serviceAmt = booking.getPrice() * 2;
            singlePrice = booking.getPrice();

            netAmount = singlePrice - serviceAmt;

            String index = (i < 9) ? "0" + (i + 1) : String.valueOf(i + 1);
            if ((i + 1) % 2 != 0) {
                remittanceDetailsTable.addCell(PDFUtil.createCell(index, 1, 7, false, 1, Element.ALIGN_CENTER, 0, 10, 10));
                remittanceDetailsTable.addCell(PDFUtil.createCell("Droppa delivery ref:" + booking.getTrackNo(), 1, 7, false, 1, Element.ALIGN_CENTER, 0, 10, 10));
                remittanceDetailsTable.addCell(PDFUtil.createCell(booking.getPaymentType().description(), 1, 7, false, 1, Element.ALIGN_CENTER, 0, 10, 10));
                remittanceDetailsTable.addCell(PDFUtil.createCell("R"+money.format(singlePrice), 1, 7, false, 2, Element.ALIGN_CENTER, 0, 10, 10));
                remittanceDetailsTable.addCell(PDFUtil.createCell("R"+money.format(serviceAmt), 1, 7, false, 2, Element.ALIGN_LEFT, 0, 10, 10));
                remittanceDetailsTable.addCell(PDFUtil.createCell("R" + money.format(netAmount), 1, 7, false, 2, Element.ALIGN_LEFT, 0, 10, 10));
            }
            else {
                remittanceDetailsTable.addCell(PDFUtil.createCell(index, 1, 7, false, 1, Element.ALIGN_CENTER, 0, 10, 10)).setBackgroundColor(PDFUtil.lighterGray);
                remittanceDetailsTable.addCell(PDFUtil.createCell("Droppa delivery ref:" + booking.getTrackNo(), 1, 7, false, 1, Element.ALIGN_CENTER, 0, 10, 10)).setBackgroundColor(PDFUtil.lighterGray);
                remittanceDetailsTable.addCell(PDFUtil.createCell(booking.getPaymentType().description(), 1, 7, false, 1, Element.ALIGN_CENTER, 0, 10, 10)).setBackgroundColor(PDFUtil.lighterGray);
                remittanceDetailsTable.addCell(PDFUtil.createCell("R"+money.format(singlePrice), 1, 7, false, 2, Element.ALIGN_CENTER, 0, 10, 10)).setBackgroundColor(PDFUtil.lighterGray);
                remittanceDetailsTable.addCell(PDFUtil.createCell("R"+money.format(serviceAmt), 1, 7, false, 2, Element.ALIGN_LEFT, 0, 10, 10)).setBackgroundColor(PDFUtil.lighterGray);
                remittanceDetailsTable.addCell(PDFUtil.createCell("R" + money.format(netAmount), 1, 7, false, 2, Element.ALIGN_LEFT, 0, 10, 10)).setBackgroundColor(PDFUtil.lighterGray);
            }
        }

        PDFUtil.addEmptyCells(remittanceDetailsTable,4);
        remittanceDetailsTable.addCell(PDFUtil.createCell("Sub Total",1, 9, false, 1, Element.ALIGN_LEFT,-1,8,8));
        PdfPCell subTotal = PDFUtil.createCell("R" + money.format(netAmount),1, 9, false, 3, Element.ALIGN_LEFT,-1,8,8);
        subTotal.setBackgroundColor(PDFUtil.lighterGray);
        remittanceDetailsTable.addCell(subTotal);


        PDFUtil.addEmptyCells(remittanceDetailsTable,4);
        remittanceDetailsTable.addCell(PDFUtil.createCell("Due To You",1, 9, false, 3, Element.ALIGN_LEFT,-1,8,8));
        PdfPCell totalCell= PDFUtil.createCell("R" + money.format(netAmount),1, 12, false, 3, Element.ALIGN_LEFT,-1,8,8);
        totalCell.setBackgroundColor(PDFUtil.lightBlue);
        remittanceDetailsTable.addCell(totalCell);

        return remittanceDetailsTable;
    }
}
