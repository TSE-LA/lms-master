package mn.erin.aim.ldap.user.service;

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
import mn.erin.aim.ldap.config.LdapConstants;
import mn.erin.domain.aim.model.user.UserAggregate;
import mn.erin.domain.aim.service.UserAggregateService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * LdapUserAggregateService directly accesses LDAP server for aggregation object.
 */
public class LdapUserAggregateService implements UserAggregateService
{
  private static final Logger LOGGER = LoggerFactory.getLogger(LdapUserAggregateService.class);

  private final LdapInterface ldapInterface;
  private final LdapAttributeMapper mapper;

  public LdapUserAggregateService(LdapInterface ldapInterface, LdapAttributeMapper mapper)
  {
    this.ldapInterface = ldapInterface;
    this.mapper = mapper;
  }

  @Override
  public Collection<UserAggregate> getAllUserAggregates(boolean checkData) throws Exception
  {
    List<UserAggregate> aggregates = new ArrayList<>();
    try
    {
      String filter = MessageFormat.format("(objectClass={0})", ldapInterface.getObjectClass());
      NamingEnumeration<SearchResult> results = ldapInterface.search(filter, controls());
      while (results.hasMore())
      {
        Optional<UserAggregate> profile = mapper.translateToAggregate(results.next().getAttributes());
        profile.ifPresent(aggregates::add);
      }
      return aggregates;
    }
    catch (NamingException e)
    {
      LOGGER.warn("Failed to retrieve LDAP aggregates", e);
      throw new Exception(e.getMessage(), e);
    }
//    return aggregates;
  }

  @Override
  public UserAggregate getUserAggregate(String userId)
  {
    try
    {
      Attributes userAttributes = ldapInterface.searchByBinding("<GUID=" + userId + ">");
      if (userAttributes == null)
      {
        return null;
      }
      Optional<UserAggregate> aggregate = mapper.translateToAggregate(userAttributes);
      if (aggregate.isPresent())
      {
        return aggregate.get();
      }
    }
    catch (NamingException e)
    {
      LOGGER.warn("Failed to get LDAP aggregate for user [ID: {}]", userId, e);
    }
    return null;
  }

  private SearchControls controls()
  {
    SearchControls controls = new SearchControls();
    controls.setSearchScope(SearchControls.SUBTREE_SCOPE);
    controls.setCountLimit(3000);
    controls.setReturningAttributes(new String[] {
        ldapInterface.getUserIdAttribute(),
        ldapInterface.getUsernameAttribute(),
        LdapConstants.ATTR_MAIL,
        LdapConstants.ATTR_TELEPHONE,
        LdapConstants.ATTR_GIVEN_NAME,
        LdapConstants.ATTR_SURNAME
    });
    return controls;
  }
}
