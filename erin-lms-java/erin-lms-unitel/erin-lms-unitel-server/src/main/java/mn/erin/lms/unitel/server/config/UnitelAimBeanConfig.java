package mn.erin.lms.unitel.server.config;

import java.util.ResourceBundle;
import javax.inject.Inject;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import mn.erin.aim.config.AimConfigProviderImpl;
import mn.erin.aim.ldap.LdapAttributeMapper;
import mn.erin.aim.ldap.LdapInterface;
import mn.erin.aim.ldap.config.LdapConfig;
import mn.erin.aim.ldap.user.repository.LdapUserIdentityRepository;
import mn.erin.aim.ldap.user.repository.LdapUserProfileRepository;
import mn.erin.aim.ldap.user.repository.LdapUserRepository;
import mn.erin.aim.ldap.user.service.LdapUserAggregateService;
import mn.erin.aim.repository.registry.spring.AimRepositoryRegistryImpl;
import mn.erin.domain.aim.repository.AimRepositoryRegistry;
import mn.erin.domain.aim.repository.RoleRepository;
import mn.erin.domain.aim.repository.UserIdentityRepository;
import mn.erin.domain.aim.repository.UserProfileRepository;
import mn.erin.domain.aim.repository.UserRepository;
import mn.erin.domain.aim.service.AimApplicationDataChecker;
import mn.erin.domain.aim.service.AimConfigProvider;
import mn.erin.domain.aim.service.AimTemporaryFileProvider;
import mn.erin.domain.aim.service.AimTemporaryFileProviderImpl;
import mn.erin.domain.aim.service.AuthenticationService;
import mn.erin.domain.aim.service.AuthorizationService;
import mn.erin.domain.aim.service.RealmTypeProvider;
import mn.erin.domain.aim.service.RealmTypeServiceImpl;
import mn.erin.domain.aim.service.TenantIdProvider;
import mn.erin.domain.aim.service.UserAggregateService;
import mn.erin.lms.aim.shiro.ShiroAuthenticationService;
import mn.erin.lms.aim.shiro.ShiroTenantIdProvider;
import mn.erin.lms.aim.shiro.realm.RealmType;
import mn.erin.lms.base.aim.AccessIdentityManagement;
import mn.erin.lms.base.aim.LmsUserService;
import mn.erin.lms.base.aim.UserServiceImpl;
import mn.erin.lms.base.aim.permission.AlwaysPermittingAuthorizationService;
import mn.erin.lms.base.aim.permission.PermissionProvider;
import mn.erin.lms.base.aim.permission.PermissionProviderImpl;
import mn.erin.lms.base.aim.role.LmsRoleRepository;
import mn.erin.lms.base.domain.repository.LmsRepositoryRegistry;
import mn.erin.lms.base.domain.service.impl.AccessIdentityManagementImpl;
import mn.erin.lms.base.mongo.LmsApplicationDataChecker;
import mn.erin.lms.base.scorm.repository.RuntimeDataRepository;
import mn.erin.lms.unitel.domain.service.UnitelLmsUserResolver;

import static mn.erin.lms.unitel.domain.SetupLms.TENANT_UNITEL;

@Configuration
public class UnitelAimBeanConfig
{
  @Inject
  LmsRepositoryRegistry lmsRepositoryRegistry;

  @Inject
  RuntimeDataRepository runtimeDataRepository;

  @Inject
  ResourceBundle messageSet;

  @Inject
  RealmType realmType;

  @Bean
  public AuthenticationService authenticationService()
  {
    return new ShiroAuthenticationService();
  }

  @Bean
  public AuthorizationService authorizationService()
  {
    return new AlwaysPermittingAuthorizationService();
  }

  @Bean
  public AccessIdentityManagement accessIdentityManagement()
  {
    return new AccessIdentityManagementImpl(authenticationService(), aimRepositoryRegistry(), this.tenantIdProvider());
  }

  @Bean
  public RealmTypeProvider realmTypeProvider()
  {
    return new RealmTypeServiceImpl(realmType.toString());
  }

  @Bean
  public PermissionProvider permissionProvider()
  {
    return new PermissionProviderImpl();
  }

  @Bean
  public LmsUserService lmsUserService()
  {
    return new UserServiceImpl(accessIdentityManagement(), new UnitelLmsUserResolver());
  }

  @Bean
  public RoleRepository roleRepository()
  {
    return new LmsRoleRepository(accessIdentityManagement(), TENANT_UNITEL, messageSet, permissionProvider());
  }

  @Bean
  public UserRepository userRepository()
  {
    return new LdapUserRepository(ldapInterface(), ldapAttributeMapper());
  }

  @Bean
  public UserIdentityRepository userIdentityRepository()
  {
    return new LdapUserIdentityRepository(ldapInterface(), ldapAttributeMapper());
  }

  @Bean
  public UserProfileRepository userProfileRepository()
  {
    return new LdapUserProfileRepository(ldapInterface(), ldapAttributeMapper());
  }

  @Bean
  public AimRepositoryRegistry aimRepositoryRegistry()
  {
    return new AimRepositoryRegistryImpl();
  }

  @Bean
  public UserAggregateService userAggregateService()
  {
    return new LdapUserAggregateService(ldapInterface(), ldapAttributeMapper());
  }

  @Bean
  public LdapConfig ldapConfig()
  {
    return LdapConfig.load(System.getProperties());
  }

  @Bean
  public LdapAttributeMapper ldapAttributeMapper()
  {
    return new LdapAttributeMapper(ldapConfig());
  }

  @Bean
  public LdapInterface ldapInterface()
  {
    return new LdapInterface(ldapConfig());
  }

  @Bean
  public AimConfigProvider aimConfigProvider()
  {
    return new AimConfigProviderImpl();
  }

  @Bean
  public AimApplicationDataChecker aimApplicationDataChecker()
  {
    return new LmsApplicationDataChecker(lmsRepositoryRegistry, runtimeDataRepository);
  }

  @Bean
  public TenantIdProvider tenantIdProvider()
  {
    return new ShiroTenantIdProvider();
  }

  @Bean
  public AimTemporaryFileProvider aimTemporaryFileProvider()
  {
    return new AimTemporaryFileProviderImpl();
  }
}
