package za.co.droppa.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import za.co.droppa.dao.BookingDAO;
import za.co.droppa.dao.ContactDAO;
import za.co.droppa.dao.PersonDAO;
import za.co.droppa.dao.RetailDAO;
import za.co.droppa.dto.*;
import za.co.droppa.model.Booking;
import za.co.droppa.model.Contact;
import za.co.droppa.model.Person;
import za.co.droppa.model.Retail;
import za.co.droppa.service.pdf.PDFDocumentService;


@RestController
@EnableWebMvc
public class PDFDocumentController {

    Logger logger = LoggerFactory.getLogger(PDFDocumentController.class);

    RetailDAO retailDAO;

    PDFDocumentService documentService;
    ContactDAO contactDAO;

    BookingDAO bookingDAO;

    PersonDAO personDAO;

    public PDFDocumentController(RetailDAO retailDAO, ContactDAO contactDAO, BookingDAO bookingDAO, PersonDAO personDAO, PDFDocumentService documentService){
        this.retailDAO = retailDAO;
        this.contactDAO = contactDAO;
        this.bookingDAO = bookingDAO;
        this.personDAO = personDAO;
        this.documentService = documentService;
    }

    @PostMapping(path = "/waybill")
    public ResponseEntity<DownloadFileDTO> createWaybill(@RequestBody WaybillCreateDTO createDTO){
        Booking bk=bookingDAO.findByTrackNo(createDTO.getWaybillNo());
        Contact pickup = contactDAO.findById(bk.getPickUpOid());
        Contact dropoff = contactDAO.findById(bk.getDropOffOid());
        Person person = personDAO.findById(bk.getUserOid());

        DownloadFileDTO dto = documentService.generateWaybill(bk,null,pickup,dropoff,person,true,bk.getTrackNo());
        Retail retail = retailDAO.findById("5b9b607528743436cc248fc2");
        logger.warn(retail.getName());
        return ResponseEntity.ok(dto);
    }

    @PostMapping(path = "/quotation")
    public ResponseEntity<?> createQuotation(@RequestBody QuoteCreateDTO dto){
        try {
            DownloadFileDTO response = documentService.generateQuotationDocument(dto);
            return ResponseEntity.ok(response);
        }
        catch (Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("failed to create quotation document: "+e.getMessage());
        }

    }
    @PostMapping(path = "/remittance/notice")
    public ResponseEntity<?> createRemittanceNotice(@RequestBody RemittanceNoticeCreateDTO dto){
        try {
            DownloadFileDTO response = documentService.generateRemittanceNotice(dto.getBooking(),dto.getPerson(),dto.getBankAccount());
            return ResponseEntity.ok(response);
        }
        catch (Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("failed to create quotation document: "+e.getMessage());
        }

    }

    @PostMapping(path = "/delivery/note")
    public ResponseEntity<?> createRemittanceNotice(@RequestBody DeliveryNoteCreateDTO dto){
        try {
            DownloadFileDTO response = documentService.generateDeliveryNote(dto.getBillDTO(),dto.getBucketBooking());
            return ResponseEntity.ok(response);
        }
        catch (Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("failed to create quotation document: "+e.getMessage());
        }

    }

    @PostMapping(path = "/campos/shipment")
    public ResponseEntity<?> createCamposShipment(@RequestBody CampostCreateDTO dto){
        try {
            DownloadFileDTO response = documentService.generateCamposRequestShipment(
                    dto.getBooking(),
                    dto.getPerson(),
                    dto.getContact(),
                    dto.getDropOff(),
                    dto.getCamposRateResponse());
            return ResponseEntity.ok(response);
        }
        catch (Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("failed to create quotation document: "+e.getMessage());
        }

    }


}
