/*
 * (C)opyright, 2019, ERIN SYSTEMS LLC
 * The source code is protected by copyright laws and international copyright
 * treaties, as well as other intellectual property laws and treaties.
 * All rights reserved.
 */

package mn.erin.lms.legacy.domain.lms.usecase.course.get_course;

import java.util.HashSet;
import java.util.Set;

/**
 * @author Oyungerel Chuluunsukh.
 */
public class UserGroupOutput
{
  private Set<String> users = new HashSet<>();
  private Set<String> groups = new HashSet<>();

  public Set<String> getUsers()
  {
    return users;
  }

  public void setUsers(Set<String> users)
  {
    this.users = users;
  }

  public Set<String> getGroups()
  {
    return groups;
  }

  public void setGroups(Set<String> groups)
  {
    this.groups = groups;
  }
}
