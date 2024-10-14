package za.co.droppa.service.aws;

import com.itextpdf.text.Image;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.auth.credentials.EnvironmentVariableCredentialsProvider;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.Base64;

@Service
public class S3Service {

    private final String region = "EU_WEST_1";

    private final S3Client s3 = S3Client.builder()
            .region(Region.EU_WEST_1)
            .credentialsProvider(EnvironmentVariableCredentialsProvider.create())
            .build();

    public String uploadPdfToS3(String bucketName, String objectKey, ByteArrayInputStream baos) {
        byte[] pdfData = baos.readAllBytes();

        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(objectKey)
                .build();

        s3.putObject(putObjectRequest, RequestBody.fromBytes(pdfData));

        return String.format("https://%s.s3.%s.amazonaws.com/%s", bucketName, region.toLowerCase().replace("_", "-"), objectKey);
    }


    public Image loadImage(String bucketName, String objectKey) {


        try {
            GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                    .bucket(bucketName)
                    .key(objectKey)
                    .build();

            ResponseInputStream<GetObjectResponse> inputStream = s3.getObject(getObjectRequest);

            Image footerImage = Image.getInstance(inputStream.readAllBytes());

            inputStream.close();
            return footerImage;


        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public String loadImageBase64(String bucketName, String objectKey) {
        try {
            GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                    .bucket(bucketName)
                    .key(objectKey)
                    .build();

            ResponseInputStream<GetObjectResponse> inputStream = s3.getObject(getObjectRequest);

            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                byteArrayOutputStream.write(buffer, 0, bytesRead);
            }

            byte[] imageBytes = byteArrayOutputStream.toByteArray();
            inputStream.close();

            return Base64.getEncoder().encodeToString(imageBytes);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public InputStream loadFileFromS3(String bucketName, String objectKey) {
        try {
            GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                    .bucket(bucketName)
                    .key(objectKey)
                    .build();

            System.out.println("loading font from s3");
            return s3.getObject(getObjectRequest);

        } catch (Exception e) {
            System.out.println("loading font from s3");
            e.printStackTrace();
        }

        return null;
    }
}


