package mn.erin.lms.legacy.domain.lms.usecase.audit;

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
import mn.erin.lms.legacy.domain.lms.usecase.audit.dto.CreateCourseAuditInput;

/**
 * @author Munkh
 */
public class CreateCourseAuditTest
{
  private static final String DEFAULT_LEARNER_ID = "defaultLearnerId";
  private static final String DEFAULT_COURSE_ID = "defaultCourseId";
  private static final String DEFAULT_AUDIT_ID = "defaultAuditId";

  private static final Course DEFAULT_COURSE = new Course(
      new CourseId(DEFAULT_COURSE_ID),
      new CourseCategoryId("category"),
      new AuthorId("author"),
      new CourseDetail("title")
  );

  private static final CourseAudit DEFAULT_COURSE_AUDIT = new CourseAudit(
      CourseAuditId.valueOf(DEFAULT_AUDIT_ID),
      new CourseId(DEFAULT_COURSE_ID),
      new LearnerId(DEFAULT_LEARNER_ID)
  );

  private CourseAuditRepository courseAuditRepository;
  private CourseRepository courseRepository;
  private UserRepository userRepository;

  private CreateCourseAudit createCourseAudit;

  @Before
  public void setUp() throws LMSRepositoryException
  {
    courseAuditRepository = Mockito.mock(CourseAuditRepository.class);
    courseRepository = Mockito.mock(CourseRepository.class);
    userRepository = Mockito.mock(UserRepository.class);

    Mockito.when(courseRepository.getCourse(new CourseId(DEFAULT_COURSE_ID))).thenReturn(DEFAULT_COURSE);
    Mockito.when(courseAuditRepository.create(new CourseId(DEFAULT_COURSE_ID), new LearnerId(DEFAULT_LEARNER_ID))).thenReturn(DEFAULT_COURSE_AUDIT);

    createCourseAudit = new CreateCourseAudit(courseAuditRepository, courseRepository, userRepository);
  }

  @Test
  public void courseAuditShouldCreated() throws UseCaseException
  {
    Assert.assertEquals(createCourseAudit.execute(new CreateCourseAuditInput(DEFAULT_COURSE_ID, DEFAULT_LEARNER_ID)), DEFAULT_AUDIT_ID);
  }

  @Test(expected = UseCaseException.class)
  public void throwsExceptionWhenCourseNotFound() throws LMSRepositoryException, UseCaseException
  {
    Mockito.when(courseRepository.getCourse(new CourseId(DEFAULT_COURSE_ID))).thenThrow(LMSRepositoryException.class);
    createCourseAudit.execute(new CreateCourseAuditInput(DEFAULT_COURSE_ID, DEFAULT_LEARNER_ID));
  }
}