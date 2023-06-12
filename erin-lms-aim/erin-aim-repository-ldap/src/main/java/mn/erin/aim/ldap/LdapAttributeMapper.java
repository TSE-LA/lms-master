package mn.erin.aim.ldap;

import java.time.LocalDateTime;
import java.util.Optional;
import javax.naming.NamingException;
import javax.naming.directory.Attributes;

import mn.erin.aim.ldap.config.LdapConfig;
import mn.erin.aim.ldap.config.LdapConstants;
import mn.erin.domain.aim.model.tenant.TenantId;
import mn.erin.domain.aim.model.user.User;
import mn.erin.domain.aim.model.user.UserAggregate;
import mn.erin.domain.aim.model.user.UserContact;
import mn.erin.domain.aim.model.user.UserId;
import mn.erin.domain.aim.model.user.UserIdentity;
import mn.erin.domain.aim.model.user.UserIdentitySource;
import mn.erin.domain.aim.model.user.UserInfo;
import mn.erin.domain.aim.model.user.UserProfile;
import mn.erin.domain.aim.model.user.UserStateChangeSource;
import mn.erin.domain.aim.model.user.UserStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * KEEP IN SYNC WITH REPOSITORY SEARCH CONTROLS.
 */
@SuppressWarnings({ "OptionalGetWithoutIsPresent", "java:S3655" })
public final class LdapAttributeMapper
{
  private static final Logger LOGGER = LoggerFactory.getLogger(LdapAttributeMapper.class);

  private final LdapConfig ldapConfig;

  public LdapAttributeMapper(LdapConfig ldapConfig)
  {
    this.ldapConfig = ldapConfig;
  }

  public Optional<UserAggregate> translateToAggregate(Attributes attributes)
  {
    String userId = getOrNull(attributes, ldapConfig.getObjectIDAttribute());
    String username = getOrNull(attributes, ldapConfig.getUsernameAttribute());
    if (userId == null || username == null)
    {
      return Optional.empty();
    }
    User user = translateToUser(attributes).get();
    UserIdentity identity = translateToIdentity(attributes).get();
    UserProfile profile = translateToProfile(attributes).get();
    return Optional.of(new UserAggregate(user, identity, profile));
  }

  public Optional<User> translateToUser(Attributes attributes)
  {
    String userId = getOrNull(attributes, ldapConfig.getObjectIDAttribute());
    if (userId == null)
    {
      return Optional.empty();
    }
    User user = new User(UserId.valueOf(userId), TenantId.valueOf(ldapConfig.getTenantId()));
    user.setStatus(UserStatus.ACTIVE);
    user.setStateChangeSource(UserStateChangeSource.SYSTEM);
    user.setStateLastModified(LocalDateTime.MIN);
    return Optional.of(user);
  }

  public Optional<UserIdentity> translateToIdentity(Attributes attributes)
  {
    String userId = getOrNull(attributes, ldapConfig.getObjectIDAttribute());
    String username = getOrNull(attributes, ldapConfig.getUsernameAttribute());
    if (userId == null || username == null)
    {
      return Optional.empty();
    }
    return Optional.of(new UserIdentity(
      UserId.valueOf(userId),
      username,
      null/*do transfer password*/,
      UserIdentitySource.LDAP
    ));
  }

  public Optional<UserProfile> translateToProfile(Attributes attributes)
  {
    String userId = getOrNull(attributes, ldapConfig.getObjectIDAttribute());
    if (userId == null)
    {
      return Optional.empty();
    }
    UserInfo info = new UserInfo(
      getOrNull(attributes, LdapConstants.ATTR_GIVEN_NAME),
      getOrNull(attributes, LdapConstants.ATTR_SURNAME));
    info.setDisplayName(getOrNull(attributes, LdapConstants.ATTR_DISPLAY_NAME));
    UserContact contact = new UserContact(
      getOrNull(attributes, LdapConstants.ATTR_MAIL),
      getOrNull(attributes, LdapConstants.ATTR_TELEPHONE));
    return Optional.of(new UserProfile(UserId.valueOf(userId), info, contact));
  }

  private String getOrNull(Attributes attributes, String attributeName)
  {
    try
    {
      if (attributes.get(attributeName) == null)
      {
        return null;
      }
      if (attributeName.equals(ldapConfig.getObjectIDAttribute()))
      {
        // object IDs return as byte array (Octet String)
        return ObjectGuids.convertToUUID((byte[])attributes.get(attributeName).get());
      }
      return String.valueOf(attributes.get(attributeName).get());
    }
    catch (NamingException e)
    {
      LOGGER.error("Failed to get attribute [{}]", attributeName, e);
    }
    return null;
  }
}
