package mn.erin.lms.base.analytics.repository.mongo.implementation;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import javax.inject.Inject;

import mn.erin.domain.aim.model.group.GroupId;
import mn.erin.lms.base.analytics.repository.mongo.PromotionAnalyticsRepository;
import mn.erin.lms.base.analytics.repository.mongo.config.AnalyticsTestBeanConfig;
import mn.erin.lms.base.analytics.usecase.dto.LearnerActivity;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * @author Munkh
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { AnalyticsTestBeanConfig.class })
public class PromotionAnalyticsRepositoryImplTest
{
  @Inject
  private PromotionAnalyticsRepository promotionAnalyticsRepository;

  @Test
  @Ignore
  public void find_all_execution_test()
  {
    Instant start = Instant.now();
    List<LearnerActivity> analytics = promotionAnalyticsRepository.getAllActivities(GroupId.valueOf("5f27a8d1507e3b4c14f8cba3"));

    Duration duration = Duration.between(start, Instant.now());
    Assert.assertTrue("Load time should be lower than 5 seconds", Duration.ofSeconds(5).toMillis() > duration.toMillis());
  }
}
