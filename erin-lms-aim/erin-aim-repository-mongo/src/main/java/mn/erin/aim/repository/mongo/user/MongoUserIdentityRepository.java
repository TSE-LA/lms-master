package mn.erin.aim.repository.mongo.user;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import javax.inject.Inject;

import mn.erin.aim.repository.mongo.user.crud_template.MongoUserIdentityRepositoryTemplate;
import mn.erin.aim.repository.mongo.user.document.MongoUserIdentity;
import mn.erin.domain.aim.exception.AimRepositoryException;
import mn.erin.domain.aim.model.user.UserId;
import mn.erin.domain.aim.model.user.UserIdentity;
import mn.erin.domain.aim.model.user.UserIdentityId;
import mn.erin.domain.aim.model.user.UserIdentitySource;
import mn.erin.domain.aim.repository.UserIdentityRepository;
import mn.erin.domain.base.model.EntityId;
import org.bson.types.ObjectId;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.stereotype.Repository;

/**
 * @author Munkh
 */
@Repository("userIdentityRepository")
public class MongoUserIdentityRepository implements UserIdentityRepository
{
  private final MongoUserIdentityRepositoryTemplate template;

  @Inject
  public MongoUserIdentityRepository(MongoUserIdentityRepositoryTemplate template)
  {
    this.template = template;
  }

  @Override
  public UserIdentity findById(EntityId entityId)
  {
    return template.findById(new ObjectId(entityId.getId())).map(this::mapToUserIdentity).orElse(null);
  }

  @Override
  public List<UserIdentity> findAll()
  {
    return template.findAll().stream().map(this::mapToUserIdentity).collect(Collectors.toList());
  }

  @Override
  public void create(UserIdentity userIdentity) throws AimRepositoryException
  {
    if (existByUsername(userIdentity.getUsername(), userIdentity.getUserIdentitySource()))
    {
      throw new AimRepositoryException("Username with same source already exists.");
    }

    template.save(
        new MongoUserIdentity()
            .setId(new ObjectId())
            .setUserId(new ObjectId(userIdentity.getUserId().getId()))
            .setUsername(userIdentity.getUsername())
            .setPassword(userIdentity.getPassword())
            .setSource(userIdentity.getUserIdentitySource().name())
    );
  }

  @Override
  public UserIdentity getUserIdentityByUsername(String username, UserIdentitySource... sources)
  {
    if (sources == null || sources.length == 0)
    {
      sources = UserIdentitySource.values();
    }
    for (UserIdentitySource source : sources)
    {
      Optional<MongoUserIdentity> byUsernameAndSource = template.findByUsernameAndSource(username, source.name());
      if (byUsernameAndSource.isPresent())
      {
        return mapToUserIdentity(byUsernameAndSource.get());
      }
    }
    return null;
  }

  @Override
  public List<UserIdentity> getAllBySource(UserIdentitySource source)
  {
    List<MongoUserIdentity> result = template.findAllBySource(source.name());
    return result.stream().map(this::mapToUserIdentity).collect(Collectors.toList());
  }

  @Override
  public UserIdentity getIdentity(UserId userId)
  {
    return getIdentity(userId, UserIdentitySource.OWN/*other sources are not supported at the moment*/);
  }

  @Override
  public UserIdentity getIdentity(UserId userId, UserIdentitySource source)
  {
    return template.findByUserIdAndSource(new ObjectId(userId.getId()), source.name())
        .map(this::mapToUserIdentity).orElse(null);
  }

  @Override
  public boolean existByUsername(String username, UserIdentitySource source)
  {
    List<MongoUserIdentity> identityDocument =  template.findAll(byUsernameAndSource(username, source));
    return identityDocument.stream().anyMatch(user-> user.getUsername().equalsIgnoreCase(username));
  }

  @Override
  public void update(UserIdentity userIdentity) throws AimRepositoryException
  {
    Optional<MongoUserIdentity> identityDocument = template.findByUserIdAndSource(
        new ObjectId(userIdentity.getUserId().getId()),
        userIdentity.getUserIdentitySource().name()
    );

    if (!identityDocument.isPresent())
    {
      throw new AimRepositoryException("User identity not found!");
    }

    MongoUserIdentity mongoIdentity = identityDocument.get()
        .setUserId(new ObjectId(userIdentity.getUserId().getId()))
        .setUsername(userIdentity.getUsername())
        .setPassword(userIdentity.getPassword())
      .setSource(userIdentity.getUserIdentitySource().name());
    template.save(mongoIdentity);
  }

  @Override
  public boolean delete(UserId userId)
  {
    template.deleteAllByUserId(new ObjectId(userId.getId()));
    return template.findAllByUserId(new ObjectId(userId.getId())).isEmpty();
  }

  @Override
  public void delete(UserIdentity userIdentity)
  {
    template.deleteByUserIdAndSource(new ObjectId(userIdentity.getUserId().getId()), userIdentity.getUserIdentitySource().name());
  }

  private UserIdentity mapToUserIdentity(MongoUserIdentity mongoUserIdentity)
  {
    return new UserIdentity(
      new UserIdentityId(mongoUserIdentity.getId().toString()),
      UserId.valueOf(mongoUserIdentity.getUserId().toString()),
      mongoUserIdentity.getUsername(),
      mongoUserIdentity.getPassword(),
      UserIdentitySource.valueOf(mongoUserIdentity.getSource())
    );
  }

  private static Example<MongoUserIdentity> byUsernameAndSource(String username, UserIdentitySource source)
  {
    return Example.of(
        new MongoUserIdentity().setUsername(username).setSource(source.name()),
        ExampleMatcher.matching().withIgnoreCase());
  }
}
