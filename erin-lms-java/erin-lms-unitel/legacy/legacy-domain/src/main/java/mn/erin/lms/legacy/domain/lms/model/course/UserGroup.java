/*
 * (C)opyright, 2019, ERIN SYSTEMS LLC
 * The source code is protected by copyright laws and international copyright
 * treaties, as well as other intellectual property laws and treaties.
 * All rights reserved.
 */

package mn.erin.lms.legacy.domain.lms.model.course;

import java.io.Serializable;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.lang3.Validate;

import mn.erin.domain.base.model.ValueObject;

/**
 * @author Oyungerel Chuluunsukh.
 */
public class UserGroup  implements ValueObject<UserGroup>, Serializable
{
  private static final long serialVersionUID = -352853834236765108L;
  private Set<String> users = new HashSet<>();
  private Set<String> groups = new HashSet<>();

  public UserGroup()
  {
  }

  public UserGroup(Set<String> users, Set<String> groups)
  {
    this.users = Validate.notNull(users, "Users cannot be null!");
    this.groups = Validate.notNull(groups, "Groups cannot be null!");
  }

  public void setUsers(Set<String> users)
  {
    this.users = users;
  }

  public void setGroups(Set<String> groups)
  {
    this.groups = groups;
  }

  public Set<String> getUsers()
  {
    return Collections.unmodifiableSet(users);
  }

  public void addUser(String userName)
  {
    this.users.add(Validate.notBlank(userName, "User id cannot be null!"));
  }

  public Set<String> getGroups()
  {
    return Collections.unmodifiableSet(groups);
  }

  public void addGroup(String groupName)
  {
    this.groups.add(Validate.notBlank(groupName, "Group id cannot be null!"));
  }

  @Override
  public boolean sameValueAs(UserGroup other)
  {
    return other != null
        && (this.users.equals(other.users)
        && this.groups.equals(other.groups));
  }
}
