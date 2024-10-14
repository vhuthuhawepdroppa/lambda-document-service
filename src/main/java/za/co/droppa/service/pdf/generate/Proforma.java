package za.co.droppa.service.pdf.generate;

import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import za.co.droppa.model.Booking;
import za.co.droppa.model.Contact;
import za.co.droppa.model.ParcelDimension;
import za.co.droppa.util.FontUtil;
import za.co.droppa.util.PDFTableUtil;
import za.co.droppa.util.PDFUtil;

import java.io.ByteArrayInputStream;
import java.util.Map;


/**
 * @author Linda J Sibeko
 * @date 2024/09/16
 */

public class Proforma extends AbstractPdfDocument{


    public Proforma(String base64String, String name, String heading, String waybill) throws DocumentException {
        super(base64String, name, heading, waybill);
    }

    @Override
    public ByteArrayInputStream buildDocument(Map<String, Object> params) throws DocumentException {
        Booking booking = (Booking) params.get("booking");
        Contact drop_off_contact = (Contact) params.get("drop_off_contact");
        Contact pickup_contact = (Contact) params.get("pickup_contact");

        document.add(shipperDetails(drop_off_contact,pickup_contact,booking));


        return null;
    }

    private PdfPTable shipperDetails(Contact drop_off_contact,Contact pickup_contact,Booking booking) throws DocumentException  {
     PdfPTable table = new PdfPTable(2);
        table.setWidthPercentage(100f);
        table.setSpacingBefore(40.f);

        table.addCell(PDFUtil.createCell("Shipper/Consignee Details", 2, PDFUtil.lightGray, 10, false, 2, Element.ALIGN_LEFT));
        PDFTableUtil.addHeaderCell(table,"Sender", FontUtil.poppinsBySize(8,false,2), PDFUtil.lightGray,PDFUtil.lightGray);
        PDFTableUtil.addHeaderCell(table,"Receiver", FontUtil.poppinsBySize(8,false,2), PDFUtil.lightGray,PDFUtil.lightGray);
        PDFTableUtil.addDataCell(table,String.format("Company:%s",pickup_contact.getCompanyName()),FontUtil.poppinsBySize(7,false,1), PDFUtil.lightGray);
        PDFTableUtil.addDataCell(table,String.format("Company:%s",drop_off_contact.getCompanyName()),FontUtil.poppinsBySize(7,false,1), PDFUtil.lightGray);
        PDFTableUtil.addDataCell(table,String.format("Address:%s",""),FontUtil.poppinsBySize(7,false,1), PDFUtil.lightGray);
        PDFTableUtil.addDataCell(table,String.format("Address:%s",""),FontUtil.poppinsBySize(7,false,1), PDFUtil.lightGray);
        PDFTableUtil.addDataCell(table,String.format("Name:%s",pickup_contact.getFirstName()),FontUtil.poppinsBySize(7,false,1), PDFUtil.lightGray);
        PDFTableUtil.addDataCell(table,String.format("Name:%s",drop_off_contact.getFirstName()),FontUtil.poppinsBySize(7,false,1), PDFUtil.lightGray);
        PDFTableUtil.addDataCell(table,String.format("Mobile:%s",pickup_contact.getPhone()),FontUtil.poppinsBySize(7,false,1), PDFUtil.lightGray);
        PDFTableUtil.addDataCell(table,String.format("Mobile:%s",drop_off_contact.getPhone()),FontUtil.poppinsBySize(7,false,1), PDFUtil.lightGray);
        PDFTableUtil.addDataCell(table,String.format("Export Code:%s",booking.getMetaData().getExportCode()),FontUtil.poppinsBySize(7,false,1), PDFUtil.lightGray);
        PDFTableUtil.addDataCell(table,String.format("County:%s",booking.getMetaData().getDropOffCountry()),FontUtil.poppinsBySize(7,false,1), PDFUtil.lightGray);


     return table;
    }

    private PdfPTable commodityItems(Booking booking)
    {
        PdfPTable table = new PdfPTable(4);
        table.addCell(PDFUtil.createCell("Commodity Items", 4, PDFUtil.lightGray, 10, false, 2, Element.ALIGN_LEFT));

        int total_qty = 0;
        double total_price = 0;
        for (ParcelDimension parcelDimension : booking.getParcelDimensions())
        {
            PDFTableUtil.addDataCell(table,parcelDimension.getItem(),FontUtil.poppinsBySize(7,false,1), PDFUtil.lightGray);
            PDFTableUtil.addDataCell(table,parcelDimension.getHs_code(),FontUtil.poppinsBySize(7,false,1), PDFUtil.lightGray);
            PDFTableUtil.addDataCell(table, parcelDimension.getParcel_number(), FontUtil.poppinsBySize(7,false,1), PDFUtil.lightGray);
            PDFTableUtil.addDataCell(table,parcelDimension.getItem(),FontUtil.poppinsBySize(7,false,1), PDFUtil.lightGray);
            PDFTableUtil.addDataCell(table,parcelDimension.getPrice(),FontUtil.poppinsBySize(7,false,1), PDFUtil.lightGray);


        }

        PdfPCell blankCell = new PdfPCell(new Phrase(""));
        blankCell.setColspan(1);
        blankCell.setBorder(0);

        table.addCell(blankCell);
        PDFTableUtil.addDataCell(table,"Total",FontUtil.poppinsBySize(7,true,1), PDFUtil.lightGray);
        PDFTableUtil.addDataCell(table,""+total_qty,FontUtil.poppinsBySize(7,true,1), PDFUtil.lightGray);
        PDFTableUtil.addDataCell(table,""+total_price,FontUtil.poppinsBySize(7,true,1), PDFUtil.lightGray);



        return table;
    }
}
