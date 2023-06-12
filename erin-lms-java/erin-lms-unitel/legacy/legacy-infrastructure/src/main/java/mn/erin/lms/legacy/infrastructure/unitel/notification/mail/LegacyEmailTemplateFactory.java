package mn.erin.lms.legacy.infrastructure.unitel.notification.mail;

import java.io.IOException;

import freemarker.template.Template;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import mn.erin.spring.common.mail.EmailTemplateFactory;

/**
 * @author Bat-Erdene Tsogoo.
 */
public class LegacyEmailTemplateFactory extends EmailTemplateFactory
{
  private static final Logger LOGGER = LoggerFactory.getLogger(LegacyEmailTemplateFactory.class);

  @Override
  public Template newTemplate()
  {
    return newTemplate("legacy-email-template.ftl");
  }

  @Override
  public Template newTemplate(String templateName)
  {
    try
    {
      return EmailTemplateFactory.getTemplate(this.getClass(), templateName, "/templates");
    }
    catch (IOException e)
    {
      LOGGER.error(e.getMessage(), e);
      return null;
    }
  }
}
