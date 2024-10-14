package za.co.droppa.util;

import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Font;
import com.itextpdf.text.pdf.BaseFont;
import za.co.droppa.service.aws.S3Service;

import java.io.IOException;
import java.io.InputStream;

public class FontUtil {

    public static final int small = 7;

    private static final S3Service s3Service = new S3Service();

    private static BaseFont poppinsLight = null;
    private static BaseFont poppinsRegular = null;
    private static BaseFont poppinsMedium = null;
    private static BaseFont poppinsSemiBold = null;

    /**
     * Creates a Font object with the specified size, boldness, and type using the Poppins font family.
     *
     * @param fontSize the size of the font.
     * @param bold whether the font should be bold.
     * @param type the type of the font, where:
     *             1 - Poppins Light
     *             2 - Poppins Medium
     *             other values - Poppins Regular
     * @return a Font object with the specified size, boldness, and type.
     */
    public static Font poppinsBySize(int fontSize, boolean bold, int type) {
        return switch (type) {
            case 1 -> new Font(poppinsLight(), fontSize, bold ? Font.BOLD : Font.NORMAL);
            case 2 -> new Font(poppinsMedium(), fontSize, bold ? Font.BOLD : Font.NORMAL);
            case 3 -> new Font(poppinsSemiBold(), fontSize, bold ? Font.BOLD : Font.NORMAL);
            default -> new Font(poppinsRegular(), fontSize, bold ? Font.BOLD : Font.NORMAL);
        };
    }

    /**
     * Retrieves and creates a BaseFont instance for the "Poppins-Regular" font.
     *
     * @return a BaseFont object representing the "Poppins-Regular" font, or null if an error occurs.
     */

    public static BaseFont poppinsRegular() {
        String fontPath = "font/Poppins-Regular.ttf";
        checkFontLoaded(poppinsRegular, fontPath,2);
        return poppinsRegular;
    }

    /**
     * Retrieves and creates a BaseFont instance for the "Poppins-Light" font.
     *
     * @return a BaseFont object representing the "Poppins-Light" font, or null if an error occurs.
     * @throws DocumentException if there is an error creating the font.
     * @throws NullPointerException if the font resource cannot be found.
     * @throws IOException if an I/O error occurs while reading the font stream.
     */

    public static BaseFont poppinsLight() {
        String fontPath = "font/Poppins-Light.ttf";
        checkFontLoaded(poppinsLight,fontPath,1);
        return poppinsLight;
    }

    /**
     * Retrieves and creates a BaseFont instance for the "Poppins-Medium" font.
     *
     * @return a BaseFont object representing the "Poppins-Medium" font, or null if an error occurs.
     */
    public static BaseFont poppinsMedium() {
        String fontPath = "font/Poppins-Medium.ttf";
        checkFontLoaded(poppinsMedium,fontPath,3);
        return poppinsMedium;
    }


    /**
     * Retrieves and creates a BaseFont instance for the "Poppins-SemiBold" font.
     *
     * @return a BaseFont object representing the "Poppins-Medium" font, or null if an error occurs.
     */
    public static BaseFont poppinsSemiBold() {
        String fontPath = "font/Poppins-SemiBold.ttf";
        checkFontLoaded(poppinsSemiBold,fontPath,4);
        return poppinsSemiBold;
    }

    private static void checkFontLoaded(BaseFont font, String fontPath, int type) {
        if (font == null) {
            InputStream fontStream = s3Service.loadFileFromS3("droppa-assets1", fontPath);
            try {
                switch (type) {
                    case 1:
                        poppinsLight = BaseFont.createFont("Poppins-Light.ttf", BaseFont.IDENTITY_H, BaseFont.EMBEDDED, true, FileUtil.readAllBytes(fontStream), null);
                        break;
                    case 2:
                        poppinsRegular = BaseFont.createFont("Poppins-Regular.ttf", BaseFont.IDENTITY_H, BaseFont.EMBEDDED, true, FileUtil.readAllBytes(fontStream), null);
                        break;
                    case 3:
                        poppinsMedium = BaseFont.createFont("Poppins-Medium.ttf", BaseFont.IDENTITY_H, BaseFont.EMBEDDED, true, FileUtil.readAllBytes(fontStream), null);
                        break;
                    case 4:
                        poppinsSemiBold = BaseFont.createFont("Poppins-SemiBold.ttf", BaseFont.IDENTITY_H, BaseFont.EMBEDDED, true, FileUtil.readAllBytes(fontStream), null);
                        break;
                    default:
                        System.out.println("no type matches");

                }
            } catch (IOException | DocumentException io) {
                System.out.println("unable to get medium font");
            }
        }
    }

}
