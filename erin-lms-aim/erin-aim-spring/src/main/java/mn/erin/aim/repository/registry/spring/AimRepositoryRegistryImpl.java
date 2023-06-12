package mn.erin.aim.repository.registry.spring;

import java.util.HashMap;
import java.util.Map;

import mn.erin.domain.aim.repository.AimFileSystem;
import mn.erin.domain.aim.repository.AimRepositoryRegistry;
import mn.erin.domain.aim.repository.GroupRepository;
import mn.erin.domain.aim.repository.MembershipRepository;
import mn.erin.domain.aim.repository.RoleRepository;
import mn.erin.domain.aim.repository.UserIdentityRepository;
import mn.erin.domain.aim.repository.UserProfileRepository;
import mn.erin.domain.aim.repository.UserRepository;
import mn.erin.domain.aim.service.UserAggregateService;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

/**
 * @author Munkh
 */
public class AimRepositoryRegistryImpl implements AimRepositoryRegistry, ApplicationContextAware
{
  private final Map<Class<?>, Object> repositories = new HashMap<>();
  private ApplicationContext context;

  @Override
  public UserRepository getUserRepository()
  {
    return (UserRepository) registerGet(UserRepository.class);
  }

  @Override
  public UserIdentityRepository getUserIdentityRepository()
  {
    return (UserIdentityRepository) registerGet(UserIdentityRepository.class);
  }

  @Override
  public UserProfileRepository getUserProfileRepository()
  {
    return (UserProfileRepository) registerGet(UserProfileRepository.class);
  }

  @Override
  public GroupRepository getGroupRepository()
  {
    return (GroupRepository) registerGet(GroupRepository.class);
  }

  @Override
  public MembershipRepository getMembershipRepository()
  {
    return (MembershipRepository) registerGet(MembershipRepository.class);
  }

  @Override
  public RoleRepository getRoleRepository()
  {
    return (RoleRepository) registerGet(RoleRepository.class);
  }

  @Override
  public AimFileSystem getAimFileSystem()
  {
    return (AimFileSystem) registerGet(AimFileSystem.class);
  }

  @Override
  public UserAggregateService getUserAggregateService()
  {
    return (UserAggregateService) registerGet(UserAggregateService.class);
  }

  @Override
  public void setApplicationContext(ApplicationContext applicationContext)
  {
    this.context = applicationContext;
  }

  private Object registerGet(Class<?> repositoryClazz)
  {
    return repositories.computeIfAbsent(repositoryClazz, c -> context.getBean(c));
  }
}
