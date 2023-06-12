package mn.erin.aim.ldap.user.repository;

import java.text.MessageFormat;
import java.util.ArrayList;
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
import mn.erin.domain.aim.model.user.UserId;
import mn.erin.domain.aim.model.user.UserIdentity;
import mn.erin.domain.aim.model.user.UserIdentitySource;
import mn.erin.domain.aim.repository.readonly.ReadOnlyUserIdentityRepository;
import mn.erin.domain.base.model.EntityId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LdapUserIdentityRepository extends ReadOnlyUserIdentityRepository
{
  private static final Logger LOGGER = LoggerFactory.getLogger(LdapUserIdentityRepository.class);

  private final LdapInterface ldapInterface;
  private final LdapAttributeMapper mapper;

  public LdapUserIdentityRepository(LdapInterface ldapInterface, LdapAttributeMapper mapper)
  {
    this.ldapInterface = ldapInterface;
    this.mapper = mapper;
  }

  @Override
  public UserIdentity findById(EntityId entityId)
  {
    throw new UnsupportedOperationException("User identity doesnt have own ID for LDAP, use find by userID methods instead");
  }

  @Override
  public List<UserIdentity> findAll()
  {
    return getAllBySource(UserIdentitySource.LDAP);
  }

  @Override
  public List<UserIdentity> getAllBySource(UserIdentitySource userIdentitySource)
  {
    if (userIdentitySource != UserIdentitySource.LDAP)
    {
      return Collections.emptyList();
    }
    try
    {
      List<UserIdentity> identities = new ArrayList<>();
      String query = MessageFormat.format("(objectClass={0})", ldapInterface.getObjectClass());
      NamingEnumeration<SearchResult> results = ldapInterface.search(query, controls());
      while (results.hasMore())
      {
        Optional<UserIdentity> identity = mapper.translateToIdentity(results.next().getAttributes());
        identity.ifPresent(identities::add);
      }
      return identities;
    }
    catch (NamingException e)
    {
      LOGGER.warn("Failed to retrieve LDAP identities", e);
    }
    return Collections.emptyList();
  }

  @Override
  public UserIdentity getIdentity(UserId userId)
  {
    try
    {
      Attributes attributes = ldapInterface.searchByBinding("<GUID=" + userId.getId() + ">");
      if (attributes == null)
      {
        return null;
      }
      Optional<UserIdentity> identity = mapper.translateToIdentity(attributes);
      if (identity.isPresent())
      {
        return identity.get();
      }
    }
    catch (NamingException e)
    {
      LOGGER.warn("Failed to get LDAP identity for user [ID: {}]", userId.getId(), e);
    }
    return null;
  }

  @Override
  public UserIdentity getIdentity(UserId userId, UserIdentitySource source)
  {
    return source == UserIdentitySource.LDAP ? getIdentity(userId) : null;
  }

  @Override
  public UserIdentity getUserIdentityByUsername(String username, UserIdentitySource... unused)
  {
    try
    {
      NamingEnumeration<SearchResult> results = searchByUsername(username);
      if (!results.hasMore())
      {
        return null;
      }
      Optional<UserIdentity> identity = mapper.translateToIdentity(results.next().getAttributes());
      if (identity.isPresent())
      {
        return identity.get();
      }
    }
    catch (NamingException e)
    {
      LOGGER.warn("Failed to get LDAP identity for user [{}]", username, e);
    }
    return null;
  }

  @Override
  public boolean existByUsername(String username, UserIdentitySource source)
  {
    try
    {
      return searchByUsername(username).hasMore();
    }
    catch (NamingException e)
    {
      LOGGER.warn("Failed to get LDAP identity for user [{}]", username, e);
    }
    return false;
  }

  private NamingEnumeration<SearchResult> searchByUsername(String username) throws NamingException
  {
    // example filter "(&(objectClass=organizationalPerson)(sAMAccountName=<username><@suffix>))"
    String filter = MessageFormat.format("(&(objectClass={0})({1}={2}))",
        ldapInterface.getObjectClass(), ldapInterface.getUsernameAttribute(), username);
    return ldapInterface.search(filter, controls());
  }

  // visible for testing
  SearchControls controls()
  {
    SearchControls controls = new SearchControls();
    controls.setSearchScope(SearchControls.SUBTREE_SCOPE);
    controls.setReturningAttributes(new String[] {
        ldapInterface.getUserIdAttribute(),
        ldapInterface.getUsernameAttribute()
    });
    return controls;
  }
}
