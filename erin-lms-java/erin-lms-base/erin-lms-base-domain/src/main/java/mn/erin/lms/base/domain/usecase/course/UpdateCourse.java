package mn.erin.lms.base.domain.usecase.course;

import java.time.LocalDateTime;

import org.apache.commons.lang3.Validate;

import mn.erin.domain.base.usecase.UseCaseException;
import mn.erin.lms.base.domain.model.Authorized;
import mn.erin.lms.base.domain.model.category.CourseCategoryId;
import mn.erin.lms.base.domain.model.course.Course;
import mn.erin.lms.base.domain.model.course.CourseDetail;
import mn.erin.lms.base.domain.model.course.CourseId;
import mn.erin.lms.base.domain.model.course.DateInfo;
import mn.erin.lms.base.aim.user.Author;
import mn.erin.lms.base.domain.repository.LmsRepositoryRegistry;
import mn.erin.lms.base.domain.service.LmsServiceRegistry;
import mn.erin.lms.base.domain.usecase.assessment.UpdateAssessmentStatus;
import mn.erin.lms.base.domain.usecase.assessment.dto.UpdateAssessmentStatusInput;
import mn.erin.lms.base.domain.usecase.course.dto.CourseDto;
import mn.erin.lms.base.domain.usecase.course.dto.UpdateCourseInput;

/**
 * @author Bat-Erdene Tsogoo.
 */
@Authorized(users = { Author.class })
public class UpdateCourse extends CourseUseCase<UpdateCourseInput, CourseDto>
{
  private final UpdateAssessmentStatus updateAssessmentStatus;

  public UpdateCourse(LmsRepositoryRegistry lmsRepositoryRegistry, LmsServiceRegistry lmsServiceRegistry)
  {
    super(lmsRepositoryRegistry, lmsServiceRegistry);
    updateAssessmentStatus = new UpdateAssessmentStatus(lmsRepositoryRegistry, lmsServiceRegistry);
  }

  @Override
  public CourseDto execute(UpdateCourseInput input) throws UseCaseException
  {
    Validate.notNull(input);

    CourseId courseId = CourseId.valueOf(input.getCourseId());
    Course oldCourse = getCourse(courseId);
    CourseDetail courseDetail = getCourseDetail(input, oldCourse.getCourseDetail());

    if(oldCourse.getCourseDetail().hasAssessment())
    {
      UpdateAssessmentStatusInput updateAssessmentStatusInput = new UpdateAssessmentStatusInput(oldCourse.getAssessmentId(), false);
      updateAssessmentStatusInput.setCurrentCourseId(courseId.getId());
      updateAssessmentStatus.execute(updateAssessmentStatusInput);
    }

    Course updatedCourse = updateCourse(courseId, CourseCategoryId.valueOf(input.getCategoryId()),
        courseDetail, getCourseType(input.getType()), input.getAssessmentId(), input.getCertificateId());

    if(updatedCourse.getCourseDetail().hasAssessment())
    {
      updateAssessmentStatus.execute(new UpdateAssessmentStatusInput(updatedCourse.getAssessmentId(), true));
    }

    return toCourseDto(updatedCourse);
  }

  private CourseDetail getCourseDetail(UpdateCourseInput input, CourseDetail oldCourseDetail)
  {
    CourseDetail courseDetail = new CourseDetail(input.getTitle());
    courseDetail.setDescription(input.getDescription());
    courseDetail.setHasFeedbackOption(input.getHasFeedback());
    courseDetail.setHasQuiz(input.getHasQuiz());
    courseDetail.setHasAssessment(input.isHasAssessment());
    courseDetail.setHasCertificate(input.isHasCertificate());
    courseDetail.changePublishStatus(oldCourseDetail.getPublishStatus());
    courseDetail.addProperties(oldCourseDetail.getProperties());
    courseDetail.addProperties(input.getProperties());
    courseDetail.setThumbnailUrl(oldCourseDetail.getThumbnailUrl());
    courseDetail.setCredit(input.getCredit());

    DateInfo dateInfo = oldCourseDetail.getDateInfo();
    dateInfo.setModifiedDate(LocalDateTime.now());

    courseDetail.setDateInfo(dateInfo);
    return courseDetail;
  }
}
