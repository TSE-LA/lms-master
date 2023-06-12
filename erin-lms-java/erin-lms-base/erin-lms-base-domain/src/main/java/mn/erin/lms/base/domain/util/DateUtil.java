package mn.erin.lms.base.domain.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;

/**
 * @author Bat-Erdene Tsogoo.
 */
public final class DateUtil
{
  private static final String BASIC_DATE_FORMAT = "yyyy-MM-dd";
  private static final String ZONE_ID = "Asia/Ulaanbaatar";
  private static final DateTimeFormatter BASIC_FORMATTER = DateTimeFormatter.ofPattern(BASIC_DATE_FORMAT);
  private static final DateTimeFormatter ID_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMdd");

  private DateUtil()
  {
  }

  public static String getCurrentDateAsString()
  {
    return LocalDate.now().format(BASIC_FORMATTER);
  }

  public static String generateIdBasedOnDate()
  {
    return LocalDate.now().format(ID_FORMATTER);
  }

  public static String convertDate(LocalDate date)
  {
    return date.format(ID_FORMATTER);
  }

  public static Date combine(Date date, String time)
  {
    LocalDate localDate = dateToLocalDate(date);
    LocalTime localTime = LocalTime.parse(time);
    return localDateTimeToDate(localDate.atTime(localTime));
  }

  public static Date localDateTimeToDate(LocalDateTime localDateTime)
  {
    return Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());
  }

  public static LocalDate dateToLocalDate(Date date)
  {
    return LocalDate.from(date.toInstant().atZone(ZoneId.systemDefault()));
  }

  public static LocalDateTime stringToLocalDateTime(String stringDate, LocalTime localTime) throws ParseException
  {
    return (new SimpleDateFormat(BASIC_DATE_FORMAT).parse(stringDate)).toInstant().atZone(ZoneId.of(ZONE_ID)).toLocalDate().atTime(localTime);
  }

  public static LocalDateTime now()
  {
    return LocalDateTime.now(ZoneId.of(ZONE_ID));
  }
}
