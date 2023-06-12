package mn.erin.lms.base.analytics.repository.mongo.implementation;

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import mn.erin.domain.base.usecase.UseCaseException;
import mn.erin.lms.base.analytics.model.learner.LearnerProgress;
import mn.erin.lms.base.analytics.repository.AnalyticsRepositoryRegistry;
import mn.erin.lms.base.analytics.repository.mongo.LearnerActivityRepository;
import mn.erin.lms.base.analytics.usecase.online_course.GenerateOnlineCourseProgress;
import mn.erin.lms.base.domain.service.CourseTypeResolver;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.any;

/**
 * @author Temuulen Naranbold
 */
public class GenerateOnlineCourseProgressTest
{
  private AnalyticsRepositoryRegistry analyticsRepositoryRegistry;
  private CourseTypeResolver courseTypeResolver;
  private LearnerActivityRepository learnerActivityRepository;
  private GenerateOnlineCourseProgress generateOnlineCourseProgress;

  @Before
  public void setUp()
  {
    analyticsRepositoryRegistry = mock(AnalyticsRepositoryRegistry.class);
    courseTypeResolver = mock(CourseTypeResolver.class);
    learnerActivityRepository = mock(LearnerActivityRepository.class);

    when(analyticsRepositoryRegistry.getLearnerActivityRepository()).thenReturn(learnerActivityRepository);

    LearnerProgress learnerProgress = new LearnerProgress("admin", 50);
    List<LearnerProgress> learnerProgresses = new ArrayList<>();
    learnerProgresses.add(learnerProgress);
    when(learnerActivityRepository.getLearnerProgress(any(), any())).thenReturn(learnerProgresses);

    generateOnlineCourseProgress = new GenerateOnlineCourseProgress(analyticsRepositoryRegistry, courseTypeResolver);
  }

  @Test
  public void whenSuccess() throws UseCaseException
  {
    Assert.assertEquals(1, generateOnlineCourseProgress.execute("groupId").size());
  }
}
