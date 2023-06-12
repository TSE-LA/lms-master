package mn.erin.lms.jarvis.rest.config;

import java.time.LocalDate;
import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;

import mn.erin.domain.aim.repository.GroupRepository;
import mn.erin.domain.aim.service.AimConfigProvider;
import mn.erin.domain.base.usecase.UseCaseException;
import mn.erin.lms.base.domain.service.LmsServiceRegistry;
import mn.erin.lms.base.scorm.repository.RuntimeDataRepository;
import mn.erin.lms.jarvis.domain.report.repository.LearnerSuccessRepository;
import mn.erin.lms.jarvis.domain.report.usecase.GenerateLearnerSuccess;

/**
 * @author Galsan Bayart
 */
public class LearnerSuccessGenerateScheduler
{
  private static final Logger LOGGER = LoggerFactory.getLogger(LearnerSuccessGenerateScheduler.class);
  private final GenerateLearnerSuccess generateLearnerSuccess;
  private final LearnerSuccessRepository learnerSuccessRepository;

  public LearnerSuccessGenerateScheduler(GroupRepository groupRepository, LmsServiceRegistry lmsServiceRegistry,
      RuntimeDataRepository runtimeDataRepository, LearnerSuccessRepository learnerSuccessRepository, AimConfigProvider aimConfigProvider)
  {
    this.learnerSuccessRepository = learnerSuccessRepository;
    generateLearnerSuccess = new GenerateLearnerSuccess(groupRepository, lmsServiceRegistry, runtimeDataRepository, learnerSuccessRepository, aimConfigProvider);
  }

  @PostConstruct
  @Scheduled(cron = "0 0 0 1 * ?", zone = "Asia/Ulaanbaatar")
  private void schedule() throws UseCaseException
  {
    LocalDate input = LocalDate.now().minusMonths(1);
    if (input.getDayOfMonth() == 1 && !learnerSuccessRepository.isExistThisMonth(input.getYear(), input.getMonthValue()))
    {
      generateLearnerSuccess.execute(input);
    }
  }
}
