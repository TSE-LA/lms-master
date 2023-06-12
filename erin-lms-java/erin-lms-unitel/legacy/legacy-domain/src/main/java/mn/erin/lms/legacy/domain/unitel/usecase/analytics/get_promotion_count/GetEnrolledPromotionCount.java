package mn.erin.lms.legacy.domain.unitel.usecase.analytics.get_promotion_count;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import mn.erin.common.datetime.DateTimeUtils;
import mn.erin.domain.base.usecase.UseCase;
import mn.erin.lms.base.aim.AccessIdentityManagement;
import mn.erin.lms.legacy.domain.lms.model.course.Course;
import mn.erin.lms.legacy.domain.lms.model.course.CourseId;
import mn.erin.lms.legacy.domain.lms.model.course.PublishStatus;
import mn.erin.lms.legacy.domain.lms.model.enrollment.LearnerId;
import mn.erin.lms.legacy.domain.lms.repository.CourseAuditRepository;
import mn.erin.lms.legacy.domain.lms.repository.CourseCategoryRepository;
import mn.erin.lms.legacy.domain.lms.repository.CourseEnrollmentRepository;
import mn.erin.lms.legacy.domain.lms.repository.CourseRepository;
import mn.erin.lms.legacy.domain.lms.repository.LMSRepositoryException;
import mn.erin.lms.legacy.domain.unitel.PromotionConstants;
import mn.erin.lms.legacy.domain.unitel.model.PromotionState;
import mn.erin.lms.legacy.domain.unitel.usecase.analytics.AnalyticsFilter;
import mn.erin.lms.legacy.domain.unitel.usecase.category.get_category.GetPromotionCategories;
import mn.erin.lms.legacy.domain.unitel.usecase.category.get_category.GetPromotionCategoryOutput;

/**
 * @author Bat-Erdene Tsogoo.
 */
public class GetEnrolledPromotionCount implements UseCase<AnalyticsFilter, Set<PromotionCount>>
{
  private static final Logger LOGGER = LoggerFactory.getLogger(GetEnrolledPromotionCount.class);

  private final AccessIdentityManagement accessIdentityManagement;
  private final CourseEnrollmentRepository courseEnrollmentRepository;
  private final CourseRepository courseRepository;
  private final CourseAuditRepository courseAuditRepository;

  private final GetPromotionCategories getPromotionCategories;
  private Set<GetPromotionCategoryOutput> categories;

  public GetEnrolledPromotionCount(AccessIdentityManagement accessIdentityManagement, CourseCategoryRepository courseCategoryRepository,
      CourseEnrollmentRepository courseEnrollmentRepository, CourseRepository courseRepository, CourseAuditRepository courseAuditRepository)
  {
    this.accessIdentityManagement = accessIdentityManagement;
    this.courseEnrollmentRepository = courseEnrollmentRepository;
    this.courseRepository = courseRepository;
    this.courseAuditRepository = courseAuditRepository;

    this.getPromotionCategories = new GetPromotionCategories(courseCategoryRepository);
  }

  @Override
  public Set<PromotionCount> execute(AnalyticsFilter input)
  {
    if (input.getStartDate().getTime() > input.getEndDate().getTime())
    {
      throw new IllegalArgumentException("Start date cannot be greater than end date");
    }

    categories = getPromotionCategories.execute(null);
    String currentUsername = accessIdentityManagement.getCurrentUsername();

    Set<String> relevantCourses = new HashSet<>();
    List<String> enrolledCourses = courseEnrollmentRepository.getEnrollmentList(new LearnerId(currentUsername))
        .stream().map(courseEnrollment -> courseEnrollment.getCourseId().getId()).collect(Collectors.toList());
    List<String> auditCourses = courseAuditRepository.listAll(new LearnerId(currentUsername))
        .stream().map(courseAudit -> courseAudit.getCourseId().getId()).collect(Collectors.toList());

    relevantCourses.addAll(enrolledCourses);
    relevantCourses.addAll(auditCourses);

    List<Course> filteredCourses = new ArrayList<>();
    for (String courseId : relevantCourses)
    {
      try
      {
        Course course = courseRepository.getCourse(new CourseId(courseId));
        Date createdDate = course.getCourseDetail().getCreatedDate();
        if (DateTimeUtils.compare(input.getStartDate(), createdDate) <= 0 && DateTimeUtils.compare(input.getEndDate(), createdDate) >= 0)
        {
          filteredCourses.add(course);
        }
      }
      catch (LMSRepositoryException e)
      {
        LOGGER.error(e.getMessage(), e);
      }
    }

    Set<PromotionCount> promotionCounts = new HashSet<>();
    promotionCounts.add(getPromotionCount(filteredCourses, PromotionState.MAIN));
    promotionCounts.add(getPromotionCount(filteredCourses, PromotionState.CURRENT));

    return promotionCounts;
  }

  private PromotionCount getPromotionCount(List<Course> courses, PromotionState promotionState)
  {
    Set<PromotionCountByCategory> promotionCountByCategories = new HashSet<>();
    for (GetPromotionCategoryOutput category : this.categories)
    {
      Integer published = count(courses, category.getCategoryId(), PublishStatus.PUBLISHED, promotionState);
      Integer unpublished = count(courses, category.getCategoryId(), PublishStatus.UNPUBLISHED, promotionState);
      promotionCountByCategories.add(new PromotionCountByCategory(category.getCategoryName(), category.getCategoryId(), published, unpublished));
    }
    return new PromotionCount(promotionState.name(), promotionCountByCategories);
  }

  private int count(List<Course> courses, String categoryId, PublishStatus publishStatus, PromotionState promotionState)
  {
    return (int) courses.stream()
        .filter(course -> categoryId.equals(course.getCourseCategoryId().getId()) && course.getCourseDetail().getPublishStatus() == publishStatus &&
            promotionState.name().equals(course.getCourseDetail().getProperties().get(PromotionConstants.PROPERTY_STATE)))
        .count();
  }
}
