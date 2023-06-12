package mn.erin.lms.jarvis.server.config;

import java.util.ResourceBundle;
import javax.inject.Inject;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import mn.erin.aim.config.AimConfigProviderImpl;
import mn.erin.aim.ldap.LdapAttributeMapper;
import mn.erin.aim.ldap.LdapInterface;
import mn.erin.aim.ldap.config.LdapConfig;
import mn.erin.aim.ldap.user.repository.LdapUserIdentityRepository;
import mn.erin.aim.ldap.user.repository.LdapUserProfileRepository;
import mn.erin.aim.ldap.user.repository.LdapUserRepository;
import mn.erin.aim.ldap.user.service.LdapUserAggregateService;
import mn.erin.aim.repository.mongo.AimMongoBeanConfig;
import mn.erin.aim.repository.mongo.user.MongoUserIdentityRepository;
import mn.erin.aim.repository.mongo.user.MongoUserProfileRepository;
import mn.erin.aim.repository.mongo.user.MongoUserRepository;
import mn.erin.aim.repository.mongo.user.crud_template.MongoUserIdentityRepositoryTemplate;
import mn.erin.aim.repository.mongo.user.crud_template.MongoUserProfileRepositoryTemplate;
import mn.erin.aim.repository.mongo.user.crud_template.MongoUserRepositoryTemplate;
import mn.erin.aim.repository.registry.spring.AimRepositoryRegistryImpl;
import mn.erin.domain.aim.repository.AimFileSystem;
import mn.erin.domain.aim.repository.AimRepositoryRegistry;
import mn.erin.domain.aim.repository.AlfrescoAimFileSystem;
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
import mn.erin.domain.aim.service.UserAggregateServiceImpl;
import mn.erin.domain.aim.usecase.user.CreateInitialAdminUser;
import mn.erin.domain.base.usecase.UseCaseException;
import mn.erin.domain.dms.repository.DocumentRepository;
import mn.erin.domain.dms.repository.FolderRepository;
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
import mn.erin.lms.base.domain.service.impl.JarvisLmsUserResolver;
import mn.erin.lms.base.mongo.LmsApplicationDataChecker;
import mn.erin.lms.base.scorm.repository.RuntimeDataRepository;

import static mn.erin.lms.jarvis.domain.SetupLms.TENANT_JARVIS;

/**
 * Overrides user and profile repository by switchable options.
 */
@Configuration
@Import(AimMongoBeanConfig.class/*Platform*/)
public class JarvisAimBeanConfig
{
  @Inject
  LmsRepositoryRegistry lmsRepositoryRegistry;

  @Inject
  RuntimeDataRepository runtimeDataRepository;

  @Inject
  RealmType realmType;

  @Inject
  ResourceBundle messageSet;

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
  public PermissionProvider permissionProvider()
  {
    return new PermissionProviderImpl();
  }

  @Bean
  public LmsUserService lmsUserService()
  {
    return new UserServiceImpl(accessIdentityManagement(), new JarvisLmsUserResolver());
  }

  @Bean
  public RoleRepository roleRepository()
  {
    return new LmsRoleRepository(accessIdentityManagement(), TENANT_JARVIS, messageSet,permissionProvider());
  }

  @Bean
  public UserRepository userRepository(MongoUserRepositoryTemplate mongoUserRepositoryTemplate)
  {
    if (realmType == RealmType.MONGO)
    {
      return new MongoUserRepository(mongoUserRepositoryTemplate);
    }
    else
    {
      return new LdapUserRepository(ldapInterface(), ldapAttributeMapper());
    }
  }

  @Bean
  public UserIdentityRepository userIdentityRepository(MongoUserIdentityRepositoryTemplate mongoUserIdentityRepositoryTemplate)
  {
    if (realmType == RealmType.MONGO)
    {
      return new MongoUserIdentityRepository(mongoUserIdentityRepositoryTemplate);
    }
    else
    {
      return new LdapUserIdentityRepository(ldapInterface(), ldapAttributeMapper());
    }
  }

  @Bean
  public UserProfileRepository userProfileRepository(MongoUserProfileRepositoryTemplate mongoUserProfileRepositoryTemplate)
  {
    if (realmType == RealmType.MONGO)
    {
      return new MongoUserProfileRepository(mongoUserProfileRepositoryTemplate);
    }
    else
    {
      return new LdapUserProfileRepository(ldapInterface(), ldapAttributeMapper());
    }
  }

  @Bean
  public AimRepositoryRegistry aimRepositoryRegistry()
  {
    return new AimRepositoryRegistryImpl();
  }

  @Bean
  public UserAggregateService userAggregateService()
  {
    if (realmType == RealmType.MONGO)
    {
      return new UserAggregateServiceImpl(aimRepositoryRegistry(), this.tenantIdProvider(), aimApplicationDataChecker());
    }
    else
    {
      return new LdapUserAggregateService(ldapInterface(), ldapAttributeMapper());
    }
  }
  @Bean
  public RealmTypeProvider realmTypeProvider()
  {
    return new RealmTypeServiceImpl(realmType.toString());
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
  public LdapConfig ldapConfig()
  {
    return LdapConfig.load(System.getProperties());
  }

  @Bean
  public AimConfigProvider aimConfigProvider()
  {
    return new AimConfigProviderImpl();
  }

  @Bean
  public CreateInitialAdminUser createInitialAdminUser() throws UseCaseException
  {
    // run once bean
    if (realmType == RealmType.MONGO)
    {
      CreateInitialAdminUser createInitialAdmin = new CreateInitialAdminUser(aimRepositoryRegistry(), aimConfigProvider());
      createInitialAdmin.execute(null);
      return createInitialAdmin;
    }
    return null;
  }

  @Bean
  public TenantIdProvider tenantIdProvider()
  {
    return new ShiroTenantIdProvider();
  }

  @Bean
  public AimApplicationDataChecker aimApplicationDataChecker()
  {
    return new LmsApplicationDataChecker(lmsRepositoryRegistry, runtimeDataRepository);
  }

  @Bean
  public AimTemporaryFileProvider aimTemporaryFileProvider(){
    return new AimTemporaryFileProviderImpl();
  }

  @Bean
  public AimFileSystem aimFileSystem(FolderRepository folderRepository, DocumentRepository documentRepository)
  {
    return new AlfrescoAimFileSystem(documentRepository, folderRepository);
  }
}
