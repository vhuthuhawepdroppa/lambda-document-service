package za.co.droppa;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;
import za.co.droppa.controller.PDFDocumentController;


@SpringBootApplication
// We use direct @Import instead of @ComponentScan to speed up cold starts
@Import({ PDFDocumentController.class })
public class Application {
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}