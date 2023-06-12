package mn.erin.lms.legacy.domain.lms.usecase.readership.create_readership;

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import mn.erin.domain.base.usecase.UseCaseException;
import mn.erin.lms.legacy.domain.lms.model.audit.CourseAudit;
import mn.erin.lms.legacy.domain.lms.model.audit.CourseAuditId;
import mn.erin.lms.legacy.domain.lms.model.course.CourseId;
import mn.erin.lms.legacy.domain.lms.model.enrollment.LearnerId;
import mn.erin.lms.legacy.domain.lms.repository.CourseAuditRepository;

/**
 * @author Munkh
 */
public class DeleteReadershipsTest
{
  private static final String DEFAULT_LEARNER_ID = "learnerId";

  private static final List<CourseAudit> AUDITS = new ArrayList<>();

  private CourseAuditRepository courseAuditRepository;

  private DeleteReaderships deleteReaderships;

  @Before
  public void setUp()
  {
    courseAuditRepository = Mockito.mock(CourseAuditRepository.class);

    AUDITS.add(new CourseAudit(CourseAuditId.valueOf("auditId1"), new CourseId("courseId1"), new LearnerId(DEFAULT_LEARNER_ID)));
    AUDITS.add(new CourseAudit(CourseAuditId.valueOf("auditId2"), new CourseId("courseId2"), new LearnerId(DEFAULT_LEARNER_ID)));
    AUDITS.add(new CourseAudit(CourseAuditId.valueOf("auditId3"), new CourseId("courseId3"), new LearnerId(DEFAULT_LEARNER_ID)));

    Mockito.when(courseAuditRepository.listAll(new LearnerId(DEFAULT_LEARNER_ID))).thenReturn(AUDITS);

    deleteReaderships = new DeleteReaderships(courseAuditRepository);
  }

  @Test
  public void shouldReturnTrueIfAllReadershipsDeleted() throws UseCaseException
  {
    Mockito.when(courseAuditRepository.delete(CourseAuditId.valueOf("auditId1"))).thenReturn(true);
    Mockito.when(courseAuditRepository.delete(CourseAuditId.valueOf("auditId2"))).thenReturn(true);
    Mockito.when(courseAuditRepository.delete(CourseAuditId.valueOf("auditId3"))).thenReturn(true);

    Assert.assertTrue(deleteReaderships.execute(DEFAULT_LEARNER_ID));
  }

  @Test
  public void shouldReturnFalseIfAllReadershipsNotDeleted() throws UseCaseException
  {
    Mockito.when(courseAuditRepository.delete(CourseAuditId.valueOf("auditId1"))).thenReturn(true);
    Mockito.when(courseAuditRepository.delete(CourseAuditId.valueOf("auditId2"))).thenReturn(false);
    Mockito.when(courseAuditRepository.delete(CourseAuditId.valueOf("auditId3"))).thenReturn(true);

    Assert.assertFalse(deleteReaderships.execute(DEFAULT_LEARNER_ID));
  }
}