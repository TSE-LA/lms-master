package mn.erin.lms.legacy.domain.lms.usecase.readership.create_readership;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import mn.erin.domain.aim.repository.UserRepository;
import mn.erin.domain.base.usecase.UseCaseException;
import mn.erin.lms.legacy.domain.lms.model.audit.CourseAudit;
import mn.erin.lms.legacy.domain.lms.model.audit.CourseAuditId;
import mn.erin.lms.legacy.domain.lms.model.course.AuthorId;
import mn.erin.lms.legacy.domain.lms.model.course.Course;
import mn.erin.lms.legacy.domain.lms.model.course.CourseCategoryId;
import mn.erin.lms.legacy.domain.lms.model.course.CourseDetail;
import mn.erin.lms.legacy.domain.lms.model.course.CourseId;
import mn.erin.lms.legacy.domain.lms.model.enrollment.LearnerId;
import mn.erin.lms.legacy.domain.lms.repository.CourseAuditRepository;
import mn.erin.lms.legacy.domain.lms.repository.CourseRepository;
import mn.erin.lms.legacy.domain.lms.repository.LMSRepositoryException;

/**
 * @author Munkh
 */
public class CreateReadershipsTest
{
  private static final String DEFAULT_LEARNER_ID = "defaultLearnerId";
  private static final CourseCategoryId DEFAULT_CATEGORY_ID = new CourseCategoryId("category");
  private static final AuthorId DEFAULT_AUTHOR_ID = new AuthorId("author");
  private static final CourseDetail DEFAULT_COURSE_DETAIL = new CourseDetail("title");

  private static final List<String> COURSE_IDS = new ArrayList<>();
  private static final List<Course> COURSES = new ArrayList<>();
  private static final List<String> EXPECTED = new ArrayList<>();

  private CourseRepository courseRepository;
  private CourseAuditRepository courseAuditRepository;
  private UserRepository userRepository;

  private CreateReaderships createReaderships;

  @Before
  public void setUp() throws LMSRepositoryException
  {
    courseRepository = Mockito.mock(CourseRepository.class);
    courseAuditRepository = Mockito.mock(CourseAuditRepository.class);
    userRepository = Mockito.mock(UserRepository.class);

    COURSE_IDS.add("courseId1");
    COURSE_IDS.add("courseId2");
    COURSE_IDS.add("courseId3");

    COURSES.add(new Course(new CourseId("courseId1"), DEFAULT_CATEGORY_ID, DEFAULT_AUTHOR_ID, DEFAULT_COURSE_DETAIL));
    COURSES.add(new Course(new CourseId("courseId2"), DEFAULT_CATEGORY_ID, DEFAULT_AUTHOR_ID, DEFAULT_COURSE_DETAIL));
    COURSES.add(new Course(new CourseId("courseId3"), DEFAULT_CATEGORY_ID, DEFAULT_AUTHOR_ID, DEFAULT_COURSE_DETAIL));

    Mockito.when(courseRepository.getCourse(new CourseId(COURSE_IDS.get(0)))).thenReturn(COURSES.get(0));
    Mockito.when(courseRepository.getCourse(new CourseId(COURSE_IDS.get(1)))).thenReturn(COURSES.get(1));
    Mockito.when(courseRepository.getCourse(new CourseId(COURSE_IDS.get(2)))).thenReturn(COURSES.get(2));

    EXPECTED.add("auditId1");
    EXPECTED.add("auditId2");
    EXPECTED.add("auditId3");

    Mockito.when(courseAuditRepository.create(new CourseId(COURSE_IDS.get(0)), new LearnerId(DEFAULT_LEARNER_ID)))
        .thenReturn(new CourseAudit(CourseAuditId.valueOf(EXPECTED.get(0)), new CourseId(COURSE_IDS.get(0)), new LearnerId(DEFAULT_LEARNER_ID)));

    Mockito.when(courseAuditRepository.create(new CourseId(COURSE_IDS.get(1)), new LearnerId(DEFAULT_LEARNER_ID)))
        .thenReturn(new CourseAudit(CourseAuditId.valueOf(EXPECTED.get(1)), new CourseId(COURSE_IDS.get(1)), new LearnerId(DEFAULT_LEARNER_ID)));

    Mockito.when(courseAuditRepository.create(new CourseId(COURSE_IDS.get(2)), new LearnerId(DEFAULT_LEARNER_ID)))
        .thenReturn(new CourseAudit(CourseAuditId.valueOf(EXPECTED.get(2)), new CourseId(COURSE_IDS.get(2)), new LearnerId(DEFAULT_LEARNER_ID)));


    createReaderships = new CreateReaderships(courseRepository, courseAuditRepository, userRepository);
  }

  @Test
  public void shouldCreateAuditsForEveryCourses() throws UseCaseException
  {
    Set<String> courseIdsAsSet = new HashSet<>(COURSE_IDS);

    List<String> result = createReaderships.execute(new CreateReadershipsInput(courseIdsAsSet, DEFAULT_LEARNER_ID));

    Assert.assertEquals(new HashSet<>(EXPECTED), new HashSet<>(result));
  }

  @Test
  public void whenCourseNotFoundShouldNotCreate() throws UseCaseException, LMSRepositoryException
  {
    Mockito.when(courseRepository.getCourse(new CourseId(COURSE_IDS.get(1)))).thenThrow(LMSRepositoryException.class);

    Set<String> courseIdsAsSet = new HashSet<>(COURSE_IDS);

    List<String> result = createReaderships.execute(new CreateReadershipsInput(courseIdsAsSet, DEFAULT_LEARNER_ID));

    Assert.assertNotEquals(new HashSet<>(EXPECTED), new HashSet<>(result));
    Assert.assertFalse(result.contains(EXPECTED.get(1)));
  }
}