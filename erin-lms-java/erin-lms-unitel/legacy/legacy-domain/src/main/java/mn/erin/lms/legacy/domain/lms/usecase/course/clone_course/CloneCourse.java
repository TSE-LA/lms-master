package mn.erin.lms.legacy.domain.lms.usecase.course.clone_course;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.apache.commons.lang3.Validate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import mn.erin.domain.base.usecase.UseCaseException;
import mn.erin.lms.legacy.domain.lms.model.assessment.Assessment;
import mn.erin.lms.legacy.domain.lms.model.assessment.TestId;
import mn.erin.lms.legacy.domain.lms.model.content.CourseContent;
import mn.erin.lms.legacy.domain.lms.model.course.CourseId;
import mn.erin.lms.legacy.domain.lms.repository.CourseAssessmentRepository;
import mn.erin.lms.legacy.domain.lms.repository.CourseCategoryRepository;
import mn.erin.lms.legacy.domain.lms.repository.CourseContentRepository;
import mn.erin.lms.legacy.domain.lms.repository.CourseRepository;
import mn.erin.lms.legacy.domain.lms.repository.LMSRepositoryException;
import mn.erin.lms.legacy.domain.lms.usecase.course.CourseUseCase;
import mn.erin.lms.legacy.domain.lms.usecase.course.create_course.CreateCourse;
import mn.erin.lms.legacy.domain.lms.usecase.course.create_course.CreateCourseOutput;
import mn.erin.lms.legacy.domain.lms.usecase.course.get_course.GetCourseOutput;
import mn.erin.lms.legacy.domain.lms.usecase.course.update_course.UpdateCourseProperties;
import mn.erin.lms.legacy.domain.lms.usecase.course.update_course.UpdateCoursePropertiesInput;
import mn.erin.lms.legacy.domain.lms.usecase.course_group.CreateCourseGroup;

/**
 * @author Bat-Erdene Tsogoo.
 */
public class CloneCourse extends CourseUseCase<CloneCourseInput, GetCourseOutput>
{
  private static final Logger LOGGER = LoggerFactory.getLogger(CloneCourse.class);

  private final CourseAssessmentRepository courseAssessmentRepository;
  private final CourseContentRepository courseContentRepository;
  private CreateCourse createCourse;
  private UpdateCourseProperties updateCourseProperties;

  public CloneCourse(CourseRepository courseRepository, CourseCategoryRepository courseCategoryRepository, CourseAssessmentRepository courseAssessmentRepository,
      CourseContentRepository courseContentRepository, CreateCourseGroup createCourseGroup)
  {
    super(courseRepository);
    this.createCourse = new CreateCourse(courseRepository, courseCategoryRepository, createCourseGroup);
    this.updateCourseProperties = new UpdateCourseProperties(courseRepository);
    this.courseAssessmentRepository = Objects.requireNonNull(courseAssessmentRepository, "CourseAssessmentRepository cannot be null!");
    this.courseContentRepository = Objects.requireNonNull(courseContentRepository, "CourseContentRepository cannot be null!");
  }

  @Override
  public GetCourseOutput execute(CloneCourseInput input) throws UseCaseException
  {
    Validate.notNull(input, "Input is required to clone a course");

    CreateCourseOutput clonedCourse;
    clonedCourse = createCourse.execute(input.getNewCourseDetail());

    CourseId existingCourseId = new CourseId(input.getCourseId());
    CourseId clonedCourseId = new CourseId(clonedCourse.getId());
    try
    {
      // Create a new assessment under the new cloned course's ID and assign the associated tests.
      Assessment assessment = courseAssessmentRepository.get(existingCourseId);
      List<TestId> testIdList = assessment.getCourseTests();
      courseAssessmentRepository.create(clonedCourseId, testIdList);
      Map<String, Object> testProperty = new HashMap<>();
      testProperty.put("hasTest", true);
      UpdateCoursePropertiesInput updateCoursePropertiesInput = new UpdateCoursePropertiesInput(clonedCourseId.getId(), testProperty);
      updateCourseProperties.execute(updateCoursePropertiesInput);
    }
    catch (LMSRepositoryException e)
    {
      LOGGER.error(e.getMessage(), e);
    }

    try
    {
      CourseContent courseContent = courseContentRepository.get(existingCourseId);

      if (courseContent != null)
      {
        courseContentRepository.create(clonedCourseId, courseContent.getModulesList(), courseContent.getAttachmentsList(), courseContent.getAdditionalFileList());
      }
    }
    catch (LMSRepositoryException e)
    {
      LOGGER.error(e.getMessage(), e);
    }

    return getCourseOutput(getCourse(clonedCourseId));
  }
}
