package mn.erin.lms.legacy.domain.lms.usecase.readership.create_readership;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.mockito.internal.util.collections.Sets;

import mn.erin.domain.base.model.EntityId;
import mn.erin.domain.base.usecase.UseCaseException;
import mn.erin.lms.base.aim.AccessIdentityManagement;
import mn.erin.lms.base.aim.role.LmsRole;
import mn.erin.lms.legacy.domain.lms.model.course.AuthorId;
import mn.erin.lms.legacy.domain.lms.model.course.Course;
import mn.erin.lms.legacy.domain.lms.model.course.CourseCategoryId;
import mn.erin.lms.legacy.domain.lms.model.course.CourseDetail;
import mn.erin.lms.legacy.domain.lms.model.course.CourseId;
import mn.erin.lms.legacy.domain.lms.model.course.PublishStatus;
import mn.erin.lms.legacy.domain.lms.model.enrollment.CourseEnrollment;
import mn.erin.lms.legacy.domain.lms.model.enrollment.CourseEnrollmentId;
import mn.erin.lms.legacy.domain.lms.model.enrollment.CourseEnrollmentState;
import mn.erin.lms.legacy.domain.lms.model.enrollment.LearnerId;
import mn.erin.lms.legacy.domain.lms.repository.CourseEnrollmentRepository;
import mn.erin.lms.legacy.domain.lms.repository.CourseRepository;

/**
 * @author Munkh
 */
public class GetNotEnrolledCoursesTest
{
  private static final String DEFAULT_COURSE_ID = "courseId";
  private static final String DEFAULT_LEARNER_ID = "learnerId";
  private static final String DEFAULT_GROUP_ID = "groupId";

  private static final CourseDetail COURSE_DETAIL = new CourseDetail("title");

  private static final Course DEFAULT_COURSE = new Course(
      new CourseId(DEFAULT_COURSE_ID),
      new CourseCategoryId("category"),
      new AuthorId("author"),
      COURSE_DETAIL
  );

  private static final CourseEnrollment DEFAULT_ENROLLMENT = new CourseEnrollment(
      new CourseEnrollmentId("enrollmentId"),
      new LearnerId(DEFAULT_LEARNER_ID),
      new CourseId(DEFAULT_COURSE_ID),
      CourseEnrollmentState.IN_PROGRESS
  );

  private static final Set<String> GROUPS = new HashSet<>();
  private static final List<Course> COURSES = new ArrayList<>();
  private static final List<CourseEnrollment> ENROLLMENTS = new ArrayList<>();

  private AccessIdentityManagement accessIdentityManagement;
  private CourseRepository courseRepository;
  private CourseEnrollmentRepository courseEnrollmentRepository;

  private GetNotEnrolledCourses getNotEnrolledCourses;

  @Before
  public void setUp()
  {
    accessIdentityManagement = Mockito.mock(AccessIdentityManagement.class);
    courseRepository = Mockito.mock(CourseRepository.class);
    courseEnrollmentRepository = Mockito.mock(CourseEnrollmentRepository.class);

    GROUPS.add(DEFAULT_GROUP_ID);
    COURSE_DETAIL.changePublishStatus(PublishStatus.PUBLISHED);
    COURSES.add(DEFAULT_COURSE);
    ENROLLMENTS.add(DEFAULT_ENROLLMENT);

    Mockito.when(accessIdentityManagement.getRole(DEFAULT_LEARNER_ID)).thenReturn(LmsRole.LMS_USER.name());
    Mockito.when(courseRepository.getCourseList(GROUPS)).thenReturn(COURSES);
    Mockito.when(courseEnrollmentRepository.getEnrollmentList(new LearnerId(DEFAULT_LEARNER_ID))).thenReturn(Collections.emptyList());

    getNotEnrolledCourses = new GetNotEnrolledCourses(accessIdentityManagement, courseRepository, courseEnrollmentRepository);
  }

  @Test
  public void shouldGetNotEnrolledCourses() throws UseCaseException
  {
    Mockito.when(courseRepository.getCourseList(Sets.newSet(DEFAULT_GROUP_ID))).thenReturn(COURSES);
    List<Course> result = getNotEnrolledCourses.execute(new GetNotEnrolledCoursesInput(DEFAULT_LEARNER_ID, DEFAULT_GROUP_ID));
    Set<String> ids = result.stream().map(Course::getCourseId).map(EntityId::getId).collect(Collectors.toSet());
    Assert.assertTrue(ids.contains(DEFAULT_COURSE_ID));
  }

  @Test
  public void shouldNotGetEnrolledCourses() throws UseCaseException
  {
    Mockito.when(courseEnrollmentRepository.getEnrollmentList(new LearnerId(DEFAULT_LEARNER_ID))).thenReturn(ENROLLMENTS);

    List<Course> result = getNotEnrolledCourses.execute(new GetNotEnrolledCoursesInput(DEFAULT_LEARNER_ID, DEFAULT_GROUP_ID));
    Assert.assertTrue(result.isEmpty());
  }

  @Test
  public void shouldGetOnlyPublishedCourses() throws UseCaseException
  {
    COURSE_DETAIL.changePublishStatus(PublishStatus.UNPUBLISHED);

    List<Course> result = getNotEnrolledCourses.execute(new GetNotEnrolledCoursesInput(DEFAULT_LEARNER_ID, DEFAULT_GROUP_ID));
    Assert.assertTrue(result.isEmpty());
  }

  @Test
  public void adminUserShouldGetAllSubDepartmentsCourses() throws UseCaseException
  {
    Mockito.when(accessIdentityManagement.getRole(DEFAULT_LEARNER_ID)).thenReturn(LmsRole.LMS_ADMIN.name());
    GROUPS.add("subGroupId");
    Mockito.when(accessIdentityManagement.getSubDepartments(DEFAULT_GROUP_ID)).thenReturn(GROUPS);
    CourseDetail courseDetail = new CourseDetail("title");
    courseDetail.changePublishStatus(PublishStatus.PUBLISHED);
    COURSES.add(new Course(new CourseId("2ndCourseId"), new CourseCategoryId("category"), new AuthorId("author"), courseDetail));
    Mockito.when(courseRepository.getCourseList(GROUPS)).thenReturn(COURSES);

    List<Course> result = getNotEnrolledCourses.execute(new GetNotEnrolledCoursesInput(DEFAULT_LEARNER_ID, DEFAULT_GROUP_ID));
    Assert.assertEquals(2, result.size());
  }
}