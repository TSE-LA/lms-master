package mn.erin.aim.ldap.user.repository;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attributes;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;

import mn.erin.aim.ldap.LdapAttributeMapper;
import mn.erin.aim.ldap.LdapInterface;
import mn.erin.aim.ldap.config.LdapConstants;
import mn.erin.domain.aim.model.user.UserId;
import mn.erin.domain.aim.model.user.UserProfile;
import mn.erin.domain.aim.repository.readonly.ReadOnlyUserProfileRepository;
import mn.erin.domain.base.model.EntityId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LdapUserProfileRepository extends ReadOnlyUserProfileRepository
{
  private static final Logger LOGGER = LoggerFactory.getLogger(LdapUserProfileRepository.class);

  private final LdapInterface ldapInterface;
  private final LdapAttributeMapper mapper;

  public LdapUserProfileRepository(LdapInterface ldapInterface, LdapAttributeMapper mapper)
  {
    this.ldapInterface = ldapInterface;
    this.mapper = mapper;
  }

  @Override
  public UserProfile findById(EntityId entityId)
  {

    throw new UnsupportedOperationException("User profile doesnt have own ID for LDAP, use find by userID methods instead");
  }

  @Override
  public Collection<UserProfile> findAll()
  {
    List<UserProfile> profiles = new ArrayList<>();
    try
    {
      String filter = MessageFormat.format("(objectClass={0})", ldapInterface.getObjectClass());
      NamingEnumeration<SearchResult> results = ldapInterface.search(filter, controls());
      while (results.hasMore())
      {
        Optional<UserProfile> profile = mapper.translateToProfile(results.next().getAttributes());
        profile.ifPresent(profiles::add);
      }
      return profiles;
    }
    catch (NamingException e)
    {
      LOGGER.warn("Failed to retrieve LDAP profiles", e);
    }
    return Collections.emptyList();
  }

  @Override
  public UserProfile findByUserId(UserId userId)
  {
    try
    {
      Attributes attributes = ldapInterface.searchByBinding("<GUID=" + userId.getId() + ">");
      if (attributes == null)
      {
        return null;
      }
      Optional<UserProfile> profile = mapper.translateToProfile(attributes);
      if (profile.isPresent())
      {
        return profile.get();
      }
    }
    catch (NamingException e)
    {
      LOGGER.warn("Failed to get LDAP profile for user [ID: {}]", userId.getId(), e);
    }
    return null;
  }

  @Override
  public UserProfile findByPhoneNumber(String phoneNumber)
  {
    String filter = MessageFormat.format("(&(objectClass={0})({1}={2}))",
        ldapInterface.getObjectClass(), LdapConstants.ATTR_TELEPHONE, phoneNumber);
    NamingEnumeration<SearchResult> results;
    try
    {
      results = ldapInterface.search(filter, controls());
      if (results.hasMore())
      {
        Optional<UserProfile> profile = mapper.translateToProfile(results.next().getAttributes());
        if (profile.isPresent())
        {
          return profile.get();
        }
      }
      else
      {
        return null;
      }
    }
    catch (NamingException e)
    {
      LOGGER.debug("Failed to get LDAP profile for phone number [ID: {}]", phoneNumber, e);
    }
    return null;
  }

  private SearchControls controls()
  {
    SearchControls controls = new SearchControls();
    controls.setSearchScope(SearchControls.SUBTREE_SCOPE);
    controls.setReturningAttributes(new String[] {
        ldapInterface.getUserIdAttribute(),
        LdapConstants.ATTR_MAIL,
        LdapConstants.ATTR_TELEPHONE,
        LdapConstants.ATTR_GIVEN_NAME,
        LdapConstants.ATTR_SURNAME
    });
    return controls;
  }
}
