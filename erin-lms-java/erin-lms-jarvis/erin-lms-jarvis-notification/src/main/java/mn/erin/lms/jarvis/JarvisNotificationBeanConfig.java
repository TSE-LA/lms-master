package mn.erin.lms.jarvis;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import mn.erin.lms.jarvis.mail.JarvisEmailBeanConfig;
import mn.erin.lms.jarvis.sms.JarvisSmsBeanConfig;

/**
 * @author Bat-Erdene Tsogoo.
 */
@Configuration
@Import({ JarvisEmailBeanConfig.class, JarvisSmsBeanConfig.class })
public class JarvisNotificationBeanConfig
{
}
