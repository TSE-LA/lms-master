package mn.erin.aim.ldap.config;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Bat-Erdene Tsogoo.
 */
public class LdapConfig
{
  private static final Logger LOG = LoggerFactory.getLogger(LdapConfig.class);

  private String url;
  private LdapType type;
  private String baseDn;
  private String username;
  private String password;
  private String principalSuffix;
  private String tenantId;
  private String exclude;
  private boolean ssl;

  public LdapConfig()
  {
  }

  public LdapConfig(String url, String type, String baseDn,
    String username, String password, String principalSuffix,
    String tenantId, String exclude, String ssl)
  {
    init(url, type, baseDn, username, password, principalSuffix, tenantId, exclude, ssl);
    LOG.info("LdapConfig initialized with properties {}", this);
  }

  public static LdapConfig load(InputStream is) throws IOException
  {
    Properties properties = new Properties();
    properties.load(is);
    return load(properties);
  }

  public static LdapConfig load(Properties properties)
  {
    return new LdapConfig(
      properties.getProperty("ldap.url"),
      properties.getProperty("ldap.type"),
      properties.getProperty("ldap.baseDn"),
      properties.getProperty("ldap.username"),
      properties.getProperty("ldap.password"),
      properties.getProperty("ldap.principalSuffix"),
      properties.getProperty("ldap.tenantId"),
      properties.getProperty("ldap.exclude"),
      properties.getProperty("ldap.ssl")
    );
  }

  public String getUrl()
  {
    return this.url;
  }

  public String getBaseDn()
  {
    return this.baseDn;
  }

  public LdapType getType()
  {
    return type;
  }

  public String getUsername()
  {
    return this.username;
  }

  public String getPassword()
  {
    return this.password;
  }

  public String getTenantId()
  {
    return this.tenantId;
  }

  public String getObjectClass()
  {
    return type.getObjectClass();
  }

  public String getObjectIDAttribute()
  {
    return type.getObjectIDAttribute();
  }

  public String getUsernameAttribute()
  {
    return type.getUsernameAttribute();
  }

  public String getExclude()
  {
    return exclude;
  }

  public String getPrincipalSuffix() {
    return principalSuffix;
}

  public boolean isSsl()
  {
    return ssl;
  }

  private void init(String url, String type, String baseDn,
    String username, String password, String principalSuffix,
    String tenantId, String exclude, String ssl)
  {
    this.url = url;
    this.type = (type == null) ? LdapType.ACTIVE_DIRECTORY : LdapType.forName(type);
    this.baseDn = baseDn;
    this.username = username;
    this.password = password;
    this.principalSuffix = principalSuffix;
    this.tenantId = tenantId;
    if (exclude != null)
    {
      this.exclude = exclude;
    }
    else
    {
      this.exclude = (this.type == LdapType.OPEN_LDAP) ? null : "(objectClass=computer)";
    }
    this.ssl = Boolean.parseBoolean(ssl);
  }

  @Override
  public String toString()
  {
    return "LdapConfig{" +
      "url='" + url + '\'' +
      ", baseDn='" + baseDn + '\'' +
      ", type=" + type +
      ", username='" + username + '\'' +
      ", password='" + password + '\'' +
      ", principalSuffix='" + principalSuffix + '\'' +
      ", tenantId='" + tenantId + '\'' +
      ", exclude='" + exclude + '\'' +
      ", ssl=" + ssl +
      '}';
  }
}
