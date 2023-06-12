package mn.erin.lms.jarvis.sms;

import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import mn.erin.common.sms.SmsMessageFactory;

/**
 * @author Bat-Erdene Tsogoo.
 */
public class JarvisSmsMessageFactory implements SmsMessageFactory
{
  private static final String MESSAGE = "Сайн байна уу? \"%s\" цахим сургалт нийтлэгдлээ дараах холбоосоор танилцана уу. %s"
      + " Нийтэлсэн багш: %s. Асууж тодруулах зүйл байвал шууд удирдлагадаа хандана уу.";
  private static final String PATH_COMPONENT = "/#/online-course/launch/";

  private static final String COURSE_NAME = "courseName";
  private static final String COURSE_ID = "courseId";
  private static final String MEMO = "memo";

  @Override
  public String newMessage(Map<String, Object> templateData)
  {
    String onlineCourseName = (String) templateData.get(COURSE_NAME);
    String onlineCourseId = (String) templateData.get(COURSE_ID);
    String author = (String) templateData.get("authorId");
    String domainName = (String) templateData.get("domainName");

    String host = domainName + PATH_COMPONENT + onlineCourseId + "/true";

    if (!StringUtils.isBlank((String) templateData.get(MEMO)))
    {
      return "[" + templateData.get(MEMO) + "] " + String.format(MESSAGE, onlineCourseName, host, author);
    }

    return String.format(MESSAGE, onlineCourseName, host, author);
  }
}
