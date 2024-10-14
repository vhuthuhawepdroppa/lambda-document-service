package za.co.droppa.service.pdf;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import za.co.droppa.dao.PersonDAO;
import za.co.droppa.dao.RetailDAO;
import za.co.droppa.dao.WhitelistingDAO;
import za.co.droppa.dto.BillDTO;
import za.co.droppa.dto.DownloadFileDTO;
import za.co.droppa.dto.QuoteCreateDTO;
import za.co.droppa.dto.campos.CamposRateResponse;
import za.co.droppa.dto.sequence.SequenceResponse;
import za.co.droppa.model.*;
import za.co.droppa.model.whitelisting.Whitelisting;
import za.co.droppa.service.aws.S3Service;
import za.co.droppa.service.pdf.generate.*;
import za.co.droppa.service.sequence.SequenceService;

import java.io.ByteArrayInputStream;
import java.util.HashMap;
import java.util.Map;

@Service
@AllArgsConstructor
public class PDFDocumentService {

    RetailDAO retailDAO;
    PersonDAO personDAO;
    WhitelistingDAO whitelistingDAO;
    S3Service s3Service;

    public DownloadFileDTO generateQuotationDocument(QuoteCreateDTO dto) throws Exception {
        SequenceService sequenceService = new SequenceService();
        SequenceResponse response = sequenceService.sendQuoteSequenceRequest();
        long code = response.getCode();
        String transId = "QOT" + code;

        System.out.println("transactionn id" + transId);

        AbstractPdfDocument quotationDocument = new DroppaQuotation("", "", "QUOTATION", "");

        Map<String, Object> params = new HashMap<>();
        params.put("transId", transId);
        params.put("dto", dto);

        ByteArrayInputStream stream = quotationDocument.buildDocument(params);

        System.out.println("saving document");
        String url = s3Service.uploadPdfToS3("droppa-document", transId + ".pdf", stream);

        return new DownloadFileDTO(url);
    }

    /**
     * Generates a waybill PDF document based on the provided booking and contact information.
     * The method creates a `WaybillPdfDocument` instance and populates it with the given parameters,
     * including booking details, parcel information, pickup and drop-off contacts, person details,
     * and whether a barcode should be included. The generated PDF is returned as a `ByteArrayInputStream`.
     *
     * @param booking The booking information to include in the waybill.
     * @param parcels An object containing the parcel details.
     * @param contactPickUp The contact information for the pickup location.
     * @param contactDropOff The contact information for the drop-off location.
     * @param person The person associated with the booking or delivery.
     * @param barcode A boolean indicating whether to include a barcode in the waybill.
     * @param waybillNo The unique waybill number to include in the document.
     * @return A `ByteArrayInputStream` containing the generated waybill PDF document.
     */
    public DownloadFileDTO generateWaybill(Booking booking, Object parcels, Contact contactPickUp, Contact contactDropOff, Person person, boolean barcode, String waybillNo) {
        Map<String,Object> params = new HashMap<>();

        params.put("person", person);
        params.put("contactPickUp", contactPickUp);
        params.put("contactDropOff", contactDropOff);
        params.put("barcode", barcode);
        params.put("waybillNo", waybillNo);
        params.put("parcels", parcels);
        params.put("booking",booking);

        if (booking.getRetailId() != null)
            handleWhiteListUser(booking.getRetailId(), params);
        else
            System.out.printf("Retail id is null for %s%n", booking.getTrackNo());

        try {
            String heading = "WAYBILL";
            String base64 = (String) params.get("base64String");
            String whitelistName = (String) params.get("whitelistName");
            AbstractPdfDocument doc = new Waybill(base64,whitelistName,heading,waybillNo);

            ByteArrayInputStream stream = doc.buildDocument(params);

            System.out.println("saving document");

            String waybillPath = "waybills/"+booking.getTrackNo()+".pdf";
            String url = s3Service.uploadPdfToS3("droppa-document", waybillPath,stream);
            return new DownloadFileDTO(url);
        }
        catch (Exception e) {
            System.out.println(e.getMessage());
        }


        return null;
    }

    /**
     * Generates a remittance notice PDF document based on the provided parameters.
     * The method creates a `RemittanceNoticePdfDocument` instance and populates it with the given parameters:
     * - `booking`: The booking details to be included in the remittance notice.
     * - `person`: The person associated with the remittance notice.
     * - `bank`: The bank account details for the remittance notice.
     *
     * @param booking The booking details to include in the remittance notice.
     * @param person The person associated with the remittance notice.
     * @param bank The bank account details to include in the remittance notice.
     * @return A `ByteArrayInputStream` containing the generated remittance notice PDF document.
     */
    public DownloadFileDTO generateRemittanceNotice(Booking booking, Person person, BankAccount bank) {

        Map<String,Object> params = new HashMap<>();

        params.put("booking", booking);
        params.put("person", person);
        params.put("bank", bank);

        try {
            AbstractPdfDocument remittanceNotice = new RemittanceNotice("","","REMITTANCE NOTICE","");

            System.out.println("saving document");
            ByteArrayInputStream stream = remittanceNotice.buildDocument(params);
            String url = s3Service.uploadPdfToS3("droppa-document", booking.getTrackNo()+"_REMITTANCE"+".pdf",stream);
            return new DownloadFileDTO(url);
        }
        catch (Exception e) {
            System.out.println(e.getMessage());
        }

        return null;
    }

    /**
     * Generates a delivery note PDF document based on the provided BillDTO.
     * The method creates a `DeliveryNotePdfDocument` instance and populates it with the given `BillDTO` parameter.
     * The generated PDF is returned as a `ByteArrayInputStream`.
     *
     * @param billDTO The data transfer object containing the details to include in the delivery note.
     * @return A `ByteArrayInputStream` containing the generated delivery note PDF document.
     */
    public DownloadFileDTO generateDeliveryNote(BillDTO billDTO, BucketBooking bucketBooking) {

        Map<String,Object> params = new HashMap<>();
        params.put("billDTO", billDTO);

        if(bucketBooking.getRetail()!=null)
            handleWhiteListUser(bucketBooking.getRetail().getId().toString(),params);
        else
            System.out.printf("Retail id null for booking: %s%n",bucketBooking.getTrackNo());

        try {
            String heading = "DELIVERY NOTE / POD";
            String base64 = (String) params.get("base64String");
            String whitelistName = (String) params.get("whitelistName");
            AbstractPdfDocument deliveryNote = new DeliveryNote(base64,whitelistName,heading,null);

            ByteArrayInputStream stream = deliveryNote.buildDocument(params);

            String url = s3Service.uploadPdfToS3("droppa-document", bucketBooking+".pdf",stream);
            return new DownloadFileDTO(url);
        }
        catch (Exception e) {
            System.out.println(e.getMessage());
        }

        return null;
    }

    /**
     * Generates a tax invoice PDF document based on the provided parameters.
     * The method creates a `TaxInvoicePdfDocument` instance and populates it with the given parameters:
     * - `invoiceType`: The type of invoice to generate.
     * - `infoObj`: An object containing the invoice details.
     * - `vatNo`: The VAT number to be included in the invoice.
     * - `bank`: The bank account details to be included in the invoice.
     *
     * @param invoiceType The type of invoice to generate.
     * @param infoObj An object containing the details for the tax invoice.
     * @param vatNo The VAT number to include in the invoice.
     * @param bank The bank account details to include in the invoice.
     * @return A `ByteArrayInputStream` containing the generated tax invoice PDF document.
     */
    public ByteArrayInputStream generateTaxInvoice(Object invoiceType, Object infoObj, String vatNo, BankAccount bank) {

        Map<String,Object> params = new HashMap<>();

        params.put("invoiceType", invoiceType);
        params.put("infoObj", infoObj);
        params.put("vatNo", vatNo);
        params.put("bank", bank);


        if (invoiceType instanceof Booking) {
            Booking booking = (Booking) invoiceType;

            if(booking.getRetailId()!=null)
                handleWhiteListUser(booking.getRetailId(), params);
            else
                System.out.printf("Retail id is null for %s%n", booking.getTrackNo());
        }


        try {
            String heading = "TAX INVOICE";
            String base64 = (String) params.get("base64String");
            String whitelistName = (String) params.get("whitelistName");

            AbstractPdfDocument taxInvoice = new TaxInvoice(base64,whitelistName,heading,null);
            return taxInvoice.buildDocument(params);
        }
        catch (Exception e) {
            System.out.println(e.getMessage());
        }

        return null;
    }



    public DownloadFileDTO generateCamposRequestShipment(Booking booking, Person person,
                                                              Contact contact, Contact dropOff,
                                                              CamposRateResponse camposRateResponse) {
        Map<String,Object> params = new HashMap<>();
        params.put("booking", booking);
        params.put("person", person);
        params.put("pickUp-contact", contact);
        params.put("dropOff-contact", dropOff);
        params.put("rate-response", camposRateResponse);

        String heading = "QUOTATION/PRO FORM A INVOICE";
        try {
            AbstractPdfDocument shipmentRequest = new CamposRequestShipment("","",heading,"");

            ByteArrayInputStream stream = shipmentRequest.buildDocument(params);
            String url = s3Service.uploadPdfToS3("droppa-document", booking+".pdf",stream);
            return new DownloadFileDTO(url);
        } catch (Exception e){
            System.out.println(e.getMessage());
        }

        return null;
    }



    private void handleWhiteListUser(String retailID, Map<String, Object> params) {

        Retail retail = retailDAO.findById(retailID);
        if (retail == null) {
            Person person = personDAO.findById(retailID);
            if (person != null) {
                retail = retailDAO.findById(person.getRetailId());
            }
        }

        if (retail != null) {
            applyWhitelistingLogo(retail.getId().toString(), params);

            if (retail.getParentRetailId() != null) {
                System.out.println("Using parent information");
                applyWhitelistingLogo(retail.getParentRetailId(), params);
            }
        } else {
            System.out.println("Retail or person not found.");
        }


    }


    private void applyWhitelistingLogo(String retailId, Map<String, Object> params) {
        Whitelisting whitelisting = whitelistingDAO.findById(retailId);
        if (whitelisting != null && whitelisting.getLogo() != null) {
            params.put("base64String", whitelisting.getLogo());
            params.put("whitelistName", whitelisting.getName());
            System.out.println("Business has logo");
        } else {
            System.out.println("Whitelisting not found or logo is missing.");
        }

    }
}
