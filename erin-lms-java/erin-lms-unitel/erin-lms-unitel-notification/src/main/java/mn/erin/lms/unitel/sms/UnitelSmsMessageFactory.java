package mn.erin.lms.unitel.sms;

import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import mn.erin.common.sms.SmsMessageFactory;

/**
 * @author Bat-Erdene Tsogoo.
 */
public class UnitelSmsMessageFactory implements SmsMessageFactory
{
  private static final String BODY =  "\"%s\" цахим сургалт нийтлэгдлээ дараах холбоосоор танилцана уу.\n"
      + "Урилга илгээсэн админ: %s.\n Асууж тодруулах зүйл байвал шууд удирдлагадаа хандана уу. \n";
  private static final String PATH_COMPONENT = "/#/online-course/launch/";

  private static final String COURSE_NAME = "courseName";
  private static final String COURSE_ID = "courseId";
  private static final String MEMO = "memo";

  @Override
  public String newMessage(Map<String, Object> templateData)
  {
    String courseName = (String) templateData.get(COURSE_NAME);
    String courseId = (String) templateData.get(COURSE_ID);
    String domainName = (String) templateData.get("domainName");
    String author = (String) templateData.get("authorId");

    String host = domainName + PATH_COMPONENT + courseId + "/true";
    String message = "Сайн байна уу? \n";
    if (!StringUtils.isBlank((String) templateData.get(MEMO)))
    {
      return message + "[" + templateData.get(MEMO) + "] " + String.format(BODY, courseName, host, author);
    }

    return message + String.format(BODY, courseName, host, author);
  }
}
