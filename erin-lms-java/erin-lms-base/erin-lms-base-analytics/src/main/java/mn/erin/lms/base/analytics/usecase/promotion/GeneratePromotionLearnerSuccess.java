package mn.erin.lms.base.analytics.usecase.promotion;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.Validate;

import mn.erin.domain.aim.service.AimConfigProvider;
import mn.erin.domain.base.usecase.UseCase;
import mn.erin.domain.base.usecase.UseCaseException;
import mn.erin.lms.base.analytics.repository.mongo.LearnerSuccessAnalyticRepository;
import mn.erin.lms.base.scorm.model.DataModel;
import mn.erin.lms.base.scorm.model.RuntimeData;

/**
 * @author Oyungerel Chuluunsukh
 **/
public class GeneratePromotionLearnerSuccess implements UseCase<LocalDate, Void>
{
  private final LearnerSuccessAnalyticRepository learnerSuccessAnalyticRepository;
  private final AimConfigProvider aimConfigProvider;
  private static final String COURSE_TYPE = "promotion";
  public static final String CMI_SCORE_RAW = "cmi.score.raw";
  public static final String CMI_SCORE_MAX = "cmi.score.max";

  public GeneratePromotionLearnerSuccess(LearnerSuccessAnalyticRepository learnerSuccessAnalyticRepository, AimConfigProvider aimConfigProvider)
  {
    this.learnerSuccessAnalyticRepository = learnerSuccessAnalyticRepository;
    this.aimConfigProvider = aimConfigProvider;
  }

  @Override
  public Void execute(LocalDate startDate) throws UseCaseException
  {
    if (learnerSuccessAnalyticRepository.isExistThisMonth(startDate.getYear(), startDate.getMonthValue(), COURSE_TYPE))
    {
      throw new UseCaseException("This month's learner successes have been already generated!");
    }

    LocalDate endDate = startDate.plusMonths(1);

    Set<String> allLearners = learnerSuccessAnalyticRepository.getAllLearners(aimConfigProvider.getDefaultTenantId().getId());

    for (String learner : allLearners)
    {
      if (learnerSuccessAnalyticRepository.isExist(learner, startDate.getYear(), startDate.getMonthValue(), COURSE_TYPE))
      {
        continue;
      }
      double performance = 0;
      LearnerScore score = getTotalAndMaxScore(learnerSuccessAnalyticRepository.listRunTimeDataTestCompleted(learner, startDate, endDate));
      List<Double> progresses = learnerSuccessAnalyticRepository.listRunTimeDataProgress(learner, startDate, endDate);

      if (!progresses.isEmpty())
      {
        performance = progresses.stream().reduce(Double::sum).orElse(0.0) / progresses.size();
      }
      learnerSuccessAnalyticRepository.save(learner, score.getScore(), score.getMaxScore(), COURSE_TYPE, performance, startDate.getYear(), startDate.getMonthValue());
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
        if (dataEntry.getKey().getName().equals(CMI_SCORE_RAW))
        {
          totalScore += Integer.parseInt((String) dataEntry.getValue());
        }
        else if (dataEntry.getKey().getName().equals(CMI_SCORE_MAX))
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

