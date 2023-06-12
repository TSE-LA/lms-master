package mn.erin.domain.aim.service;

import java.lang.reflect.Field;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import mn.erin.domain.aim.annotation.Authorized;
import mn.erin.domain.aim.model.permission.Permission;
import mn.erin.domain.aim.usecase.AuthorizedUseCase;
import mn.erin.domain.base.model.ApplicationModule;
import org.apache.commons.lang3.StringUtils;
import org.reflections.Reflections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Represents a base permission service with auto scanning for @Authorized classes
 *
 * @author EBazarragchaa
 */
public abstract class BasePermissionService implements PermissionService
{
  private static final Logger LOGGER = LoggerFactory.getLogger(BasePermissionService.class);

  private Reflections reflections = new Reflections("mn.erin.domain");
  private Map<ApplicationModule, Set<Permission>> permissions;

  @Override
  public Collection<String> findAllPermissions()
  {
    this.permissions = new LinkedHashMap<>();
    Set<String> permissionStrings = new TreeSet<>();
    Set<Class<?>> authorizedUseCaseClasses = reflections.getTypesAnnotatedWith(Authorized.class);
    authorizedUseCaseClasses.removeIf(AuthorizedUseCase.class::equals);

    Set<String> errors = new TreeSet<>();
    for (Class<?> clz : authorizedUseCaseClasses)
    {
      try
      {
        Field field = clz.getDeclaredField("permission");
        field.setAccessible(true);
        Permission permission = (Permission) field.get(null);
        if (null != permission && !permissionStrings.contains(permission.getPermissionString()))
        {
          permissionStrings.add(permission.getPermissionString());
        }
        else
        {
          errors.add(clz.getName() + " doesn't have a permission!");
        }
      }
      catch (IllegalAccessException | NoSuchFieldException | NullPointerException e)
      {
        errors.add(clz.getName() + " doesn't have a permission! Cause: [" + e + "]");
      }
    }
    if (!errors.isEmpty())
    {
      throw new IllegalStateException(StringUtils.join(errors, "\n"));
    }
    return Collections.unmodifiableSet(permissionStrings);
  }

  @Override
  public Collection<String> findAllPermittedApplications()
  {
    throw new UnsupportedOperationException();
  }

  @Override
  public Collection<String> findAllPermittedModules(String applicationId)
  {
    throw new UnsupportedOperationException();
  }
}
