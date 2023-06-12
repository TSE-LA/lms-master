package mn.erin.lms.base.domain.service.impl;

import java.util.NoSuchElementException;

import mn.erin.domain.base.model.person.PersonId;
import mn.erin.lms.base.aim.LmsUserResolver;
import mn.erin.lms.base.aim.user.LmsAdmin;
import mn.erin.lms.base.aim.user.LmsEmployee;
import mn.erin.lms.base.aim.user.LmsManager;
import mn.erin.lms.base.aim.user.LmsSupervisor;
import mn.erin.lms.base.aim.user.LmsUser;
import mn.erin.lms.base.aim.role.LmsRole;


/**
 * @author Bat-Erdene Tsogoo.
 */
public class JarvisLmsUserResolver implements LmsUserResolver
{
  @Override
  public LmsUser resolve(String userId, String roleId)
  {
    PersonId id = PersonId.valueOf(userId);

    if (LmsRole.LMS_USER.name().equalsIgnoreCase(roleId))
    {
      return new LmsEmployee(id);
    }
    else if (LmsRole.LMS_SUPERVISOR.name().equalsIgnoreCase(roleId))
    {
      return new LmsSupervisor(id);
    }
    else if (LmsRole.LMS_ADMIN.name().equalsIgnoreCase(roleId))
    {
      return new LmsAdmin(id);
    }
    else if (LmsRole.LMS_MANAGER.name().equalsIgnoreCase(roleId))
    {
      return new LmsManager(id);
    }
    else
    {
      throw new NoSuchElementException("Role: [" + roleId + "] does not exist!");
    }
  }
}
