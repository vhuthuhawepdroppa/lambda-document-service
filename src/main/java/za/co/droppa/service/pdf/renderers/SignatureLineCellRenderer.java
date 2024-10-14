package za.co.droppa.service.pdf.renderers;

import com.itextpdf.text.Image;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPCellEvent;
import com.itextpdf.text.pdf.PdfPTable;
import za.co.droppa.model.Contact;
import za.co.droppa.util.DateUtil;
import za.co.droppa.util.FontUtil;

import java.time.LocalDateTime;


/**
 * @author Vhuthuhawe
 * @date 2024/08/03
 */
public class SignatureLineCellRenderer implements PdfPCellEvent {


    private final float x1;
    private final float x2;
    private final float y;
    private final String type;
    private final Contact contact;

    public SignatureLineCellRenderer(float x1, float x2, float y, String type, Contact contact) {
        this.x1 = x1;
        this.x2 = x2;
        this.y = y;
        this.type = type;
        this.contact = contact;
    }

    /**
     * Customizes the layout of a cell by drawing a line and adding a signature and date if they exist.
     *
     * @param cell      The PdfPCell that is being customized.
     * @param position  The Rectangle representing the cell's position.
     * @param canvases  An array of PdfContentByte objects used for drawing on different layers of the cell.
     */
    public void cellLayout(PdfPCell cell, Rectangle position, PdfContentByte[] canvases) {
        PdfContentByte canvas = canvases[PdfPTable.LINECANVAS];

        float x1 = position.getLeft() + this.x1;
        float x2 = position.getRight() - this.x2;
        float y = (position.getBottom() + (position.getHeight() / 2)) - this.y;

        canvas.moveTo(x1, y);
        canvas.lineTo(x2, y);
        canvas.stroke();

        try {


            if(contact.getSignature()!=null) {
                addSignatureIfExists(canvas, contact);
                addDateIfExists(canvas, contact, position);
            }


        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }


    /**
     * Adds a signature image to the PDF canvas if the type is "signature".
     *
     * @param canvas   The PdfContentByte canvas on which the image is drawn.
     * @param contact  The Contact object containing the signature image path or URL.
     */
    private void addSignatureIfExists(PdfContentByte canvas, Contact contact) {

        try {
            String signature = contact.getSignature();
            if (this.type.equals("signature")) {
                Image signatureImage = Image.getInstance(signature);
                signatureImage.scaleToFit(35, 15);
                float imageX = (x1 + x2) / 2 - (signatureImage.getScaledWidth() / 2);
                float imageY = y + 2;
                canvas.addImage(signatureImage, signatureImage.getScaledWidth(), 0, 0, signatureImage.getScaledHeight(), imageX, imageY);
            }
        }
        catch (Exception exception){
            System.out.println(exception.getMessage());
        }
    }


    /**
     * Adds a date to the specified position on a PDF canvas if the type is "date".
     *
     * @param canvas   The PdfContentByte canvas on which the text is drawn.
     * @param contact  The Contact object containing the date to be added.
     * @param position The Rectangle specifying the position where the date should be drawn.
     */
    private void addDateIfExists(PdfContentByte canvas, Contact contact, Rectangle position) {
        if(this.type.equals("date")){
            canvas.beginText();
            canvas.setFontAndSize(FontUtil.poppinsLight(),7);
            canvas.setTextMatrix(position.getLeft()+35, position.getBottom()+15);
            canvas.showText(DateUtil.toDate(LocalDateTime.now()));
            canvas.endText();
        }
    }
}
