package mn.erin.lms.unitel.domain.usecase;

import mn.erin.domain.base.usecase.UseCaseException;
import mn.erin.lms.base.domain.model.category.CourseCategoryId;
import mn.erin.lms.base.domain.model.course.Course;
import mn.erin.lms.base.domain.model.course.CourseDetail;
import mn.erin.lms.base.domain.model.course.CourseId;
import mn.erin.lms.base.domain.model.course.CourseType;
import mn.erin.lms.base.domain.repository.LmsRepositoryException;
import mn.erin.lms.base.domain.repository.LmsRepositoryRegistry;
import mn.erin.lms.base.domain.service.LmsServiceRegistry;
import mn.erin.lms.base.domain.service.UnknownCourseTypeException;
import mn.erin.lms.base.domain.usecase.course.CourseUseCase;
import mn.erin.lms.unitel.domain.usecase.dto.UpdateEnrollmentCountInput;

/**
 * @author Munkh
 */
public class UpdateCourseEnrollmentCount extends CourseUseCase<UpdateEnrollmentCountInput, Void>
{

  public UpdateCourseEnrollmentCount(LmsRepositoryRegistry lmsRepositoryRegistry,
      LmsServiceRegistry lmsServiceRegistry)
  {
    super(lmsRepositoryRegistry, lmsServiceRegistry);
  }

  @Override
  public Void execute(UpdateEnrollmentCountInput input) throws UseCaseException
  {
    try
    {
      Course course = getCourse(CourseId.valueOf(input.getCourseId()));
      CourseType courseType = courseTypeResolver.resolve(course.getCourseType().getType());
      String assessmentId = course.getAssessmentId();
      String certificateId = course.getCertificateId();
      CourseDetail courseDetail = course.getCourseDetail();
      courseDetail.getProperties().put("enrollmentCount", input.getEnrollmentCount());
      courseRepository.update(CourseId.valueOf(input.getCourseId()), CourseCategoryId.valueOf(course.getCourseCategoryId().getId()),
          courseDetail, courseType, assessmentId, certificateId);
    }
    catch (LmsRepositoryException | UnknownCourseTypeException e)
    {
      throw new UseCaseException(e.getMessage(), e);
    }
    return null;
  }
}
