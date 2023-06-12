package mn.erin.aim.ldap.config;

/**
 * @author Bat-Erdene Tsogoo.
 */
public final class LdapConstants
{
  public static final String JNDI_LDAP_CTX_FACTORY = "com.sun.jndi.ldap.LdapCtxFactory";
  public static final String BINARY_ATTRIBUTE = "java.naming.ldap.attributes.binary";

  public static final String ATTR_GIVEN_NAME = "givenName";
  public static final String ATTR_SURNAME = "sn";
  public static final String ATTR_DISPLAY_NAME = "displayName";
  public static final String ATTR_MAIL = "mail";
  public static final String ATTR_TELEPHONE = "telephoneNumber";

  private LdapConstants()
  {
    // ctor
  }
}
