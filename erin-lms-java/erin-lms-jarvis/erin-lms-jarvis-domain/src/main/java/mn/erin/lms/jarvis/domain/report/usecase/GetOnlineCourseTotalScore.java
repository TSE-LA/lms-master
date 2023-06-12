package mn.erin.lms.jarvis.domain.report.usecase;

import java.util.List;

import org.apache.commons.lang3.Validate;

import mn.erin.domain.base.usecase.UseCaseException;
import mn.erin.lms.base.domain.model.Authorized;
import mn.erin.lms.jarvis.domain.report.model.analytics.CourseAnalyticData;
import mn.erin.lms.base.domain.model.enrollment.CourseEnrollment;
import mn.erin.lms.base.aim.user.Author;
import mn.erin.lms.base.aim.user.Instructor;
import mn.erin.lms.base.aim.user.Learner;
import mn.erin.lms.base.aim.user.LearnerId;
import mn.erin.lms.base.aim.user.Manager;
import mn.erin.lms.base.aim.user.Supervisor;
import mn.erin.lms.base.domain.repository.LmsRepositoryException;
import mn.erin.lms.base.domain.repository.LmsRepositoryRegistry;
import mn.erin.lms.base.domain.usecase.course.CourseUseCase;
import mn.erin.lms.jarvis.domain.report.service.CourseAnalyticsService;
import mn.erin.lms.jarvis.domain.service.JarvisLmsServiceRegistry;

/**
 *
 * @author Munkh
 */
@Authorized(users = { Author.class, Manager.class, Instructor.class, Supervisor.class, Learner.class })
public class GetOnlineCourseTotalScore extends CourseUseCase<String, Integer>
{
  private CourseAnalyticsService courseAnalyticsService;

  public GetOnlineCourseTotalScore(LmsRepositoryRegistry lmsRepositoryRegistry, JarvisLmsServiceRegistry lmsServiceRegistry)
  {
    super(lmsRepositoryRegistry, lmsServiceRegistry);
    this.courseAnalyticsService = lmsServiceRegistry.getCourseAnalyticsService();
  }

  @Override
  public Integer execute(String input) throws UseCaseException
  {
    Validate.notNull(input);
    int totalScore = 0;
    List<CourseEnrollment> enrolledCourses = courseEnrollmentRepository.listAll(LearnerId.valueOf(input));

    try
    {
      for(CourseEnrollment courseEnrollment: enrolledCourses)
      {
        if (!courseRepository.exists(courseEnrollment.getCourseId()) || courseRepository.fetchById(courseEnrollment.getCourseId()).getCourseContentId() == null)
        {
          continue;
        }
        CourseAnalyticData analyticData = courseAnalyticsService.getCourseAnalytics(courseEnrollment.getCourseId(), LearnerId.valueOf(input));
        totalScore += analyticData != null && analyticData.getScore() != null ? analyticData.getScore() : 0;
      }
    }
    catch (LmsRepositoryException e)
    {
      throw new UseCaseException(e.getMessage(), e);
    }

    return totalScore;
  }
}
