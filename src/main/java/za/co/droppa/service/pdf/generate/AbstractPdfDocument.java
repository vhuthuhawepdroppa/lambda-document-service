package za.co.droppa.service.pdf.generate;


import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.pdf.PdfWriter;
import za.co.droppa.service.aws.S3Service;
import za.co.droppa.service.pdf.events.PageFooterEventHandler;
import za.co.droppa.util.PDFUtil;

import java.io.*;
import java.text.DecimalFormat;
import java.util.Base64;
import java.util.Map;

/**
 * @author Vhuthuhawe
 * @date 2024/08/03
 */
public abstract class AbstractPdfDocument {

    protected final DecimalFormat money = new DecimalFormat("####0.00");
    S3Service s3Service = new S3Service();

    Document document;
    protected PdfWriter writer;
    ByteArrayOutputStream out;

    protected String base64String;
    protected String name;
    protected String heading;
    protected String waybill;

    /**
     * Constructor for creating an AbstractPdfDocument.
     *
     * Initializes a new PDF document and a corresponding output stream.
     * Sets up a PdfWriter instance to write the PDF content to the output stream.
     * Also sets a custom page event handler to add a footer to each page.
     *
     * @throws DocumentException if there is an error creating the PdfWriter instance or setting up the document.
     */
    public AbstractPdfDocument() throws DocumentException {
        this.document = new Document();
        this.out = new ByteArrayOutputStream();
        this.writer = PdfWriter.getInstance(document,out);

        writer.setPageEvent(new PageFooterEventHandler(0));
    }

    /**
     * Constructor for creating an AbstractPdfDocument with specific content details.
     *
     * Initializes a new PDF document and a corresponding output stream.
     * Sets up a PdfWriter instance to write the PDF content to the output stream.
     * Stores provided content details like base64 encoded string, name, heading, and waybill.
     * Also sets a custom page event handler to add a footer to each page.
     *
     * @param base64String a base64 encoded string representing additional content or data for the PDF.
     * @param name the name to be included in the PDF.
     * @param heading the heading or title to be used in the PDF.
     * @param waybill the waybill number to be included in the PDF.
     * @throws DocumentException if there is an error creating the PdfWriter instance or setting up the document.
     */
    public AbstractPdfDocument(String base64String,String name,String heading, String waybill) throws DocumentException {
        this.document = new Document();
        this.out = new ByteArrayOutputStream();
        this.writer = PdfWriter.getInstance(document,out);

        this.base64String=base64String;
        this.name=name;
        this.heading=heading;
        this.waybill=waybill;

        writer.setPageEvent(new PageFooterEventHandler(0));
    }

    /**
     * Builds a PDF document based on the provided parameters.
     *
     * @param params a Map containing key-value pairs of parameters used to build the document.
     * @return a ByteArrayInputStream representing the PDF document.
     * @throws DocumentException if there is an error during the document creation process.
     */
    public abstract ByteArrayInputStream buildDocument(Map<String,Object> params) throws DocumentException;

    /**
     * Opens the PDF document for writing content.
     *
     * This method opens the document, making it ready to accept content.
     * After opening the document, it adds a header to the document.
     */
    public void open() {
        document.open();

        addHeader();
    }

    /**
     * Adds a header to the PDF document based on the type of document.
     *
     * If the document's heading is "WAYBILL", this method adds a header with a barcode using the provided
     * waybill number and base64 encoded string. Otherwise, it adds a basic header with the specified heading.
     *
     */
    private void addHeader() {
        if(heading.equals("WAYBILL"))
            PDFUtil.addHeaderWithBarcode(document,writer,heading,waybill,base64String);
        else if(heading.equals("QUOTATION/PRO FORM A INVOICE")){

            try {
                String base64 = s3Service.loadImageBase64("droppa-assets1","images/camposLogo.png");
                PDFUtil.addLogo(document,base64);
            } catch (Exception exception){
                exception.printStackTrace();
                System.out.println("Failed to load image");
            }



        }else if(heading.equals("Commercial Invoice")) {
            PDFUtil.addBasicHeader(document,"Commercial Invoice",false);
            return;
        }
        else PDFUtil.addBasicHeader(document,heading,base64String);

    }

    private static String encodeFileToBase64(String filePath) {
        try {
            File file = new File(filePath);
            FileInputStream fis = new FileInputStream(file);
            byte[] fileBytes = new byte[(int) file.length()];
            fis.read(fileBytes);
            fis.close();
            return Base64.getEncoder().encodeToString(fileBytes);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Closes the PDF document.
     * This method finalizes the document, preventing any further content from being added.
     * It should be called after all content has been added to the document.
     */
    public void close() {
        document.close();
    }

}
