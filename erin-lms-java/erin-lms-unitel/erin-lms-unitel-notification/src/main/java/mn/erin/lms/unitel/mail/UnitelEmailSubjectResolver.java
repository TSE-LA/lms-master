package mn.erin.lms.unitel.mail;

import java.util.Map;

import mn.erin.common.mail.EmailSubjectResolver;

/**
 * @author Bat-Erdene Tsogoo.
 */
public class UnitelEmailSubjectResolver implements EmailSubjectResolver
{
  @SuppressWarnings("unchecked")
  @Override
  public String resolveSubject(Object data)
  {
    if (data instanceof Map)
    {
      Map<String, Object> templateData = (Map<String, Object>) data;
      if (templateData.get("startDate") != null)
      {
        return promotionSubject(templateData);
      }
      else
      {
        return onlineCourseSubject(templateData);
      }
    }
    else
    {
      return "";
    }
  }

  private String promotionSubject(Map<String, Object> templateData)
  {
    String courseName = (String) templateData.get("courseName");
    return "\"" + courseName + "\"" + " нэртэй урамшуулал нийтлэгдлээ";
  }

  private String onlineCourseSubject(Map<String, Object> templateData)
  {
    String courseName = (String) templateData.get("courseName");
    return "\"" + courseName + "\"" + " нэртэй цахим сургалт нийтлэгдлээ";
  }
}
