/*
 * (C)opyright, 2020, ERIN SYSTEMS LLC
 * The source code is protected by copyright laws and international copyright
 * treaties, as well as other intellectual property laws and treaties.
 * All rights reserved.
 */

package mn.erin.aim.repository.memory;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import mn.erin.domain.aim.exception.AimRepositoryException;
import mn.erin.domain.aim.model.tenant.TenantId;
import mn.erin.domain.aim.model.user.User;
import mn.erin.domain.aim.model.user.UserId;
import mn.erin.domain.aim.model.user.UserStateChangeSource;
import mn.erin.domain.aim.model.user.UserStatus;
import mn.erin.domain.aim.repository.UserRepository;
import mn.erin.domain.base.model.EntityId;
import org.springframework.stereotype.Repository;

/**
 * @author EBazarragchaa
 */
@Repository
public class MemoryUserRepository implements UserRepository
{
  private static final String TENANT_ID = "xac";
  private static List<User> users = new ArrayList<>();

  private static final User EBANK = new User(UserId.valueOf("ebank"), TenantId.valueOf(TENANT_ID));
  private static final User ADMIN = new User(UserId.valueOf("admin"), TenantId.valueOf(TENANT_ID));
  private static final User TEST_USER = new User(UserId.valueOf("testuser"), TenantId.valueOf(TENANT_ID));

  static
  {
    users.add(EBANK);
    users.add(ADMIN);
    users.add(TEST_USER);
  }

  public MemoryUserRepository()
  {
    // system always

  }

  @Override
  public List<User> getAllUsers(TenantId tenantId)
  {
    List<User> userList = new ArrayList<>();

    for (User user : users)
    {
      if (user.getTenantId().equals(tenantId))
      {
        userList.add(user);
      }
    }
    return userList;
  }

  @Override
  public User createUser(TenantId tenantId)
  {
    String userId = UUID.randomUUID().toString();
    User user = new User(UserId.valueOf(userId), tenantId);
    user.setStateChangeSource(UserStateChangeSource.ADMIN);
    user.setStateLastModified(LocalDateTime.now());
    users.add(user);
    return user;
  }

  @Override
  public boolean delete(UserId userId)
  {
    User user = findById(userId);
    if (user != null)
    {
      users.remove(user);
      return true;
    }

    return false;
  }

  @Override
  public boolean doesExistById(UserId userId)
  {
    for (User user : users)
    {
      if (user.getUserId().equals(userId))
      {
        return true;
      }
    }
    return false;
  }

  @Override
  public void changeState(UserId userId, UserStatus status, UserStateChangeSource source) throws AimRepositoryException
  {
    User user = findById(userId);
    if (user == null)
    {
      throw new AimRepositoryException("User doesn't exist [" + userId + "]");
    }
    user.setStatus(status);
    user.setStateChangeSource(source);
  }

  @Override
  public User changeTenant(UserId userId, TenantId newTenantId)
  {
    User user = findById(userId);
    if (user == null)
    {
      return null;
    }
    User clone = new User(userId, newTenantId, user.getStatus(), user.getStateChangeSource(), LocalDateTime.now());
    int index = users.indexOf(user);
    users.remove(index);
    users.add(index, clone);
    return clone;
  }

  @Override
  public User findById(EntityId entityId)
  {
    return users.stream().filter(user -> user.getUserId().equals(entityId)).findFirst().orElse(null);
  }

  @Override
  public Collection<User> findAll()
  {
    return Collections.unmodifiableList(users);
  }
}
