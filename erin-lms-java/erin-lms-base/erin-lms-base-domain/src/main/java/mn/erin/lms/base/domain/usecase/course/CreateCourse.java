package mn.erin.lms.base.domain.usecase.course;

import java.time.LocalDateTime;
import java.util.Map;

import org.apache.commons.lang3.Validate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import mn.erin.domain.base.usecase.UseCaseException;
import mn.erin.lms.base.domain.model.Authorized;
import mn.erin.lms.base.domain.model.CourseConstants;
import mn.erin.lms.base.domain.model.category.CourseCategoryId;
import mn.erin.lms.base.domain.model.course.Course;
import mn.erin.lms.base.domain.model.course.CourseDepartmentRelation;
import mn.erin.lms.base.domain.model.course.CourseDetail;
import mn.erin.lms.base.domain.model.course.CourseType;
import mn.erin.lms.base.domain.model.course.DateInfo;
import mn.erin.lms.base.aim.organization.DepartmentId;
import mn.erin.lms.base.aim.user.Author;
import mn.erin.lms.base.domain.repository.LmsRepositoryException;
import mn.erin.lms.base.domain.repository.LmsRepositoryRegistry;
import mn.erin.lms.base.aim.LmsDepartmentService;
import mn.erin.lms.base.domain.service.LmsFileSystemService;
import mn.erin.lms.base.domain.service.LmsServiceRegistry;
import mn.erin.lms.base.domain.service.ThumbnailService;
import mn.erin.lms.base.domain.usecase.course.dto.CreateCourseInput;
import mn.erin.lms.base.domain.usecase.course.dto.CreateCourseOutput;

/**
 * @author Bat-Erdene Tsogoo.
 */
@Authorized(users = { Author.class })
public class CreateCourse extends CourseUseCase<CreateCourseInput, CreateCourseOutput>
{
  private static final Logger LOGGER = LoggerFactory.getLogger(CreateCourse.class);

  private final LmsDepartmentService lmsDepartmentService;
  private final LmsFileSystemService lmsFileSystemService;
  private final ThumbnailService thumbnailService;

  public CreateCourse(LmsRepositoryRegistry lmsRepositoryRegistry, LmsServiceRegistry lmsServiceRegistry)
  {
    super(lmsRepositoryRegistry, lmsServiceRegistry);
    this.lmsDepartmentService = lmsServiceRegistry.getDepartmentService();
    this.lmsFileSystemService = lmsServiceRegistry.getLmsFileSystemService();
    this.thumbnailService = lmsServiceRegistry.getThumbnailService();
  }

  @Override
  public CreateCourseOutput execute(CreateCourseInput input) throws UseCaseException
  {
    Validate.notNull(input);
    Course course = createCourse(input);
    createCourseFolder(course);
    return new CreateCourseOutput(course.getCourseId().getId());
  }

  private Course createCourse(CreateCourseInput input) throws UseCaseException
  {
    CourseCategoryId courseCategoryId = validateCourseCategory(input.getCategoryId());
    CourseType courseType = getCourseType(input.getCourseType());
    CourseDetail courseDetail = getCourseDetail(input);
    CourseDepartmentRelation courseDepartmentRelation = getCourseDepartmentRelation();
    String courseCategoryName = "";
    try
    {
      courseCategoryName = courseCategoryRepository.getCourseCategoryNameById(courseCategoryId);
    }
    catch (LmsRepositoryException e)
    {
      LOGGER.error("failed to get categoryName", e);
    }
    try
    {
      return courseRepository.create(courseCategoryId, courseCategoryName, courseDetail, courseDepartmentRelation, courseType, input.getAssessmentId(), input.getCertificateId());
    }
    catch (LmsRepositoryException e)
    {
      throw new UseCaseException(e.getMessage(), e);
    }
  }

  private CourseCategoryId validateCourseCategory(String categoryId) throws UseCaseException
  {
    CourseCategoryId courseCategoryId = CourseCategoryId.valueOf(categoryId);
    boolean categoryExists = courseCategoryRepository.exists(courseCategoryId);

    if (!categoryExists)
    {
      throw new UseCaseException("Course category with the ID: [" + courseCategoryId.getId() + "] does not exist");
    }

    return courseCategoryId;
  }

  private CourseDetail getCourseDetail(CreateCourseInput input)
  {
    CourseDetail courseDetail = new CourseDetail(input.getTitle());
    courseDetail.setDescription(input.getDescription());
    courseDetail.addProperties(input.getProperties());
    courseDetail.setCredit(input.getCredit());

    DateInfo dateInfo = new DateInfo();
    dateInfo.setCreatedDate(LocalDateTime.now());
    dateInfo.setModifiedDate(LocalDateTime.now());

    courseDetail.setDateInfo(dateInfo);

    String defaultThumbnailUrl = thumbnailService.getDefaultThumbnailUrl();
    courseDetail.setThumbnailUrl(defaultThumbnailUrl);
    return courseDetail;
  }

  private CourseDepartmentRelation getCourseDepartmentRelation()
  {
    DepartmentId currentDepartment = DepartmentId.valueOf(lmsDepartmentService.getCurrentDepartmentId());
    return new CourseDepartmentRelation(currentDepartment);
  }

  private void createCourseFolder(Course course) throws UseCaseException
  {
    String courseFolderId = lmsFileSystemService.createCourseFolder(course.getCourseId().getId(), course.getCourseDetail().getTitle());
    Map<String, String> courseProperties = course.getCourseDetail().getProperties();
    courseProperties.put(CourseConstants.PROPERTY_COURSE_FOLDER_ID, courseFolderId);
    updateCourse(course.getCourseId(), course.getCourseCategoryId(), course.getCourseDetail(), course.getCourseType(), course.getAssessmentId(), course.getCertificateId());
  }
}
