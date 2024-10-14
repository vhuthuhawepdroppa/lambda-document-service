package za.co.droppa.util;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;


/**
 * Utility class for adding various types of cells to a PDF table.
 * This class provides methods to add detail cells, header cells, data cells,
 * subtotal cells, and total cells to a PdfPTable.
 *
 * @author Linda J Sibeko
 * @date 2024/07/31
 */
public class PDFTableUtil
{

    /**
     * Creates a new cell with the specified properties.
     *
     * @param text the text to be displayed in the cell
     * @param font the font to be used for the text
     * @param padding the padding inside the cell
     * @param border the border style of the cell
     * @param borderColor the border color of the cell
     * @param bgColor the background color of the cell
     * @param hAlign the horizontal alignment of the cell
     * @param colspan the number of columns the cell spans
     * @return a configured PdfPCell instance
     */
    private static PdfPCell createCell(String text, Font font, float padding, int border, BaseColor borderColor, BaseColor bgColor, int hAlign, int colspan) {
        PdfPCell cell = new PdfPCell(new Phrase(text, font));
        cell.setPadding(padding);
        cell.setBorder(border);
        cell.setBorderColor(borderColor);
        cell.setBackgroundColor(bgColor);
        cell.setHorizontalAlignment(hAlign);
        cell.setColspan(colspan);
        return cell;
    }

    /**
     * Adds a detail cell to the specified table with a label and its corresponding value.
     *
     * @param table the table to which the cell is to be added
     * @param label the label text for the cell
     * @param value the value text for the cell
     */
    public static void addDetailsCell(PdfPTable table, String label, String value) {
        table.addCell(createCell(label, FontUtil.poppinsBySize(7, false, 1), 3.5f, Rectangle.NO_BORDER, null, null, Element.ALIGN_LEFT, 1));
        table.addCell(createCell(value, FontUtil.poppinsBySize(7, false, 1), 3.5f, Rectangle.NO_BORDER, null, null, Element.ALIGN_LEFT, 1));
    }

    public static void addDetailsCell(PdfPTable table, String label, String value,int colspan) {
        table.addCell(createCell(label, FontUtil.poppinsBySize(7, false, 1), 3.5f, Rectangle.NO_BORDER, null, null, Element.ALIGN_LEFT, 1));
        table.addCell(createCell(value, FontUtil.poppinsBySize(7, false, 1), 3.5f, Rectangle.NO_BORDER, null, null, Element.ALIGN_LEFT, colspan));
    }

    /**
     * Adds a header cell to the specified table with the given text and styling.
     *
     * @param table the table to which the cell is to be added
     * @param text the text to be displayed in the header cell
     * @param font the font to be used for the text
     * @param headerColor the background color of the header cell
     * @param borderColor the border color of the header cell
     */
    public static void addHeaderCell(PdfPTable table, String text, Font font, BaseColor headerColor, BaseColor borderColor) {
        table.addCell(createCell(text.toUpperCase(), font, 10, Rectangle.BOX, borderColor, headerColor, Element.ALIGN_CENTER, 1));
    }

    /**
     * Adds a header cell to the specified table with the given text and styling.
     *
     * @param table the table to which the cell is to be added
     * @param text the text to be displayed in the header cell
     * @param font the font to be used for the text
     * @param headerColor the background color of the header cell
     * @param borderColor the border color of the header cell
     * @param colspan the number of rows it should take
     */
    public static void addHeaderCell(PdfPTable table, String text, Font font, BaseColor headerColor, BaseColor borderColor,int colspan) {
        table.addCell(createCell(text.toUpperCase(), font, 10, Rectangle.BOX, borderColor, headerColor, Element.ALIGN_CENTER, colspan));
    }

    /**
     * Adds a data cell to the specified table with the given text and styling.
     *
     * @param table the table to which the cell is to be added
     * @param text the text to be displayed in the data cell
     * @param font the font to be used for the text
     * @param borderColor the border color of the data cell
     */
    public static void addDataCell(PdfPTable table, String text, Font font, BaseColor borderColor) {
        table.addCell(createCell(text, font, 10, Rectangle.BOX, borderColor, null, Element.ALIGN_CENTER, 1));
    }

    /**
     * Adds a data cell to the specified table with the given text and styling.
     *
     * @param table the table to which the cell is to be added
     * @param text the text to be displayed in the data cell
     * @param font the font to be used for the text
     * @param borderColor the border color of the data cell
     * @param colspan the number of rows it should take
     */
    public static void addDataCell(PdfPTable table, String text, Font font, BaseColor borderColor, int colspan) {
        table.addCell(createCell(text, font, 10, Rectangle.BOX, borderColor, null, Element.ALIGN_CENTER, colspan));
    }

    /**
     * Adds a subtotal cell to the specified table with the given label, amount, and styling.
     *
     * @param table the table to which the cell is to be added
     * @param label the label text for the subtotal
     * @param amount the amount for the subtotal
     * @param borderColor the border color of the subtotal cell
     * @param bgColor the background color of the subtotal cell
     * @param colspan the number of columns the empty cell spans
     */
    public static void addSubTotalsCell(PdfPTable table, String label, double amount, BaseColor borderColor, BaseColor bgColor, int colspan) {
        table.addCell(createCell("", null, 10, Rectangle.NO_BORDER, borderColor, null, Element.ALIGN_CENTER, colspan));
        table.addCell(createCell(label, FontUtil.poppinsBySize(9, false, 3), 10, Rectangle.BOX, borderColor, null, Element.ALIGN_LEFT, 1));
        table.addCell(createCell(String.format("R %.2f", amount), FontUtil.poppinsBySize(9, false, 3), 10, Rectangle.BOX, borderColor, bgColor, Element.ALIGN_LEFT, 1));
    }

    /**
     * Adds a total cell to the specified table with the given label, amount, and styling.
     *
     * @param table the table to which the cell is to be added
     * @param label the label text for the total
     * @param amount the amount for the total
     * @param borderColor the border color of the total cell
     * @param bgColor the background color of the total cell
     * @param colspan the number of columns the empty cell spans
     */
    public static void addTotalCell(PdfPTable table, String label, double amount, BaseColor borderColor, BaseColor bgColor, int colspan) {
        table.addCell(createCell("", null, 10, Rectangle.NO_BORDER, borderColor, null, Element.ALIGN_CENTER, colspan));
        table.addCell(createCell(label, FontUtil.poppinsBySize(12, false, 3), 10, Rectangle.BOX, borderColor, null, Element.ALIGN_LEFT, 1));
        table.addCell(createCell(String.format("R %.2f", amount), FontUtil.poppinsBySize(12, false, 3), 10, Rectangle.BOX, borderColor, bgColor, Element.ALIGN_LEFT, 1));
    }

}
