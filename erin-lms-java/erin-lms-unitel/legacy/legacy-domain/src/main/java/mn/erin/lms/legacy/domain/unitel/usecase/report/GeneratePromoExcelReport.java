package mn.erin.lms.legacy.domain.unitel.usecase.report;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.lang3.Validate;

import mn.erin.domain.aim.repository.GroupRepository;
import mn.erin.domain.aim.repository.MembershipRepository;
import mn.erin.domain.base.usecase.UseCase;
import mn.erin.domain.base.usecase.UseCaseException;
import mn.erin.lms.base.aim.AccessIdentityManagement;
import mn.erin.lms.base.aim.role.LmsRole;
import mn.erin.lms.legacy.domain.lms.repository.CourseCategoryRepository;
import mn.erin.lms.legacy.domain.lms.repository.CourseGroupRepository;
import mn.erin.lms.legacy.domain.lms.repository.CourseRepository;
import mn.erin.lms.legacy.domain.lms.usecase.course.get_course.GetCourseOutput;
import mn.erin.lms.legacy.domain.lms.usecase.course.get_course_list.GetCourseList;
import mn.erin.lms.legacy.domain.lms.usecase.course.get_course_list.GetCourseListInput;
import mn.erin.lms.legacy.domain.lms.usecase.course_group.GetGroupCourseInput;
import mn.erin.lms.legacy.domain.lms.usecase.course_group.GetGroupCourses;
import mn.erin.lms.legacy.domain.unitel.service.PromoExcelReportGenerator;
import mn.erin.lms.legacy.domain.unitel.usecase.DateFilter;
import mn.erin.lms.legacy.domain.unitel.usecase.category.get_category.GetPromotionCategories;
import mn.erin.lms.legacy.domain.unitel.usecase.category.get_category.GetPromotionCategoryOutput;

/**
 * @author Bat-Erdene Tsogoo.
 */
public class GeneratePromoExcelReport implements UseCase<DateFilter, byte[]>
{
  private static final String[] PROMO_REPORT_HEADERS = {
      "Урамшууллын тоо", "Нөхцөлийн төрөл", "Нөхцөлийн нэр", "Код", "Эхлэх хугацаа", "Дуусах хугацаа", "Сегмент", "Мэдээлэл хүргэлт хийсэн",
      "Товч агуулга", "Тэмдэглэл"
  };

  private final PromoExcelReportGenerator generator;
  private final AccessIdentityManagement accessIdentityManagement;
  private final CourseRepository courseRepository;
  private final CourseGroupRepository courseGroupRepository;
  private final MembershipRepository membershipRepository;
  private final GroupRepository groupRepository;

  private final GetPromotionCategories getPromotionCategories;
  private final GetCourseList getCourseList;

  private final List<String> courses;

  public GeneratePromoExcelReport(PromoExcelReportGenerator generator, CourseCategoryRepository courseCategoryRepository,
      CourseRepository courseRepository, AccessIdentityManagement accessIdentityManagement, CourseGroupRepository courseGroupRepository,
      GroupRepository groupRepository, MembershipRepository membershipRepository)
  {
    this.generator = Objects.requireNonNull(generator, "Generator cannot be null!");
    this.getPromotionCategories = new GetPromotionCategories(Objects.requireNonNull(courseCategoryRepository, "CourseCategoryRepository cannot be null!"));
    this.getCourseList = new GetCourseList(Objects.requireNonNull(courseRepository, "CourseRepository cannot be null!"));
    this.accessIdentityManagement = accessIdentityManagement;
    this.courseRepository = courseRepository;
    this.courseGroupRepository = courseGroupRepository;
    this.membershipRepository = membershipRepository;
    this.groupRepository = groupRepository;

    this.courses = getCourses();
  }

  @Override
  public byte[] execute(DateFilter dateFilter) throws UseCaseException
  {
    Validate.notNull(dateFilter, "Date filter cannot be null");

    Set<GetPromotionCategoryOutput> categories = getPromotionCategories.execute(null);

    Map<String, List<GetCourseOutput>> categoryPromoRelation = new HashMap<>();

    for (GetPromotionCategoryOutput category : categories)
    {
      GetCourseListInput getCourseListInput = new GetCourseListInput(category.getCategoryId());
      List<GetCourseOutput> courses = getCourseList.execute(getCourseListInput);

      courses = courses.stream().filter(courseOutput -> this.courses.contains(courseOutput.getId())).collect(Collectors.toList());

      categoryPromoRelation.put(category.getCategoryName(), courses);
    }

    return generator.generatePromoExcelReport(categoryPromoRelation, PROMO_REPORT_HEADERS, dateFilter.getStartDate(), dateFilter.getEndDate());
  }

  private List<String> getCourses()
  {
    String currentUsername = accessIdentityManagement.getCurrentUsername();
    LmsRole role = LmsRole.valueOf(accessIdentityManagement.getRole(currentUsername));

    if (role == LmsRole.LMS_ADMIN)
    {
      return new GetGroupCourses(accessIdentityManagement, courseGroupRepository, groupRepository, membershipRepository)
          .execute(new GetGroupCourseInput(null));
    }
    else
    {
      String currentUserGroupId = accessIdentityManagement.getUserDepartmentId(currentUsername);
      Set<String> groups = accessIdentityManagement.getSubDepartments(currentUserGroupId);

      return courseRepository.getCourseList(groups).stream().map(course -> course.getCourseId().getId()).collect(Collectors.toList());
    }
  }
}
