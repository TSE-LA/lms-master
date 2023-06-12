package mn.erin.lms.base.aim;

import mn.erin.domain.base.model.person.ContactInfo;
import mn.erin.lms.base.aim.user.LmsUser;

/**
 * @author Bat-Erdene Tsogoo.
 */
public interface LmsUserService
{
  /**
   * @deprecated
   * Use access identity management current Username instead
   */
  @Deprecated()
  LmsUser getCurrentUser();

  ContactInfo getContactInfo(String user);
}
