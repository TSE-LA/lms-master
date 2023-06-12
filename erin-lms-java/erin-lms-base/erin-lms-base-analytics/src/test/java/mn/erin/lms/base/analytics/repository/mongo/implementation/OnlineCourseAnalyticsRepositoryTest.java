package mn.erin.lms.base.analytics.repository.mongo.implementation;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import javax.inject.Inject;

import mn.erin.domain.aim.model.group.GroupId;
import mn.erin.lms.base.analytics.model.Analytic;
import mn.erin.lms.base.analytics.repository.mongo.CourseAnalyticsRepository;
import mn.erin.lms.base.analytics.repository.mongo.config.AnalyticsTestBeanConfig;
import mn.erin.lms.base.domain.model.category.CourseCategoryId;
import mn.erin.lms.base.domain.model.online_course.EmployeeType;
import org.junit.Assert;
import org.junit.Before;
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
public class OnlineCourseAnalyticsRepositoryTest
{

  @Inject
  private CourseAnalyticsRepository courseAnalyticsRepository;

  @Before
  public void setUp()
  {
  }

  @Test
  @Ignore // Cannot generate group ID
  public void find_all_execution_test()
  {
    Instant start = Instant.now();
    List<Analytic> analytics = courseAnalyticsRepository.getAll(
        GroupId.valueOf("5f27a8d1507e3b4c14f8cba3"),
        CourseCategoryId.valueOf("online-course"),
        LocalDate.of(2020, 7, 1),
        LocalDate.now()
    );
    Duration duration = Duration.between(start, Instant.now());
    Assert.assertTrue("Load time should be lower than 5 seconds", Duration.ofSeconds(5).toMillis() > duration.toMillis());
  }

  @Test
  @Ignore // Cannot generate group ID
  public void find_all_execution_with_type_test()
  {
    Instant start = Instant.now();
    courseAnalyticsRepository.getAll(
        GroupId.valueOf("5f27a8d1507e3b4c14f8cba3"),
        CourseCategoryId.valueOf("online-course"),
        new EmployeeType(),
        LocalDate.of(2020, 7, 1),
        LocalDate.now()
    );
    Duration duration = Duration.between(start, Instant.now());
    Assert.assertTrue("Load time should be lower than 5 seconds", Duration.ofSeconds(5).toMillis() > duration.toMillis());
  }
}
