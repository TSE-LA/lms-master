package mn.erin.lms.jarvis.mail;

import java.util.Map;

import mn.erin.common.mail.EmailSubjectResolver;

/**
 * @author Bat-Erdene Tsogoo.
 */
public class JarvisEmailSubjectResolver implements EmailSubjectResolver
{
  @SuppressWarnings("unchecked")
  @Override
  public String resolveSubject(Object data)
  {
    if (data instanceof Map)
    {
      Map<String, Object> templateData = (Map<String, Object>) data;

      String courseName = (String) templateData.get("courseName");
      return "\"" + courseName + "\"" + " нэртэй цахим сургалт нийтлэгдлээ";
    }
    else
    {
      return "";
    }
  }
}
