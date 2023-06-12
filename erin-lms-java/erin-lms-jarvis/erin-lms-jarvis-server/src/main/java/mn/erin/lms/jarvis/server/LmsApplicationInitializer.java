package mn.erin.lms.jarvis.server;

import java.util.EnumSet;
import javax.servlet.DispatcherType;
import javax.servlet.FilterRegistration;
import javax.servlet.ServletContext;
import javax.servlet.ServletRegistration;

import org.jetbrains.annotations.NotNull;
import org.springframework.web.WebApplicationInitializer;
import org.springframework.web.context.ContextLoaderListener;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
import org.springframework.web.filter.DelegatingFilterProxy;
import org.springframework.web.servlet.DispatcherServlet;

import mn.erin.lms.jarvis.server.config.JarvisShiroBeanConfig;
import mn.erin.lms.jarvis.server.config.JarvisServerBeanConfig;

/**
 * @author Bat-Erdene Tsogoo.
 */
public class LmsApplicationInitializer implements WebApplicationInitializer
{
  @Override
  public void onStartup(@NotNull ServletContext servletContext)
  {
    AnnotationConfigWebApplicationContext ctx = new AnnotationConfigWebApplicationContext();
    ctx.register(JarvisServerBeanConfig.class);
    ctx.register(JarvisShiroBeanConfig.class);
    ctx.setServletContext(servletContext);

    servletContext.addListener(new ContextLoaderListener(ctx));

    FilterRegistration.Dynamic shiroFilter = servletContext.addFilter("shiroFilterFactoryBean", DelegatingFilterProxy.class);
    shiroFilter.setInitParameter("targetFilterLifecycle", "true");
    shiroFilter.addMappingForUrlPatterns(EnumSet.allOf(DispatcherType.class), false, "/*");

    ServletRegistration.Dynamic servlet = servletContext.addServlet("dispatcher", new DispatcherServlet(ctx));
    servlet.setLoadOnStartup(1);
    servlet.addMapping("/");
    servlet.setAsyncSupported(true);
  }
}
