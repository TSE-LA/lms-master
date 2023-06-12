package mn.erin.lms.legacy.infrastructure.unitel.notification.sms;

import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import mn.erin.common.sms.SmsMessageFactory;
import mn.erin.lms.legacy.domain.unitel.PromotionConstants;

/**
 * @author Bat-Erdene Tsogoo.
 */
public class LegacyUnitelSmsMessageFactory implements SmsMessageFactory
{
  private static final String MESSAGE = "Сайн байна уу?\n";
  private static final String BODY = "%s, \"%s\" нэртэй урамшуулал шинээр нийтлэгдлээ! "
      + "Та дараах холбоосоор орж урамшуулалтай танилцана уу. \n"
      + "%s";
  private static final String PATH_COMPONENT = "/#/timed-course/launch/";

  private static final String COURSE_NAME = "courseName";
  private static final String COURSE_ID = "courseId";
  private static final String MEMO = "memo";
  private static final String DOMAIN_NAME = "domainName";

  @Override
  public String newMessage(Map<String, Object> templateData)
  {
    String code = (String) templateData.get(PromotionConstants.PROPERTY_CODE);
    String promotionName = (String) templateData.get(COURSE_NAME);
    String promotionId = (String) templateData.get(COURSE_ID);
    String domainName = (String) templateData.get(DOMAIN_NAME);

    String host = domainName + PATH_COMPONENT + promotionId + "/true ";

    if (!StringUtils.isBlank((String) templateData.get(MEMO)))
    {
      return MESSAGE + String.format(BODY, code, promotionName, host) + "[" + templateData.get(MEMO) + "] ";
    }

    return String.format(MESSAGE + BODY, code, promotionName, host);
  }
}
