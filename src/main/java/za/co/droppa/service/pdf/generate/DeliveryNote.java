package za.co.droppa.service.pdf.generate;

import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import za.co.droppa.dto.BillDTO;
import za.co.droppa.util.FontUtil;
import za.co.droppa.util.PDFUtil;

import java.io.ByteArrayInputStream;
import java.text.DecimalFormat;
import java.util.Map;

/**
 * @author Vhuthuhawe
 * @date 2024/08/03
 */
public class DeliveryNote extends AbstractPdfDocument {

    public DeliveryNote(String base64String, String name, String heading, String waybill) throws DocumentException{
        super(base64String,name,heading,waybill);
    }

    /**
     * Builds a PDF document for a delivery note based on the provided parameters.
     *
     * @param params a Map containing key-value pairs, including a "billDTO" object to be used in the document.
     * @return a ByteArrayInputStream representing the generated PDF document.
     *
     */
    @Override
    public ByteArrayInputStream buildDocument(Map<String, Object> params) {
        BillDTO billDTO = (BillDTO) params.get("billDTO");

        try {
            open();

            document.add(PDFUtil.blankLine(8));

            document.add(clientInformationTableDNote(billDTO));
            document.add(PDFUtil.blankLine(-15));

            document.add(deliveryNoteDetails(billDTO));
            document.add(PDFUtil.blankLine(0));


            close();
        } catch (DocumentException e) {
            System.out.printf("unable to generate delivery note, {%s}%n", e.getMessage());
        }

        return new ByteArrayInputStream(out.toByteArray());
    }


    /**
     * Creates a PDF table containing client information for a delivery note.
     *
     * @param billDTO an instance of BillDTO containing the client's details.
     * @return a PdfPTable object representing the client information section of the delivery note.
     */
    private PdfPTable clientInformationTableDNote(BillDTO billDTO) {
        PdfPTable clientInformationTable = new PdfPTable(3);


        PDFUtil.setColumnWidths(clientInformationTable, new float[]{4f, 3f, 4f});


        clientInformationTable.setWidthPercentage(100);

        clientInformationTable.addCell(PDFUtil.createCell("Client Information", 3, PDFUtil.lightGray, 10, false, 2, Element.ALIGN_LEFT)).setPaddingLeft(10);


        PdfPTable leftClientInformationTable = new PdfPTable(2);
        leftClientInformationTable.addCell(PDFUtil.createCell("Full Names:", FontUtil.small, false, 1));
        leftClientInformationTable.addCell(PDFUtil.createCell(billDTO.getRecipientName(), FontUtil.small, false, 1));


        leftClientInformationTable.addCell(PDFUtil.createCell("Email:", FontUtil.small, false, 1));
        leftClientInformationTable.addCell(PDFUtil.createCell(billDTO.getRecipientEmail(), FontUtil.small, false, 1));

        leftClientInformationTable.addCell(PDFUtil.createCell("REF:", FontUtil.small, false, 1));
        leftClientInformationTable.addCell(PDFUtil.createCell(billDTO.getReference(), FontUtil.small, false, 1));

        leftClientInformationTable.addCell(PDFUtil.createCell("Tel:", FontUtil.small, false, 1));
        leftClientInformationTable.addCell(PDFUtil.createCell(billDTO.getRecipientPhone(), FontUtil.small, false, 1));


        clientInformationTable.addCell(PDFUtil.toPDFTableCell(leftClientInformationTable, 1));


        PdfPTable emptyMiddleTable = new PdfPTable(1);
        PDFUtil.addEmptyCells(emptyMiddleTable,4);
        clientInformationTable.addCell(PDFUtil.toPDFTableCell(emptyMiddleTable,1));

        PdfPTable rightClientInformationTable = new PdfPTable(2);
        rightClientInformationTable.addCell(PDFUtil.createCell("Issue Date:", FontUtil.small, false, 1));
        rightClientInformationTable.addCell(PDFUtil.createCell(billDTO.getGeneratedDate(), FontUtil.small, false, 1));


        clientInformationTable.addCell(PDFUtil.toPDFTableCell(rightClientInformationTable, 1));

        return clientInformationTable;
    }


    /**
     * Creates a PDF table containing delivery note details based on the provided invoice.
     *
     * @param companyInvoice an instance of BillDTO containing the invoice details to be included in the delivery note.
     * @return a PdfPTable object representing the delivery note details section.
     */
    private PdfPTable deliveryNoteDetails(BillDTO companyInvoice) {
        PdfPTable deliveryInformationTable = new PdfPTable(3);

        PDFUtil.setColumnWidths(deliveryInformationTable,new float[]{1f, 6f,2f});

        deliveryInformationTable.setWidthPercentage(100);

        deliveryInformationTable.addCell(PDFUtil.createCell("ID", 1, PDFUtil.lightGray, 10, false, 2, Element.ALIGN_CENTER)).setPaddingLeft(10);
        deliveryInformationTable.addCell(PDFUtil.createCell("DESCRIPTION", 1, PDFUtil.lightGray, 10, false, 2, Element.ALIGN_LEFT)).setPaddingLeft(10);
        deliveryInformationTable.addCell(PDFUtil.createCell("TOTAL", 1, PDFUtil.lightGray, 10, false, 2, Element.ALIGN_CENTER)).setPaddingLeft(10);


        double singlePrice = 0.0;
        double totalPrice = 0.0;
        DecimalFormat df = new DecimalFormat("####0.00");


        // loop through the invoice details and print on PDF
        for (int x = 0; x < companyInvoice.getListOfInvoice().size(); x++) {

            int quantity = companyInvoice.getListOfInvoice().get(x).getQuantity();

            String index = (x < 9) ? "0" + (x + 1) : String.valueOf(x + 1);

//            singlePrice = (Double.parseDouble(companyInvoice.getListOfInvoice().get(x).getPrice()) * quantity) / configuration().reverseVat();
            singlePrice = (Double.parseDouble(companyInvoice.getListOfInvoice().get(x).getPrice()) * quantity) / 2;

            if ((x + 1) % 2 != 0) {

                deliveryInformationTable.addCell(PDFUtil.createCell(index, 1, 7, false, 1, Element.ALIGN_CENTER, 0, 10, 10));
                deliveryInformationTable.addCell(PDFUtil.createCell("Droppa delivery reference : " + companyInvoice.getListOfInvoice().get(x).getDescription(), 1, 7, false, 1, Element.ALIGN_CENTER, 0, 10, 10));
                deliveryInformationTable.addCell(PDFUtil.createCell("R" + money.format(singlePrice), 1, 7, false, 2, Element.ALIGN_LEFT, 0, 10, 10));
            } else {

                deliveryInformationTable.addCell(PDFUtil.createCell(index, 1, 7, false, 1, Element.ALIGN_CENTER, 0, 10, 10)).setBackgroundColor(PDFUtil.lighterGray);
                deliveryInformationTable.addCell(PDFUtil.createCell("Droppa delivery reference : " + companyInvoice.getListOfInvoice().get(x).getDescription(), 1, 7, false, 1, Element.ALIGN_CENTER, 0, 10, 10)).setBackgroundColor(PDFUtil.lighterGray);
                deliveryInformationTable.addCell(PDFUtil.createCell("R" + money.format(singlePrice), 1, 7, false, 2, Element.ALIGN_LEFT, 0, 10, 10)).setBackgroundColor(PDFUtil.lighterGray);
            }

//            singlePrice = (Double.parseDouble(companyInvoice.getListOfInvoice().get(x).getPrice()) * quantity) / configuration().reverseVat();
            singlePrice = (Double.parseDouble(companyInvoice.getListOfInvoice().get(x).getPrice()) * quantity) / 2;


            totalPrice += singlePrice;

        }

        double overralTotal = totalPrice;
//        double vat = overralTotal * configuration().vatPercent();
        double vat = overralTotal * 2;

        overralTotal = overralTotal + vat;



        deliveryInformationTable.addCell(PDFUtil.createCell(" "));
        deliveryInformationTable.addCell(PDFUtil.createCell("Sub Total",1, 9, false, 1, Element.ALIGN_RIGHT,-1,8,8));
        PdfPCell subTotal = PDFUtil.createCell("R" + df.format(totalPrice),1, 9, false, 3, Element.ALIGN_CENTER,-1,8,8);
        subTotal.setBackgroundColor(PDFUtil.lightGray);
        deliveryInformationTable.addCell(subTotal);


        deliveryInformationTable.addCell(PDFUtil.createCell(" "));
        deliveryInformationTable.addCell(PDFUtil.createCell("VAT 15%",1, 9, false, 1, Element.ALIGN_RIGHT,-1,8,8));
        deliveryInformationTable.addCell(PDFUtil.createCell("R" + df.format(vat),1, 9, false, 3, Element.ALIGN_CENTER,-1,8,8));

        deliveryInformationTable.addCell(PDFUtil.createCell(" "));
        deliveryInformationTable.addCell(PDFUtil.createCell("Total",1, 9, false, 3, Element.ALIGN_RIGHT,-1,10,10));
        PdfPCell totalCell= PDFUtil.createCell("R" + df.format(overralTotal),1, 12, false, 3, Element.ALIGN_CENTER,-1,10,10);
        totalCell.setBackgroundColor(PDFUtil.lightBlue);
        deliveryInformationTable.addCell(totalCell);

        return deliveryInformationTable;
    }
}
