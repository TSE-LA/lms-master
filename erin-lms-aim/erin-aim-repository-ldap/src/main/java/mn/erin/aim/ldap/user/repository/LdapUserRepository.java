package mn.erin.aim.ldap.user.repository;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attributes;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;

import mn.erin.aim.ldap.LdapAttributeMapper;
import mn.erin.aim.ldap.LdapInterface;
import mn.erin.domain.aim.model.tenant.TenantId;
import mn.erin.domain.aim.model.user.User;
import mn.erin.domain.aim.model.user.UserId;
import mn.erin.domain.aim.repository.readonly.ReadOnlyUserRepository;
import mn.erin.domain.base.model.EntityId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Bat-Erdene Tsogoo.
 */
public class LdapUserRepository extends ReadOnlyUserRepository
{
  private static final Logger LOGGER = LoggerFactory.getLogger(LdapUserRepository.class);

  private final LdapInterface ldapInterface;
  private final LdapAttributeMapper mapper;

  public LdapUserRepository(LdapInterface ldapInterface, LdapAttributeMapper mapper)
  {
    this.ldapInterface = ldapInterface;
    this.mapper = mapper;
  }

  @Override
  public User findById(EntityId entityId)
  {
    try
    {
      Attributes userAttributes = ldapInterface.searchByBinding("<GUID=" + entityId.getId() + ">");
      if (userAttributes == null)
      {
        return null;
      }
      Optional<User> user = mapper.translateToUser(userAttributes);
      if (user.isPresent())
      {
        return user.get();
      }
    }
    catch (NamingException e)
    {
      LOGGER.warn("Failed to get LDAP user [{}]", entityId, e);
    }
    return null;
  }

  @Override
  public Collection<User> findAll()
  {
    return getAllUsers(null);
  }

  @Override
  public List<User> getAllUsers(TenantId unused)
  {
    List<User> users = new ArrayList<>();
    try
    {
      String filter = MessageFormat.format("(objectClass={0})", ldapInterface.getObjectClass());
      NamingEnumeration<SearchResult> results = ldapInterface.search(filter, controls());
      while (results.hasMore())
      {
        Optional<User> user = mapper.translateToUser(results.next().getAttributes());
        user.ifPresent(users::add);
      }
      LOGGER.info("############ LDAP user count: {}", users.size());
      return users;
    }
    catch (NamingException e)
    {
      LOGGER.error("Failed to retrieve LDAP users", e);
    }
    return users;
  }

  @Override
  public boolean doesExistById(UserId userId)
  {
    try
    {
      return ldapInterface.searchByBinding("<GUID=" + userId.getId() + ">") != null;
    }
    catch (NamingException e)
    {
      LOGGER.error("Failed to check user by ID [{}]", userId, e);
    }
    return false;
  }

  private SearchControls controls()
  {
    SearchControls controls = new SearchControls();
    controls.setSearchScope(SearchControls.SUBTREE_SCOPE);
    controls.setReturningAttributes(new String[] { ldapInterface.getUserIdAttribute() });
    return controls;
  }
}
