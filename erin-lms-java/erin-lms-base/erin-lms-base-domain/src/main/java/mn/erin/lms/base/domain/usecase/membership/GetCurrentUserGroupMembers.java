package mn.erin.lms.base.domain.usecase.membership;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import mn.erin.domain.aim.model.user.UserAggregate;
import mn.erin.domain.aim.model.user.UserProfile;
import mn.erin.domain.aim.service.UserAggregateService;
import mn.erin.domain.base.usecase.UseCase;
import mn.erin.domain.base.usecase.UseCaseException;
import mn.erin.lms.base.aim.AccessIdentityManagement;
import mn.erin.lms.base.domain.usecase.membership.dto.GroupMembers;
import mn.erin.lms.base.domain.usecase.membership.dto.GroupUser;
import mn.erin.lms.base.domain.usecase.membership.dto.MembershipGroup;

/**
 * @author Oyungerel Chuluunsukh.
 */
public class GetCurrentUserGroupMembers implements UseCase<Void, Set<GroupMembers>>
{
  private final AccessIdentityManagement accessIdentityManagement;
  private final UserAggregateService userAggregateService;

  public GetCurrentUserGroupMembers(AccessIdentityManagement accessIdentityManagement, UserAggregateService userAggregateService)
  {
    this.accessIdentityManagement = Objects.requireNonNull(accessIdentityManagement, "GetGroupMembers: AccessIdentityManagement cannot be null!!");
    this.userAggregateService = Objects.requireNonNull(userAggregateService, "GetGroupMembers: UserAggregate cannot be null!!");
  }

  @Override
  public Set<GroupMembers> execute(Void input) throws UseCaseException
  {
    String username = accessIdentityManagement.getCurrentUsername();
    Set<String> departments = accessIdentityManagement.getSubDepartments(accessIdentityManagement.getUserDepartmentId(username));

    if (departments == null || departments.isEmpty())
    {
      return Collections.emptySet();
    }

    Set<GroupMembers> result = new HashSet<>();
    Map<String, UserProfile> users = null;
    try
    {
      users = userAggregateService.getAllUserAggregates(false).stream()
          .collect(Collectors.toMap(UserAggregate::getUsername, UserAggregate::getProfile));
    }
    catch (Exception e)
    {
      throw new UseCaseException(e.getMessage());
    }

    for (String department : departments)
    {
      String departmentName = accessIdentityManagement.getDepartmentName(department);
      Set<String> learners = accessIdentityManagement.getLearners(department);
      learners.remove(username);
      List<GroupUser> userResponseModel = new ArrayList<>();

      for (String learner : learners)
      {
        UserProfile profile = users.get(learner);

        if (profile != null)
        {
          userResponseModel.add(new GroupUser(learner, profile.getUserInfo().getFirstName(), profile.getUserInfo().getLastName()));
        }
      }

      result.add(new GroupMembers(new MembershipGroup(department, departmentName), userResponseModel));
    }
    return result;
  }
}
