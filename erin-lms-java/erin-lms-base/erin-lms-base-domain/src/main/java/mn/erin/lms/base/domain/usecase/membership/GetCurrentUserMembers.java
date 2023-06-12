package mn.erin.lms.base.domain.usecase.membership;

import java.util.HashMap;
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
import mn.erin.lms.base.domain.usecase.membership.dto.GroupUser;

/**
 * @author Munkh
 */
public class GetCurrentUserMembers implements UseCase<Void, Set<GroupUser>>
{
  private final AccessIdentityManagement accessIdentityManagement;
  private final UserAggregateService userAggregateService;
  private Map<String, UserProfile> users = new HashMap<>();

  public GetCurrentUserMembers(AccessIdentityManagement accessIdentityManagement, UserAggregateService userAggregateService)
  {
    this.accessIdentityManagement = Objects.requireNonNull(accessIdentityManagement, "GetGroupMembers: AccessIdentityManagement cannot be null!!");
    this.userAggregateService = Objects.requireNonNull(userAggregateService, "GetGroupMembers: UserAggregate cannot be null!!");
  }

  @Override
  public Set<GroupUser> execute(Void input) throws UseCaseException
  {
    String departmentId = accessIdentityManagement.getCurrentUserDepartmentId();
    try
    {
      users = userAggregateService.getAllUserAggregates(false).stream()
          .collect(Collectors.toMap(UserAggregate::getUsername, UserAggregate::getProfile));
    }
    catch (Exception e)
    {
      throw new UseCaseException(e.getMessage());
    }

    return accessIdentityManagement.getAllLearners(departmentId).stream()
        .map(learner -> {
          UserProfile profile = users.get(learner);
          if (profile != null)
          {
            return new GroupUser(learner, profile.getUserInfo().getFirstName(), profile.getUserInfo().getLastName());
          }
          else
          {
            return new GroupUser(learner, null, null);
          }
        })
        .collect(Collectors.toSet());
  }
}
