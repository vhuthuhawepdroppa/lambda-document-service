package za.co.droppa.service.pdf.renderers;

import com.itextpdf.text.Element;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.*;
import za.co.droppa.util.FontUtil;


/**
 * @author Vhuthuhawe
 * @date 2024/08/03
 */
public class SquareCheckBoxCellRenderer implements PdfPCellEvent {

    private final String text;

    public SquareCheckBoxCellRenderer(String text) {
        this.text = text;
    }


    /**
     * Customizes the layout of a PdfPCell by drawing a rectangle and adding text within it.
     *
     * @param cell the PdfPCell being customized
     * @param position the position and size of the cell
     * @param canvases an array of PdfContentByte objects for drawing on the page
     */
    public void cellLayout(PdfPCell cell, Rectangle position, PdfContentByte[] canvases) {
        PdfContentByte canvas = canvases[PdfPTable.TEXTCANVAS];
        float x = position.getLeft() + 2;
        float y = (position.getBottom() + (position.getHeight()) / 2) - 7;
        float size = 15;

        canvas.rectangle(x, y, size, size);
        canvas.stroke();

        Phrase phrase = new Phrase(text, FontUtil.poppinsBySize(7,false,2));
        ColumnText.showTextAligned(canvas, Element.ALIGN_LEFT, phrase, x + size + 5, (y + size / 2) - 4, 0);
    }
}
