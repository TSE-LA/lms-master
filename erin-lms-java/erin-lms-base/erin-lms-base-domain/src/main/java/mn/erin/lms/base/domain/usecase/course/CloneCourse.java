package mn.erin.lms.base.domain.usecase.course;

import java.util.Set;

import org.apache.commons.lang3.Validate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import mn.erin.domain.base.usecase.UseCaseException;
import mn.erin.domain.dms.repository.DMSRepositoryException;
import mn.erin.lms.base.domain.model.Authorized;
import mn.erin.lms.base.domain.model.assessment.CourseAssessment;
import mn.erin.lms.base.domain.model.assessment.QuizId;
import mn.erin.lms.base.domain.model.content.CourseContent;
import mn.erin.lms.base.domain.model.course.Course;
import mn.erin.lms.base.domain.model.course.CourseDetail;
import mn.erin.lms.base.domain.model.course.CourseId;
import mn.erin.lms.base.aim.user.Author;
import mn.erin.lms.base.domain.repository.CourseAssessmentRepository;
import mn.erin.lms.base.domain.repository.CourseContentRepository;
import mn.erin.lms.base.domain.repository.LmsRepositoryException;
import mn.erin.lms.base.domain.repository.LmsRepositoryRegistry;
import mn.erin.lms.base.domain.service.LmsServiceRegistry;
import mn.erin.lms.base.domain.usecase.course.dto.CloneCourseInput;
import mn.erin.lms.base.domain.usecase.course.dto.CourseDto;
import mn.erin.lms.base.domain.usecase.course.dto.CreateCourseInput;
import mn.erin.lms.base.domain.usecase.course.dto.CreateCourseOutput;

/**
 * @author Bat-Erdene Tsogoo.
 */
@Authorized(users = { Author.class })
public class CloneCourse extends CourseUseCase<CloneCourseInput, CourseDto>
{
  private static final Logger LOGGER = LoggerFactory.getLogger(CloneCourse.class);

  private final CourseAssessmentRepository courseAssessmentRepository;
  private final CourseContentRepository courseContentRepository;

  private CreateCourse createCourse;

  public CloneCourse(LmsRepositoryRegistry lmsRepositoryRegistry, LmsServiceRegistry lmsServiceRegistry)
  {
    super(lmsRepositoryRegistry, lmsServiceRegistry);
    this.courseAssessmentRepository = lmsRepositoryRegistry.getCourseAssessmentRepository();
    this.courseContentRepository = lmsRepositoryRegistry.getCourseContentRepository();
    createCourse = new CreateCourse(lmsRepositoryRegistry, lmsServiceRegistry);
  }

  @Override
  public CourseDto execute(CloneCourseInput input) throws UseCaseException
  {
    Validate.notNull(input);

    CourseId existingCourseId = CourseId.valueOf(input.getCourseId());
    CourseId clonedCourseId = createClone(input.getCreateCourseInput());

    return clone(existingCourseId, clonedCourseId);
  }

  private CourseId createClone(CreateCourseInput createCourseInput) throws UseCaseException
  {
    CreateCourseOutput output = createCourse.execute(createCourseInput);
    return CourseId.valueOf(output.getCourseId());
  }

  private CourseDto clone(CourseId existingCourseId, CourseId clonedCourseId) throws UseCaseException
  {
    Course existingCourse = getCourse(existingCourseId);
    Course clonedCourse = getCourse(clonedCourseId);

    CourseDetail courseDetail = clonedCourse.getCourseDetail();
    courseDetail.setHasQuiz(existingCourse.getCourseDetail().hasQuiz());
    courseDetail.setHasFeedbackOption(existingCourse.getCourseDetail().hasFeedbackOption());
    courseDetail.setHasAssessment(existingCourse.getCourseDetail().hasAssessment());

    Course course;
    try
    {
      course = courseRepository.update(clonedCourseId, existingCourse.getCourseCategoryId(),
          courseDetail, clonedCourse.getCourseType(), clonedCourse.getAssessmentId(), clonedCourse.getCertificateId());
    }
    catch (LmsRepositoryException e)
    {
      LOGGER.error(e.getMessage(), e);
      throw new UseCaseException(e.getMessage(), e);
    }

    cloneAssessmentAndContent(existingCourseId, clonedCourseId);
    try
    {
      lmsServiceRegistry.getLmsFileSystemService().clone(existingCourseId.getId(), clonedCourseId.getId());
    }
    catch (DMSRepositoryException e)
    {
      LOGGER.warn(e.getMessage(), e);
    }

    return toCourseDto(course);
  }

  private void cloneAssessmentAndContent(CourseId existingCourseId, CourseId clonedCourseId)
  {
    cloneAssessment(existingCourseId, clonedCourseId);
    cloneCourseContent(existingCourseId, clonedCourseId);
  }

  private void cloneAssessment(CourseId existingCourseId, CourseId clonedCourseId)
  {
    try
    {
      // Create a new assessment under the new cloned course's ID and assign the associated tests.
      CourseAssessment assessment = courseAssessmentRepository.fetchById(existingCourseId);
      Set<QuizId> testIdList = assessment.getQuizzes();
      courseAssessmentRepository.create(clonedCourseId, testIdList);
    }
    catch (LmsRepositoryException e)
    {
      LOGGER.error(e.getMessage(), e);
    }
  }

  private void cloneCourseContent(CourseId existingCourseId, CourseId clonedCourseId)
  {
    try
    {
      CourseContent courseContent = courseContentRepository.fetchById(existingCourseId);

      if (courseContent != null)
      {
        courseContentRepository.create(clonedCourseId, courseContent.getModules(), courseContent.getAttachments());
      }
    }
    catch (LmsRepositoryException e)
    {
      LOGGER.error(e.getMessage(), e);
    }
  }
}
