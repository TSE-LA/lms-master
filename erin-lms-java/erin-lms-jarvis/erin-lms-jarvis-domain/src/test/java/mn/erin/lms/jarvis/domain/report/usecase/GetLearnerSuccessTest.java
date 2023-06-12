package mn.erin.lms.jarvis.domain.report.usecase;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.Mockito;

import mn.erin.domain.aim.exception.AimRepositoryException;
import mn.erin.domain.aim.model.group.GroupId;
import mn.erin.domain.aim.model.membership.Membership;
import mn.erin.domain.aim.model.membership.MembershipId;
import mn.erin.domain.aim.model.role.RoleId;
import mn.erin.domain.aim.repository.GroupRepository;
import mn.erin.domain.aim.repository.MembershipRepository;
import mn.erin.domain.base.usecase.UseCaseException;
import mn.erin.lms.base.aim.LmsDepartmentService;
import mn.erin.lms.base.aim.user.LearnerId;
import mn.erin.lms.base.domain.service.LmsServiceRegistry;
import mn.erin.lms.jarvis.domain.report.model.LearnerSuccess;
import mn.erin.lms.jarvis.domain.report.repository.LearnerSuccessRepository;
import mn.erin.lms.jarvis.domain.report.usecase.dto.DateType;
import mn.erin.lms.jarvis.domain.report.usecase.dto.GetLearnerSuccessInput;
import mn.erin.lms.jarvis.domain.report.usecase.dto.GroupType;
import mn.erin.lms.jarvis.domain.report.usecase.dto.LearnerSuccessDto;
import mn.erin.lms.jarvis.domain.report.usecase.dto.LearnerSuccessMonthData;

/**
 * @author Galsan Bayart
 */
public class GetLearnerSuccessTest
{
  public static final String LEARNER_ID = "learnerId";
  public static final int YEAR = 2021;
  public static final boolean IS_FIRST_HALF_YEAR = true;
  public static final boolean IS_AVERAGE = false;
  public static final String GROUP_ID = "groupId";
  public static final int NUMBER_OF_GROUP_MEMBER = 2;

  private LearnerSuccessRepository learnerSuccessRepository;
  private LmsDepartmentService lmsDepartmentService;
  private LmsServiceRegistry lmsServiceRegistry;
  private MembershipRepository membershipRepository;

  private GetLearnerSuccess getLearnerSuccess;
  private GetLearnerSuccessInput getLearnerSuccessInput;

  private final List<LearnerSuccess> mySuccesses = new ArrayList<>();
  private final List<LearnerSuccess> beforeHalfSuccesses = new ArrayList<>();
  private final List<LearnerSuccess> groupMemberSuccesses = new ArrayList<>();
  private final List<LearnerSuccess> myAllTimeData = new ArrayList<>();
  private final List<LearnerSuccessMonthData> learnerSuccessMonthData = new ArrayList<>();

  private final Membership membership = new Membership(MembershipId.valueOf("MembershipId"), "UserName", GroupId.valueOf(GROUP_ID), RoleId.valueOf("RoleId"));
  private final LearnerSuccessDto learnerSuccessDto = new LearnerSuccessDto();

  @Before
  public void setUp()
  {
    learnerSuccessRepository = Mockito.mock(LearnerSuccessRepository.class);

    lmsDepartmentService = Mockito.mock(LmsDepartmentService.class);
    lmsServiceRegistry = Mockito.mock(LmsServiceRegistry.class);
    Mockito.when(lmsServiceRegistry.getDepartmentService()).thenReturn(lmsDepartmentService);
    membershipRepository = Mockito.mock(MembershipRepository.class);
    GroupRepository groupRepository = Mockito.mock(GroupRepository.class);

    getLearnerSuccess = new GetLearnerSuccess(learnerSuccessRepository, lmsServiceRegistry, membershipRepository, groupRepository);
    getLearnerSuccessInput = new GetLearnerSuccessInput(LEARNER_ID, YEAR, DateType.FIRST_HALF, GroupType.MY_GROUP);
    generateLearnerSuccess(mySuccesses);
    generateLearnerSuccess(beforeHalfSuccesses);
    myAllTimeData.addAll(mySuccesses);
    myAllTimeData.addAll(beforeHalfSuccesses);
    generateGroupLearnerSuccess(groupMemberSuccesses, NUMBER_OF_GROUP_MEMBER);
    groupMemberSuccesses.addAll(mySuccesses);

    mySuccesses.forEach(learnerSuccess -> {
      List<LearnerSuccess> monthData = groupMemberSuccesses.stream().filter(f -> f.getMonth() == learnerSuccess
          .getMonth()).collect(Collectors.toList());

      learnerSuccessMonthData
          .add(new LearnerSuccessMonthData(learnerSuccess.getScore(),
              groupAvg(monthData.stream().map(f -> f.getScore() / (double) f.getMaxScore()).collect(Collectors.toList())),
              learnerSuccess.getPerformance(), groupAvg(monthData.stream().map(LearnerSuccess::getPerformance).collect(Collectors.toList())),
              learnerSuccess.getMonth()));
    });

    learnerSuccessDto.setLearnerSuccesses(learnerSuccessMonthData);
    learnerSuccessDto.setDifference(learnerHalfYearAverage(mySuccesses) - learnerHalfYearAverage(beforeHalfSuccesses));
    learnerSuccessDto.setOverallScore(scoreSum(myAllTimeData.stream().map(LearnerSuccess::getScore).collect(Collectors.toList())));
    learnerSuccessDto.setOverallMaxScore(scoreSum(myAllTimeData.stream().map(LearnerSuccess::getMaxScore).collect(Collectors.toList())));
  }

  @Test
  // TODO: fix test
  @Ignore
  public void whenSuccess() throws AimRepositoryException, UseCaseException
  {
    Mockito.when(learnerSuccessRepository.getSuccesses(LearnerId.valueOf(LEARNER_ID), YEAR, IS_FIRST_HALF_YEAR)).thenReturn(mySuccesses);
    Mockito.when(membershipRepository.findByUsername(LEARNER_ID)).thenReturn(membership);

    Mockito.when(lmsDepartmentService.getLearners(GROUP_ID))
        .thenReturn(groupMemberSuccesses.stream().map(LearnerSuccess::getLearnerId).collect(Collectors.toSet()));

    Mockito.when(lmsDepartmentService.getAllLearners(Mockito.anyString()))
        .thenReturn(groupMemberSuccesses.stream().map(LearnerSuccess::getLearnerId).collect(Collectors.toSet()));

    Mockito.when(learnerSuccessRepository.getGroupMembersSuccesses(Mockito.anySet(), Mockito.eq(YEAR), Mockito.eq(IS_FIRST_HALF_YEAR)))
        .thenReturn(groupMemberSuccesses);

    Mockito.when(learnerSuccessRepository.getSuccesses(LearnerId.valueOf(LEARNER_ID), IS_FIRST_HALF_YEAR ? YEAR - 1 : YEAR, !IS_FIRST_HALF_YEAR))
        .thenReturn(beforeHalfSuccesses);

    Mockito.when(learnerSuccessRepository.getAllByLearner(LEARNER_ID)).thenReturn(myAllTimeData);

    Assert.assertTrue(learnerSuccessDtoEquals(getLearnerSuccess.execute(getLearnerSuccessInput)));
  }

  @Test(expected = NullPointerException.class)
  public void whenLearnerIdNull() throws UseCaseException
  {
    getLearnerSuccessInput = new GetLearnerSuccessInput(null, 2022, DateType.FIRST_HALF, GroupType.MY_GROUP);
    getLearnerSuccess.execute(getLearnerSuccessInput);
  }

  @Test(expected = NullPointerException.class)
  public void whenGroupNull() throws UseCaseException
  {
    getLearnerSuccessInput = new GetLearnerSuccessInput(LEARNER_ID, 2022, DateType.FIRST_HALF, null);
    getLearnerSuccess.execute(getLearnerSuccessInput);
  }

  @Test
  // TODO: fix test
  @Ignore
  public void differenceExpected() throws UseCaseException, AimRepositoryException
  {
    Mockito.when(learnerSuccessRepository.getSuccesses(LearnerId.valueOf(LEARNER_ID), YEAR, IS_FIRST_HALF_YEAR))
        .thenReturn(generateLearnerThisHalfSuccess(new ArrayList<>()));
    Mockito.when(learnerSuccessRepository.getSuccesses(LearnerId.valueOf(LEARNER_ID), IS_FIRST_HALF_YEAR ? YEAR - 1 : YEAR, !IS_FIRST_HALF_YEAR))
        .thenReturn(generateLearnerBeforeHalfSuccess(generateLearnerBeforeHalfSuccess(new ArrayList<>())));

    Mockito.when(membershipRepository.findByUsername(Mockito.anyString())).thenReturn(membership);

    Mockito.when(lmsDepartmentService.getLearners(Mockito.anyString())).thenReturn(new HashSet<>());

    Assert.assertEquals(Double.valueOf(0.25), Double.valueOf(getLearnerSuccess.execute(getLearnerSuccessInput).getDifference()));
  }

  @Test
  public void overallScoreExpected() throws UseCaseException, AimRepositoryException
  {
    Mockito.when(learnerSuccessRepository.getSuccesses(LearnerId.valueOf(LEARNER_ID), YEAR, IS_FIRST_HALF_YEAR))
        .thenReturn(generateLearnerThisHalfSuccess(new ArrayList<>()));
    Mockito.when(learnerSuccessRepository.getSuccesses(LearnerId.valueOf(LEARNER_ID), IS_FIRST_HALF_YEAR ? YEAR - 1 : YEAR, !IS_FIRST_HALF_YEAR))
        .thenReturn(generateLearnerBeforeHalfSuccess(generateLearnerBeforeHalfSuccess(new ArrayList<>())));

    Mockito.when(membershipRepository.findByUsername(Mockito.anyString())).thenReturn(membership);
    Mockito.when(lmsDepartmentService.getLearners(Mockito.anyString())).thenReturn(new HashSet<>());

    Mockito.when(learnerSuccessRepository.getSuccesses(LearnerId.valueOf(LEARNER_ID), YEAR, IS_FIRST_HALF_YEAR)).thenReturn(mySuccesses);
    List<LearnerSuccess> thisHalfYear = generateLearnerThisHalfSuccess(new ArrayList<>());
    List<LearnerSuccess> beforeHalfYear = generateLearnerBeforeHalfSuccess(new ArrayList<>());
    thisHalfYear.addAll(beforeHalfYear);

    Mockito.when(learnerSuccessRepository.getAllByLearner(LEARNER_ID)).thenReturn(thisHalfYear);
    Assert.assertEquals(Integer.valueOf(30), Integer.valueOf(getLearnerSuccess.execute(getLearnerSuccessInput).getOverallScore()));
  }

  @Test
  public void overallMaxScoreExpected() throws UseCaseException, AimRepositoryException
  {
    Mockito.when(learnerSuccessRepository.getSuccesses(LearnerId.valueOf(LEARNER_ID), YEAR, IS_FIRST_HALF_YEAR))
        .thenReturn(generateLearnerThisHalfSuccess(new ArrayList<>()));
    Mockito.when(learnerSuccessRepository.getSuccesses(LearnerId.valueOf(LEARNER_ID), IS_FIRST_HALF_YEAR ? YEAR - 1 : YEAR, !IS_FIRST_HALF_YEAR))
        .thenReturn(generateLearnerBeforeHalfSuccess(generateLearnerBeforeHalfSuccess(new ArrayList<>())));

    Mockito.when(membershipRepository.findByUsername(Mockito.anyString())).thenReturn(membership);
    Mockito.when(lmsDepartmentService.getLearners(Mockito.anyString())).thenReturn(new HashSet<>());

    Mockito.when(learnerSuccessRepository.getSuccesses(LearnerId.valueOf(LEARNER_ID), YEAR, IS_FIRST_HALF_YEAR)).thenReturn(mySuccesses);
    List<LearnerSuccess> thisHalfYear = generateLearnerThisHalfSuccess(new ArrayList<>());
    List<LearnerSuccess> beforeHalfYear = generateLearnerBeforeHalfSuccess(new ArrayList<>());
    thisHalfYear.addAll(beforeHalfYear);

    Mockito.when(learnerSuccessRepository.getAllByLearner(LEARNER_ID)).thenReturn(thisHalfYear);
    Assert.assertEquals(Integer.valueOf(30), Integer.valueOf(getLearnerSuccess.execute(getLearnerSuccessInput).getOverallScore()));

    Assert.assertEquals(Integer.valueOf(48), Integer.valueOf(getLearnerSuccess.execute(getLearnerSuccessInput).getOverallMaxScore()));
  }

  private boolean learnerSuccessDtoEquals(LearnerSuccessDto useCaseResult)
  {
    if (learnerSuccessDto.getLearnerSuccesses().size() != useCaseResult.getLearnerSuccesses().size())
      return false;
    if (learnerSuccessDto.getDifference() != useCaseResult.getDifference())
      return false;
    if (learnerSuccessDto.getOverallMaxScore() != useCaseResult.getOverallMaxScore())
      return false;
    if (learnerSuccessDto.getOverallScore() != useCaseResult.getOverallScore())
      return false;
    List<LearnerSuccessMonthData> useCaseResultOutputs = useCaseResult.getLearnerSuccesses();

    for (LearnerSuccessMonthData useCaseOutput : useCaseResultOutputs)
    {
      boolean isExist = false;
      for (LearnerSuccessMonthData expectedOutput : learnerSuccessMonthData)
      {
        if (useCaseOutput.getMonth() == expectedOutput.getMonth())
        {
          isExist = useCaseOutput.getGroupAvg() == expectedOutput.getGroupAvg() &&
              useCaseOutput.getScore() == useCaseOutput.getScore() &&
              useCaseOutput.getGroupPerformance() == useCaseOutput.getGroupPerformance() &&
              useCaseOutput.getMyPerformance() == useCaseOutput.getMyPerformance();
        }
      }
      if (!isExist)
        return false;
    }
    return true;
  }

  private int scoreSum(List<Integer> scores)
  {
    return scores.stream().reduce(Integer::sum).orElse(0);
  }

  private double learnerHalfYearAverage(List<LearnerSuccess> learnerSuccesses)
  {
    if (learnerSuccesses.isEmpty())
    {
      return 0;
    }
    List<Double> percents = learnerSuccesses.stream().map(this::monthPercent).collect(Collectors.toList());
    return percents.stream().reduce(Double::sum).orElse(0.0) / (int) percents.stream().filter(f -> !f.equals(0.0)).count();
  }

  private double monthPercent(LearnerSuccess learnerSuccess)
  {
    if (learnerSuccess.getMaxScore() == 0)
      return 0.0;
    else
      return learnerSuccess.getScore() / (double) learnerSuccess.getMaxScore();
  }

  private double groupAvg(List<Double> percents)
  {
    if (percents.isEmpty())
    {
      return 0;
    }
    return percents.stream().reduce(Double::sum).orElse(0.0) / percents.size();
  }

  private void generateLearnerSuccess(List<LearnerSuccess> learnerSuccessList)
  {
    if (IS_FIRST_HALF_YEAR)
    {
      for (int j = 1; j < 7; j++)
      {
        learnerSuccessList.add(new LearnerSuccess(LEARNER_ID, "", j, j + 3, j * 50 / 100.0, YEAR, j));
      }
    }
    else
    {
      for (int j = 7; j < 13; j++)
      {
        learnerSuccessList.add(new LearnerSuccess(LEARNER_ID, "", j, j + 3, j * 50 / 100.0, YEAR, j));
      }
    }
  }

  private List<LearnerSuccess> generateLearnerThisHalfSuccess(List<LearnerSuccess> learnerSuccessList)
  {
    if (IS_FIRST_HALF_YEAR)
    {
      for (int j = 1; j < 7; j++)
      {
        learnerSuccessList.add(new LearnerSuccess(LEARNER_ID, "", 3, 4, j * 50 / 100.0, YEAR, j));
      }
    }
    else
    {
      for (int j = 7; j < 13; j++)
      {
        learnerSuccessList.add(new LearnerSuccess(LEARNER_ID, "", 3, 4, j * 50 / 100.0, YEAR, j));
      }
    }
    return learnerSuccessList;
  }

  private List<LearnerSuccess> generateLearnerBeforeHalfSuccess(List<LearnerSuccess> learnerSuccessList)
  {
    if (IS_FIRST_HALF_YEAR)
    {
      for (int j = 1; j < 7; j++)
      {
        learnerSuccessList.add(new LearnerSuccess(LEARNER_ID, "", 2, 4, j * 50 / 100.0, YEAR, j));
      }
    }
    else
    {
      for (int j = 7; j < 13; j++)
      {
        learnerSuccessList.add(new LearnerSuccess(LEARNER_ID, "", 2, 4, j * 50 / 100.0, YEAR, j));
      }
    }
    return learnerSuccessList;
  }

  private void generateGroupLearnerSuccess(List<LearnerSuccess> learnerSuccessList, int numberOfGroupMember)
  {
    List<String> learnerIds = new ArrayList<>();

    for (int i = 0; i < numberOfGroupMember; i++)
    {
      learnerIds.add("User" + (i + 1));
    }

    if (IS_FIRST_HALF_YEAR)
    {
      for (int j = 1; j < 7; j++)
      {
        for (int i = 0; i < numberOfGroupMember; i++)
        {
          learnerSuccessList.add(new LearnerSuccess(learnerIds.get(i), "", j + i, j + i + 5, (j + i) / (j + i + 10.0), YEAR, j));
        }
      }
    }
    else
    {
      for (int j = 7; j < 13; j++)
      {
        for (int i = 0; i < numberOfGroupMember; i++)
        {
          learnerSuccessList.add(new LearnerSuccess(learnerIds.get(i), "", j + i, j + i + 5, (j + i) / (j + i + 10.0), YEAR, j));
        }
      }
    }
  }

  private void generateLearnerSuccessRandom(List<LearnerSuccess> learnerSuccessList)
  {
    Random random = new Random();
    if (IS_FIRST_HALF_YEAR)
    {
      for (int j = 1; j < 7; j++)
      {
        learnerSuccessList.add(new LearnerSuccess(LEARNER_ID, "", random.nextInt(100), random.nextInt(100) + 100, random.nextInt(10000) / 100.0, YEAR, j));
      }
    }
    else
    {
      for (int j = 7; j < 13; j++)
      {
        learnerSuccessList.add(new LearnerSuccess(LEARNER_ID, "", random.nextInt(100), random.nextInt(100) + 100, random.nextInt(10000) / 100.0, YEAR, j));
      }
    }
  }

  private void generateGroupLearnerSuccessRandom(List<LearnerSuccess> learnerSuccessList, int numberOfGroupMember)
  {
    Random random = new Random();
    List<String> learnerIds = new ArrayList<>();

    for (int i = 0; i < numberOfGroupMember; i++)
    {
      learnerIds.add("User" + i);
    }

    if (IS_FIRST_HALF_YEAR)
    {
      for (int j = 1; j < 7; j++)
      {
        for (int i = 0; i < numberOfGroupMember; i++)
        {
          learnerSuccessList.add(
              new LearnerSuccess(learnerIds.get(i), "", random.nextInt(100), random.nextInt(100) + 100, random.nextInt(10000) / 100.0, YEAR, j));
        }
      }
    }
    else
    {
      for (int j = 7; j < 13; j++)
      {
        for (int i = 0; i < numberOfGroupMember; i++)
        {
          learnerSuccessList.add(
              new LearnerSuccess(learnerIds.get(i), "", random.nextInt(100), random.nextInt(100) + 100, random.nextInt(10000) / 100.0, YEAR, j));
        }
      }
    }
  }
}
