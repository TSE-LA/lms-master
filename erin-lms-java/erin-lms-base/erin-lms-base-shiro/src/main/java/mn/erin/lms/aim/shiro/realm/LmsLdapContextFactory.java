package mn.erin.lms.aim.shiro.realm;

import javax.naming.NamingException;
import javax.naming.ldap.LdapContext;

import org.apache.shiro.realm.ldap.JndiLdapContextFactory;

import mn.erin.aim.ldap.config.LdapConfig;
import mn.erin.aim.ldap.config.LdapType;

/**
 * @author mLkhagvasuren
 */
public class LmsLdapContextFactory extends JndiLdapContextFactory
{
  private final LdapConfig ldapConfig;

  public LmsLdapContextFactory(LdapConfig ldapConfig)
  {
    this.ldapConfig = ldapConfig;
  }

  @Override
  public LdapContext getLdapContext(String username, String password) throws NamingException
  {
    if (ldapConfig.getType() == LdapType.OPEN_LDAP)
    {
      return super.getLdapContext(username, password);
    }
    if (username != null && ldapConfig.getPrincipalSuffix() != null)
    {
      username += ldapConfig.getPrincipalSuffix();
    }
    return super.getLdapContext(username, password);
  }
}
