/*
 * (C)opyright, 2019, ERIN SYSTEMS LLC
 * The source code is protected by copyright laws and international copyright
 * treaties, as well as other intellectual property laws and treaties.
 * All rights reserved.
 */

package mn.erin.lms.base.rest.api.aim.model;

import java.util.List;

/**
 * @author Bat-Erdene Tsogoo.
 */
public class RestGroupMembers
{
  private RestMemebshipGroup group;
  private List<RestGroupUser> users;

  public RestGroupMembers(RestMemebshipGroup group, List<RestGroupUser> users)
  {
    this.group = group;
    this.users = users;
  }

  public RestMemebshipGroup getGroup()
  {
    return group;
  }

  public List<RestGroupUser> getUsers()
  {
    return users;
  }
}
