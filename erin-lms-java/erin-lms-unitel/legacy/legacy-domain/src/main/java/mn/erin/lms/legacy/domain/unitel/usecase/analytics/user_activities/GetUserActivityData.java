package mn.erin.lms.legacy.domain.unitel.usecase.analytics.user_activities;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import mn.erin.common.datetime.TimeUtils;
import mn.erin.domain.aim.model.group.Group;
import mn.erin.domain.aim.model.group.GroupId;
import mn.erin.domain.aim.repository.GroupRepository;
import mn.erin.domain.base.usecase.UseCase;
import mn.erin.lms.base.aim.AccessIdentityManagement;
import mn.erin.lms.base.aim.role.LmsRole;
import mn.erin.lms.legacy.domain.lms.model.enrollment.LearnerId;
import mn.erin.lms.legacy.domain.unitel.model.UserActivityData;
import mn.erin.lms.legacy.domain.unitel.service.EmployeeAnalytics;

/**
 * @author Bat-Erdene Tsogoo.
 */
public class GetUserActivityData implements UseCase<List<String>, GetUserActivityDataOutput>
{
  private final EmployeeAnalytics employeeAnalytics;
  private final AccessIdentityManagement accessIdentityManagement;
  private final GroupRepository groupRepository;

  private Integer employeesCount = 0;

  public GetUserActivityData(EmployeeAnalytics employeeAnalytics, AccessIdentityManagement accessIdentityManagement,
      GroupRepository groupRepository)
  {
    this.employeeAnalytics = Objects.requireNonNull(employeeAnalytics, "EmployeeAnalytics service cannot be null!");
    this.accessIdentityManagement = Objects.requireNonNull(accessIdentityManagement);
    this.groupRepository = Objects.requireNonNull(groupRepository);
  }

  @Override
  public GetUserActivityDataOutput execute(List<String> users)
  {
    List<LearnerId> learners = users
        .stream().map(LearnerId::new).collect(Collectors.toList());

    for (LearnerId learner : learners)
    {
      if (getRole(learner) == LmsRole.LMS_USER)
      {
        employeesCount++;
      }
    }

    Set<UserActivityData> userActivityData = employeeAnalytics.getUserActivityData(learners);

    if (userActivityData.isEmpty())
    {
      return getDefaultOutput();
    }

    return toOutput(userActivityData);
  }

  private GetUserActivityDataOutput toOutput(Set<UserActivityData> userActivityData)
  {
    List<Long> spentTimeList = new ArrayList<>();
    int perfectCompletionCountByEmployee = 0;
    int perfectCompletionCountBySupervisors = 0;

    List<OtherActivityData> otherActivityData = new ArrayList<>();

    for (UserActivityData datum : userActivityData)
    {
      if (datum.getOverallTime() != 0)
      {
        spentTimeList.add(datum.getOverallTime());
      }

      LmsRole role = getRole(datum.getLearnerId());
      if (datum.getAverageStatus() >= 100 && role == LmsRole.LMS_USER)
      {
        perfectCompletionCountByEmployee++;
      }
      else if (datum.getAverageStatus() >= 100 && (role == LmsRole.LMS_SUPERVISOR || role == LmsRole.LMS_MANAGER))
      {
        perfectCompletionCountBySupervisors++;
      }

      String currentUserGroupId = accessIdentityManagement.getUserDepartmentId(datum.getLearnerId().getId());
      Group group = groupRepository.findById(GroupId.valueOf(currentUserGroupId));
      otherActivityData.add(getOtherActivityData(datum.getLearnerId().getId(), group.getName(), getRole(datum.getLearnerId()).name(), datum.getAverageStatus()));
    }

    GetUserActivityDataOutput output = new GetUserActivityDataOutput();
    output.setPerfectSupersCount(perfectCompletionCountBySupervisors);
    output.setPerfectEmployeeCount(perfectCompletionCountByEmployee);
    output.setEmployeeCount(this.employeesCount);
    output.setAverageTime(TimeUtils.convertToStringRepresentation(getMedianTime(spentTimeList)));
    output.setOtherActivityDataList(otherActivityData);
    return output;
  }

  private OtherActivityData getOtherActivityData(String learnerId, String groupName, String role, Float averageStatus)
  {
    OtherActivityData otherActivityData = new OtherActivityData();
    otherActivityData.setUsername(learnerId);
    otherActivityData.setGroupName(groupName);
    otherActivityData.setRole(role);
    otherActivityData.setStatus(averageStatus);
    return otherActivityData;
  }

  private LmsRole getRole(LearnerId learnerId)
  {
    String role = accessIdentityManagement.getRole(learnerId.getId());
    return LmsRole.valueOf(role.toUpperCase());
  }

  private Long getMedianTime(List<Long> spentTimeList)
  {
    spentTimeList.sort(Comparator.reverseOrder());
    if (spentTimeList.isEmpty())
    {
      return 0L;
    }

    int size = spentTimeList.size();
    int middle = size / 2;

    if (size % 2 == 1)
    {
      return spentTimeList.get(middle);
    }
    else
    {
      return (spentTimeList.get(middle - 1) + spentTimeList.get(middle)) / 2;
    }
  }

  private static GetUserActivityDataOutput getDefaultOutput()
  {
    GetUserActivityDataOutput defaultOutput = new GetUserActivityDataOutput();
    defaultOutput.setAverageTime("00:00:00");
    defaultOutput.setEmployeeCount(0);
    defaultOutput.setPerfectEmployeeCount(0);
    defaultOutput.setPerfectSupersCount(0);
    return defaultOutput;
  }
}
