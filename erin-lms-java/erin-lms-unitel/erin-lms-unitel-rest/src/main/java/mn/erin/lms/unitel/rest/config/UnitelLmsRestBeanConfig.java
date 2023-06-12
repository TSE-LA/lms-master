package mn.erin.lms.unitel.rest.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import mn.erin.lms.base.rest.config.BaseLmsRestBeanConfig;

/**
 * @author Bat-Erdene Tsogoo.
 */
@Configuration
@Import(BaseLmsRestBeanConfig.class)
@ComponentScan({ "mn.erin.lms.unitel.rest.api" })
public class UnitelLmsRestBeanConfig
{
}
