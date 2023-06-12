package mn.erin.lms.base.aim.user;

import mn.erin.domain.base.model.EntityId;

/**
 * LMS Administrator is a static, non-editable role that can do everything in the system.
 * LMS Administrator is primarily responsible for configuring/maintaining the system's settings.
 *
 * @author Bat-Erdene Tsogoo.
 */
public class LmsAdministrator implements LmsUser
{
  public static final String LMS_ADMINISTRATOR = "admin";

  @Override
  public EntityId getId()
  {
    return new EntityId("admin")
    {
    };
  }
}
