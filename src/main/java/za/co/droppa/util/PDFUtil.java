package za.co.droppa.util;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.Barcode128;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import za.co.droppa.model.BankAccount;
import za.co.droppa.service.aws.S3Service;
import za.co.droppa.service.pdf.events.PageFooterEventHandler;
import za.co.droppa.service.pdf.renderers.SquareCheckBoxCellRenderer;

import java.util.Base64;


public class PDFUtil {
    public static final BaseColor lightGray = new BaseColor(230, 230, 230);
    public static final BaseColor lighterGray = new BaseColor(245, 245, 245);
    public static final BaseColor lightBlue = new BaseColor(139, 207, 234, 255);
    public static final BaseColor transparent = new BaseColor(0, 0, 0, 0);


    private static final S3Service s3Service = new S3Service();

    /**
     * Creates a PdfPCell with the specified content, font size, boldness, font type, and column span.
     *
     * @param content  the text content of the cell.
     * @param fontSize the font size of the text.
     * @param bold     whether the text should be bold.
     * @param type     the font type.
     * @param colspan  the number of columns the cell should span.
     * @return a PdfPCell object with the specified content, font settings, column span, and no border.
     */
    public static PdfPCell createCell(String content, int fontSize, boolean bold, int type, int colspan) {
        PdfPCell cell = new PdfPCell(new Phrase(content, FontUtil.poppinsBySize(fontSize, bold, type)));
        setLeftPadding(cell);
        cell.setColspan(colspan);
        cell.setBorder(0);
        return cell;
    }

    /**
     * Creates a PdfPCell with the specified content, alignment, and padding, using default border color and font settings.
     *
     * @param content   the text content of the cell.
     * @param alignment the horizontal alignment of the cell's content.
     * @param padding   the padding to be applied to the cell.
     * @return a PdfPCell object with the specified content, alignment, and padding, and default border color.
     */
    public static PdfPCell createCell(String content, int alignment, int padding) {
        PdfPCell cell = new PdfPCell(new Phrase(content, FontUtil.poppinsBySize(15, true, 0)));
        setLeftPadding(cell);
        cell.setHorizontalAlignment(alignment);
        cell.setBorderColor(lightGray);
        return cell;
    }

    /**
     * Creates a PdfPCell with the specified content, alignment, padding, and border configuration.
     *
     * @param content   the text content of the cell.
     * @param alignment the horizontal alignment of the cell's content.
     * @param padding   the padding to be applied to both the top and bottom of the cell.
     * @param border    an integer representing the border configuration, as defined in the setCustomBorders method.
     * @return a PdfPCell object with the specified content, alignment, padding, and border configuration.
     */
    public static PdfPCell createCell(String content, int alignment, int padding, int border) {
        PdfPCell cell = new PdfPCell(new Phrase(content, FontUtil.poppinsBySize(7, false, 1)));
        cell.setPadding(0);

        cell.setPaddingTop(padding);
        cell.setPaddingBottom(padding);

        setLeftPadding(cell);
        cell.setHorizontalAlignment(alignment);
        cell.setBorderColor(lightGray);
        setCustomBorders(cell, border);
        return cell;
    }


    /**
     * Sets custom borders for the specified PdfPCell based on the given border code.
     */
    public static void setCustomBorders(PdfPCell cell, int border) {
        switch (border) {
            case 0:
                cell.setBorder(PdfPCell.LEFT | PdfPCell.RIGHT | PdfPCell.TOP | PdfPCell.BOTTOM);
                break;
            case 1:
                cell.setBorder(PdfPCell.LEFT | PdfPCell.RIGHT);
                break;
            case 3:
                cell.setBorder(PdfPCell.LEFT | PdfPCell.RIGHT | PdfPCell.BOTTOM);
                break;
            case 4:
                cell.setBorder(PdfPCell.TOP | PdfPCell.BOTTOM);
                break;
            case 5:
                cell.setBorder(PdfPCell.TOP | PdfPCell.BOTTOM | PdfPCell.RIGHT);
                break;
            case 7:
                cell.setBorder(PdfPCell.LEFT | PdfPCell.TOP | PdfPCell.BOTTOM);
                break;
            case 8:
                cell.setBorder(PdfPCell.RIGHT | PdfPCell.TOP);
                break;
            case 9:
                cell.setBorder(PdfPCell.TOP);
                break;
            case 10:
                cell.setBorder(PdfPCell.RIGHT);
                break;
            default:
                cell.setBorder(0);
        }
    }


    /**
     * Creates a PdfPCell with the specified content, column span, and background color, using default font settings.
     *
     * @param content the text content of the cell.
     * @param colspan the number of columns the cell should span.
     * @param bgColor the background color of the cell.
     * @return a PdfPCell object with the specified content, column span, and background color.
     */

    public static PdfPCell createCell(String content, int colspan, BaseColor bgColor) {
        PdfPCell cell = new PdfPCell(new Phrase(content, FontUtil.poppinsBySize(12, true, 0)));
        setLeftPadding(cell);
        cell.setBorder(0);
        cell.setColspan(colspan);
        cell.setBackgroundColor(bgColor);
        return cell;
    }


    /**
     * Creates a PdfPCell with the specified content, formatting options, and background color.
     *
     * @param content  the text content of the cell.
     * @param colspan  the number of columns the cell should span.
     * @param bgColor  the background color of the cell.
     * @param fontSize the font size of the text.
     * @param bold     whether the text should be bold.
     * @param type     the font type.
     * @return a PdfPCell object with the specified content, formatting options, and background color.
     */
    public static PdfPCell createCell(String content, int colspan, BaseColor bgColor, int fontSize, boolean bold, int type, int alignment) {
        PdfPCell cell = new PdfPCell(new Phrase(content, FontUtil.poppinsBySize(fontSize, bold, type)));

        if (alignment != Element.ALIGN_CENTER)
            setLeftPadding(cell);


        cell.setPaddingTop(8);
        cell.setPaddingBottom(10);
        cell.setBorder(0);
        cell.setColspan(colspan);
        cell.setBackgroundColor(bgColor);
        cell.setHorizontalAlignment(alignment);
        return cell;
    }


    /**
     * Creates a PdfPCell with the specified content and formatting options.
     *
     * @param content    the text content of the cell.
     * @param colspan    the number of columns the cell should span.
     * @param fontSize   the font size of the text.
     * @param bold       whether the text should be bold.
     * @param type       the font type.
     * @param alignment  the horizontal alignment of the cell's content.
     * @param borderType the type of border for the cell.
     * @param pt         the top padding of the cell.
     * @param pb         the bottom padding of the cell.
     * @return a PdfPCell object with the specified content and formatting options.
     */
    public static PdfPCell createCell(String content, int colspan, int fontSize, boolean bold, int type, int alignment, int borderType, int pt, int pb) {
        PdfPCell cell = new PdfPCell(new Phrase(content, FontUtil.poppinsBySize(fontSize, bold, type)));

        if (alignment != Element.ALIGN_CENTER)
            setLeftPadding(cell);

        cell.setPaddingTop(pt);
        cell.setPaddingBottom(pb);
        cell.setColspan(colspan);
        cell.setHorizontalAlignment(alignment);

        setCustomBorders(cell, borderType);

        if (alignment == Element.ALIGN_RIGHT) setRightPadding(cell);

        cell.setBorderColor(lightGray);
        return cell;
    }


    /**
     * Creates a PdfPCell with the specified content and default font settings, padding, and no border.
     *
     * @param content the text content of the cell.
     * @return a PdfPCell object with the specified content, default font settings, left padding, and no border.
     */
    public static PdfPCell createCell(String content) {
        PdfPCell cell = new PdfPCell(new Phrase(content, FontUtil.poppinsBySize(12, false, 0)));
        setLeftPadding(cell);
        cell.setBorder(0);
        return cell;
    }

    /**
     * Creates a PdfPCell with the specified content and default font settings, padding, and no border.
     *
     * @return a PdfPCell object with the specified content, default font settings, left padding, and no border.
     */
    public static PdfPCell createCell(Phrase phrase) {
        PdfPCell cell = new PdfPCell(phrase);
        setLeftPadding(cell);
        cell.setBorder(0);
        return cell;
    }

    /**
     * Creates a PdfPCell with the specified content, font size, boldness, and font type.
     *
     * @param content  the text content of the cell.
     * @param fontSize the size of the font.
     * @param bold     whether the font should be bold.
     * @return a PdfPCell object with the specified content, font settings, left padding, and no border.
     */
    public static PdfPCell createCell(String content, int fontSize, boolean bold, int type) {
        PdfPCell cell = new PdfPCell(new Phrase(content, FontUtil.poppinsBySize(fontSize, bold, type)));
        setLeftPadding(cell);
        cell.setBorder(0);
        return cell;
    }


    /**
     * Sets the right padding of the specified PdfPCell.
     *
     * @param cell the PdfPCell to which the right padding will be applied.
     */
    public static void setRightPadding(PdfPCell cell) {
        cell.setPaddingRight(8);
    }

    /**
     * Sets the left padding of the specified PdfPCell.
     *
     * @param cell the PdfPCell to which the left padding will be applied.
     */
    public static void setLeftPadding(PdfPCell cell) {
        cell.setPaddingLeft(8);
    }

    /**
     * Sets the left padding of the specified PdfPCell.
     *
     * @param cell the PdfPCell to which the left padding will be applied.
     */
    public static void setLeftPadding(PdfPCell cell, int pl) {
        cell.setPaddingLeft(pl);
    }

    /**
     * Sets the left padding of the specified PdfPCell.
     *
     * @param cell the PdfPCell to which the left padding will be applied.
     */
    public static void setHorizontalPadding(PdfPCell cell, int pl, int pr) {
        cell.setPaddingLeft(pl);
        cell.setPaddingRight(pr);
    }

    /**
     * Sets the left padding of the specified PdfPCell.
     *
     * @param cell the PdfPCell to which the left padding will be applied.
     */
    public static void setVerticalPadding(PdfPCell cell, int pt, int pb) {
        cell.setPaddingTop(pt);
        cell.setPaddingBottom(pb);
    }

    /**
     * Converts a PdfPTable into a PdfPCell with the specified colspan and no border.
     *
     * @param pdfPTable the PdfPTable to be added to the cell
     * @param colspan the number of columns the cell should span
     * @return a PdfPCell containing the specified PdfPTable with the given settings
     */
    public static PdfPCell toPDFTableCell(PdfPTable pdfPTable, int colspan) {
        PdfPCell cell = new PdfPCell();
        cell.addElement(pdfPTable);
        cell.setBorder(0);
        cell.setColspan(colspan);
        pdfPTable.setWidthPercentage(100);
        return cell;
    }

    /**
     * Converts a PdfPTable into a PdfPCell with the specified colspan and border type.
     *
     * @param pdfPTable the PdfPTable to be added to the cell
     * @param colspan the number of columns the cell should span
     * @param borderType the type of border to apply to the cell
     * @return a PdfPCell containing the specified PdfPTable with the given settings
     */
    public static PdfPCell toPDFTableCell(PdfPTable pdfPTable, int colspan,int borderType) {
        PdfPCell cell = new PdfPCell();
        cell.addElement(pdfPTable);
        cell.setBorder(0);
        cell.setColspan(colspan);
        pdfPTable.setWidthPercentage(100);
        setCustomBorders(cell, borderType);
        cell.setBorderColor(lightGray);

        return cell;
    }

    /**
     * Creates a Paragraph representing a blank line with the specified spacing before it.
     *
     * @param spacing the amount of space to add before the blank line
     * @return a Paragraph object representing a blank line with the specified spacing
     */
    public static Paragraph blankLine(int spacing) {
        Paragraph blankLine = new Paragraph(" ");
        blankLine.setSpacingBefore(spacing);

        return blankLine;
    }

    /**
     * Creates a Paragraph with the specified text, alignment, font size, and style.
     *
     * @param text the text to be displayed in the paragraph
     * @param alignment the alignment of the paragraph content (e.g., Element.ALIGN_LEFT, Element.ALIGN_CENTER)
     * @param fontSize the size of the font
     * @param bold whether the text should be bold
     * @param type the type of font
     * @return a Paragraph object with the specified settings
     */
    public static Paragraph createParagraph(String text, int alignment, int fontSize, boolean bold, int type) {
        Paragraph paragraph = new Paragraph(text, FontUtil.poppinsBySize(fontSize, bold, type));
        paragraph.setAlignment(Element.ALIGN_CENTER);
        return paragraph;
    }




    /**
     * Adds a basic footer to the PDF document using a specified footer type.
     *
     * @param writer the PdfWriter instance used to write the document
     * @param type the type of footer to be added
     */
    public static void addBasicFooter(PdfWriter writer, int type) {
        writer.setPageEvent(new PageFooterEventHandler(type));
    }









    /**
     * Creates a PdfPCell containing a checkbox with the specified text and border type.
     *
     * @param text the text to be displayed in the checkbox cell
     * @param borderType the type of border to apply to the cell
     * @return a PdfPCell containing a checkbox with the specified settings
     */
    public static PdfPCell createCheckBoxCell(String text, int borderType) {
        PdfPCell cell = new PdfPCell();
        cell.setCellEvent(new SquareCheckBoxCellRenderer(text));
        cell.setPadding(5);
        PDFUtil.setCustomBorders(cell, borderType);
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell.setBorderColor(lightGray);

        return cell;
    }


    /**
     * Sets the column widths for a PdfPTable.
     *
     * @param pdfPTable The PdfPTable to set the column widths for.
     * @param columnWidths An array of floats representing the column widths.
     */
    public static void setColumnWidths(PdfPTable pdfPTable,float[] columnWidths){
        try {
            pdfPTable.setWidths(columnWidths);
        }
        catch (DocumentException e) {
            System.out.printf("unable to set table widths {%s}%n",e.getMessage());
        }
    }


    /**
     * Adds a specified number of empty cells to a given PdfPTable.
     *
     * @param pdfPTable the PdfPTable to which the empty cells will be added
     * @param numCells the number of empty cells to add
     */
    public static void addEmptyCells(PdfPTable pdfPTable,int numCells){
        for (int i = 0; i < numCells; i++) {
            pdfPTable.addCell(PDFUtil.createCell(" "));
        }
    }



    public static PdfPCell createCellWithDifferentFonts(String label, Font labelFont, String text, Font textFont, int colspan, int hAlign, int vAlign) {
        // Create chunks for each part of the text
        Chunk labelChunk = new Chunk(label, labelFont);
        Chunk textChunk = new Chunk(text, textFont);

        // Combine chunks into a phrase
        Phrase phrase = new Phrase();
        phrase.add(labelChunk);
        phrase.add(textChunk);

        // Create cell with the phrase
        PdfPCell cell = createCell(phrase);
        cell.setColspan(colspan);
        return cell;
    }

    public static void addBasicHeader(Document document, String heading, String base64Image){
        try {

            document.add(blankLine(1));
            //addLogo(document,base64Image);
            addDroppaImage(document);
            document.add(blankLine(1));

            Paragraph p = new Paragraph(heading, FontUtil.poppinsBySize(19, true, 2));
            p.setAlignment(1);
            document.add(p);

            document.add(blankLine(8));
        }
        catch (DocumentException e) {
            System.out.println("unable to add header and footer");
        }


    }


    /**
     * Adds a basic header to the specified document, including a blank line, an image, and a heading.
     *
     * @param document the Document to which the header will be added
     * @param heading the text to be displayed as the heading
     */
    public static void addBasicHeader(Document document, String heading,boolean droppaLogo){
        try {
            if(droppaLogo){
                document.add(blankLine(1));
                addDroppaImage(document);
            }

            document.add(blankLine(1));

            Paragraph p = new Paragraph(heading, FontUtil.poppinsBySize(19, true, 2));
            p.setAlignment(1);
            document.add(p);
            document.add(PDFUtil.blankLine(8));
        }
        catch (DocumentException e) {
            System.out.println("unable to add header and footer");
        }
    }

    /**
     * Adds a basic header to the specified document, including a blank line, an image, and a heading.
     *
     * @param document the Document to which the header will be added
     * @param heading the text to be displayed as the heading
     */
    public static void addBasicHeader(Document document, String heading){
        try {
            document.add(blankLine(1));
            addDroppaImage(document);
            document.add(blankLine(1));

            Paragraph p = new Paragraph(heading, FontUtil.poppinsBySize(19, true, 2));
            p.setAlignment(1);
            document.add(p);
            document.add(PDFUtil.blankLine(8));
        }
        catch (DocumentException e) {
            System.out.println("unable to add header and footer");
        }
    }

    public static void addHeaderWithBarcode(Document document,PdfWriter writer,  String heading, String waybillNo,String base64String){

        try {
            document.add(blankLine(1));
            if(base64String!=null){
                addLogo(document,base64String);
            }else{
                addDroppaImage(document);
            }

            document.add(blankLine(1));

            Paragraph p = new Paragraph(heading, FontUtil.poppinsBySize(19, true, 2));
            p.setAlignment(1);
            document.add(p);



            Barcode128 code128 = new Barcode128();
            code128.setFont(FontUtil.poppinsRegular());
            code128.setBaseline(7);
            code128.setCode(waybillNo);
            code128.setCodeType(Barcode128.CODE128);

            Image code128Image = code128.createImageWithBarcode(writer.getDirectContent(), null, null);
            code128Image.scaleAbsolute(140, 55);
            code128Image.setAbsolutePosition(400, 760);
            document.add(code128Image);

            document.add(PDFUtil.blankLine(8));
        }
        catch (DocumentException e) {
            System.out.println("unable to add header and footer");
        }
    }

    /**
     * Adds a logo to the specified document.
     * This method decodes a Base64-encoded image and adds it to the provided
     * document at a specified position. The logo is scaled to fit within the
     * dimensions of 146x65 pixels and aligned to the bottom.
     *
     * @param document   the document to which the logo will be added
     * @param base64Image the Base64-encoded string representing the image
     */
    public static void addLogo(Document document,String base64Image){

        try {
            if(base64Image!=null&& !base64Image.isEmpty()) {
                byte[] imageBytes = Base64.getDecoder().decode(base64Image);
                Image image = Image.getInstance(imageBytes);
                image.setAlignment(Element.ALIGN_BOTTOM);
                image.scaleToFit(146, 65);
                image.setAbsolutePosition(30, 755);
                document.add(image);
            }
            else
                addDroppaImage(document);
        }catch (Exception e){
            System.out.println(String.format("unable to add droppa logo image, {%s}", e.getMessage()));
        }
    }

    /**
     * Adds the Droppa logo image to the specified document.
     *
     * @param document the Document to which the image will be added
     */
    public static void addDroppaImage(Document document){
        try {

            Image footerImage =  s3Service.loadImage("droppa-assets1","images/DROPPA_LOGO.png");

            footerImage.setAlignment(Element.ALIGN_BOTTOM);
            footerImage.scaleToFit(146, 65);
            footerImage.setAbsolutePosition(30, 755);
            document.add(footerImage);
        }
        catch (Exception e){
            System.out.println(String.format("unable to add droppa logo image, {%s}", e.getMessage()));
        }
    }

    public static void addBankingDetailsAbsolute(PdfWriter writer, BankAccount bank){

//        BankAccount bank = new BankAccount();
//
//        bank.setBankName("Standard Bank");
//        bank.setAccountNo("042537819");
//        bank.setAccountName("Dropper Group(Pty) Ltd ");
//        bank.setBranchCode("051001");



        PdfPTable table = new PdfPTable(1); // 3 columns
//        setColumnWidths(table,new float[]{1.5f});
        table.setTotalWidth(170);
        table.setLockedWidth(true);

        table.addCell(createCell("Banking Details",7,false,2,1));

        table.addCell(createCell(String.format("Bank: %s",bank.getBankName()),7,false,1,1));
//        table.addCell(createCell(String.format(bank.getBankName(),7,false,1,1));


        table.addCell(createCell(String.format("Account Name: %s",bank.getAccountName()),7,false,1,1));
//        table.addCell(createCell(bank.getAccountName(),7,false,1,1));


        if(bank.getAccountNo()!=null && !bank.getAccountNo().isEmpty()) {
            table.addCell(createCell(String.format("Account: %s",bank.getAccountNo()), 7, false, 1, 1));
        }


        table.addCell(createCell(String.format("Branch Code: %s",bank.getBranchCode()),7,false,1,1));
//        table.addCell(createCell(bank.getBranchCode(),7,false,1,1));

        // Write the table at an absolute position
        table.writeSelectedRows(0, -1, 30, 145, writer.getDirectContent()); // Adjust the position as needed
    }
}
