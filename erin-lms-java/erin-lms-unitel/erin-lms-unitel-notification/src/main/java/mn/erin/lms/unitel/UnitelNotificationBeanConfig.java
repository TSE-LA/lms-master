package mn.erin.lms.unitel;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import mn.erin.lms.unitel.mail.UnitelEmailBeanConfig;
import mn.erin.lms.unitel.sms.UnitelSmsBeanConfig;

/**
 * @author Bat-Erdene Tsogoo.
 */
@Configuration
@Import({ UnitelEmailBeanConfig.class, UnitelSmsBeanConfig.class })
public class UnitelNotificationBeanConfig
{
}
