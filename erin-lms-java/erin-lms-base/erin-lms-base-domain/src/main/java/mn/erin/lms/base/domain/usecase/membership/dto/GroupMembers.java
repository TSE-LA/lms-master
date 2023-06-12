/*
 * (C)opyright, 2019, ERIN SYSTEMS LLC
 * The source code is protected by copyright laws and international copyright
 * treaties, as well as other intellectual property laws and treaties.
 * All rights reserved.
 */

package mn.erin.lms.base.domain.usecase.membership.dto;

import java.util.List;

/**
 * @author Bat-Erdene Tsogoo.
 */
public class GroupMembers
{
  private MembershipGroup group;
  private List<GroupUser> users;

  public GroupMembers(MembershipGroup group, List<GroupUser> users)
  {
    this.group = group;
    this.users = users;
  }

  public MembershipGroup getGroup()
  {
    return group;
  }

  public List<GroupUser> getUsers()
  {
    return users;
  }
}
