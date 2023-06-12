package mn.erin.lms.unitel.domain.service;

import java.util.NoSuchElementException;

import mn.erin.domain.base.model.person.PersonId;
import mn.erin.lms.base.aim.LmsUserResolver;
import mn.erin.lms.base.aim.role.LmsRole;
import mn.erin.lms.base.aim.user.LmsUser;
import mn.erin.lms.unitel.domain.model.user.UnitelAdmin;
import mn.erin.lms.unitel.domain.model.user.UnitelEmployee;
import mn.erin.lms.unitel.domain.model.user.UnitelManager;
import mn.erin.lms.unitel.domain.model.user.UnitelSupervisor;

/**
 * @author Bat-Erdene Tsogoo.
 */
public class UnitelLmsUserResolver implements LmsUserResolver
{
  @Override
  public LmsUser resolve(String userId, String roleId)
  {
    PersonId id = PersonId.valueOf(userId);

    if (LmsRole.LMS_USER.name().equalsIgnoreCase(roleId))
    {
      return new UnitelEmployee(id);
    }
    else if (LmsRole.LMS_SUPERVISOR.name().equalsIgnoreCase(roleId))
    {
      return new UnitelSupervisor(id);
    }
    else if (LmsRole.LMS_ADMIN.name().equalsIgnoreCase(roleId))
    {
      return new UnitelAdmin(id);
    }
    else if (LmsRole.LMS_MANAGER.name().equalsIgnoreCase(roleId))
    {
      return new UnitelManager(id);
    }
    else
    {
      throw new NoSuchElementException("Role: [" + roleId + "] does not exist!");
    }
  }
}
