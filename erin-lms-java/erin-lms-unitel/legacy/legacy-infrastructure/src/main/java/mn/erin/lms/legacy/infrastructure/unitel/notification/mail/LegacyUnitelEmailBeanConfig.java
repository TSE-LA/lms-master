package mn.erin.lms.legacy.infrastructure.unitel.notification.mail;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import mn.erin.common.mail.EmailService;
import mn.erin.common.mail.EmailSubjectResolver;
import mn.erin.lms.base.notification.LmsEmailConfigFactory;
import mn.erin.spring.common.mail.EmailConfigFactory;
import mn.erin.spring.common.mail.EmailServiceImpl;
import mn.erin.spring.common.mail.EmailTemplateFactory;

/**
 * @author Bat-Erdene Tsogoo.
 */
@Configuration
public class LegacyUnitelEmailBeanConfig
{
  @Bean(name = "legacyEmailConfigFactory")
  public EmailConfigFactory emailConfigFactory()
  {
    return new LmsEmailConfigFactory();
  }

  @Bean(name = "legacyEmailTemplateFactory")
  public EmailTemplateFactory emailTemplateFactory()
  {
    return new LegacyEmailTemplateFactory();
  }

  @Bean(name = "legacyEmailService")
  public EmailService emailService()
  {
    return new EmailServiceImpl(emailConfigFactory(), new LegacyEmailTemplateFactory());
  }

  @Bean(name = "legacySubjectResolver")
  public EmailSubjectResolver subjectResolver()
  {
    return new LegacyUnitelEmailSubjectResolver();
  }
}
