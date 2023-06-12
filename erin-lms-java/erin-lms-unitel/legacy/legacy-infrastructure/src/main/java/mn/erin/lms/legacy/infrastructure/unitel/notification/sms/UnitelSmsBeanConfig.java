package mn.erin.lms.legacy.infrastructure.unitel.notification.sms;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import mn.erin.common.sms.SmsMessageFactory;
import mn.erin.common.sms.SmsSender;

/**
 * @author Bat-Erdene Tsogoo.
 */
@Configuration
public class UnitelSmsBeanConfig
{
  @Bean(name = "legacySmsSender")
  public SmsSender smsSender()
  {
    return new LegacyUnitelSmsSender();
  }

  @Bean(name = "legacySmsMessageFactory")
  public SmsMessageFactory smsMessageFactory()
  {
    return new LegacyUnitelSmsMessageFactory();
  }
}
