package mn.erin.lms.jarvis;

import org.springframework.context.ApplicationContext;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;

import mn.erin.lms.base.domain.repository.LmsRepositoryRegistry;
import mn.erin.lms.base.domain.service.LmsServiceRegistry;
import mn.erin.lms.jarvis.domain.SetupLms;

public class LmsSetupListener
{
  @EventListener
  public void onApplicationEvent(ContextRefreshedEvent event)
  {
    ApplicationContext ctx = event.getApplicationContext();
    new SetupLms(
        ctx.getBean(LmsRepositoryRegistry.class),
        ctx.getBean(LmsServiceRegistry.class))
        .execute();
  }
}
