package mn.erin.lms.base.domain.usecase.course;

import java.io.File;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import mn.erin.domain.dms.model.document.Document;
import mn.erin.domain.dms.repository.DMSRepositoryException;
import mn.erin.lms.base.aim.LmsDepartmentService;
import mn.erin.lms.base.domain.model.content.Attachment;
import mn.erin.lms.base.domain.model.content.AttachmentId;
import mn.erin.lms.base.domain.repository.*;

import mn.erin.domain.base.model.EntityId;
import mn.erin.domain.base.usecase.UseCase;
import mn.erin.domain.base.usecase.UseCaseException;
import mn.erin.lms.base.domain.model.category.CourseCategoryId;
import mn.erin.lms.base.domain.model.course.Course;
import mn.erin.lms.base.domain.model.course.CourseDepartmentRelation;
import mn.erin.lms.base.domain.model.course.CourseDetail;
import mn.erin.lms.base.domain.model.course.CourseId;
import mn.erin.lms.base.domain.model.course.CourseType;
import mn.erin.lms.base.domain.service.CourseTypeResolver;
import mn.erin.lms.base.domain.service.LmsServiceRegistry;
import mn.erin.lms.base.domain.service.UnknownCourseTypeException;
import mn.erin.lms.base.domain.usecase.course.dto.CourseDto;

/**
 * @author Bat-Erdene Tsogoo.
 */
public abstract class CourseUseCase<I, O> implements UseCase<I, O>
{

  protected final LmsRepositoryRegistry lmsRepositoryRegistry;
  protected final LmsServiceRegistry lmsServiceRegistry;

  protected CourseRepository courseRepository;
  protected CourseEnrollmentRepository courseEnrollmentRepository;
  protected CourseCategoryRepository courseCategoryRepository;
  protected CourseTypeResolver courseTypeResolver;
  protected CourseSuggestedUsersRepository courseSuggestedUsersRepository;
  protected LmsDepartmentService lmsDepartmentService;

  public CourseUseCase(LmsRepositoryRegistry lmsRepositoryRegistry, LmsServiceRegistry lmsServiceRegistry)
  {
    this.lmsRepositoryRegistry = Objects.requireNonNull(lmsRepositoryRegistry);
    this.lmsServiceRegistry = Objects.requireNonNull(lmsServiceRegistry);

    this.courseRepository = lmsRepositoryRegistry.getCourseRepository();
    this.courseEnrollmentRepository = lmsRepositoryRegistry.getCourseEnrollmentRepository();
    this.courseCategoryRepository = lmsRepositoryRegistry.getCourseCategoryRepository();
    this.courseTypeResolver = lmsServiceRegistry.getCourseTypeResolver();
    this.courseSuggestedUsersRepository = lmsRepositoryRegistry.getCourseSuggestedUsersRepository();
    this.lmsDepartmentService = lmsServiceRegistry.getDepartmentService();
  }

  protected boolean exists(CourseId courseId)
  {
    return courseRepository.exists(courseId);
  }

  protected void validateCourse(CourseId courseId) throws UseCaseException
  {
    boolean courseExists = courseRepository.exists(courseId);

    if (!courseExists)
    {
      throw new UseCaseException("The course with the ID: [" + courseId.getId() + "] does not exist!");
    }
  }

  protected Course getCourse(CourseId courseId) throws UseCaseException
  {
    try
    {
      return courseRepository.fetchById(courseId);
    }
    catch (LmsRepositoryException e)
    {
      throw new UseCaseException("The course with the ID: [" + courseId.getId() + "] does not exist!");
    }
  }

  protected Course updateCourse(CourseId courseId, CourseCategoryId courseCategoryId, CourseDetail courseDetail, CourseType courseType, String assessmentId,
      String certificateId)
      throws UseCaseException
  {
    try
    {
      return courseRepository.update(courseId, courseCategoryId, courseDetail, courseType, assessmentId, certificateId);
    }
    catch (LmsRepositoryException e)
    {
      throw new UseCaseException(e.getMessage(), e);
    }
  }

  protected Course updateCourse(CourseId courseId, CourseCategoryId courseCategoryId, CourseDetail courseDetail, CourseType courseType,
      CourseDepartmentRelation courseDepartmentRelation, String assessmentId, String certificateId)
      throws UseCaseException
  {
    try
    {
      courseRepository.update(courseId, courseCategoryId, courseDetail, courseType, assessmentId, certificateId);
      return courseRepository.update(courseId, courseDepartmentRelation);
    }
    catch (LmsRepositoryException e)
    {
      throw new UseCaseException(e.getMessage(), e);
    }
  }

  protected CourseType getCourseType(String type) throws UseCaseException
  {
    try
    {
      return courseTypeResolver.resolve(type);
    }
    catch (UnknownCourseTypeException e)
    {
      throw new UseCaseException("Unknown course type: [" + type + "]");
    }
  }

  protected CourseDto toCourseDto(Course course)
  {
    CourseDetail courseDetail = course.getCourseDetail();
    Set<String> assignedDepartments = course.getCourseDepartmentRelation().getAssignedDepartments()
        .stream().map(EntityId::getId).collect(Collectors.toSet());
    Map<String, String> assignedDepartmentEntity = new HashMap<>();
    if (!assignedDepartments.isEmpty())
    {
      for (String departmentId : assignedDepartments)
      {
        String departmentName = lmsDepartmentService.getDepartmentName(departmentId);
        if (departmentName != null)
        {
          assignedDepartmentEntity.put(departmentId, departmentName);
        }
      }
    }

    Set<String> assignedLearners = course.getCourseDepartmentRelation().getAssignedLearners()
        .stream().map(EntityId::getId).collect(Collectors.toSet());
    String enrollmentCount = courseDetail.getProperties().get("enrollmentCount");
    Set<String> suggestedLearners = courseSuggestedUsersRepository.getSuggestedUsers(course.getCourseId().getId());
    int enrollmentCountInteger = 0;
    if (enrollmentCount != null)
    {
      enrollmentCountInteger = Integer.parseInt(enrollmentCount);
    }

    String categoryName = null;
    try
    {
      categoryName = courseCategoryRepository.getCourseCategoryNameById(course.getCourseCategoryId());
    }
    catch (LmsRepositoryException ignored)
    {
      // Category not found
    }

    return new CourseDto.Builder(course.getCourseId().getId())
        .ofCategory(course.getCourseCategoryId().getId())
        .ofCategoryName(categoryName)
        .ofType(course.getCourseType() != null ? course.getCourseType().getType() : null)
        .withTitle(course.getCourseDetail().getTitle())
        .withAssignedDepartments(assignedDepartments)
        .withAssignedLearners(assignedLearners)
        .withAuthor(course.getAuthorId().getId())
        .withContent(course.getCourseContentId() != null ? course.getCourseContentId().getId() : null)
        .withDescription(courseDetail.getDescription())
        .withPublishStatus(courseDetail.getPublishStatus().name())
        .withHasQuiz(courseDetail.hasQuiz())
        .withHasFeedback(courseDetail.hasFeedbackOption())
        .withHasAssessment(courseDetail.hasAssessment())
        .withSuggestedLearners(suggestedLearners)
        .withHasCertificate(courseDetail.hasCertificate())
        .withAssessmentId(course.getAssessmentId())
        .withCertificateId(course.getCertificateId())
        .withProperties(courseDetail.getProperties())
        .withThumbnailUrl(courseDetail.getThumbnailUrl())
        .createdAt(Date.from(courseDetail.getDateInfo().getCreatedDate().atZone(ZoneId.systemDefault()).toInstant()))
        .modifiedAt(Date.from(courseDetail.getDateInfo().getModifiedDate().atZone(ZoneId.systemDefault()).toInstant()))
        .scheduledAt(courseDetail.getDateInfo().getPublishDate() != null ?
            Date.from(courseDetail.getDateInfo().getPublishDate().atZone(ZoneId.systemDefault()).toInstant()) : null)
        .belongsTo(course.getCourseDepartmentRelation().getCourseDepartment().getId())
        .belongsToName(course.getCourseDetail().getProperties().get("belongingDepartmentName"))
        .withEnrollmentCount(enrollmentCountInteger)
        .withAssignedDepartmentsEntity(assignedDepartmentEntity)
        .withCredit(courseDetail.getCredit())
        .build();
  }

  protected CourseDto toAlternateCourseDto(Course course, int modulesCount, Object interactionsCount)
  {
    CourseDetail courseDetail = course.getCourseDetail();
    Set<String> assignedDepartments = course.getCourseDepartmentRelation().getAssignedDepartments()
        .stream().map(EntityId::getId).collect(Collectors.toSet());
    Set<String> assignedLearners = course.getCourseDepartmentRelation().getAssignedLearners()
        .stream().map(EntityId::getId).collect(Collectors.toSet());

    return new CourseDto.Builder(course.getCourseId().getId())
        .ofCategory(course.getCourseCategoryId().getId())
        .ofCategoryName(course.getCourseCategoryName())
        .ofType(course.getCourseType() != null ? course.getCourseType().getType() : null)
        .withTitle(course.getCourseDetail().getTitle())
        .withAssignedDepartments(assignedDepartments)
        .withAssignedLearners(assignedLearners)
        .withAuthor(course.getAuthorId().getId())
        .withContent(course.getCourseContentId() != null ? course.getCourseContentId().getId() : null)
        .withDescription(courseDetail.getDescription())
        .withPublishStatus(courseDetail.getPublishStatus().name())
        .withHasQuiz(courseDetail.hasQuiz())
        .withHasFeedback(courseDetail.hasFeedbackOption())
        .withHasAssessment(courseDetail.hasAssessment())
        .withHasCertificate(courseDetail.hasCertificate())
        .withAssessmentId(course.getAssessmentId())
        .withCertificateId(course.getCertificateId())
        .withProperties(courseDetail.getProperties())
        .withThumbnailUrl(courseDetail.getThumbnailUrl())
        .withModulesCount(modulesCount)
        .withInteractionsCount(interactionsCount)
        .createdAt(Date.from(courseDetail.getDateInfo().getCreatedDate().atZone(ZoneId.systemDefault()).toInstant()))
        .modifiedAt(Date.from(courseDetail.getDateInfo().getModifiedDate().atZone(ZoneId.systemDefault()).toInstant()))
        .scheduledAt(courseDetail.getDateInfo().getPublishDate() != null ?
            Date.from(courseDetail.getDateInfo().getPublishDate().atZone(ZoneId.systemDefault()).toInstant()) : null)
        .belongsTo(course.getCourseDepartmentRelation().getCourseDepartment().getId())
        .withCredit(courseDetail.getCredit())
        .build();
  }

  protected List<Attachment> uploadAttachment(List<File> files, String attachmentFolderId) throws UseCaseException
  {
    List<Attachment> attachments = new ArrayList<>();
    for (File file : files)
    {
      try
      {
        Document document = lmsServiceRegistry.getLmsFileSystemService().createFileInFolder(attachmentFolderId, file);
        attachments.add(new Attachment(AttachmentId.valueOf(document.getDocumentId().getId()), document.getDocumentName(), attachmentFolderId));
      }
      catch (DMSRepositoryException e)
      {
        throw new UseCaseException("Could not create attachment in folder!");
      }
    }
    return attachments;
  }
}
