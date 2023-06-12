package mn.erin.lms.base.aim;

import mn.erin.domain.base.model.person.ContactInfo;
import mn.erin.lms.base.aim.user.LmsUser;

/**
 * @author Bat-Erdene Tsogoo.
 */
public class UserServiceImpl extends BaseLmsAimService implements LmsUserService
{
  private final LmsUserResolver lmsUserResolver;

  public UserServiceImpl(AccessIdentityManagement accessIdentityManagement, LmsUserResolver lmsUserResolver)
  {
    super(accessIdentityManagement);
    this.lmsUserResolver = lmsUserResolver;
  }

  @Override
  public LmsUser getCurrentUser()
  {
    String username = accessIdentityManagement.getCurrentUsername();
    return lmsUserResolver.resolve(username, accessIdentityManagement.getRole(username));
  }

  @Override
  public ContactInfo getContactInfo(String user)
  {
    return accessIdentityManagement.getContactInfo(user);
  }
}
