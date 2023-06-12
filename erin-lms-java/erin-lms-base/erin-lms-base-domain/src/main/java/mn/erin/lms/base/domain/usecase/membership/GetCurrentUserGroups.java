package mn.erin.lms.base.domain.usecase.membership;

import java.util.Collections;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import mn.erin.domain.base.usecase.UseCase;
import mn.erin.domain.base.usecase.UseCaseException;
import mn.erin.lms.base.aim.AccessIdentityManagement;
import mn.erin.lms.base.domain.usecase.membership.dto.MembershipGroup;

/**
 * @author Munkh
 */
public class GetCurrentUserGroups implements UseCase<Void, Set<MembershipGroup>>
{
  private final AccessIdentityManagement accessIdentityManagement;

  public GetCurrentUserGroups(AccessIdentityManagement accessIdentityManagement)
  {
    this.accessIdentityManagement = Objects.requireNonNull(accessIdentityManagement, "GetGroupMembers: AccessIdentityManagement cannot be null!!");
  }

  @Override
  public Set<MembershipGroup> execute(Void input) throws UseCaseException
  {
    String username = accessIdentityManagement.getCurrentUsername();

    Set<String> departments = accessIdentityManagement.getSubDepartments(accessIdentityManagement.getUserDepartmentId(username));

    if (departments == null || departments.isEmpty())
    {
      return Collections.emptySet();
    }

    return accessIdentityManagement.getDepartments(departments).stream()
        .map(group -> new MembershipGroup(group.getId().getId(), group.getName()))
        .collect(Collectors.toSet());
  }
}
