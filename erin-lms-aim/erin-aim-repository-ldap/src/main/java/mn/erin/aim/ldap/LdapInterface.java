package mn.erin.aim.ldap;

import java.text.MessageFormat;
import java.util.Properties;
import javax.naming.Context;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attributes;
import javax.naming.directory.DirContext;
import javax.naming.directory.InitialDirContext;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;

import org.apache.commons.lang3.StringUtils;

import mn.erin.aim.ldap.config.LdapConfig;
import mn.erin.aim.ldap.config.LdapConstants;
import mn.erin.domain.aim.model.tenant.TenantId;

public class LdapInterface
{
  private final LdapConfig ldapConfig;

  public LdapInterface(LdapConfig ldapConfig)
  {
    this.ldapConfig = ldapConfig;
  }

  public TenantId getTenantId()
  {
    return TenantId.valueOf(ldapConfig.getTenantId());
  }

  public String getObjectClass()
  {
    return ldapConfig.getObjectClass();
  }

  public String getUserIdAttribute()
  {
    return ldapConfig.getObjectIDAttribute();
  }

  public String getUsernameAttribute()
  {
    return ldapConfig.getUsernameAttribute();
  }

  public NamingEnumeration<SearchResult> search(String query, SearchControls controls) throws NamingException
  {
    return search(ldapConfig.getBaseDn(), query, controls);
  }

  @Deprecated // FIXME GUID binding doesn't work for Open LDAP
  public Attributes searchByBinding(String bindingString) throws NamingException
  {
    return new InitialDirContext(ldapProperties()).getAttributes(bindingString);
  }

  private NamingEnumeration<SearchResult> search(String baseDn, String query, SearchControls controls) throws NamingException
  {
    DirContext dirContext = new InitialDirContext(ldapProperties());
    if (StringUtils.isBlank(ldapConfig.getExclude()))
    {
      return dirContext.search(baseDn, query, controls);
    }
    String filter = MessageFormat.format("(&{0}(!{1}))", query, ldapConfig.getExclude());
    return dirContext.search(baseDn, filter, controls);
  }

  private Properties ldapProperties()
  {
    Properties ldapProperties = new Properties();
    ldapProperties.put(Context.INITIAL_CONTEXT_FACTORY, LdapConstants.JNDI_LDAP_CTX_FACTORY);
    ldapProperties.put(Context.PROVIDER_URL, ldapConfig.getUrl());
    ldapProperties.put(Context.SECURITY_PRINCIPAL, ldapConfig.getUsername());
    ldapProperties.put(Context.SECURITY_CREDENTIALS, ldapConfig.getPassword());
    ldapProperties.put(Context.SECURITY_AUTHENTICATION, "simple");
    ldapProperties.put(LdapConstants.BINARY_ATTRIBUTE, "objectGUID entryUUID"/*binary attributes*/);
    if (ldapConfig.isSsl())
    {
      ldapProperties.put(Context.SECURITY_PROTOCOL, "ssl");
    }
    return ldapProperties;
  }
}
