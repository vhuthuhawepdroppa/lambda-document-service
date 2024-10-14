package za.co.droppa.service.pdf.events;

import com.itextpdf.text.Document;
import com.itextpdf.text.Image;
import com.itextpdf.text.pdf.PdfPageEventHelper;
import com.itextpdf.text.pdf.PdfWriter;
import za.co.droppa.service.aws.S3Service;


/**
 * @author Vhuthuhawe
 * @date 2024/07/03
 */
public class PageFooterEventHandler extends PdfPageEventHelper {

    private final int type;

    public PageFooterEventHandler(int type){
        this.type = type;
    }


    /**
     * Adds a footer image to the document at the start of each page.
     *
     * @param writer the PdfWriter instance used to write the document
     * @param document the Document to which the footer image will be added
     */
    @Override
    public void onStartPage(PdfWriter writer, Document document) {
        S3Service s3Service = new S3Service();
        try {
            Image footerImage = Image.getInstance(s3Service.loadImage("droppa-assets1","images/LETTERHEAD_FOOTER.png"));
            footerImage.scaleToFit(document.getPageSize().getWidth() - 55, footerImage.getHeight());

            float xPosition = ((document.getPageSize().getWidth() - footerImage.getScaledWidth()) / 2) - 5;
            footerImage.setAbsolutePosition(xPosition, 20);

            document.add(footerImage);

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
