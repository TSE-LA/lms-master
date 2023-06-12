package mn.erin.lms.aim.shiro.realm;

import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.realm.Realm;

import mn.erin.domain.aim.repository.UserRepository;

public class DummyRealm implements Realm
{
  public DummyRealm(UserRepository userRepository)
  {
  }

  @Override
  public String getName()
  {
    return null;
  }

  @Override
  public boolean supports(AuthenticationToken token)
  {
    return false;
  }

  @Override
  public AuthenticationInfo getAuthenticationInfo(AuthenticationToken token) throws AuthenticationException
  {
    return null;
  }
}
