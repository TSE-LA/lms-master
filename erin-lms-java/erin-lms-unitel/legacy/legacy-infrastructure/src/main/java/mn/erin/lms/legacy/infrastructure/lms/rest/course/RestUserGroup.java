/*
 * (C)opyright, 2019, ERIN SYSTEMS LLC
 * The source code is protected by copyright laws and international copyright
 * treaties, as well as other intellectual property laws and treaties.
 * All rights reserved.
 */

package mn.erin.lms.legacy.infrastructure.lms.rest.course;

import java.util.HashSet;
import java.util.Set;


/**
 * @author Oyungerel Chuluunsukh.
 */
public class RestUserGroup
{
  private Set<String> users =  new HashSet<>();
  private Set<String> groups =  new HashSet<>();
  private boolean groupEnroll;

  public Set<String> getUsers()
  {
    return users;
  }

  public Set<String> getGroups()
  {
    return groups;
  }

  public void setGroups(Set<String> groups)
  {
    this.groups = groups;
  }

  public void setUsers(Set<String> users)
  {
    this.users = users;
  }

  public boolean hasGroupEnroll()
  {
    return groupEnroll;
  }

  public void setGroupEnroll(boolean groupEnroll)
  {
    this.groupEnroll = groupEnroll;
  }
}
