package mn.erin.lms.legacy.domain.unitel.usecase.notification;

import java.util.Collection;
import java.util.List;
import java.util.Objects;

import mn.erin.domain.base.usecase.UseCase;
import mn.erin.domain.base.usecase.UseCaseException;
import mn.erin.lms.legacy.domain.lms.model.course.CompanyId;
import mn.erin.lms.legacy.domain.lms.model.course.Course;
import mn.erin.lms.legacy.domain.lms.model.course.CourseCategory;
import mn.erin.lms.legacy.domain.lms.model.course.CourseCategoryId;
import mn.erin.lms.legacy.domain.lms.model.enrollment.CourseEnrollment;
import mn.erin.lms.legacy.domain.lms.model.enrollment.CourseEnrollmentState;
import mn.erin.lms.legacy.domain.lms.model.enrollment.LearnerId;
import mn.erin.lms.legacy.domain.lms.repository.CourseCategoryRepository;
import mn.erin.lms.legacy.domain.lms.repository.CourseEnrollmentRepository;
import mn.erin.lms.legacy.domain.lms.repository.CourseRepository;
import mn.erin.lms.legacy.domain.lms.service.EnrollmentStateService;
import mn.erin.lms.legacy.domain.unitel.CategoryConstants;
import mn.erin.lms.legacy.domain.unitel.PromotionConstants;

/**
 * @author Oyungerel Chuluunsukh.
 */
public class GetPromotionNotification implements UseCase<GetPromotionNotificationInput, GetPromotionNotificationOutput>
{
  private CourseCategoryRepository courseCategoryRepository;
  private CourseRepository courseRepository;
  private EnrollmentStateService enrollmentStateService;
  private CourseEnrollmentRepository courseEnrollmentRepository;

  public GetPromotionNotification(
      CourseCategoryRepository courseCategoryRepository,
      CourseRepository courseRepository,
      EnrollmentStateService enrollmentStateService,
      CourseEnrollmentRepository courseEnrollmentRepository)
  {
    this.courseCategoryRepository = Objects.requireNonNull(courseCategoryRepository, "Course category repository cannot be null!");
    this.enrollmentStateService = Objects.requireNonNull(enrollmentStateService, "Enrollment state service cannot be null!");
    this.courseEnrollmentRepository = Objects.requireNonNull(courseEnrollmentRepository, "Course enrollment repository cannot be null!");
    this.courseRepository = Objects.requireNonNull(courseRepository, "Course repository cannot be null!");
  }

  @Override
  public GetPromotionNotificationOutput execute(GetPromotionNotificationInput input) throws UseCaseException
  {

    if (input == null)
    {
      throw new UseCaseException("Get promotion notification input cannot be null!");
    }
    LearnerId learnerId = new LearnerId(input.getLearnerId());
    CompanyId companyId = new CompanyId(CategoryConstants.COMPANY_ID);
    CourseCategoryId courseCategoryId = new CourseCategoryId(CategoryConstants.CATEGORY_PROMOTION);
    Collection<CourseCategory> courseCategories = courseCategoryRepository.listAll(companyId, courseCategoryId);
    GetPromotionNotificationOutput output = new GetPromotionNotificationOutput();
    if (courseCategories != null && !courseCategories.isEmpty())
    {
      output.setCategoryId(courseCategoryId.getId());
      for (CourseCategory courseCategory : courseCategories)
      {
        addToTotalNotification(learnerId, output, courseCategory);
      }
    }
    return output;
  }

  private void addToTotalNotification(LearnerId learnerId, GetPromotionNotificationOutput output, CourseCategory courseCategory)
  {
    GetPromotionNotificationOutput subCategory = new GetPromotionNotificationOutput();
    subCategory.setCategoryId(courseCategory.getCategoryId().getId());
    List<Course> courseList = courseRepository.getCourseList(courseCategory.getCategoryId());
    if (!courseList.isEmpty())
    {
      for (Course course : courseList)
      {
        if (!course.getCourseDetail().getProperties().get(PromotionConstants.PROPERTY_STATE).equals("EXPIRED"))
        {
          CourseEnrollment courseEnrollment = courseEnrollmentRepository.getEnrollment(learnerId, course.getCourseId());
          if (courseEnrollment != null)
          {
            subCategory.addTotal(1);
            if (CourseEnrollmentState.NEW == enrollmentStateService.getCourseEnrollmentState(course.getCourseId()))
            {
              subCategory.addNewTotal(1);
            }
          }
        }
      }
    }
    output.addToSubCategory(subCategory);
  }
}
