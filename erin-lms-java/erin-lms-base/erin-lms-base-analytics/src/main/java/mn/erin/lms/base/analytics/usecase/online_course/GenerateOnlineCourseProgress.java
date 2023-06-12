package mn.erin.lms.base.analytics.usecase.online_course;

import java.util.List;

import mn.erin.domain.aim.model.group.GroupId;
import mn.erin.domain.base.usecase.UseCaseException;
import mn.erin.lms.base.analytics.model.learner.LearnerProgress;
import mn.erin.lms.base.analytics.repository.AnalyticsRepositoryRegistry;
import mn.erin.lms.base.analytics.usecase.AnalyticsUseCase;
import mn.erin.lms.base.domain.model.category.CourseCategoryId;
import mn.erin.lms.base.domain.service.CourseTypeResolver;

/**
 * @author Temuulen Naranbold
 */
public class GenerateOnlineCourseProgress extends AnalyticsUseCase<String, List<LearnerProgress>>
{
  private static final String ONLINE_COURSE_CATEGORY = "online-course";
  public GenerateOnlineCourseProgress(AnalyticsRepositoryRegistry analyticsRepositoryRegistry,
      CourseTypeResolver courseTypeResolver)
  {
    super(analyticsRepositoryRegistry, courseTypeResolver);
  }

  @Override
  public List<LearnerProgress> execute(String input) throws UseCaseException
  {
    return analyticsRepositoryRegistry.getLearnerActivityRepository().getLearnerProgress(GroupId.valueOf(input), CourseCategoryId.valueOf(ONLINE_COURSE_CATEGORY));
  }
}
