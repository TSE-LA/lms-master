package mn.erin.lms.base.analytics.config;

import java.time.LocalDate;
import javax.annotation.PostConstruct;

import org.springframework.scheduling.annotation.Scheduled;

import mn.erin.domain.aim.service.AimConfigProvider;
import mn.erin.domain.base.usecase.UseCaseException;
import mn.erin.lms.base.analytics.repository.mongo.LearnerSuccessAnalyticRepository;
import mn.erin.lms.base.analytics.usecase.promotion.GeneratePromotionLearnerSuccess;

/**
 * @author Galsan Bayart
 */
public class LearnerSuccessGenerateScheduler
{
  public static final String DEFAULT_TENANT_ID = "jarvis";
  private final GeneratePromotionLearnerSuccess generateLearnerSuccess;
  private final LearnerSuccessAnalyticRepository learnerSuccessAnalyticRepository;
  private final AimConfigProvider aimConfigProvider;

  public LearnerSuccessGenerateScheduler(LearnerSuccessAnalyticRepository learnerSuccessAnalyticRepository, AimConfigProvider aimConfigProvider)
  {
    this.learnerSuccessAnalyticRepository = learnerSuccessAnalyticRepository;
    this.aimConfigProvider = aimConfigProvider;
    generateLearnerSuccess = new GeneratePromotionLearnerSuccess(learnerSuccessAnalyticRepository, aimConfigProvider);
  }

  @PostConstruct
  @Scheduled(cron = "0 0 0 1 * ?", zone = "Asia/Ulaanbaatar")
  private void schedulePromotionLearnerSuccess() throws UseCaseException
  {
    if(!aimConfigProvider.getDefaultTenantId().getId().equals(DEFAULT_TENANT_ID)){
      LocalDate input = LocalDate.now().minusMonths(1);
      if (input.getDayOfMonth() == 1 && !learnerSuccessAnalyticRepository.isExistThisMonth(input.getYear(), input.getMonthValue(), "promotion"))
      {
        generateLearnerSuccess.execute(input);
      }
    }
  }
}
