package mn.erin.lms.unitel.rest;

import javax.annotation.PostConstruct;

import org.springframework.scheduling.annotation.Scheduled;

import mn.erin.lms.base.domain.repository.LmsRepositoryRegistry;
import mn.erin.lms.unitel.domain.usecase.ExpirePromotion;

/**
 * @author Bat-Erdene Tsogoo.
 */
public class PromotionExpiryScheduler
{
  private ExpirePromotion usecase;

  public PromotionExpiryScheduler(LmsRepositoryRegistry repositoryRegistry)
  {
    this.usecase = new ExpirePromotion(repositoryRegistry);
  }

  @PostConstruct
  @Scheduled(cron = "0 0 0 * * ?", zone = "Asia/Ulaanbaatar")
  private void schedule()
  {
    usecase.execute(null);
  }
}
