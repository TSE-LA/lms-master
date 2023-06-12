package mn.erin.aim.repository.mongo.user;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import javax.inject.Inject;

import mn.erin.aim.repository.mongo.user.crud_template.MongoUserRepositoryTemplate;
import mn.erin.aim.repository.mongo.user.document.MongoUser;
import mn.erin.domain.aim.exception.AimRepositoryException;
import mn.erin.domain.aim.model.tenant.TenantId;
import mn.erin.domain.aim.model.user.User;
import mn.erin.domain.aim.model.user.UserId;
import mn.erin.domain.aim.model.user.UserStateChangeSource;
import mn.erin.domain.aim.model.user.UserStatus;
import mn.erin.domain.aim.repository.UserRepository;
import mn.erin.domain.base.model.EntityId;
import org.bson.types.ObjectId;
import org.springframework.stereotype.Repository;

/**
 * @author Munkh
 */
@Repository("userRepository")
public class MongoUserRepository implements UserRepository
{
  private final MongoUserRepositoryTemplate template;

  @Inject
  public MongoUserRepository(MongoUserRepositoryTemplate template)
  {
    this.template = template;
  }

  @Override
  public List<User> getAllUsers(TenantId tenantId)
  {
    return template.findAllByTenantId(tenantId.getId()).stream().map(this::mapToUser).collect(Collectors.toList());
  }

  @Override
  public User createUser(TenantId tenantId)
  {
    MongoUser mongoUser = new MongoUser()
      .setId(new ObjectId())
      .setTenantId(tenantId.getId())
      .setStatus(UserStatus.ACTIVE.name())
      .setSource(UserStateChangeSource.ADMIN.name())
      .setLastModified(LocalDateTime.now());
    return mapToUser(template.save(mongoUser));
  }

  @Override
  public User changeTenant(UserId userId, TenantId newTenantId) throws AimRepositoryException
  {
    Optional<MongoUser> userDocument = template.findById(new ObjectId(userId.getId()));
    if (!userDocument.isPresent())
    {
      throw new AimRepositoryException("User doesnt exit by ID [" + userId + "]");
    }
    userDocument.get().setTenantId(newTenantId.getId());
    MongoUser changed = template.save(userDocument.get());
    return mapToUser(changed);
  }

  @Override
  public boolean delete(UserId userId)
  {
    if (template.existsById(new ObjectId(userId.getId())))
    {
      template.deleteById(new ObjectId(userId.getId()));
      return true;
    }
    return false;
  }

  @Override
  public boolean doesExistById(UserId userId)
  {
    return template.findById(new ObjectId(userId.getId())).isPresent();
  }

  @Override
  public void changeState(UserId userId, UserStatus status, UserStateChangeSource source) throws AimRepositoryException
  {
    Optional<MongoUser> userDocument = template.findById(new ObjectId(userId.getId()));
    if (!userDocument.isPresent())
    {
      throw new AimRepositoryException("User doesnt exit by ID [" + userId + "]");
    }
    template.save(userDocument.get()
      .setStatus(status.name())
      .setSource(source.name())
      .setLastModified(LocalDateTime.now())
    );
  }

  @Override
  public User findById(EntityId entityId)
  {
    return template.findById(new ObjectId(entityId.getId()))
      .map(this::mapToUser)
      .orElse(null);
  }

  @Override
  public Collection<User> findAll()
  {
    return template.findAll().stream().map(this::mapToUser).collect(Collectors.toList());
  }

  private User mapToUser(MongoUser mongoUser)
  {
    User user = new User(UserId.valueOf(mongoUser.getId().toString()), TenantId.valueOf(mongoUser.getTenantId()));
    user.setStatus(UserStatus.valueOf(mongoUser.getStatus()));
    user.setStateLastModified(mongoUser.getLastModified());
    user.setStateChangeSource(UserStateChangeSource.valueOf(mongoUser.getSource()));
    return user;
  }
}
