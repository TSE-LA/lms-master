package mn.erin.lms.base.notification;

import mn.erin.spring.common.mail.EmailConfig;
import mn.erin.spring.common.mail.EmailConfigFactory;

/**
 * @author Oyungerel Chuluunsukh.
 */
public class LmsEmailConfigFactory extends EmailConfigFactory
{
  @Override
  public EmailConfig newEmailConfig()
  {
    return new EmailConfig.Builder(System.getProperty("spring.mail.host"))
        .setAuthenticated(Boolean.parseBoolean(System.getProperty("spring.mail.smtp.auth")))
        .setStartTLS(Boolean.parseBoolean(System.getProperty("spring.mail.smtp.starttls.enable")))
        .withProtocol(System.getProperty("spring.mail.protocol"))
        .withUsername(System.getProperty("spring.mail.username"))
        .withPassword(System.getProperty("spring.mail.password"))
        .withFrom(System.getProperty("spring.mail.from"))
        .setMailDebug(Boolean.parseBoolean(System.getProperty("spring.mail.debug")))
        .setSsl(Boolean.parseBoolean(System.getProperty("spring.mail.smtp.ssl.enable")))
        .onPort(Integer.parseInt(System.getProperty("spring.mail.port")))
        .build();
  }
}
