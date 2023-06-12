package mn.erin.lms.jarvis.sms;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import mn.erin.common.sms.SmsMessageFactory;
import mn.erin.common.sms.SmsSender;

/**
 * @author Bat-Erdene Tsogoo.
 */
@Configuration
public class JarvisSmsBeanConfig
{
  @Bean
  public SmsSender smsSender()
  {
    return new JarvisSmsSender();
  }

  @Bean
  public SmsMessageFactory smsMessageFactory()
  {
    return new JarvisSmsMessageFactory();
  }
}
