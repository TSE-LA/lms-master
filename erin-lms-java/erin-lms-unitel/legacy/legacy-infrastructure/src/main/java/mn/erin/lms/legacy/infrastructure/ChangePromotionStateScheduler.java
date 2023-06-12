package mn.erin.lms.legacy.infrastructure;

import javax.annotation.PostConstruct;

import org.springframework.scheduling.annotation.Scheduled;

import mn.erin.lms.legacy.domain.lms.repository.CourseCategoryRepository;
import mn.erin.lms.legacy.domain.lms.repository.CourseRepository;
import mn.erin.lms.legacy.domain.unitel.usecase.promotion.ChangeCurrentPromotionState;

/**
 * @author Bat-Erdene Tsogoo.
 */
public class ChangePromotionStateScheduler
{
  private ChangeCurrentPromotionState usecase;

  public ChangePromotionStateScheduler(CourseRepository courseRepository, CourseCategoryRepository courseCategoryRepository)
  {
    usecase = new ChangeCurrentPromotionState(courseRepository, courseCategoryRepository);
    usecase.execute(null);
  }

  @PostConstruct
  @Scheduled(cron = "0 0 0 * * ?", zone = "Asia/Ulaanbaatar")
  private void schedule()
  {
    usecase.execute(null);
  }
}
