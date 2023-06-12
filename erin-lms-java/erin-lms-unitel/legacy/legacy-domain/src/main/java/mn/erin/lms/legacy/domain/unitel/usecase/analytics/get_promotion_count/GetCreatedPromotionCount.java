package mn.erin.lms.legacy.domain.unitel.usecase.analytics.get_promotion_count;

import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import mn.erin.domain.aim.repository.GroupRepository;
import mn.erin.domain.aim.repository.MembershipRepository;
import mn.erin.domain.base.usecase.UseCase;
import mn.erin.lms.base.aim.AccessIdentityManagement;
import mn.erin.lms.legacy.domain.lms.model.course.AuthorId;
import mn.erin.lms.legacy.domain.lms.model.course.CourseCategoryId;
import mn.erin.lms.legacy.domain.lms.model.course.PublishStatus;
import mn.erin.lms.legacy.domain.lms.repository.CourseCategoryRepository;
import mn.erin.lms.legacy.domain.lms.repository.CourseGroupRepository;
import mn.erin.lms.legacy.domain.lms.service.CourseCounter;
import mn.erin.lms.legacy.domain.lms.usecase.course_group.GetGroupCourseInput;
import mn.erin.lms.legacy.domain.lms.usecase.course_group.GetGroupCourses;
import mn.erin.lms.legacy.domain.unitel.model.PromotionState;
import mn.erin.lms.legacy.domain.unitel.usecase.analytics.AnalyticsFilter;
import mn.erin.lms.legacy.domain.unitel.usecase.category.get_category.GetPromotionCategories;
import mn.erin.lms.legacy.domain.unitel.usecase.category.get_category.GetPromotionCategoryOutput;

/**
 * @author Bat-Erdene Tsogoo.
 */
public class GetCreatedPromotionCount implements UseCase<AnalyticsFilter, Set<PromotionCount>>
{
  private static final String PROPERTY_KEY_START_DATE = "startDate";
  private static final String PROPERTY_KEY_END_DATE = "endDate";
  private static final String PROPERTY_KEY_STATE = "state";

  private final GetPromotionCategories getPromotionCategories;
  private final CourseCounter createdCourseCounter;

  private Set<GetPromotionCategoryOutput> categories;

  private AccessIdentityManagement accessIdentityManagement;

  private CourseGroupRepository courseGroupRepository;

  private GroupRepository groupRepository;

  private MembershipRepository membershipRepository;

  public GetCreatedPromotionCount(CourseCategoryRepository courseCategoryRepository, CourseCounter createdCourseCounter,
      AccessIdentityManagement accessIdentityManagement, CourseGroupRepository courseGroupRepository, GroupRepository groupRepository,
      MembershipRepository membershipRepository)
  {
    this.createdCourseCounter = Objects.requireNonNull(createdCourseCounter, "Course counter cannot be null!");
    this.getPromotionCategories = new GetPromotionCategories(courseCategoryRepository);
    this.accessIdentityManagement = accessIdentityManagement;
    this.courseGroupRepository = courseGroupRepository;
    this.groupRepository = groupRepository;
    this.membershipRepository = membershipRepository;
  }

  @Override
  public Set<PromotionCount> execute(AnalyticsFilter input)
  {
    categories = getPromotionCategories.execute(null);

    Set<PromotionCount> promotionCounts = new HashSet<>();

    List<String> groupIds = new GetGroupCourses(accessIdentityManagement, courseGroupRepository, groupRepository, membershipRepository)
        .execute(new GetGroupCourseInput(null));
    Set<String> ids = new HashSet<>(groupIds);
    promotionCounts.add(getPromotionCount(PromotionState.MAIN, input.getStartDate(), input.getEndDate(), input.getUserId(), ids));
    promotionCounts.add(getPromotionCount(PromotionState.CURRENT, input.getStartDate(), input.getEndDate(), input.getUserId(), ids));

    return promotionCounts;
  }

  private PromotionCount getPromotionCount(PromotionState promotionState, Date startDate, Date endDate, String userId, Set<String> groupIds)
  {
    if (startDate.getTime() > endDate.getTime())
    {
      throw new IllegalArgumentException("Start date cannot be greater than end date");
    }

    Map<String, Object> properties = new HashMap<>();

    properties.put(PROPERTY_KEY_STATE, promotionState.name());
    properties.put(PROPERTY_KEY_START_DATE, startDate);
    properties.put(PROPERTY_KEY_END_DATE, endDate);

    Set<PromotionCountByCategory> promotionCountByCategories = new HashSet<>();
    for (GetPromotionCategoryOutput category : categories)
    {
      CourseCategoryId courseCategoryId = new CourseCategoryId(category.getCategoryId());
      Integer published;
      Integer unpublished;
      Integer pending;
      if (userId != null)
      {
        published = createdCourseCounter.countCreatedCourses(courseCategoryId, PublishStatus.PUBLISHED, new AuthorId(userId), properties, groupIds);
        pending = createdCourseCounter.countCreatedCourses(courseCategoryId, PublishStatus.PENDING, new AuthorId(userId), properties, groupIds);
        unpublished = createdCourseCounter.countCreatedCourses(courseCategoryId, PublishStatus.UNPUBLISHED, new AuthorId(userId), properties, groupIds);
      }
      else
      {
        published = createdCourseCounter.countCreatedCourses(courseCategoryId, PublishStatus.PUBLISHED, properties, groupIds);
        pending = createdCourseCounter.countCreatedCourses(courseCategoryId, PublishStatus.PENDING, properties, groupIds);
        unpublished = createdCourseCounter.countCreatedCourses(courseCategoryId, PublishStatus.UNPUBLISHED, properties, groupIds);
      }

      published += pending;
      promotionCountByCategories.add(new PromotionCountByCategory(category.getCategoryName(), category.getCategoryId(), published, unpublished));
    }

    return new PromotionCount(promotionState.name(), promotionCountByCategories);
  }
}
