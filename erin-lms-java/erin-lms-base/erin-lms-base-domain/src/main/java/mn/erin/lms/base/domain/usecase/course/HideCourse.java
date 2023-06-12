package mn.erin.lms.base.domain.usecase.course;

import org.apache.commons.lang3.Validate;

import mn.erin.domain.base.usecase.UseCaseException;
import mn.erin.lms.base.domain.model.Authorized;
import mn.erin.lms.base.domain.model.content.CourseContentId;
import mn.erin.lms.base.domain.model.course.Course;
import mn.erin.lms.base.domain.model.course.CourseId;
import mn.erin.lms.base.domain.model.course.PublishStatus;
import mn.erin.lms.base.aim.user.Author;
import mn.erin.lms.base.domain.repository.LmsRepositoryException;
import mn.erin.lms.base.domain.repository.LmsRepositoryRegistry;
import mn.erin.lms.base.domain.service.CoursePublisher;
import mn.erin.lms.base.domain.service.LmsServiceRegistry;
import mn.erin.lms.base.domain.usecase.certificate.DeleteLearnerCertificate;

/**
 * @author Bat-Erdene Tsogoo.
 */
@Authorized(users = { Author.class })
public class HideCourse extends CourseUseCase<String, Boolean>
{
  public HideCourse(LmsRepositoryRegistry lmsRepositoryRegistry, LmsServiceRegistry lmsServiceRegistry)
  {
    super(lmsRepositoryRegistry, lmsServiceRegistry);

  }

  @Override
  public Boolean execute(String id) throws UseCaseException
  {
    Validate.notBlank(id);
    CourseId courseId = CourseId.valueOf(id);
    Course course = getCourse(courseId);

    if (PublishStatus.PUBLISHED != course.getCourseDetail().getPublishStatus())
    {
      throw new UseCaseException("Cannot hide a course that has not been published");
    }

    try
    {
      courseRepository.update(courseId, PublishStatus.UNPUBLISHED);
    }
    catch (LmsRepositoryException e)
    {
      throw new UseCaseException("Failed to hide the course with the ID: [" + id + "]", e);
    }
    CourseContentId courseContentId = course.getCourseContentId();

    courseEnrollmentRepository.deleteAll(courseId);

    GetSCORMFolderId getSCORMFolderId = new GetSCORMFolderId(lmsRepositoryRegistry, lmsServiceRegistry);
    DeleteSCORMFolder deleteSCORMFolder = new DeleteSCORMFolder(lmsRepositoryRegistry, lmsServiceRegistry);

    if (null != courseContentId )
    {
      CoursePublisher coursePublisher = lmsServiceRegistry.getCoursePublisher();
      coursePublisher.delete(courseContentId.getId());
      String  courseFolderId = getSCORMFolderId.execute(courseId.getId());
      deleteSCORMFolder.execute(courseFolderId);
    }

    if(course.getCertificateId() != null){
      DeleteLearnerCertificate deleteLearnerCertificate = new DeleteLearnerCertificate(lmsRepositoryRegistry, lmsServiceRegistry);
      deleteLearnerCertificate.execute(courseId.getId());
    }

    return true;
  }
}
