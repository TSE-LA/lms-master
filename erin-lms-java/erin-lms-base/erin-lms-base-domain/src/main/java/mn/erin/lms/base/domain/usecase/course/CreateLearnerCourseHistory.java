package mn.erin.lms.base.domain.usecase.course;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;

import mn.erin.domain.base.usecase.UseCaseException;
import mn.erin.lms.base.domain.model.category.CourseCategory;
import mn.erin.lms.base.domain.model.category.CourseCategoryId;
import mn.erin.lms.base.domain.model.course.Course;
import mn.erin.lms.base.domain.model.course.LearnerCourseHistory;
import mn.erin.lms.base.domain.repository.LmsRepositoryException;
import mn.erin.lms.base.domain.repository.LmsRepositoryRegistry;
import mn.erin.lms.base.domain.service.LmsServiceRegistry;
import mn.erin.lms.base.domain.usecase.course.dto.LearnerCourseHistoryInput;

/**
 * @author Temuulen Naranbold
 */
public class CreateLearnerCourseHistory extends CourseUseCase<LearnerCourseHistoryInput, LearnerCourseHistory>
{
  private static final String ONLINE_COURSE = "online-course";
  private static final String CLASSROOM_COURSE = "classroom-course";

  public CreateLearnerCourseHistory(LmsRepositoryRegistry lmsRepositoryRegistry,
      LmsServiceRegistry lmsServiceRegistry)
  {
    super(lmsRepositoryRegistry, lmsServiceRegistry);
  }

  @Override
  public LearnerCourseHistory execute(LearnerCourseHistoryInput input) throws UseCaseException
  {
    try
    {
      Course course = lmsRepositoryRegistry.getCourseRepository().fetchById(input.getCourseId());
      String courseType = getCourseType(course.getCourseCategoryId());
      LocalDateTime date = courseType.equals(ONLINE_COURSE) ? LocalDateTime.now() : courseType.equals(CLASSROOM_COURSE) ?
          new SimpleDateFormat("yyyy-MM-dd").parse(course.getCourseDetail().getProperties().get("date")).toInstant().atZone(ZoneId.of("Asia/Ulaanbaatar")).toLocalDateTime() : null;

      if (lmsRepositoryRegistry.getLearnerCourseHistoryRepository().exists(course.getCourseId().getId(), input.getUserId()))
      {
        return lmsRepositoryRegistry.getLearnerCourseHistoryRepository().update(
            course.getCourseId().getId(),
            input.getUserId(),
            course.getCourseDetail().getTitle(),
            courseType,
            course.getCourseDetail().getCredit(),
            date,
            input.getPercentage()
        );
      }
      else
      {
        return lmsRepositoryRegistry.getLearnerCourseHistoryRepository().create(
            course.getCourseId().getId(),
            input.getUserId(),
            course.getCourseDetail().getTitle(),
            courseType,
            course.getCourseDetail().getCredit(),
            date,
            input.getPercentage()
        );
      }
    }
    catch (LmsRepositoryException | ParseException e)
    {
      throw new UseCaseException(e.getMessage());
    }
  }

  private String getCourseType(CourseCategoryId courseCategoryId) throws UseCaseException
  {
    try
    {
      CourseCategory courseCategory = lmsRepositoryRegistry.getCourseCategoryRepository().getById(courseCategoryId);
      if (courseCategory.getParentCategoryId().getId().equals(ONLINE_COURSE))
      {
        return ONLINE_COURSE;
      }
      else if (courseCategory.getParentCategoryId().getId().equals(CLASSROOM_COURSE))
      {
        return CLASSROOM_COURSE;
      }
    }
    catch (LmsRepositoryException e)
    {
      throw new UseCaseException(e.getMessage());
    }
    throw new UseCaseException("Could not get course type");
  }
}
