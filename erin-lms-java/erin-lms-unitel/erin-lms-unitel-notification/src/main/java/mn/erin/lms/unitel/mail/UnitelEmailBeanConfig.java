package mn.erin.lms.unitel.mail;

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
public class UnitelEmailBeanConfig
{
  @Bean
  public EmailConfigFactory emailConfigFactory()
  {
    return new LmsEmailConfigFactory();
  }

  @Bean
  public EmailTemplateFactory emailTemplateFactory()
  {
    return new UnitelEmailTemplateFactory();
  }

  @Bean
  public EmailService emailService()
  {
    return new EmailServiceImpl(emailConfigFactory(), emailTemplateFactory());
  }

  @Bean
  public EmailSubjectResolver subjectResolver()
  {
    return new UnitelEmailSubjectResolver();
  }
}
