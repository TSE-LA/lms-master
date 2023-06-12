package mn.erin.aim.ldap.config;

public enum LdapType
{
  ACTIVE_DIRECTORY("organizationalPerson", "objectGUID", "sAMAccountName"),
  ACTIVE_DIRECTORY_UPN("organizationalPerson", "objectGUID", "userPrincipalName"),
  OPEN_LDAP("inetOrgPerson", "entryUUID", "uid");

  private final String objectClass;
  private final String objectIDAttribute;
  private final String usernameAttribute;

  LdapType(String objectClass, String objectIDAttribute, String usernameAttribute)
  {
    this.objectClass = objectClass;
    this.objectIDAttribute = objectIDAttribute;
    this.usernameAttribute = usernameAttribute;
  }

  public static LdapType forName(String directoryName)
  {
    if ("activeDirectory".equalsIgnoreCase(directoryName))
    {
      return ACTIVE_DIRECTORY;
    }
    else if ("activeDirectory-upn".equalsIgnoreCase(directoryName))
    {
      return ACTIVE_DIRECTORY_UPN;
    }
    else if ("openLdap".equalsIgnoreCase(directoryName))
    {
      return OPEN_LDAP;
    }
    throw new IllegalArgumentException("Unsupported directory type [" + directoryName + "]");
  }

  public String getObjectClass()
  {
    return objectClass;
  }

  public String getObjectIDAttribute()
  {
    return objectIDAttribute;
  }

  public String getUsernameAttribute()
  {
    return usernameAttribute;
  }

  @Override
  public String toString()
  {
    return "LdapType{" +
        "objectClass='" + objectClass + '\'' +
        ", objectIDAttribute='" + objectIDAttribute + '\'' +
        ", authenticatingAttribute='" + usernameAttribute + '\'' +
        '}';
  }
}
