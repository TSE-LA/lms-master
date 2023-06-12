package mn.erin.lms.base.aim;

import mn.erin.lms.base.aim.user.LmsUser;

/**
 * @author Bat-Erdene Tsogoo.
 */
public interface LmsUserResolver
{
  LmsUser resolve(String userId, String roleId);
}
