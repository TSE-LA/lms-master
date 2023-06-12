package mn.erin.lms.base.analytics.repository.mongo.implementation;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import javax.inject.Inject;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import mn.erin.lms.base.aim.user.LearnerId;
import mn.erin.lms.base.analytics.model.Analytic;
import mn.erin.lms.base.analytics.repository.mongo.LearnerAnalyticsRepository;
import mn.erin.lms.base.analytics.repository.mongo.config.AnalyticsTestBeanConfig;
import mn.erin.lms.base.domain.model.category.CourseCategoryId;

/**
 * @author Munkh
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { AnalyticsTestBeanConfig.class })
public class OnlineLearnerAnalyticsRepositoryTest
{
  @Inject
  private LearnerAnalyticsRepository learnerAnalyticsRepository;

  @Test
  @Ignore // Cannot generate learner ID
  public void find_all_execution_test()
  {
    Instant start = Instant.now();
    List<Analytic> analytics = learnerAnalyticsRepository.getAnalytics(
        LearnerId.valueOf("employer1"),
        CourseCategoryId.valueOf("online-course"),
        LocalDate.of(2020, 7, 1),
        LocalDate.now()
    );

    Duration duration = Duration.between(start, Instant.now());
    Assert.assertTrue("Load time should be lower than 5 seconds", Duration.ofSeconds(5).toMillis() > duration.toMillis());
  }
}
