package mn.erin.lms.unitel.domain.usecase;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import mn.erin.common.datetime.TimeUtils;
import mn.erin.domain.base.usecase.UseCase;
import mn.erin.lms.base.aim.AccessIdentityManagement;
import mn.erin.lms.base.aim.role.LmsRole;
import mn.erin.lms.base.aim.user.LearnerId;
import mn.erin.lms.unitel.domain.model.analytics.UserActivityData;
import mn.erin.lms.unitel.domain.service.EmployeeAnalyticsService;
import mn.erin.lms.unitel.domain.usecase.dto.OtherActivityData;
import mn.erin.lms.unitel.domain.usecase.dto.UserActivityDataDto;

/**
 * @author Bat-Erdene Tsogoo.
 */
public class GetUserActivityData implements UseCase<String, UserActivityDataDto>
{
  private final EmployeeAnalyticsService employeeAnalyticsService;
  private final AccessIdentityManagement aimService;

  private Integer employeesCount = 0;

  public GetUserActivityData(EmployeeAnalyticsService employeeAnalyticsService, AccessIdentityManagement aimService)
  {
    this.employeeAnalyticsService = Objects.requireNonNull(employeeAnalyticsService, "EmployeeAnalytics service cannot be null!");
    this.aimService = aimService;
  }

  @Override
  public UserActivityDataDto execute(String departmentId)
  {
    List<LearnerId> learners = getAllLearners(departmentId)
        .stream().map(LearnerId::valueOf).collect(Collectors.toList());

    for (LearnerId learner : learners)
    {
      if (getRole(learner) == LmsRole.LMS_USER)
      {
        employeesCount++;
      }
    }

    Set<UserActivityData> userActivityData = employeeAnalyticsService.getUserActivityData(learners);

    if (userActivityData.isEmpty())
    {
      return getDefaultOutput();
    }

    return toOutput(userActivityData);
  }

  private UserActivityDataDto toOutput(Set<UserActivityData> userActivityData)
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

      if (datum.getAverageStatus() >= 100 && getRole(datum.getLearnerId()) == LmsRole.LMS_USER)
      {
        perfectCompletionCountByEmployee++;
      }
      else if (datum.getAverageStatus() >= 100 && (getRole(datum.getLearnerId()) == LmsRole.LMS_SUPERVISOR || getRole(datum.getLearnerId()) == LmsRole.LMS_MANAGER))
      {
        perfectCompletionCountBySupervisors++;
      }
      else
      {
        String department = aimService.getUserDepartmentId(datum.getLearnerId().getId());
        otherActivityData.add(getOtherActivityData(datum.getLearnerId().getId(), department, getRole(datum.getLearnerId()).name(),
            datum.getAverageStatus()));
      }
    }

    UserActivityDataDto output = new UserActivityDataDto();
    output.setPerfectSupersCount(perfectCompletionCountBySupervisors);
    output.setPerfectEmployeeCount(perfectCompletionCountByEmployee);
    output.setEmployeeCount(this.employeesCount);
    output.setAverageTime(TimeUtils.convertToStringRepresentation(getMedianTime(spentTimeList)));
    output.setOtherActivityDataList(otherActivityData);
    return output;
  }

  private Set<String> getAllLearners(String departmentId)
  {
    return aimService.getLearners(departmentId);
  }

  private OtherActivityData getOtherActivityData(String learnerId, String departmentName, String role, Float averageStatus)
  {
    OtherActivityData otherActivityData = new OtherActivityData();
    otherActivityData.setUsername(learnerId);
    otherActivityData.setGroupName(departmentName);
    otherActivityData.setRole(role);
    otherActivityData.setStatus(averageStatus);
    return otherActivityData;
  }

  private LmsRole getRole(LearnerId learnerId)
  {
    String role = aimService.getRole(learnerId.getId());
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

  private static UserActivityDataDto getDefaultOutput()
  {
    UserActivityDataDto defaultOutput = new UserActivityDataDto();
    defaultOutput.setAverageTime("00:00:00");
    defaultOutput.setEmployeeCount(0);
    defaultOutput.setPerfectEmployeeCount(0);
    defaultOutput.setPerfectSupersCount(0);
    return defaultOutput;
  }
}
