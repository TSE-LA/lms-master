package mn.erin.lms.unitel.domain.usecase;

import java.io.Serializable;
import java.time.LocalDate;
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
import mn.erin.domain.aim.service.AimConfigProvider;
import mn.erin.domain.base.usecase.UseCase;
import mn.erin.domain.base.usecase.UseCaseException;
import mn.erin.lms.base.aim.user.LearnerId;
import mn.erin.lms.base.domain.service.LmsServiceRegistry;
import mn.erin.lms.base.scorm.constants.DataModelConstants;
import mn.erin.lms.base.scorm.model.DataModel;
import mn.erin.lms.base.scorm.model.RuntimeData;
import mn.erin.lms.base.scorm.repository.RuntimeDataRepository;
import mn.erin.lms.unitel.domain.model.analytics.LearnerSuccess;
import mn.erin.lms.unitel.domain.repository.LearnerSuccessRepository;

/**
 * @author Galsan Bayart
 */
public class GenerateLearnerSuccess implements UseCase<LocalDate, Void>
{
  public static final Logger LOGGER = LoggerFactory.getLogger(GenerateLearnerSuccess.class);

  private final GroupRepository groupRepository;
  private final LmsServiceRegistry lmsServiceRegistry;
  private final RuntimeDataRepository runtimeDataRepository;
  private final LearnerSuccessRepository learnerSuccessRepository;
  private final AimConfigProvider aimConfigProvider;
  private static final String COURSE_TYPE = "online-course";

  public GenerateLearnerSuccess(GroupRepository groupRepository, LmsServiceRegistry lmsServiceRegistry,
      RuntimeDataRepository runtimeDataRepository, LearnerSuccessRepository learnerSuccessRepository,
      AimConfigProvider aimConfigProvider)
  {
    this.groupRepository = groupRepository;
    this.lmsServiceRegistry = lmsServiceRegistry;
    this.runtimeDataRepository = runtimeDataRepository;
    this.learnerSuccessRepository = learnerSuccessRepository;
    this.aimConfigProvider = aimConfigProvider;
  }

  @Override
  public Void execute(LocalDate startDate) throws UseCaseException
  {
    if (learnerSuccessRepository.isExistThisMonth(startDate.getYear(), startDate.getMonthValue(), COURSE_TYPE))
    {
      throw new UseCaseException("This month's learner successes have been already generated!");
    }

    LocalDate endDate = startDate.plusMonths(1);

    String tenantId = aimConfigProvider.getDefaultTenantId().getId();

    List<Group> rootGroups;
    try
    {
      rootGroups = groupRepository.getAllRootGroups(TenantId.valueOf(tenantId));
    }
    catch (AimRepositoryException e)
    {
      throw new UseCaseException(e.getMessage(), e);
    }

    Set<String> allLearners = new HashSet<>();

    for (Group group : rootGroups)
    {
      allLearners.addAll(lmsServiceRegistry.getDepartmentService().getAllLearners(group.getId().getId()));
    }



    for (String learner : allLearners)
    {
      if (learnerSuccessRepository.isExist(LearnerId.valueOf(learner), startDate.getYear(), startDate.getMonthValue()))
      {
        continue;
      }
      double performance = 0;
      LearnerScore score = getTotalAndMaxScore(runtimeDataRepository.listRunTimeDataTestCompleted(learner, startDate, endDate));
      List<Double> progresses = runtimeDataRepository.listRunTimeDataProgress(learner, startDate, endDate);

      if (!progresses.isEmpty())
      {
        performance = progresses.stream().reduce(Double::sum).orElse(0.0) / progresses.size();
      }
      learnerSuccessRepository
          .save(new LearnerSuccess(learner,  score.getScore(), score.getMaxScore(), COURSE_TYPE, performance, startDate.getYear(), startDate.getMonthValue()));
    }
    return null;
  }

  private LearnerScore getTotalAndMaxScore(List<RuntimeData> runtimeDataList)
  {
    Validate.notNull(runtimeDataList);
    if (runtimeDataList.isEmpty())
    {
      return new LearnerScore();
    }
    int totalScore = 0;
    int maxScore = 0;

    for (RuntimeData runtimeData : runtimeDataList)
    {
      Map<DataModel, Serializable> data = runtimeData.getData();
      for (Map.Entry<DataModel, Serializable> dataEntry : data.entrySet())
      {
        if (dataEntry.getKey().getName().equals(DataModelConstants.CMI_SCORE_RAW))
        {
          totalScore += Integer.parseInt((String) dataEntry.getValue());
        }
        else if (dataEntry.getKey().getName().equals(DataModelConstants.CMI_SCORE_MAX))
        {
          maxScore += Integer.parseInt((String) dataEntry.getValue());
        }
      }
    }

    return new LearnerScore(totalScore, maxScore);
  }

  private static class LearnerScore
  {
    private final int score;
    private final int maxScore;

    public LearnerScore()
    {
      this.score = 0;
      this.maxScore = 0;
    }

    public LearnerScore(int score, int maxScore)
    {
      this.score = score;
      this.maxScore = maxScore;
    }

    public int getScore()
    {
      return score;
    }

    public int getMaxScore()
    {
      return maxScore;
    }
  }
}
