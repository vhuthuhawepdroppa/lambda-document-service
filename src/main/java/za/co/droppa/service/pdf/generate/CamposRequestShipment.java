package za.co.droppa.service.pdf.generate;

import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import za.co.droppa.dto.campos.CamposRateResponse;
import za.co.droppa.model.Booking;
import za.co.droppa.model.ParcelDimension;
import za.co.droppa.model.Person;
import za.co.droppa.util.FontUtil;
import za.co.droppa.util.PDFTableUtil;
import za.co.droppa.util.PDFUtil;
import za.co.droppa.util.ParcelsCalculator;

import java.io.ByteArrayInputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * @author Linda J Sibeko
 * @date 2024/08/27
 */
public class CamposRequestShipment extends AbstractPdfDocument
{

    public CamposRequestShipment(String base64String, String name, String heading, String waybill) throws DocumentException {
          super(base64String,name,heading,waybill);
    }

    @Override
    public ByteArrayInputStream buildDocument(Map<String, Object> params) throws DocumentException {
        Booking booking = (Booking) params.get("booking");
        Person person = (Person) params.get("person");
        CamposRateResponse rateResponse = (CamposRateResponse) params.get("rate-response");

        try {
            document.setMargins(document.leftMargin(), document.rightMargin(), document.topMargin(), document.bottomMargin() + 120);
            open();

            document.add(PDFUtil.blankLine(20));
            System.out.println("opening file");
            document.add(senderInformation(person));
            document.add(bookingsDetails(booking));
            document.add(requestedObject(booking,rateResponse));
            if(booking.getVehicleType().equals("SMALL")){
                document.add(parcelsDims(booking.getParcelDimensions()));
            }


            float availableHeight = document.getPageSize().getHeight() - document.topMargin() - document.bottomMargin();
            PdfPTable table = pricingDetails(rateResponse);
            if (table.getTotalHeight() > availableHeight - 120) {
                document.newPage();
            }
            document.add(table);


            System.out.println("closing file");
            close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new ByteArrayInputStream(out.toByteArray());
    }

    private PdfPTable parcelsDims(List<ParcelDimension> parcelDimensions) {
        PdfPTable table = new PdfPTable(6);
        table.setWidthPercentage(100);
        PDFTableUtil.addHeaderCell(table,"No", FontUtil.poppinsBySize(8,false,2),PDFUtil.lightGray,PDFUtil.lightGray);
        PDFTableUtil.addHeaderCell(table,"Item", FontUtil.poppinsBySize(8,false,2),PDFUtil.lightGray,PDFUtil.lightGray);
        PDFTableUtil.addHeaderCell(table,"Description",FontUtil.poppinsBySize(8,false,2),PDFUtil.lightGray,PDFUtil.lightGray,2);
        PDFTableUtil.addHeaderCell(table,"Total volume (m^3)",FontUtil.poppinsBySize(8,false,2),PDFUtil.lightGray,PDFUtil.lightGray);
        PDFTableUtil.addHeaderCell(table,"Quantity",FontUtil.poppinsBySize(8,false,2),PDFUtil.lightGray,PDFUtil.lightGray);


        Map<String, ParcelDimension> uniqueDimensions = new HashMap<>();

        for (ParcelDimension parcelDimension : parcelDimensions) {

            String key = parcelDimension.getItem() + "|" + parcelDimension.getDescription() + "|"+parcelDimension.getVolume();

            if (uniqueDimensions.containsKey(key)) {
                ParcelDimension existingDimension = uniqueDimensions.get(key);
                existingDimension.setQuantity(existingDimension.getQuantity() + parcelDimension.getQuantity());
            } else {
                // Put new entry in the map if the key does not exist
                uniqueDimensions.put(key, parcelDimension);
            }

        }

        int  index = 1;
        for (Map.Entry<String, ParcelDimension> entry : uniqueDimensions.entrySet()) {
            ParcelDimension parcelDimension = entry.getValue();

            PDFTableUtil.addDataCell(table, String.valueOf(index),  FontUtil.poppinsBySize(7,false,1), PDFUtil.lightGray);
            PDFTableUtil.addDataCell(table,parcelDimension.getItem(),FontUtil.poppinsBySize(7,false,1), PDFUtil.lightGray);
            PDFTableUtil.addDataCell(table,parcelDimension.getDescription(),FontUtil.poppinsBySize(7,false,1), PDFUtil.lightGray,2);
            PDFTableUtil.addDataCell(table,String.valueOf(ParcelsCalculator.calculateVolume(parcelDimension)),FontUtil.poppinsBySize(7,false,1), PDFUtil.lightGray);
            PDFTableUtil.addDataCell(table,String.valueOf(parcelDimension.getQuantity()),FontUtil.poppinsBySize(7,false,1), PDFUtil.lightGray);

        }

        return table;
    }

    private PdfPTable requestedObject(Booking booking,CamposRateResponse response) {
        System.out.println("Campos Response  "+response.toString());
        PdfPTable table = new PdfPTable(5);
        table.setWidthPercentage(100);

        table.addCell(PDFUtil.createCell("Shipment Information", 5, PDFUtil.lightGray, 10, false, 2, Element.ALIGN_LEFT));
        PDFTableUtil.addDetailsCell(table,"Collection", "YES",4);
        PDFTableUtil.addDetailsCell(table,"Small or Full load", (booking.getVehicleType().equals("SMALL") && response.getRecommendVehicle() == null  ? "SMALL" : "FULL LOADS"),4);
        PDFTableUtil.addDetailsCell(table,"Type",booking.getVehicleType().equalsIgnoreCase("SMALL") && response.getRecommendVehicle() != null ?
                response.getRecommendVehicle() : booking.getVehicleType(),4);
        PDFTableUtil.addDetailsCell(table,"Pick Up Date",booking.getPickUpDate(),4);
        PDFTableUtil.addDetailsCell(table,"Pick Up Time",booking.getPickUpTime(),4);

      /*  if(booking.getVehicleType().equals("SMALL"){

        }*/


        return table;
    }

    private PdfPTable senderInformation(Person person) {
        PdfPTable table = new PdfPTable(4);
        table.setWidthPercentage(100);
        table.setSpacingBefore(20);


       table.addCell(PDFUtil.createCell("Client Information", 4, PDFUtil.lightGray, 10, false, 2, Element.ALIGN_LEFT));
        PDFTableUtil.addDetailsCell(table,"Name", person.getFirstName(),3);
        PDFTableUtil.addDetailsCell(table,"Email", person.getEmail(),3);
        PDFTableUtil.addDetailsCell(table,"Mobile",person.getMobile(),3);


        return table;
    }

    private PdfPTable bookingsDetails(Booking booking) {
        PdfPTable table = new PdfPTable(4);
        table.setWidthPercentage(100);
        table.addCell(PDFUtil.createCell("Pick Up Details", 2, PDFUtil.lightGray, 10, false, 2, Element.ALIGN_LEFT));
        table.addCell(PDFUtil.createCell("Drop Off Details", 2, PDFUtil.lightGray, 10, false, 2, Element.ALIGN_LEFT));
        PDFTableUtil.addDetailsCell(table,"Street Address",booking.getPickUpAddress());
        PDFTableUtil.addDetailsCell(table,"Street Address",booking.getDropOffAddress());
        PDFTableUtil.addDetailsCell(table,"Province",booking.getProvince().name());
        PDFTableUtil.addDetailsCell(table,"Province",booking.getDestinationProvince().name());

        return table;
    }

    private PdfPTable pricingDetails(CamposRateResponse rateResponse) {
        PdfPTable table = new PdfPTable(4);
        table.setSpacingBefore(20);
        table.setWidthPercentage(100);
        PdfPCell blankCell = new PdfPCell(new Phrase(""));
        blankCell.setColspan(4);
        blankCell.setBorder(0);

        table.addCell(PDFUtil.createCell("Rates", 4, PDFUtil.lightGray, 10, false, 2, Element.ALIGN_LEFT));
        PDFTableUtil.addSubTotalsCell(table,"Transport Rates",rateResponse.getRateAmount(),PDFUtil.lightGray,PDFUtil.lighterGray,2);
        PDFTableUtil.addSubTotalsCell(table,"Collection",rateResponse.getCollectionAmount(),PDFUtil.lightGray,PDFUtil.lighterGray,2);
        PDFTableUtil.addSubTotalsCell(table,"Labour",0D,PDFUtil.lightGray,PDFUtil.lighterGray,2);
        PDFTableUtil.addSubTotalsCell(table,"Fuel Levy",rateResponse.getFuel_levey(),PDFUtil.lightGray,PDFUtil.lighterGray,2);
        PDFTableUtil.addSubTotalsCell(table,"Other",0D,PDFUtil.lightGray,PDFUtil.lighterGray,2);
        table.addCell(blankCell);
        PDFTableUtil.addSubTotalsCell(table,"Total Excluding VAT",rateResponse.price_excluding_vat,PDFUtil.lightGray,PDFUtil.lighterGray,2);
        PDFTableUtil.addSubTotalsCell(table,"VAT",rateResponse.getVat(),PDFUtil.lightGray,PDFUtil.lighterGray,2);
        table.addCell(blankCell);
        PDFTableUtil.addTotalCell(table,"Total Including VAT",rateResponse.getTotal_price(),PDFUtil.lightGray,PDFUtil.lighterGray,2);

        return table;
    }
}
