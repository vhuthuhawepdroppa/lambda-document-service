package za.co.droppa.util;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

/**
 * @author Nicolas
 * @date 2021/08/18
 */
public final class DateUtil {

    private static final DateTimeFormatter tf = DateTimeFormatter.ofPattern("HH:mm");
    private static final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public static String format(LocalTime value) {
        Objects.requireNonNull(value);
        return value.format(tf);
    }

    public static String toDate (LocalDateTime dateTime) {
        return dateFormatter.format(dateTime);
    }

}
