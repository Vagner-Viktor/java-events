package events;

import java.time.format.DateTimeFormatter;

public class Constants {
    public static final String DATA_PATTERN = "yyyy-MM-dd HH:mm:ss";
    public static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern(DATA_PATTERN);
    public static final Double DEFAULT_LOCATION_RADIUS = 10D;
    public static final Long DEFAULT_LOCATION_TYPE_ID = 1L;
}
