package mn.erin.lms.base.aim;

/**
 * @author Bat-Erdene Tsogoo.
 */
public abstract class BaseLmsAimService
{
  protected final AccessIdentityManagement accessIdentityManagement;

  public BaseLmsAimService(AccessIdentityManagement accessIdentityManagement)
  {
    this.accessIdentityManagement = accessIdentityManagement;
  }
}
