package mn.erin.lms.legacy.domain.lms.usecase.audit;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.mockito.internal.util.collections.Sets;

import mn.erin.domain.base.usecase.UseCaseException;
import mn.erin.lms.legacy.domain.lms.model.audit.CourseAudit;
import mn.erin.lms.legacy.domain.lms.model.audit.CourseAuditId;
import mn.erin.lms.legacy.domain.lms.model.course.AuthorId;
import mn.erin.lms.legacy.domain.lms.model.course.Course;
import mn.erin.lms.legacy.domain.lms.model.course.CourseCategoryId;
import mn.erin.lms.legacy.domain.lms.model.course.CourseDetail;
import mn.erin.lms.legacy.domain.lms.model.course.CourseId;
import mn.erin.lms.legacy.domain.lms.model.enrollment.CourseEnrollment;
import mn.erin.lms.legacy.domain.lms.model.enrollment.CourseEnrollmentId;
import mn.erin.lms.legacy.domain.lms.model.enrollment.CourseEnrollmentState;
import mn.erin.lms.legacy.domain.lms.model.enrollment.LearnerId;
import mn.erin.lms.legacy.domain.lms.repository.CourseAuditRepository;
import mn.erin.lms.legacy.domain.lms.repository.CourseEnrollmentRepository;
import mn.erin.lms.legacy.domain.lms.repository.CourseRepository;
import mn.erin.lms.legacy.domain.lms.usecase.audit.dto.EnrollmentToReaderShipInput;

import static mn.erin.lms.legacy.domain.unitel.PromotionConstants.PROPERTY_END_DATE;

/**
 * @author Galsan Bayart
 */
public class ChangeEnrollmentToReadershipTest
{
  private static final String COURSE_ID_TEST = "testCourseId";
  private static final String CATEGORY_ID_TEST = "c1";

  private CourseEnrollmentRepository courseEnrollmentRepository;
  private CourseAuditRepository courseAuditRepository;
  private CourseRepository courseRepository;

  private ChangeEnrollmentToReadership enrollmentToReadership;

  Set<String> users = Sets.newSet("name1", "name2");

  List<CourseEnrollment> enrollments;
  List<Course> courses;

  EnrollmentToReaderShipInput input;

  Set<LearnerId> dummyLearnerIds;
  Set<CourseId> dummyCourseIds;

  @Before
  public void setUp()
  {
    this.courseEnrollmentRepository = Mockito.mock(CourseEnrollmentRepository.class);
    this.courseAuditRepository = Mockito.mock(CourseAuditRepository.class);
    this.courseRepository = Mockito.mock(CourseRepository.class);

    enrollmentToReadership = new ChangeEnrollmentToReadership(courseEnrollmentRepository, courseAuditRepository, courseRepository);

    enrollments = getEnrollments();
    courses = getCourseList();

    dummyLearnerIds = users.stream().map(LearnerId::new).collect(Collectors.toSet());
    dummyCourseIds = courses.stream().map(Course::getCourseId).collect(Collectors.toSet());

    CourseAudit dummyAudit = new CourseAudit(CourseAuditId.valueOf("caid"), enrollments.get(0).getCourseId(), enrollments.get(0).getLearnerId());

    enrollments
        .forEach(enrollment -> Mockito.when(courseEnrollmentRepository.deleteEnrollment(enrollment.getCourseId(), enrollment.getLearnerId())).thenReturn(true));
    enrollments.forEach(enrollment -> Mockito.when(courseAuditRepository.create(enrollment.getCourseId(), enrollment.getLearnerId())).thenReturn(dummyAudit));
  }

  @Test(expected = NullPointerException.class)
  public void whenEndDateNull() throws UseCaseException
  {
    input = new EnrollmentToReaderShipInput(localDateToDate(LocalDate.of(2021, 3, 31)), null, "LMS", "all");

    enrollmentToReadership.execute(input);
  }

  @Test(expected = NullPointerException.class)
  public void whenStartDateNull() throws UseCaseException
  {
    input = new EnrollmentToReaderShipInput(null, localDateToDate(LocalDate.of(2021, 3, 31)), "LMS", "all");

    enrollmentToReadership.execute(input);
  }

  @Test(expected = NullPointerException.class)
  public void whenGroupIdNull() throws UseCaseException
  {
    input = new EnrollmentToReaderShipInput(localDateToDate(LocalDate.of(2021, 3, 31)), localDateToDate(LocalDate.now()), null, "all");
    enrollmentToReadership.execute(input);
  }

  @Test
  public void whenDatesSwitched() throws UseCaseException
  {
    input = new EnrollmentToReaderShipInput(localDateToDate(LocalDate.of(2021, 3, 31)), localDateToDate(LocalDate.of(2021, 3, 30)), "LMS", "all");

    Mockito.when(courseRepository.getCourseList(input.getEndDate(), endDateTimeToMax(input.getStartDate()))).thenReturn(courses);

    Mockito.when(courseEnrollmentRepository.getEnrollments(dummyCourseIds)).thenReturn(enrollments);

    Assert.assertTrue(enrollmentToReadership.execute(input));
  }

  @Test
  public void whenCoursesAreEmpty() throws UseCaseException
  {
    input = new EnrollmentToReaderShipInput(localDateToDate(LocalDate.of(2021, 3, 31)), localDateToDate(LocalDate.now()), "LMS", "all");

    Mockito.when(courseRepository.getCourseList(input.getStartDate(), endDateTimeToMax(input.getEndDate()))).thenReturn(Collections.emptyList());

    Assert.assertFalse(enrollmentToReadership.execute(input));
  }

  @Test
  public void whenEnrollmentsAreEmpty() throws UseCaseException
  {
    input = new EnrollmentToReaderShipInput(localDateToDate(LocalDate.of(2021, 3, 31)), localDateToDate(LocalDate.now()), "LMS", "all");

    Mockito.when(courseRepository.getCourseList(input.getStartDate(), endDateTimeToMax(input.getEndDate()))).thenReturn(courses);

    Mockito.when(courseEnrollmentRepository.getEnrollments(dummyCourseIds)).thenReturn(Collections.emptyList());

    Assert.assertFalse(enrollmentToReadership.execute(input));
  }

  @Test
  public void whenDatesAreEqual() throws UseCaseException
  {
    input = new EnrollmentToReaderShipInput(localDateToDate(LocalDate.of(2021, 3, 31)), localDateToDate(LocalDate.of(2021, 3, 31)), "LMS", "all");

    Mockito.when(courseRepository.getCourseList(input.getStartDate(), endDateTimeToMax(input.getEndDate()))).thenReturn(courses);

    Mockito.when(courseEnrollmentRepository.getEnrollments(dummyCourseIds)).thenReturn(enrollments);

    Assert.assertTrue(enrollmentToReadership.execute(input));
  }

  @Test
  public void whenEnrollmentToReadershipSuccess() throws UseCaseException
  {
    input = new EnrollmentToReaderShipInput(localDateToDate(LocalDate.of(2021, 3, 31)), localDateToDate(LocalDate.now()), "LMS", "all");

    Mockito.when(courseRepository.getCourseList(input.getStartDate(), endDateTimeToMax(input.getEndDate()))).thenReturn(courses);

    Mockito.when(courseEnrollmentRepository.getEnrollments(dummyCourseIds)).thenReturn(enrollments);

    Assert.assertTrue(enrollmentToReadership.execute(input));
  }

  private List<Course> getCourseList()
  {
    Course course = new Course(new CourseId(COURSE_ID_TEST), new CourseCategoryId(CATEGORY_ID_TEST), new AuthorId("author"), getCourseDetail());

    return Collections.singletonList(course);
  }

  private CourseDetail getCourseDetail()
  {
    CourseDetail courseDetail = new CourseDetail("test");
    courseDetail.addProperty(PROPERTY_END_DATE, "2021-3-30");

    return courseDetail;
  }

  private List<CourseEnrollment> getEnrollments()
  {
    return Collections.singletonList(new CourseEnrollment(new CourseEnrollmentId("lid"), new LearnerId("lid"), new CourseId("cid"), CourseEnrollmentState.NEW));
  }

  private java.util.Date localDateToDate(LocalDate date)
  {
    return Date.from(date.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant());
  }

  private Date endDateTimeToMax(Date date)
  {
    return timeToMax(convertToLocalDate(date));
  }

  private java.util.Date timeToMax(LocalDate date)
  {
    return Date.from(date.atTime(LocalTime.MAX).atZone(ZoneId.systemDefault()).toInstant());
  }

  public LocalDate convertToLocalDate(java.util.Date dateToConvert)
  {
    return dateToConvert.toInstant()
        .atZone(ZoneId.systemDefault())
        .toLocalDate();
  }
}
