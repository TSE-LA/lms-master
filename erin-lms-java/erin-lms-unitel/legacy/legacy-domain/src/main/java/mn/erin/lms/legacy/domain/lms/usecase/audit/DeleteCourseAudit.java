package mn.erin.lms.legacy.domain.lms.usecase.audit;

import mn.erin.domain.base.usecase.UseCase;
import mn.erin.lms.legacy.domain.lms.model.audit.CourseAuditId;
import mn.erin.lms.legacy.domain.lms.repository.CourseAuditRepository;

/**
 * @author Bat-Erdene Tsogoo.
 */
public class DeleteCourseAudit implements UseCase<String, Boolean>
{
  private final CourseAuditRepository courseAuditRepository;

  public DeleteCourseAudit(CourseAuditRepository courseAuditRepository)
  {
    this.courseAuditRepository = courseAuditRepository;
  }

  @Override
  public Boolean execute(String id)
  {
    return courseAuditRepository.delete(CourseAuditId.valueOf(id));
  }
}
