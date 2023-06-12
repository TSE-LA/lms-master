package mn.erin.lms.unitel.server;

import java.util.EnumSet;
import javax.servlet.DispatcherType;
import javax.servlet.FilterRegistration;
import javax.servlet.ServletContext;
import javax.servlet.ServletRegistration;

import mn.erin.domain.aim.repository.GroupRepository;
import mn.erin.domain.aim.repository.MembershipRepository;
import mn.erin.lms.base.domain.repository.LmsRepositoryRegistry;
import mn.erin.lms.base.domain.service.LmsServiceRegistry;
import mn.erin.lms.unitel.domain.SetupLms;
import mn.erin.lms.unitel.server.config.ServerBeanConfig;
import mn.erin.lms.unitel.server.config.UnitelShiroBeanConfig;
import org.jetbrains.annotations.NotNull;
import org.springframework.web.WebApplicationInitializer;
import org.springframework.web.context.ContextLoaderListener;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
import org.springframework.web.filter.DelegatingFilterProxy;
import org.springframework.web.servlet.DispatcherServlet;

/**
 * @author Bat-Erdene Tsogoo.
 */
public class LmsApplicationInitializer implements WebApplicationInitializer
{
  @Override
  public void onStartup(@NotNull ServletContext servletContext)
  {
    AnnotationConfigWebApplicationContext ctx = new AnnotationConfigWebApplicationContext();
    ctx.register(ServerBeanConfig.class);
    ctx.register(UnitelShiroBeanConfig.class);
    ctx.setServletContext(servletContext);

    servletContext.addListener(new ContextLoaderListener(ctx));

    ctx.refresh();
    SetupLms setupLms = new SetupLms(ctx.getBean(GroupRepository.class), ctx.getBean(MembershipRepository.class),
        ctx.getBean(LmsRepositoryRegistry.class), ctx.getBean(LmsServiceRegistry.class));
    setupLms.execute();

    FilterRegistration.Dynamic shiroFilter = servletContext.addFilter("shiroFilterFactoryBean", DelegatingFilterProxy.class);
    shiroFilter.setInitParameter("targetFilterLifecycle", "true");
    shiroFilter.addMappingForUrlPatterns(EnumSet.allOf(DispatcherType.class), false, "/*");

    ServletRegistration.Dynamic servlet = servletContext.addServlet("dispatcher", new DispatcherServlet(ctx));
    servlet.setLoadOnStartup(1);
    servlet.addMapping("/");
    servlet.setAsyncSupported(true);
  }
}
