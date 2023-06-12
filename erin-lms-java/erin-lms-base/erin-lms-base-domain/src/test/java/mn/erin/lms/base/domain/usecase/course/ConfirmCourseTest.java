package mn.erin.lms.base.domain.usecase.course;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import mn.erin.domain.aim.exception.AimRepositoryException;
import mn.erin.domain.aim.model.group.GroupId;
import mn.erin.domain.aim.model.membership.Membership;
import mn.erin.domain.aim.model.membership.MembershipId;
import mn.erin.domain.aim.model.role.RoleId;
import mn.erin.domain.aim.repository.AimRepositoryRegistry;
import mn.erin.domain.aim.repository.GroupRepository;
import mn.erin.domain.aim.repository.MembershipRepository;
import mn.erin.domain.base.usecase.UseCaseException;
import mn.erin.lms.base.aim.AccessIdentityManagement;
import mn.erin.lms.base.aim.LmsDepartmentService;
import mn.erin.lms.base.aim.organization.DepartmentId;
import mn.erin.lms.base.aim.user.AuthorId;
import mn.erin.lms.base.aim.user.LearnerId;
import mn.erin.lms.base.domain.model.category.CourseCategoryId;
import mn.erin.lms.base.domain.model.course.Course;
import mn.erin.lms.base.domain.model.course.CourseDepartmentRelation;
import mn.erin.lms.base.domain.model.course.CourseDetail;
import mn.erin.lms.base.domain.model.course.CourseId;
import mn.erin.lms.base.domain.model.course.DateInfo;
import mn.erin.lms.base.domain.model.online_course.EmployeeType;
import mn.erin.lms.base.domain.model.online_course.ManagerType;
import mn.erin.lms.base.domain.repository.ClassroomCourseAttendanceRepository;
import mn.erin.lms.base.domain.repository.CourseCategoryRepository;
import mn.erin.lms.base.domain.repository.CourseEnrollmentRepository;
import mn.erin.lms.base.domain.repository.CourseRepository;
import mn.erin.lms.base.domain.repository.CourseSuggestedUsersRepository;
import mn.erin.lms.base.domain.repository.LmsRepositoryException;
import mn.erin.lms.base.domain.repository.LmsRepositoryRegistry;
import mn.erin.lms.base.domain.service.LmsServiceRegistry;
import mn.erin.lms.base.domain.service.NotificationService;

/**
 * @author Galsan Bayart.
 */
public class ConfirmCourseTest
{
  private ConfirmCourse confirmCourse;

  private Course course;
  private final CourseDetail courseDetail = new CourseDetail("Title");

  private CourseEnrollmentRepository courseEnrollmentRepository;
  private CourseSuggestedUsersRepository courseSuggestedUsersRepository;
  private MembershipRepository membershipRepository;
  private CourseRepository courseRepository;

  Set<String> suggestedDepartments = new HashSet<>();

  @Before
  public void setUp() throws Exception
  {
    LmsRepositoryRegistry lmsRepositoryRegistry = Mockito.mock(LmsRepositoryRegistry.class);
    LmsServiceRegistry lmsServiceRegistry = Mockito.mock(LmsServiceRegistry.class);
    courseSuggestedUsersRepository = Mockito.mock(CourseSuggestedUsersRepository.class);
    courseEnrollmentRepository = Mockito.mock(CourseEnrollmentRepository.class);
    ClassroomCourseAttendanceRepository classroomCourseAttendanceRepository = Mockito.mock(ClassroomCourseAttendanceRepository.class);
    NotificationService notificationService = Mockito.mock(NotificationService.class);
    LmsDepartmentService lmsDepartmentService = Mockito.mock(LmsDepartmentService.class);
    membershipRepository = Mockito.mock(MembershipRepository.class);
    courseRepository = Mockito.mock(CourseRepository.class);
    CourseCategoryRepository courseCategoryRepository = Mockito.mock(CourseCategoryRepository.class);
    AimRepositoryRegistry aimRepositoryRegistry = Mockito.mock(AimRepositoryRegistry.class);
    GroupRepository groupRepository = Mockito.mock(GroupRepository.class);
    AccessIdentityManagement accessIdentityManagement = Mockito.mock(AccessIdentityManagement.class);


    Mockito.when(lmsServiceRegistry.getNotificationService()).thenReturn(notificationService);
    Mockito.when(lmsServiceRegistry.getDepartmentService()).thenReturn(lmsDepartmentService);
    Mockito.when(lmsRepositoryRegistry.getCourseSuggestedUsersRepository()).thenReturn(courseSuggestedUsersRepository);
    Mockito.when(lmsRepositoryRegistry.getCourseEnrollmentRepository()).thenReturn(courseEnrollmentRepository);
    Mockito.when(lmsRepositoryRegistry.getCourseRepository()).thenReturn(courseRepository);
    Mockito.when(lmsRepositoryRegistry.getCourseCategoryRepository()).thenReturn(courseCategoryRepository);
    Mockito.when(aimRepositoryRegistry.getGroupRepository()).thenReturn(groupRepository);
    Mockito.when(lmsServiceRegistry.getAccessIdentityManagement()).thenReturn(accessIdentityManagement);

    confirmCourse = new ConfirmCourse(lmsRepositoryRegistry, lmsServiceRegistry, membershipRepository, classroomCourseAttendanceRepository,
        aimRepositoryRegistry);

    DateInfo dateInfo = new DateInfo();
    dateInfo.setPublishDate(LocalDateTime.now());
    dateInfo.setModifiedDate(LocalDateTime.now());
    dateInfo.setCreatedDate(LocalDateTime.now());
    courseDetail.setDateInfo(dateInfo);
    courseDetail.getProperties().put("enrollmentCount", Integer.toString(0));
    courseDetail.getProperties().put("state", "READY");

    suggestedDepartments.add("SuggestedDepartmentId");

    CourseDepartmentRelation courseDepartmentRelation = new CourseDepartmentRelation(DepartmentId.valueOf("DepartmentId"));

    Set<DepartmentId> suggestedDepartmentIds = new HashSet<>();
    suggestedDepartmentIds.add(DepartmentId.valueOf("SuggestedDepartmentId"));
    courseDepartmentRelation.setAssignedDepartments(suggestedDepartmentIds);
    courseDepartmentRelation.setAssignedLearners(Collections.emptySet());

    course = new Course(CourseId.valueOf("CourseId"), CourseCategoryId.valueOf("CourseCategoryId"), courseDetail, AuthorId.valueOf("AuthorId"),
        courseDepartmentRelation);

    Map<String, Set<String>> suggested = new HashMap<>();

    suggested.put("users", Collections.emptySet());
    suggested.put("groups", suggestedDepartments);

    Mockito.when(courseSuggestedUsersRepository.fetchAll("CourseId")).thenReturn(suggested);

    course.setCourseType(new EmployeeType());
    Mockito.when(courseRepository.fetchById(CourseId.valueOf("CourseId"))).thenReturn(course);

    Mockito.when(lmsDepartmentService.getLearnersByRole(Mockito.any(), Mockito.any())).thenReturn(Collections.emptySet());
    Mockito.when(courseRepository.update(Mockito.any(), Mockito.any(), Mockito.any())).thenReturn(course);

    Mockito.when(courseCategoryRepository.getCourseCategoryNameById(Mockito.any())).thenReturn("CategoryName");
    Mockito.when(groupRepository.findById(Mockito.any())).thenReturn(null);
  }

  @Test
  public void whenSuccess() throws UseCaseException
  {
    Assert.assertTrue(CourseTestUtils.isEqual(CourseTestUtils.generateCourseDto(course, courseDetail), confirmCourse.execute("CourseId")));
  }

  @Test
  public void whenCourseTypeIsManager_success() throws UseCaseException
  {
    course.setCourseType(new ManagerType());
    courseDetail.getProperties().put("enrollmentCount", Integer.toString(5));

    Mockito.when(courseEnrollmentRepository.getEnrollmentCount((CourseId) Mockito.any())).thenReturn(5);
    Assert.assertTrue(CourseTestUtils.isEqual(CourseTestUtils.generateCourseDto(course, courseDetail), confirmCourse.execute("CourseId")));
  }

  @Test
  public void whenUsersNotEmpty() throws UseCaseException, AimRepositoryException
  {
    Map<String, Set<String>> suggested = new HashMap<>();

    Set<String> users = new HashSet<>();
    users.add("User1");
    users.add("User2");

    suggested.put("users", users);
    suggested.put("groups", suggestedDepartments);

    courseDetail.getProperties().put("enrollmentCount", Integer.toString(2));
    Mockito.when(courseSuggestedUsersRepository.fetchAll("CourseId")).thenReturn(suggested);
    Mockito.when(membershipRepository.findByUsername(Mockito.anyString()))
        .thenReturn(new Membership(MembershipId.valueOf("MembershipId"), "Username", GroupId.valueOf("GroupId"), RoleId
            .valueOf("roleId")));

    Set<LearnerId> learnerIds = new HashSet<>();
    learnerIds.add(LearnerId.valueOf("User1"));
    learnerIds.add(LearnerId.valueOf("User2"));
    course.getCourseDepartmentRelation().setAssignedLearners(learnerIds);

    Assert.assertTrue(CourseTestUtils.isEqual(CourseTestUtils.generateCourseDto(course, courseDetail), confirmCourse.execute("CourseId")));
  }

  @Test(expected = UseCaseException.class)
  public void whenGettingCourseFails_throwsUseCaseException() throws UseCaseException, LmsRepositoryException
  {
    Mockito.when(courseRepository.fetchById(Mockito.any())).thenThrow(LmsRepositoryException.class);
    confirmCourse.execute("CourseId");
  }

  @Test(expected = UseCaseException.class)
  public void whenUpdatingCourseFails_throwsUseCaseException() throws UseCaseException, LmsRepositoryException
  {
    Mockito.when(courseRepository.update(Mockito.any(), Mockito.any(), Mockito.any())).thenThrow(LmsRepositoryException.class);
    confirmCourse.execute("CourseId");
  }

}