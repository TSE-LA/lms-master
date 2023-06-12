package mn.erin.domain.aim;

import mn.erin.domain.aim.service.AuthenticationService;
import mn.erin.domain.aim.service.AuthorizationService;
import mn.erin.domain.aim.service.TenantIdProvider;
import org.junit.Before;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

/**
 * @author Bat-Erdene Tsogoo.
 */
public abstract class BaseUseCaseTestAbstract
{
  protected static final String TENANT_ID = "-erin-";
  protected static final String USER_ID = "-user-";

  @Mock(name = "authenticationService")
  protected AuthenticationService authenticationService;

  @Mock(name = "authorizationService")
  protected AuthorizationService authorizationService;

  @Mock(name = "tenantIdProvider")
  protected TenantIdProvider tenantIdProvider;

  @Before
  public void before()
  {
    MockitoAnnotations.initMocks(this);
  }

  protected void mockRequiredAuthServices()
  {
    when(tenantIdProvider.getCurrentUserTenantId()).thenReturn(TENANT_ID);
    when(authenticationService.getCurrentUsername()).thenReturn(USER_ID);
    when(authorizationService.hasPermission(anyString(), anyString())).thenReturn(true);
  }
}
