package za.co.droppa.service.pdf.generate;

import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import za.co.droppa.model.*;
import za.co.droppa.service.pdf.renderers.SignatureLineCellRenderer;
import za.co.droppa.util.DateUtil;
import za.co.droppa.util.FontUtil;
import za.co.droppa.util.PDFUtil;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author Vhuthuhawe
 * @date 2024/08/03
 */
public class Waybill extends AbstractPdfDocument {
    public Waybill(String base64String, String name, String heading, String waybill) throws DocumentException {
        super(base64String,name,heading,waybill);
    }


    /**
     * Generates a PDF document for a waybill based on the provided parameters.
     *
     * @param params A map containing:
     *               - "booking" (Booking): Booking details.
     *               - "parcels" (Object): Parcel details, if any.
     *               - "contactPickUp" (Contact): Pickup contact information.
     *               - "contactDropOff" (Contact): Drop-off contact information.
     *               - "person" (Person): Person associated with the booking.
     *               - "barcode" (Boolean): Whether to include a barcode.
     *               - "waybillNo" (String): Waybill number.
     * @return A ByteArrayInputStream of the generated PDF document.
     */
    @Override
    public ByteArrayInputStream buildDocument(Map<String,Object> params) {

        Booking booking = (Booking) params.get("booking");
        Object parcels = params.get("parcels");
        Contact contactPickUp = (Contact) params.get("contactPickUp");
        Contact contactDropOff = (Contact) params.get("contactDropOff");
        Person person = (Person) params.get("person");

        try {

            document.setMargins(document.leftMargin(), document.rightMargin(), document.topMargin(), document.bottomMargin() + 120);

            open();

            BookingParcel bookingParcel = parcels != null ? (BookingParcel) parcels : null;

            document.add(PDFUtil.blankLine(8));


            document.add(clientInformationTable(booking, person, bookingParcel));

            document.add(PDFUtil.blankLine(-30));

            document.add(deliveryDetails(booking, contactPickUp, contactDropOff, bookingParcel));
            document.add(PDFUtil.blankLine(0));


            if (bookingParcel != null) {
                document.add(parcelsTable(booking, bookingParcel));
            } else
                document.add(parcelsTable(booking));


            document.add(PDFUtil.blankLine(0));

            document.add(conditionOfGoods());
            document.add(PDFUtil.blankLine(0));

            document.add(signaturesTable(contactPickUp, contactDropOff));
            document.add(PDFUtil.blankLine(0));

            document.add(PDFUtil.createParagraph(String.format("Special Instruction: %s", booking.getComment() != null ? booking.getComment() : ""),
                    Element.ALIGN_CENTER, 7, false, 2));

            document.add(PDFUtil.blankLine(0));

            document.add(PDFUtil.createParagraph("Disclaimer: Unless indicated on the face hereof, " +
                    "Droppa reserves the right to dispatch and charge at the published Domestic " +
                    "Budget Cargo tariff", Element.ALIGN_CENTER, 7, false, 2));


            close();

        } catch (DocumentException e) {
            System.out.println(String.format("unable to generate waybill, {%s}", e.getMessage()));
        }


        return new ByteArrayInputStream(out.toByteArray());

    }

    /**
     * Creates a PDF table displaying client information.
     *
     * @param booking The booking details.
     * @param person The person associated with the booking.
     * @param parcel The parcel details, if available.
     * @return A PdfPTable containing the client information.
     */
    private PdfPTable clientInformationTable(Booking booking, Person person, BookingParcel parcel) {
        PdfPTable clientInformationTable = new PdfPTable(3);
        clientInformationTable.setWidthPercentage(100);

        clientInformationTable.addCell(PDFUtil.createCell("Client Information", 3, PDFUtil.lightGray, 10, false, 2,Element.ALIGN_LEFT)).setPaddingLeft(10);


        PdfPTable leftClientInformationTable = new PdfPTable(2);
        PDFUtil.setColumnWidths(leftClientInformationTable,new float[]{1.5f,2f});
        leftClientInformationTable.addCell(PDFUtil.createCell("Full Names:", FontUtil.small, false, 1));
        leftClientInformationTable.addCell(PDFUtil.createCell(person.getFirstName()+" "+person.getSurname(), FontUtil.small, false, 1));


        leftClientInformationTable.addCell(PDFUtil.createCell("Email:", FontUtil.small, false, 1));
        leftClientInformationTable.addCell(PDFUtil.createCell(person.getEmail(), FontUtil.small, false, 1));

        leftClientInformationTable.addCell(PDFUtil.createCell("Phone No:", FontUtil.small, false, 1));
        leftClientInformationTable.addCell(PDFUtil.createCell(person.getMobile(), FontUtil.small, false, 1));


        clientInformationTable.addCell(PDFUtil.toPDFTableCell(leftClientInformationTable, 1));


        PdfPTable emptyMiddleTable = new PdfPTable(1);
        PDFUtil.addEmptyCells(emptyMiddleTable,4);

        clientInformationTable.addCell(PDFUtil.toPDFTableCell(emptyMiddleTable,1));

        PdfPTable rightClientInformationTable = new PdfPTable(2);
        PDFUtil.setColumnWidths(rightClientInformationTable,new float[]{1.5f,2f});
        WaybillResponse waybillResponse = booking.getWaybillResponse();
        rightClientInformationTable.addCell(PDFUtil.createCell("Waybill No:", FontUtil.small, false, 1));
        rightClientInformationTable.addCell(PDFUtil.createCell(waybillResponse!=null?waybillResponse.getWaybillNumber():"NO_WAYBILL", FontUtil.small, false, 1));

        rightClientInformationTable.addCell(PDFUtil.createCell("Droppa Ref:", FontUtil.small, false, 1));
        rightClientInformationTable.addCell(PDFUtil.createCell(booking.getTrackNo(), FontUtil.small, false, 1));



        String customerRef = parcel!=null?parcel.getCustomer_reference(): booking.getCustomerReference();
        String costCenterNum = parcel!=null? parcel.getCostCenter():booking.getCostCenter();


        rightClientInformationTable.addCell(PDFUtil.createCell("Customer Ref:", FontUtil.small, false, 1));
        rightClientInformationTable.addCell(PDFUtil.createCell(customerRef, FontUtil.small, false, 1));

        rightClientInformationTable.addCell(PDFUtil.createCell("Cost Center No:", FontUtil.small, false, 1));
        rightClientInformationTable.addCell(PDFUtil.createCell(costCenterNum, FontUtil.small, false, 1));


        clientInformationTable.addCell(PDFUtil.toPDFTableCell(rightClientInformationTable, 1));

        return clientInformationTable;
    }

    /**
     * Creates a PDF table displaying parcel information.
     *
     * @param booking The booking details, including parcel dimensions.
     * @return A PdfPTable containing the parcel information.
     */
    private PdfPTable parcelsTable(Booking booking) {
        PdfPTable parcelsTable = new PdfPTable(4);
        parcelsTable.setWidthPercentage(100);

        parcelsTable.addCell(PDFUtil.createCell("No. Of Parcels", 1, 8, false, 2, Element.ALIGN_CENTER, 7, 8, 10));


        parcelsTable.addCell(PDFUtil.createCell("Description of Contents", 1, 8, false, 2, Element.ALIGN_CENTER, 7, 8, 10));

        PdfPTable dimensionsTable = new PdfPTable(3);
        dimensionsTable.addCell(PDFUtil.createCell("Dimensions (centimetres)", 3, 7, false, 2, Element.ALIGN_CENTER, -1, 0, 5));
        dimensionsTable.addCell(PDFUtil.createCell("Width", 1, 7, false, 2, Element.ALIGN_LEFT, 8, 5, 5));
        dimensionsTable.addCell(PDFUtil.createCell("Length", 1, 7, false, 2, Element.ALIGN_LEFT, 8, 5, 5));
        dimensionsTable.addCell(PDFUtil.createCell("Height", 1, 7, false, 2, Element.ALIGN_LEFT, 9, 5, 5));

        parcelsTable.addCell(PDFUtil.toPDFTableCell(dimensionsTable, 1, 0));

        parcelsTable.addCell(PDFUtil.createCell("Mass(kg/parcel)", 1, 8, false, 2, Element.ALIGN_CENTER, 5, 8, 10));

        List<ParcelDimension> dimensions = booking.getParcelDimensions();



        if(dimensions!=null) {

            if (dimensions.size() < 5) {
                for (int i = 0; i < dimensions.size(); i++) {
                    ParcelDimension currentDimension = dimensions.get(i);
                    String index = (i < 9) ? "0" + (i + 1) : String.valueOf(i + 1);

                    parcelsTable.addCell(PDFUtil.createCell(index, 1, 7, false, 1, Element.ALIGN_CENTER, 0, 8, 10));
                    parcelsTable.addCell(PDFUtil.createCell("Parcel No. " + index, 1, 7, false, 1, Element.ALIGN_CENTER, 0, 8, 10));

                    PdfPTable dimensionsTable2 = new PdfPTable(3);

                    dimensionsTable2.addCell(PDFUtil.createCell(String.format("%.1f", currentDimension.getParcel_breadth()), 1, 7, false, 1, Element.ALIGN_CENTER, 10, 4, 6));
                    dimensionsTable2.addCell(PDFUtil.createCell(String.format("%.1f", currentDimension.getParcel_length()), 1, 7, false, 1, Element.ALIGN_CENTER, 10, 4, 6));
                    dimensionsTable2.addCell(PDFUtil.createCell(String.format("%.1f", currentDimension.getParcel_height()), 1, 7, false, 1, Element.ALIGN_CENTER, -1, 4, 6));
                    parcelsTable.addCell(PDFUtil.toPDFTableCell(dimensionsTable2, 1, 0));


                    parcelsTable.addCell(PDFUtil.createCell(String.format("%.1f", currentDimension.getParcel_mass()), 1, 7, false, 1, Element.ALIGN_CENTER, 0, 8, 10));
                }
            } else {
                List<Pair<Integer, ParcelDimension>> parcelDimensionsPairs = new ArrayList<>();

                for (ParcelDimension currentParcel : dimensions) {
                    if (!parcelExists(parcelDimensionsPairs, currentParcel))
                        parcelDimensionsPairs.add(new Pair<>(1, currentParcel));
                }

                for (int i = 0; i < parcelDimensionsPairs.size(); i++) {
                    Pair<Integer, ParcelDimension> currentDimensionPair = parcelDimensionsPairs.get(i);
                    ParcelDimension currentDimension = currentDimensionPair.getValue();
                    String index = (i < 9) ? "0" + (i + 1) : String.valueOf(i + 1);

                    parcelsTable.addCell(PDFUtil.createCell(currentDimensionPair.getKey() + "", 1, 7, false, 1, Element.ALIGN_CENTER, 0, 8, 10));
                    parcelsTable.addCell(PDFUtil.createCell("Parcel No. " + index, 1, 7, false, 1, Element.ALIGN_CENTER, 0, 8, 10));

                    PdfPTable dimensionsTable2 = new PdfPTable(3);

                    dimensionsTable2.addCell(PDFUtil.createCell(String.format("%.1f", currentDimension.getParcel_breadth()), 1, 7, false, 1, Element.ALIGN_CENTER, 10, 4, 6));
                    dimensionsTable2.addCell(PDFUtil.createCell(String.format("%.1f", currentDimension.getParcel_length()), 1, 7, false, 1, Element.ALIGN_CENTER, 10, 4, 6));
                    dimensionsTable2.addCell(PDFUtil.createCell(String.format("%.1f", currentDimension.getParcel_height()), 1, 7, false, 1, Element.ALIGN_CENTER, -1, 4, 6));
                    parcelsTable.addCell(PDFUtil.toPDFTableCell(dimensionsTable2, 1, 0));


                    parcelsTable.addCell(PDFUtil.createCell(String.format("%.1f", currentDimension.getParcel_mass()), 1, 7, false, 1, Element.ALIGN_CENTER, 0, 8, 10));
                }

            }
        }
        return parcelsTable;
    }

    /**
     * Checks if a parcel already exists in the list of pairs and increments its count if found.
     *
     * @param pairs A list of pairs where each pair contains a count and a parcel dimension.
     * @param p The parcel dimension to check for existence.
     * @return {@code true} if the parcel exists in the list; {@code false} otherwise.
     */
    private boolean parcelExists(List<Pair<Integer,ParcelDimension>> pairs, ParcelDimension p){
        for (Pair<Integer, ParcelDimension> pair : pairs) {
            if (pair.getValue().equals(p)) {
                pair.setKey(pair.getKey() + 1);
                return true;
            }
        }
        return false;
    }

    /**
     * Creates a table for signatures including fields for sender, Droppa, and receiver/proof of delivery.
     *
     * @param pickup The contact information for the pickup location, used for rendering a signature line.
     * @param dropoff The contact information for the dropoff location, used for rendering a signature line.
     * @return A PdfPTable containing the signature and date/time fields.
     */
    private PdfPTable signaturesTable(Contact pickup, Contact dropoff) {
        PdfPTable signaturesTable = new PdfPTable(3);
        signaturesTable.setKeepTogether(true);
        signaturesTable.setWidthPercentage(100);

        signaturesTable.addCell(PDFUtil.createCell("Sender", 1, 8, false, 2, Element.ALIGN_CENTER,0,8,10));
        signaturesTable.addCell(PDFUtil.createCell("Droppa", 1, 8, false, 2, Element.ALIGN_CENTER,0,8,10));
        signaturesTable.addCell(PDFUtil.createCell("Receiver / Proof of Delivery", 1, 8, false, 2, Element.ALIGN_CENTER,0,8,10));

        signaturesTable.addCell(PDFUtil.createCell("We have seen and agree to the standard condition of carriage of Droppa", 0, 10, 1));
        signaturesTable.addCell(PDFUtil.createCell("Received By", 0, 10, 1));
        signaturesTable.addCell(PDFUtil.createCell("Received in good order and good Condition", 0, 10, 1));


        PdfPCell signatureCell = PDFUtil.createCell("Signature", 0, 10, 1);
        signatureCell.setCellEvent(new SignatureLineCellRenderer(45,10,3,"signature",pickup));
        signaturesTable.addCell(signatureCell);

        signatureCell = PDFUtil.createCell("Signature", 0, 10, 1);
        signatureCell.setCellEvent(new SignatureLineCellRenderer(45,10,3, "",null));
        signaturesTable.addCell(signatureCell);

        signatureCell = PDFUtil.createCell("Signature", 0, 10, 1);
        signatureCell.setCellEvent(new SignatureLineCellRenderer(45,10,3, "signature",dropoff));

        signaturesTable.addCell(signatureCell);


        for(int x=1;x<4;x++) {
            PdfPTable dateTimeTable1 = new PdfPTable(2);


            for (int i = 0; i < dateTimeTable1.getRows().size(); i++) {
                for (PdfPCell cell : dateTimeTable1.getRow(i).getCells()) {
                    if (cell != null) {
                        cell.setPadding(0);
                    }
                }
            }

            PdfPCell dateCell1 = PDFUtil.createCell("Date:", 0, 10, -1);
            dateCell1.setPaddingLeft(6);
            dateCell1.setCellEvent(new SignatureLineCellRenderer(27, 0, 3,"date",x==1?pickup:x==3?dropoff:null));
            dateTimeTable1.addCell(dateCell1);


            PdfPCell timeCell1 = PDFUtil.createCell("Time:", 0, 10, -1);
            timeCell1.setCellEvent(new SignatureLineCellRenderer(27, 8, 3,"",null));
            dateTimeTable1.addCell(timeCell1);

            signaturesTable.addCell(PDFUtil.toPDFTableCell(dateTimeTable1,1,3));
        }



        return signaturesTable;
    }

    /**
     * Creates a table for specifying the condition of goods with checkboxes for various conditions.
     *
     * @return A PdfPTable with checkboxes for "Good", "Damaged", "Wet", and "Dry" conditions.
     */
    private PdfPTable conditionOfGoods() {
        PdfPTable conditionOfGoodsTable = new PdfPTable(6);
        conditionOfGoodsTable.setWidthPercentage(100);

        conditionOfGoodsTable.addCell(PDFUtil.createCell("Condition of Goods?", 2, 8, false, 2, Element.ALIGN_LEFT,7,8,10)).setPaddingLeft(10);

        String[] checkboxTexts = {"Good", "Damaged", "Wet", "Dry"};


        for (String checkboxText : checkboxTexts) {
            PdfPCell checkBoxCell = checkboxText.equals("Dry") ? PDFUtil.createCheckBoxCell(checkboxText, 5) : PDFUtil.createCheckBoxCell(checkboxText, 4);

            conditionOfGoodsTable.addCell(checkBoxCell);
        }


        return conditionOfGoodsTable;
    }

    /**
     * Creates a table displaying parcel information for a booking.
     * Includes columns for number of parcels, description of contents, dimensions, and mass.
     *
     * @param booking The booking containing information about the parcels.
     * @param parcel The specific parcel details to be displayed.
     * @return A PdfPTable with parcel details including number, description, dimensions, and mass.
     */
    private PdfPTable parcelsTable(Booking booking ,BookingParcel parcel) {
        PdfPTable parcelsTable = new PdfPTable(4);
        parcelsTable.setWidthPercentage(100);

        parcelsTable.addCell(PDFUtil.createCell("No. Of Parcels", 1, 8, false, 2, Element.ALIGN_CENTER, 7, 8, 10));


        parcelsTable.addCell(PDFUtil.createCell("Description of Contents", 1, 8, false, 2, Element.ALIGN_CENTER, 7, 8, 10));

        PdfPTable dimensionsTable = new PdfPTable(3);
        dimensionsTable.addCell(PDFUtil.createCell("Dimensions (centimetres)", 3, 7, false, 2, Element.ALIGN_CENTER, -1, 0, 5));
        dimensionsTable.addCell(PDFUtil.createCell("Width", 1, 7, false, 2, Element.ALIGN_LEFT, 8, 5, 5));
        dimensionsTable.addCell(PDFUtil.createCell("Length", 1, 7, false, 2, Element.ALIGN_LEFT, 8, 5, 5));
        dimensionsTable.addCell(PDFUtil.createCell("Height", 1, 7, false, 2, Element.ALIGN_LEFT, 9, 5, 5));

        parcelsTable.addCell(PDFUtil.toPDFTableCell(dimensionsTable, 1, 0));

        parcelsTable.addCell(PDFUtil.createCell("Mass(kg/parcel)", 1, 8, false, 2, Element.ALIGN_CENTER, 5, 8, 10));



        for (int i = 0; i < 1; i++) {
            String index = (i < 9) ? "0" + (i + 1) : String.valueOf(i + 1);

            parcelsTable.addCell(PDFUtil.createCell(String.format("%02d", Integer.parseInt(parcel.getParcel_number())) + " of " +
                    String.format("%02d", booking.getParcels().size()), 1, 7, false, 1, Element.ALIGN_CENTER, 0, 8, 10));

            parcelsTable.addCell(PDFUtil.createCell("Parcel No. " + index, 1, 7, false, 1, Element.ALIGN_CENTER, 0, 8, 10));

            PdfPTable dimensionsTable2 = new PdfPTable(3);

            dimensionsTable2.addCell(PDFUtil.createCell(String.format("%.1f", parcel.getParcel_breadth()), 1, 7, false, 1, Element.ALIGN_CENTER, 10, 4, 6));
            dimensionsTable2.addCell(PDFUtil.createCell(String.format("%.1f", parcel.getParcel_length()), 1, 7, false, 1, Element.ALIGN_CENTER, 10, 4, 6));
            dimensionsTable2.addCell(PDFUtil.createCell(String.format("%.1f", parcel.getParcel_height()), 1, 7, false, 1, Element.ALIGN_CENTER, -1, 4, 6));
            parcelsTable.addCell(PDFUtil.toPDFTableCell(dimensionsTable2, 1, 0));


            parcelsTable.addCell(PDFUtil.createCell(String.format("%.1f", parcel.getParcel_mass()), 1, 7, false, 1, Element.ALIGN_CENTER, 0, 8, 10));
        }

        return parcelsTable;


    }


    /**
     * Creates a table displaying delivery details for a booking, including pickup and drop-off information.
     * The table contains details such as creation and collection dates, company names, addresses, contact persons, and type of service delivery.
     *
     * @param booking The booking information including pickup and drop-off addresses and dates.
     * @param pickup The contact information for the pickup location.
     * @param dropOff The contact information for the drop-off location.
     * @param parcel Optional parcel information, including the number of boxes.
     * @return A PdfPTable containing the formatted delivery details.
     */
    private PdfPTable deliveryDetails(Booking booking,Contact pickup, Contact dropOff, BookingParcel parcel) {
        PdfPTable deliveryInformationTable = new PdfPTable(2);
        deliveryInformationTable.setWidthPercentage(100);

        deliveryInformationTable.addCell(PDFUtil.createCell("Pick Up Details", 1, PDFUtil.lightGray, 10, false, 2, Element.ALIGN_LEFT)).setPaddingLeft(10);
        deliveryInformationTable.addCell(PDFUtil.createCell("Drop Off Details", 1, PDFUtil.lightGray, 10, false, 2, Element.ALIGN_LEFT)).setPaddingLeft(10);

        PdfPTable leftDeliveryInformationTable = new PdfPTable(2);
        leftDeliveryInformationTable.setWidthPercentage(100);

        leftDeliveryInformationTable.addCell(PDFUtil.createCell("Creation Date:", FontUtil.small, false, 1));
        leftDeliveryInformationTable.addCell(PDFUtil.createCell(DateUtil.toDate(booking.getBookingCreatedDate()), FontUtil.small, false, 2));

        leftDeliveryInformationTable.addCell(PDFUtil.createCell("Collection Date:", FontUtil.small, false, 1));
        leftDeliveryInformationTable.addCell(PDFUtil.createCell(booking.getPickUpDate(), FontUtil.small, false, 2));

        leftDeliveryInformationTable.addCell(PDFUtil.createCell("Company Name:", FontUtil.small, false, 1));
        leftDeliveryInformationTable.addCell(PDFUtil.createCell(pickup.getCompanyName(), FontUtil.small, false, 2));

        leftDeliveryInformationTable.addCell(PDFUtil.createCell("Complex / Building Name:", FontUtil.small, false, 1));
        leftDeliveryInformationTable.addCell(PDFUtil.createCell(pickup.getComplex(), FontUtil.small, false, 2));

        leftDeliveryInformationTable.addCell(PDFUtil.createCell("Unit Number:", FontUtil.small, false, 1));
        leftDeliveryInformationTable.addCell(PDFUtil.createCell(pickup.getUnitNo(), FontUtil.small, false, 2));


        leftDeliveryInformationTable.addCell(
                PDFUtil.createCellWithDifferentFonts(
                        "Address: ", FontUtil.poppinsBySize(7,false,1),
                        booking.getPickUpAddress(), FontUtil.poppinsBySize(7,false,2),
                        2, Element.ALIGN_LEFT, Element.ALIGN_LEFT
                )
        );


        leftDeliveryInformationTable.addCell(PDFUtil.createCell("Contact Person", FontUtil.small, false, 2, 2));

        PdfPTable leftDeliveryInformationSub = new PdfPTable(2);


        leftDeliveryInformationTable.addCell(
                PDFUtil.createCellWithDifferentFonts(
                        "Name: ", FontUtil.poppinsBySize(7,false,1),
                        pickup.getFirstName(), FontUtil.poppinsBySize(7,false,2),
                        1, Element.ALIGN_LEFT, Element.ALIGN_LEFT
                )
        );


        leftDeliveryInformationTable.addCell(
                PDFUtil.createCellWithDifferentFonts(
                        "Contact: ", FontUtil.poppinsBySize(7,false,1),
                        pickup.getPhone(), FontUtil.poppinsBySize(7,false,2),
                        1, Element.ALIGN_LEFT, Element.ALIGN_LEFT
                )
        );


        leftDeliveryInformationTable.addCell(PDFUtil.toPDFTableCell(leftDeliveryInformationSub, 2));


        PdfPTable rightDeliveryInformationTable = new PdfPTable(2);
        rightDeliveryInformationTable.addCell(PDFUtil.createCell("Company Name:", FontUtil.small, false, 1));
        rightDeliveryInformationTable.addCell(PDFUtil.createCell(dropOff.getCompanyName(), FontUtil.small, false, 2));

        rightDeliveryInformationTable.addCell(PDFUtil.createCell("Complex / Building Name:", FontUtil.small, false, 1));
        rightDeliveryInformationTable.addCell(PDFUtil.createCell(dropOff.getComplex(), FontUtil.small, false, 2));

        rightDeliveryInformationTable.addCell(PDFUtil.createCell("Unit Number:", FontUtil.small, false, 1));
        rightDeliveryInformationTable.addCell(PDFUtil.createCell(dropOff.getUnitNo(), FontUtil.small, false, 2));

        if(parcel!=null){
            rightDeliveryInformationTable.addCell(PDFUtil.createCell("Number of boxes:", FontUtil.small, false, 1));
            rightDeliveryInformationTable.addCell(PDFUtil.createCell(parcel.getNoBox(), FontUtil.small, false, 2));
        }

        rightDeliveryInformationTable.addCell(PDFUtil.createCell(" "));
        rightDeliveryInformationTable.addCell(PDFUtil.createCell(" "));




        rightDeliveryInformationTable.addCell(
                PDFUtil.createCellWithDifferentFonts(
                        "Address: ", FontUtil.poppinsBySize(7,false,1),
                        booking.getDropOffAddress(), FontUtil.poppinsBySize(7,false,2),
                        2, Element.ALIGN_LEFT, Element.ALIGN_LEFT
                )
        );


        rightDeliveryInformationTable.addCell(PDFUtil.createCell("Contact Person", FontUtil.small, false, 2, 2));
        PdfPTable rightDeliveryInformationSub = new PdfPTable(2);



        rightDeliveryInformationTable.addCell(
                PDFUtil.createCellWithDifferentFonts(
                        "Name: ", FontUtil.poppinsBySize(7,false,1),
                        dropOff.getFirstName(), FontUtil.poppinsBySize(7,false,2),
                        1, Element.ALIGN_LEFT, Element.ALIGN_LEFT
                )
        );


        rightDeliveryInformationTable.addCell(
                PDFUtil.createCellWithDifferentFonts(
                        "Contact: ", FontUtil.poppinsBySize(7,false,1),
                        dropOff.getPhone(), FontUtil.poppinsBySize(7,false,2),
                        1, Element.ALIGN_LEFT, Element.ALIGN_LEFT
                )
        );

        rightDeliveryInformationTable.addCell(PDFUtil.toPDFTableCell(rightDeliveryInformationSub, 2));


        deliveryInformationTable.addCell(PDFUtil.toPDFTableCell(leftDeliveryInformationTable, 1));
        deliveryInformationTable.addCell(PDFUtil.toPDFTableCell(rightDeliveryInformationTable, 1));

        deliveryInformationTable.addCell(PDFUtil.createCell("Type of Service Delivery", 1, 8, false, 2, Element.ALIGN_LEFT,7,8,10)).setPaddingLeft(10);
        deliveryInformationTable.addCell(PDFUtil.createCell(booking.getType().toString(), 1, 8, false, 2, Element.ALIGN_RIGHT,5,8,10));

        return deliveryInformationTable;
    }
}

