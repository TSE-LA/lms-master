package mn.erin.lms.aim.shiro.realm;

import org.apache.shiro.authc.AccountException;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.DisabledAccountException;
import org.apache.shiro.authc.IncorrectCredentialsException;
import org.apache.shiro.authc.LockedAccountException;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.authc.UnknownAccountException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.authc.credential.PasswordService;
import org.apache.shiro.realm.Realm;

import mn.erin.domain.aim.model.tenant.TenantId;
import mn.erin.domain.aim.model.user.User;
import mn.erin.domain.aim.model.user.UserIdentity;
import mn.erin.domain.aim.model.user.UserIdentitySource;
import mn.erin.domain.aim.model.user.UserProfile;
import mn.erin.domain.aim.model.user.UserStatus;
import mn.erin.domain.aim.repository.AimRepositoryRegistry;

/**
 * @author Munkh
 */
public class AimRealm implements Realm
{
  private static final String INVALID_INPUT = "Invalid input!";
  private static final String USERNAME_NOT_FOUND = "Username not found!";
  private static final String USER_NOT_FOUND = "User not found!";
  private static final String INCORRECT_PASSWORD = "Password is incorrect!";
  private static final String UNKNOWN_STATE = "User state is unknown!";
  private static final String DISABLED_ACCOUNT = "Account is disabled!";
  private static final String LOCKED_ACCOUNT = "Account is locked!";

  private final TenantId tenantId;
  private final AimRepositoryRegistry aimRepositoryRegistry;
  private final PasswordService passwordService;

  public AimRealm(TenantId tenantId, AimRepositoryRegistry aimRepositoryRegistry, PasswordService passwordService)
  {
    this.tenantId = tenantId;
    this.aimRepositoryRegistry = aimRepositoryRegistry;
    this.passwordService = passwordService;
  }

  @Override
  public String getName()
  {
    return "Aim Realm";
  }

  @Override
  public boolean supports(AuthenticationToken token)
  {
    return true;
  }

  @Override
  public AuthenticationInfo getAuthenticationInfo(AuthenticationToken token)
  {
    UsernamePasswordToken uToken = (UsernamePasswordToken) token;

    String username = uToken.getUsername();

    if (username == null
        || username.isEmpty())
    {
      throw new IncorrectCredentialsException(INVALID_INPUT);
    }

    UserIdentity userIdentity = aimRepositoryRegistry.getUserIdentityRepository().getUserIdentityByUsername(username, UserIdentitySource.OWN);

    if (userIdentity == null)
    {
      throw new UnknownAccountException(USERNAME_NOT_FOUND);
    }

    User user = aimRepositoryRegistry.getUserRepository().findById(userIdentity.getUserId());
    if (user == null || !user.getTenantId().equals(tenantId))
    {
      throw new AuthenticationException(USER_NOT_FOUND);
    }

    if (user.getStatus() == null)
    {
      throw new AccountException(UNKNOWN_STATE);
    }
    else if (user.getStatus().equals(UserStatus.ARCHIVED))
    {
      throw new DisabledAccountException(DISABLED_ACCOUNT);
    }
    else if (user.getStatus().equals(UserStatus.LOCKED))
    {
      throw new LockedAccountException(LOCKED_ACCOUNT);
    }

    if (!passwordService.passwordsMatch(uToken.getPassword(), userIdentity.getPassword()))
    {
      throw new AuthenticationException(INCORRECT_PASSWORD);
    }

    return new SimpleAuthenticationInfo(
        userIdentity.getUsername(),
        userIdentity.getUsername(),
        getName());
  }
}
