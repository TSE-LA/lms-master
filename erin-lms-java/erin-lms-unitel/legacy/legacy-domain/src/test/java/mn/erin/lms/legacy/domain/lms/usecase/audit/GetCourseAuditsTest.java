package mn.erin.lms.legacy.domain.lms.usecase.audit;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import mn.erin.lms.legacy.domain.lms.model.audit.CourseAudit;
import mn.erin.lms.legacy.domain.lms.model.audit.CourseAuditId;
import mn.erin.lms.legacy.domain.lms.model.course.CourseId;
import mn.erin.lms.legacy.domain.lms.model.enrollment.LearnerId;
import mn.erin.lms.legacy.domain.lms.repository.CourseAuditRepository;

/**
 * @author Munkh
 */
public class GetCourseAuditsTest
{
  private static final String DEFAULT_LEARNER_ID = "learnerId";

  private static final List<CourseAudit> AUDITS = new ArrayList<>();

  private CourseAuditRepository courseAuditRepository;

  private GetCourseAudits getCourseAudits;

  @Before
  public void setUp()
  {
    courseAuditRepository = Mockito.mock(CourseAuditRepository.class);

    AUDITS.add(new CourseAudit(CourseAuditId.valueOf("courseAuditId1"), new CourseId("courseId1"), new LearnerId(DEFAULT_LEARNER_ID)));
    AUDITS.add(new CourseAudit(CourseAuditId.valueOf("courseAuditId2"), new CourseId("courseId2"), new LearnerId(DEFAULT_LEARNER_ID)));
    AUDITS.add(new CourseAudit(CourseAuditId.valueOf("courseAuditId3"), new CourseId("courseId3"), new LearnerId(DEFAULT_LEARNER_ID)));

    Mockito.when(courseAuditRepository.listAll(new LearnerId(DEFAULT_LEARNER_ID))).thenReturn(AUDITS);

    getCourseAudits = new GetCourseAudits(courseAuditRepository);
  }

  @Test
  public void shouldReturnEveryAudits()
  {

  }
}