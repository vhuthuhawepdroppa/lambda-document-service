package za.co.droppa.model;


import lombok.Data;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document(collection = "droppa.contact")
public class Contact {

	@Id
	private ObjectId id;

	private String firstName;

	private String lastName;

	private String phone;

	private String email;

	private String toEmail;

	private String companyName;

	private String complex;

	private String unitNo;

	private String signature;

	private String pickCode;

	private String dropOffCode;

}
