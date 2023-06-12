package mn.erin.lms.unitel.sms;

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
  @Bean
  public SmsSender smsSender()
  {
    return new UnitelSmsSender();
  }

  @Bean
  public SmsMessageFactory smsMessageFactory()
  {
    return new UnitelSmsMessageFactory();
  }
}
