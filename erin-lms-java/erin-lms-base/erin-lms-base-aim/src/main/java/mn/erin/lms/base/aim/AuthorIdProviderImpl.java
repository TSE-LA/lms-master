package mn.erin.lms.base.aim;


/**
 * @author Bat-Erdene Tsogoo.
 */
public class AuthorIdProviderImpl extends BaseLmsAimService implements AuthorIdProvider
{
  public AuthorIdProviderImpl(AccessIdentityManagement accessIdentityManagement)
  {
    super(accessIdentityManagement);
  }

  @Override
  public String getAuthorId()
  {
    return accessIdentityManagement.getCurrentUsername();
  }
}
