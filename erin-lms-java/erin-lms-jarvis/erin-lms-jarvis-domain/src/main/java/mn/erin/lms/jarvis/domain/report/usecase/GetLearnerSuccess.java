package mn.erin.lms.jarvis.domain.report.usecase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.Validate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import mn.erin.domain.aim.exception.AimRepositoryException;
import mn.erin.domain.aim.model.group.Group;
import mn.erin.domain.aim.model.tenant.TenantId;
import mn.erin.domain.aim.repository.GroupRepository;
import mn.erin.domain.aim.repository.MembershipRepository;
import mn.erin.domain.base.usecase.UseCase;
import mn.erin.domain.base.usecase.UseCaseException;
import mn.erin.lms.base.aim.user.LearnerId;
import mn.erin.lms.base.domain.service.LmsServiceRegistry;
import mn.erin.lms.jarvis.domain.report.model.LearnerSuccess;
import mn.erin.lms.jarvis.domain.report.repository.LearnerSuccessRepository;
import mn.erin.lms.jarvis.domain.report.usecase.dto.DateType;
import mn.erin.lms.jarvis.domain.report.usecase.dto.GetLearnerSuccessInput;
import mn.erin.lms.jarvis.domain.report.usecase.dto.LearnerSuccessDto;
import mn.erin.lms.jarvis.domain.report.usecase.dto.LearnerSuccessMonthData;

/**
 * @author Galsan Bayart
 */
public class GetLearnerSuccess implements UseCase<GetLearnerSuccessInput, LearnerSuccessDto>
{
  public static final Logger LOGGER = LoggerFactory.getLogger(GetLearnerSuccess.class);
  private final LearnerSuccessRepository learnerSuccessRepository;
  private final LmsServiceRegistry lmsServiceRegistry;
  private final MembershipRepository membershipRepository;
  private final GroupRepository groupRepository;

  public GetLearnerSuccess(LearnerSuccessRepository learnerSuccessRepository,
      LmsServiceRegistry lmsServiceRegistry, MembershipRepository membershipRepository, GroupRepository groupRepository)
  {
    this.learnerSuccessRepository = learnerSuccessRepository;
    this.lmsServiceRegistry = lmsServiceRegistry;
    this.membershipRepository = membershipRepository;
    this.groupRepository = groupRepository;
  }

  @Override
  public LearnerSuccessDto execute(GetLearnerSuccessInput input) throws UseCaseException
  {
    Validate.notNull(input.getLearnerId());
    Validate.notNull(input.getGroupType());
    Validate.notNull(input.getDateType());

    List<LearnerSuccessMonthData> monthData = new ArrayList<>();

    boolean isFirstHalf = input.getDateType() == DateType.FIRST_HALF;

    List<LearnerSuccess> learnerSuccesses = learnerSuccessRepository.getSuccesses(LearnerId.valueOf(input.getLearnerId()), input.getYear(), isFirstHalf);

    List<LearnerSuccess> previousLearnerSuccesses = learnerSuccessRepository.getSuccesses(
        LearnerId.valueOf(input.getLearnerId()), isFirstHalf ? input.getYear() - 1 : input.getYear(), !isFirstHalf
    );

    Set<String> learners;
    try
    {
      switch (input.getGroupType())
      {
      case ALL_GROUP:
        List<Group> rootGroups = groupRepository.getAllRootGroups(TenantId.valueOf(lmsServiceRegistry.getOrganizationIdProvider().getOrganizationId()));
        if (!rootGroups.isEmpty() && rootGroups.get(0) != null)
        {
          learners = lmsServiceRegistry.getDepartmentService().getAllLearners(rootGroups.get(0).getId().getId());
        }
        else
        {
          learners = new HashSet<>();
        }
        break;
      case MY_GROUP:
        learners = lmsServiceRegistry.getDepartmentService().getLearners(membershipRepository.findByUsername(input.getLearnerId()).getGroupId().getId());
        break;
      default:
        learners = new HashSet<>();
      }
    }
    catch (AimRepositoryException e)
    {
      learners = new HashSet<>();
    }

    // Remove self
    learners.remove(input.getLearnerId());

    List<LearnerSuccess> groupMembersSuccesses = learnerSuccessRepository.getGroupMembersSuccesses(learners, input.getYear(), isFirstHalf);

    Map<Integer, List<LearnerSuccess>> mappedOthersSuccess = new HashMap<>();

    for (LearnerSuccess otherSuccess : groupMembersSuccesses)
    {
      if (mappedOthersSuccess.containsKey(otherSuccess.getMonth()))
      {
        mappedOthersSuccess.get(otherSuccess.getMonth()).add(otherSuccess);
      }
      else
      {
        List<LearnerSuccess> singleMonthData = new ArrayList<>();
        singleMonthData.add(otherSuccess);
        mappedOthersSuccess.put(otherSuccess.getMonth(), singleMonthData);
      }
    }

    for (LearnerSuccess learnerSuccess : learnerSuccesses)
    {
      List<LearnerSuccess> singleMonthData = mappedOthersSuccess.get(learnerSuccess.getMonth());

      if (singleMonthData != null && !singleMonthData.isEmpty())
      {
        int totalScore = singleMonthData.stream().map(LearnerSuccess::getScore).reduce(Integer::sum).orElse(0);
        int totalMaxScore = singleMonthData.stream().map(LearnerSuccess::getMaxScore).reduce(Integer::sum).orElse(0);

        double totalPerformance = singleMonthData.stream().map(LearnerSuccess::getPerformance).reduce(Double::sum).orElse(0.0);
        long count = singleMonthData.stream().filter(success -> success.getPerformance() != 0.0).count();

        monthData.add(new LearnerSuccessMonthData(
            learnerSuccess.getMaxScore() != 0 ? (double) learnerSuccess.getScore() / (double) learnerSuccess.getMaxScore() * 100 : 0,
            totalMaxScore != 0 ? (double) totalScore / (double) totalMaxScore * 100 : 0,
            learnerSuccess.getPerformance(),
            count != 0 ? totalPerformance / count : 0,
            learnerSuccess.getMonth()
        ));
      }
      else
      {
        monthData.add(new LearnerSuccessMonthData(
            learnerSuccess.getMaxScore() != 0 ? (double) learnerSuccess.getScore() / learnerSuccess.getMaxScore() * 100 : 0,
            0,
            learnerSuccess.getPerformance(),
            0,
            learnerSuccess.getMonth()
        ));
      }
    }

    List<LearnerSuccess> learnerAllTimeData = learnerSuccessRepository.getAllByLearner(input.getLearnerId());

    return new LearnerSuccessDto(
        monthData,
        learnerAllTimeData.stream().map(LearnerSuccess::getScore).reduce(Integer::sum).orElse(0),
        learnerAllTimeData.stream().map(LearnerSuccess::getMaxScore).reduce(Integer::sum).orElse(0),
        average(learnerSuccesses) - average(previousLearnerSuccesses));
  }

  private double average(List<LearnerSuccess> learnerSuccesses)
  {
    if (learnerSuccesses == null || learnerSuccesses.isEmpty())
    {
      return 0;
    }

    double total = learnerSuccesses.stream()
        .map(success -> success.getMaxScore() != 0 ? (double) success.getScore() / (double) success.getMaxScore() * 100 : 0)
        .reduce(Double::sum).orElse(0.0);

    return total / 6;
  }
}
