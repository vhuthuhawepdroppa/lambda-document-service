package za.co.droppa.service.pdf.generate;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.draw.LineSeparator;
import za.co.droppa.dto.QuoteCreateDTO;
import za.co.droppa.model.BankAccount;
import za.co.droppa.model.CourierInsuranceRates;
import za.co.droppa.model.Type;
import za.co.droppa.util.FontUtil;
import za.co.droppa.util.PDFUtil;

import java.io.ByteArrayInputStream;
import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;

public class DroppaQuotation extends AbstractPdfDocument {

    public DroppaQuotation(String base64String, String name, String heading, String waybill) throws DocumentException{
        super(base64String,name,heading,waybill);
    }


    @Override
    public ByteArrayInputStream buildDocument(Map<String, Object> params) throws DocumentException {

        QuoteCreateDTO dto = (QuoteCreateDTO) params.get("dto");
        String transId = params.get("transId").toString();

        // insurance rate (%) for company, including droppa margin percentage
        double fInsuranceRate = setInsurancePercentageByCompany(dto);

        // actual insurance amount
        double fInsuranceAmount = calculateInsuranceAmount(dto.insuranceAmount, fInsuranceRate);

        // subtotal amount to be paid
        double fSubtotalAmount = calculateSubTotalAmount(dto.totalPrice);

        // cost of tax
        double fVatAmount = calculateVatAmount(dto.totalPrice, fSubtotalAmount);

        // total amount to be paid including tax
        double fTotalAmount = calculateTotalAmount(fInsuranceAmount, dto.totalPrice);


        System.out.println(dto);

        open();



        document.setMargins(document.leftMargin(), document.rightMargin(), document.topMargin(), document.bottomMargin() + 120);

        Font baseFontMediumS = FontUtil.poppinsBySize(8,false,2);
        Font baseFontRegular = FontUtil.poppinsBySize(8,false,-1);
        Font baseFontSemiBold = FontUtil.poppinsBySize(10,false,3);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        String date_string = LocalDateTime.now().format(formatter);//WRITE FORMAT 14-April-2024

        Paragraph billTo = new Paragraph(new Phrase(new Chunk("Bill To:", baseFontSemiBold)));
        billTo.setAlignment(Element.ALIGN_LEFT);
        billTo.setSpacingBefore(20);
        document.add(billTo);


        Paragraph droppaGroup = new Paragraph(new Phrase("Droppa Group", baseFontRegular));
        document.add(droppaGroup);

        PdfPTable address = new PdfPTable(3);
        address.setSpacingBefore(15f);
        address.setWidthPercentage(100f);
        float[] columnWidths = {6f, 1f, 1f};
        address.setWidths(columnWidths);

        PdfPCell addressLine = new PdfPCell(new Phrase("Building 13,Woodlands Office Park,", baseFontRegular));
        addressLine.setBorder(Rectangle.NO_BORDER);
        PdfPCell addressLine1 = new PdfPCell(new Phrase("Quote No.", baseFontRegular));
        addressLine1.setBorder(Rectangle.NO_BORDER);
        PdfPCell addressLine2 = new PdfPCell(new Phrase(transId, baseFontRegular));
        addressLine2.setBorder(Rectangle.NO_BORDER);
        addressLine2.setHorizontalAlignment(Element.ALIGN_RIGHT);

        address.addCell(addressLine);
        address.addCell(addressLine1);
        address.addCell(addressLine2);

        PdfPCell addressLine3 = new PdfPCell(new Phrase("20 Woodlands Dr, Woodmead,", baseFontRegular));
        addressLine3.setBorder(Rectangle.NO_BORDER);
        PdfPCell addressLine4 = new PdfPCell(new Phrase("Issue Date.", baseFontRegular));
        addressLine4.setBorder(Rectangle.NO_BORDER);
        PdfPCell addressLine5 = new PdfPCell(new Phrase(date_string, baseFontRegular));
        addressLine5.setBorder(Rectangle.NO_BORDER);
        addressLine5.setHorizontalAlignment(Element.ALIGN_RIGHT);

        address.addCell(addressLine3);
        address.addCell(addressLine4);
        address.addCell(addressLine5);


        PdfPCell addressLine6 = new PdfPCell(new Phrase("Johannesburg, 2191", baseFontRegular));
        addressLine6.setBorder(Rectangle.NO_BORDER);
        PdfPCell addressLine7 = new PdfPCell(new Phrase("Expiry Date.", baseFontRegular));
        addressLine7.setBorder(Rectangle.NO_BORDER);
        String expS = LocalDateTime.now().plusDays(1).format(formatter);
        PdfPCell addressLine8 = new PdfPCell(new Phrase(expS, baseFontRegular));
        addressLine8.setBorder(Rectangle.NO_BORDER);
        addressLine8.setHorizontalAlignment(Element.ALIGN_RIGHT);

        address.addCell(addressLine6);
        address.addCell(addressLine7);
        address.addCell(addressLine8);

        document.add(address);

        PdfPTable table = new PdfPTable(2);
        table.setWidthPercentage(100f);
        table.setSpacingBefore(15);


        PdfPCell droppaServiceType = new PdfPCell(new Phrase("Droppa Service Type:", baseFontRegular));
        PdfPCell serviceType = new PdfPCell(new Phrase(dto.selectedService.toUpperCase().replace("_", " "), baseFontMediumS));
        droppaServiceType.setBorder(Rectangle.NO_BORDER);
        serviceType.setBorder(Rectangle.NO_BORDER);

        String type = dto.isFurniture() ? "Trips" : "Parcels";

        PdfPCell totalNumberOfParcel = new PdfPCell(new Phrase(String.format("Total Number of %s", type), baseFontRegular));
        PdfPCell numberOfParcel = new PdfPCell(new Phrase(dto.parcelNumber + "", baseFontRegular));
        totalNumberOfParcel.setBorder(Rectangle.NO_BORDER);
        numberOfParcel.setBorder(Rectangle.NO_BORDER);
        totalNumberOfParcel.setHorizontalAlignment(Element.ALIGN_RIGHT);
        numberOfParcel.setHorizontalAlignment(Element.ALIGN_RIGHT);


        table.addCell(droppaServiceType);
        table.addCell(totalNumberOfParcel);


        table.addCell(serviceType);


        table.addCell(numberOfParcel);

        //number of helpers

        if (dto.isFurniture()) {
            PdfPCell totalNumberOfHelpers = new PdfPCell(new Phrase("Total number of helpers", baseFontRegular));
            PdfPCell numberOfHelpers = new PdfPCell(new Phrase(dto.numHelpers + "", baseFontRegular));
            totalNumberOfHelpers.setBorder(Rectangle.NO_BORDER);
            numberOfHelpers.setBorder(Rectangle.NO_BORDER);
            totalNumberOfHelpers.setHorizontalAlignment(Element.ALIGN_RIGHT);
            numberOfHelpers.setHorizontalAlignment(Element.ALIGN_RIGHT);

            table.addCell(emptyCell());
            table.addCell(totalNumberOfHelpers);
            table.addCell(emptyCell());
            table.addCell(numberOfHelpers);

        }


        document.add(table);

        PdfPTable description = new PdfPTable(1);
        description.setWidthPercentage(100);

        description.setSpacingBefore(15f);
        PdfPCell des = new PdfPCell(new Phrase("DESCRIPTION", baseFontSemiBold));
        des.setPadding(10f);
        BaseColor color = new BaseColor(206, 233, 244);
        des.setBackgroundColor(PDFUtil.lightGray);
        des.setBorder(Rectangle.NO_BORDER);
        description.addCell(des);


        document.add(description);



        Paragraph pickUpAddressLabel = new Paragraph(new Phrase("Pick Up Address", baseFontMediumS));
        pickUpAddressLabel.setSpacingBefore(10f);
        pickUpAddressLabel.setFirstLineIndent(10f);
        document.add(pickUpAddressLabel);

        if (dto.pickUpAddresses != null) {
            dto.pickUpAddresses.forEach(s -> {
                Paragraph pickUpAddress = new Paragraph(new Phrase(s, baseFontRegular));
                pickUpAddress.setSpacingAfter(10f);
                pickUpAddress.setFirstLineIndent(10f);
                try {
                    document.add(pickUpAddress);
                } catch (DocumentException e) {
                    throw new RuntimeException(e);
                }
            });
        }


        LineSeparator separator = new LineSeparator();
        separator.setLineColor(PDFUtil.lightGray);
        document.add(separator);

        Paragraph dropOffAddressLabel = new Paragraph(new Phrase("Drop Off Address",baseFontMediumS));
        dropOffAddressLabel.setSpacingBefore(10f);
        dropOffAddressLabel.setFirstLineIndent(10f);

        document.add(dropOffAddressLabel);

        if (dto.dropOffAddresses != null) {
            dto.dropOffAddresses.forEach(s -> {
                Paragraph dropOffAddress = new Paragraph(new Phrase(s, baseFontRegular));
                dropOffAddress.setSpacingAfter(10f);
                dropOffAddress.setFirstLineIndent(10f);
                try {
                    document.add(dropOffAddress);
                } catch (DocumentException e) {
                    throw new RuntimeException(e);
                }
            });
        }


        DecimalFormat decimalFormat = new DecimalFormat("R###.00");


        PdfPTable tableTotals = new PdfPTable(2);
        tableTotals.setWidthPercentage(50);
        tableTotals.setSpacingBefore(35f);
        tableTotals.setHorizontalAlignment(Element.ALIGN_RIGHT);


        if (dto.isFurniture()) {
            PdfPCell helpersCost = new PdfPCell(new Phrase("Cost of helpers", baseFontRegular));
            helpersCost.setBorder(Rectangle.NO_BORDER);
            helpersCost.setPadding(5f);
            double helpersTotal = (dto.numHelpers * 200);
            PdfPCell helpersAmount = new PdfPCell(new Phrase(decimalFormat.format(helpersTotal), baseFontRegular));
            helpersAmount.setBorder(Rectangle.NO_BORDER);
            helpersAmount.setPadding(5f);
            helpersAmount.setHorizontalAlignment(Element.ALIGN_RIGHT);
            tableTotals.addCell(helpersCost);
            tableTotals.addCell(helpersAmount);
        }


        //System.out.printf("insurance Amount : %s%n", dto.insuranceAmount);

        if (dto.insuranceAmount > 0) {

            PdfPCell insurance = new PdfPCell(new Phrase("Insurance", baseFontRegular));
            insurance.setBorder(Rectangle.NO_BORDER);
            insurance.setPadding(5f);
            double insuranceTotal = (dto.insuranceAmount * (1.25 / 100));
            PdfPCell insuranceAmount = new PdfPCell(new Phrase(decimalFormat.format(fInsuranceAmount), baseFontRegular));
            insuranceAmount.setBorder(Rectangle.NO_BORDER);
            insuranceAmount.setPadding(5f);
            insuranceAmount.setHorizontalAlignment(Element.ALIGN_RIGHT);
            tableTotals.addCell(insurance);
            tableTotals.addCell(insuranceAmount);
        }

        PdfPCell subTotal = new PdfPCell(new Phrase("Shipping Amount", baseFontRegular));
        subTotal.setBorder(Rectangle.NO_BORDER);
        subTotal.setPadding(5f);
        PdfPCell subTotalAmount = new PdfPCell(new Phrase(decimalFormat.format(fSubtotalAmount), baseFontRegular));
        subTotalAmount.setBorder(Rectangle.NO_BORDER);
        subTotalAmount.setPadding(5f);
        subTotalAmount.setHorizontalAlignment(Element.ALIGN_RIGHT);

        tableTotals.addCell(subTotal);
        tableTotals.addCell(subTotalAmount);


        PdfPCell vatAmountTotal = new PdfPCell(new Phrase("Vat Amount", baseFontRegular));
        vatAmountTotal.setBorder(Rectangle.NO_BORDER);
        vatAmountTotal.setPadding(5f);
        vatAmountTotal.setPaddingLeft(5f);
        vatAmountTotal.setPaddingBottom(15f);
        PdfPCell vatAmount = new PdfPCell(new Phrase(decimalFormat.format(fVatAmount), baseFontRegular));
        vatAmount.setBorder(Rectangle.NO_BORDER);
        vatAmount.setHorizontalAlignment(Element.ALIGN_RIGHT);
        vatAmount.setPadding(5f);
        vatAmount.setPaddingBottom(15f);


        tableTotals.addCell(vatAmountTotal);
        tableTotals.addCell(vatAmount);

        PdfPCell total = new PdfPCell(new Phrase("Total Cost", baseFontSemiBold));
        total.setBorder(Rectangle.NO_BORDER);
        total.setPaddingTop(10f);
        total.setBackgroundColor(color);
        total.setPadding(10f);
        PdfPCell totalAmount = new PdfPCell(new Phrase(decimalFormat.format(fTotalAmount), baseFontSemiBold));
        totalAmount.setBorder(Rectangle.NO_BORDER);
        totalAmount.setPaddingTop(10f);
        totalAmount.setHorizontalAlignment(Element.ALIGN_RIGHT);
        totalAmount.setBackgroundColor(color);
        totalAmount.setPadding(10f);

        tableTotals.addCell(total);
        tableTotals.addCell(totalAmount);


        if (dto.insuranceAmount > 0) {
            PdfPCell disclaimer = new PdfPCell(new Phrase(String.format("(Cover of up to %s)", decimalFormat.format(dto.insuranceAmount)), new Font(FontFactory.getFont("poppinsRegular", 8, Font.ITALIC))));
            disclaimer.setBorder(Rectangle.NO_BORDER);
            disclaimer.setPaddingTop(2f);
            disclaimer.setPadding(2f);
            disclaimer.setColspan(2);
            tableTotals.addCell(disclaimer);

        }

        tableTotals.setKeepTogether(true);


        document.add(tableTotals);


        BankAccount bank = new BankAccount();
        bank.setBankName("Standard Bank");
        bank.setAccountName("Droppa Group (Pty)Ltd");
        bank.setAccountNo("042537819");
        bank.setBranchCode("051001");
        PDFUtil.addBankingDetailsAbsolute(writer,bank);


        close();

        //System.out.println("PDF Created!");


        return new ByteArrayInputStream(out.toByteArray());
    }

    /**
     * Calculates the total amount by summing the insurance amount and the total price.
     *
     * @param fInsuranceAmount the insurance amount
     * @param totalPrice       the total price
     * @return the total amount, which is the sum of the insurance amount and the total price
     */
    private double calculateTotalAmount(double fInsuranceAmount, double totalPrice) {
        return fInsuranceAmount + totalPrice;
    }

    /**
     * Calculates the VAT amount by subtracting the subtotal from the total price.
     *
     * @param totalPrice the total price including VAT
     * @param subTotal   the subtotal excluding VAT
     * @return the VAT amount, which is the difference between the total price and the subtotal
     */
    private double calculateVatAmount(double totalPrice, double subTotal) {
        return totalPrice - subTotal;
    }

    /**
     * Calculates the subtotal amount from the total price, assuming the total price includes VAT at 15%.
     *
     * @param totalPrice the total price including VAT
     * @return the subtotal amount, which is the total price excluding VAT
     */
    private double calculateSubTotalAmount(double totalPrice) {
        return totalPrice * (100.00 / 115.00);
    }


    /**
     * Calculates the insurance amount as 1.25% of the given amount.
     *
     * @param amount the amount on which insurance is calculated
     * @return the insurance amount, which is 1.25% of the given amount
     */
    private double calculateInsuranceAmount(double amount, double insuranceRate) {
        return amount * (insuranceRate / 100);
    }


    /**
     * Creates an empty PdfPCell with no border.
     *
     * @return a PdfPCell with no border
     */
    private PdfPCell emptyCell() {
        PdfPCell cell = new PdfPCell();
        cell.setBorder(Rectangle.NO_BORDER);
        return cell;
    }

    /**
     * Sets the insurance percentage based on the company name.
     *
     * @param dto the DownloadQuoteDTO object containing the selected service
     * @return the insurance rate based on the company, or 0 if the company is not recognized
     */
    private double setInsurancePercentageByCompany(QuoteCreateDTO dto) {
        Type type = Type.findByName(dto.selectedService);

        if (type != null) {
            if (Type.companyName(type).equals("SKYNET"))
                return CourierInsuranceRates.SKYNET + CourierInsuranceRates.DROPPA_SKYNET;
            else if (Type.companyName(type).equals("COURIER_GUY"))
                return CourierInsuranceRates.COURIER_GUY + CourierInsuranceRates.DROPPA_COURIER_GUY;
            else if (Type.companyName(type).equals("DSV"))
                return CourierInsuranceRates.DSV + CourierInsuranceRates.DROPPA_DSV;
            else if (Type.companyName(type).equals("DPD_LASER"))
                return CourierInsuranceRates.DPD_LASER + CourierInsuranceRates.DROPPA_DPD_LASER;

        }

        return 0;
    }
}
