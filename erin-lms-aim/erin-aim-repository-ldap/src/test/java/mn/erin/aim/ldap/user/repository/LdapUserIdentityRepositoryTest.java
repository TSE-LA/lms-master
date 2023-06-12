package mn.erin.aim.ldap.user.repository;

import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.SearchControls;

import mn.erin.aim.ldap.LdapAttributeMapper;
import mn.erin.aim.ldap.LdapInterface;
import mn.erin.aim.ldap.config.LdapConfig;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

/**
 * @author mLkhagvasuren
 */
public class LdapUserIdentityRepositoryTest
{
  private LdapUserIdentityRepository repository;
  private LdapInterface ldapInterface;

  private static final SearchControls CONTROLS = new SearchControls();

  static
  {
    CONTROLS.setSearchScope(SearchControls.SUBTREE_SCOPE);
    CONTROLS.setReturningAttributes(new String[] { "objecGUID", "sAMAccountName" });
  }

  @Before
  public void setUp() throws Exception
  {
    ldapInterface = mock(LdapInterface.class);
    repository = new LdapUserIdentityRepository(ldapInterface, new LdapAttributeMapper(new LdapConfig()));
  }

  @Test
  public void exist_by_username() throws NamingException
  {
    doReturn("userPrincipalName").when(ldapInterface).getUsernameAttribute();
    doReturn("organizationalPerson").when(ldapInterface).getObjectClass();

    NamingEnumeration enumeration = mock(NamingEnumeration.class);
    doReturn(true).when(enumeration).hasMore();
    doReturn(enumeration).when(ldapInterface).search(any(), any());

    repository.existByUsername("TEST_USER@TEST_ORG", null);
    ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);
    verify(ldapInterface).search(captor.capture(), any());
    assertEquals("(&(objectClass=organizationalPerson)(userPrincipalName=TEST_USER@TEST_ORG))", captor.getValue());
  }
}
