package mn.erin.lms.legacy.domain.lms.usecase.course_content.get_course_publish_status;

import java.util.Map;
import java.util.Objects;

import org.apache.commons.lang3.Validate;

import mn.erin.domain.base.usecase.UseCase;
import mn.erin.domain.base.usecase.UseCaseException;
import mn.erin.lms.legacy.domain.lms.model.assessment.Assessment;
import mn.erin.lms.legacy.domain.lms.model.content.CourseContent;
import mn.erin.lms.legacy.domain.lms.model.content.CourseModule;
import mn.erin.lms.legacy.domain.lms.model.content.CourseSection;
import mn.erin.lms.legacy.domain.lms.model.course.Course;
import mn.erin.lms.legacy.domain.lms.model.course.CourseId;
import mn.erin.lms.legacy.domain.lms.repository.CourseAssessmentRepository;
import mn.erin.lms.legacy.domain.lms.repository.CourseContentRepository;
import mn.erin.lms.legacy.domain.lms.repository.CourseRepository;
import mn.erin.lms.legacy.domain.lms.repository.LMSRepositoryException;
import mn.erin.lms.legacy.domain.lms.usecase.course_content.get_course_content.GetCourseContentInput;

/**
 * @author Erdenetulga
 */
public class GetCoursePublishStatus implements UseCase<GetCourseContentInput, GetCoursePublishStatusOutput>
{
  private static final String ERROR_MSG_FOR_INPUT = "Course content input data cannot be null!";
  private static final String ERR_MSG_GET_COURSE_STATUS = "Failed to get course publish status with ID [%s]";
  public static final String HAS_TEST = "hasTest";
  public static final String NOT_READY = "notReady";
  public static final String READY = "ready";

  private final CourseContentRepository contentRepository;
  private final CourseRepository courseRepository;
  private final CourseAssessmentRepository courseAssessmentRepository;

  public GetCoursePublishStatus(CourseContentRepository contentRepository, CourseRepository courseRepository,
      CourseAssessmentRepository courseAssessmentRepository)
  {
    this.contentRepository = Objects.requireNonNull(contentRepository);
    this.courseRepository = Objects.requireNonNull(courseRepository);
    this.courseAssessmentRepository = courseAssessmentRepository;
  }

  @Override
  public GetCoursePublishStatusOutput execute(GetCourseContentInput input) throws UseCaseException
  {
    Validate.notNull(input, ERROR_MSG_FOR_INPUT);

    try
    {
      String id = input.getCourseId();
      CourseId courseId = new CourseId(id);
      Course course = courseRepository.getCourse(courseId);
      CourseContent courseContent = contentRepository.get(courseId);

      String publishStatus = READY;
      publishStatus = checkTest(courseId, course, publishStatus);

      if (courseContent != null)
      {
        for (CourseModule module : courseContent.getModulesList())
        {
          for (CourseSection section : module.getSectionList())
          {
            if (section.getAttachmentId().getId().equals("empty"))
            {
              publishStatus = NOT_READY;
              break;
            }
          }
        }
      }
      else{
        publishStatus = NOT_READY;
      }
      return new GetCoursePublishStatusOutput(courseId.getId(), publishStatus);
    }
    catch (LMSRepositoryException e)
    {
      throw new UseCaseException(String.format(ERR_MSG_GET_COURSE_STATUS, input.getCourseId()), e);
    }
  }

  private String checkTest(CourseId courseId, Course course, String publishStatus)
  {
    boolean hasTest = false;
    Map<String, Object> properties = course.getCourseDetail().getProperties();
    if (properties.containsKey(HAS_TEST))
    {
      hasTest = (boolean) properties.get(HAS_TEST);
    }

    try
    {
     Assessment assessment = courseAssessmentRepository.get(courseId);
      if (hasTest && assessment.getId().getId().equals("empty"))
      {
        publishStatus = NOT_READY;
      }
    }
    catch (LMSRepositoryException e)
    {
      publishStatus = hasTest? NOT_READY : READY;
    }
    return publishStatus;
  }
}
