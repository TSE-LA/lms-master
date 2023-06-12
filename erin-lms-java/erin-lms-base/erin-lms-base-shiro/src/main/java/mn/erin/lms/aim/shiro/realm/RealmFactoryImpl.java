package mn.erin.lms.aim.shiro.realm;

import javax.inject.Inject;
import javax.naming.Context;

import org.apache.shiro.authc.credential.PasswordService;
import org.apache.shiro.realm.Realm;
import org.apache.shiro.realm.activedirectory.ActiveDirectoryRealm;
import org.apache.shiro.realm.ldap.DefaultLdapRealm;
import org.apache.shiro.realm.ldap.JndiLdapContextFactory;
import org.apache.shiro.realm.ldap.LdapContextFactory;
import org.springframework.beans.factory.BeanFactory;

import mn.erin.aim.ldap.config.LdapConfig;
import mn.erin.domain.aim.repository.AimRepositoryRegistry;
import mn.erin.domain.aim.service.AimConfigProvider;

public class RealmFactoryImpl implements RealmFactory
{
  private final BeanFactory beanFactory;
  private AimConfigProvider aimConfigProvider;
  private PasswordService passwordService;

  public RealmFactoryImpl(BeanFactory beanFactory)
  {
    this.beanFactory = beanFactory;
  }

  @Inject
  public void setAimConfigProvider(AimConfigProvider aimConfigProvider)
  {
    this.aimConfigProvider = aimConfigProvider;
  }

  @Inject
  public void setPasswordService(PasswordService passwordService)
  {
    this.passwordService = passwordService;
  }

  @Override
  public Realm getRealm(RealmType realmType)
  {
    LdapConfig ldapConfig = ldapConfig();
    switch (realmType)
    {
      case AD:
        ActiveDirectoryRealm adRealm = new ActiveDirectoryRealm();
        adRealm.setLdapContextFactory(getContextFactory(ldapConfig));
        adRealm.setPrincipalSuffix(ldapConfig.getPrincipalSuffix());
        adRealm.setSearchBase(ldapConfig.getBaseDn());
        return adRealm;

      case LDAP:
        DefaultLdapRealm ldapRealm = new DefaultLdapRealm();
        ldapRealm.setContextFactory(getContextFactory(ldapConfig));
        ldapRealm.setUserDnTemplate("uid={0}," + ldapConfig.getBaseDn());
        return ldapRealm;

      case MONGO:
      default:
        return new AimRealm(
                aimConfigProvider.getDefaultTenantId(),
                beanFactory.getBean(AimRepositoryRegistry.class),
                passwordService
        );
    }
  }

  private LdapConfig ldapConfig()
  {
    return beanFactory.getBean(LdapConfig.class);
  }

  private static LdapContextFactory getContextFactory(LdapConfig ldapConfig)
  {
    JndiLdapContextFactory contextFactory = new LmsLdapContextFactory(ldapConfig);
    contextFactory.setUrl(ldapConfig.getUrl());
    contextFactory.setSystemUsername(ldapConfig.getUsername());
    contextFactory.setSystemPassword(ldapConfig.getPassword());
    if (ldapConfig.isSsl())
    {
      contextFactory.getEnvironment().put(Context.SECURITY_PROTOCOL, "ssl");
    }
    return contextFactory;
  }
}
