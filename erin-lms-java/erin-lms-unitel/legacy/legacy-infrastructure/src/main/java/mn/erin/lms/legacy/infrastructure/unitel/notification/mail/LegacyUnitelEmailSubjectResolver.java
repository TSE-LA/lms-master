package mn.erin.lms.legacy.infrastructure.unitel.notification.mail;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import mn.erin.common.datetime.DateTimeUtils;
import mn.erin.common.mail.EmailSubjectResolver;

/**
 * @author Bat-Erdene Tsogoo.
 */
public class LegacyUnitelEmailSubjectResolver implements EmailSubjectResolver
{
  private static final Logger LOGGER = LoggerFactory.getLogger(LegacyUnitelEmailSubjectResolver.class);

  SimpleDateFormat formatter = new SimpleDateFormat(DateTimeUtils.FULL_ISO_DATE_FORMAT);

  @SuppressWarnings("unchecked")
  @Override
  public String resolveSubject(Object data)
  {
    if (data instanceof Map)
    {
      Map<String, Object> templateData = (Map<String, Object>) data;

      try
      {
        Date startDate = formatter.parse((String) templateData.get("startDate"));
        templateData.put("startDate", DateTimeUtils.toIsoString(startDate, DateTimeUtils.SHORT_ISO_DATE_FORMAT));

        if (templateData.containsKey("endDate"))
        {
          Date endDate = formatter.parse((String) templateData.get("endDate"));
          templateData.put("endDate", DateTimeUtils.toIsoString(endDate, DateTimeUtils.SHORT_ISO_DATE_FORMAT));
        }
        else
        {
          templateData.put("endDate", "-");
        }

      }
      catch (ParseException e)
      {
        LOGGER.error(e.getMessage(), e);
      }

      String promoCode = (String) templateData.get("code");
      String keyword = (String) templateData.get("keyWord");
      String promoName = (String) templateData.get("courseName");
      return keyword + " " + promoCode + " - " + promoName;
    }
    else
    {
      return "";
    }
  }
}
