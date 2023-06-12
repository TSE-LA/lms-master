package mn.erin.lms.legacy.domain.lms.usecase.readership.create_readership;

import java.util.List;

import org.apache.commons.lang3.Validate;

import mn.erin.domain.base.usecase.UseCase;
import mn.erin.domain.base.usecase.UseCaseException;
import mn.erin.lms.legacy.domain.lms.repository.CourseAuditRepository;
import mn.erin.lms.legacy.domain.lms.usecase.audit.DeleteCourseAudit;
import mn.erin.lms.legacy.domain.lms.usecase.audit.GetCourseAudits;
import mn.erin.lms.legacy.domain.lms.usecase.audit.dto.GetCourseAuditOutput;

/**
 * @author Munkh
 */
public class DeleteReaderships implements UseCase<String, Boolean>
{
  private final CourseAuditRepository courseAuditRepository;

  public DeleteReaderships(CourseAuditRepository courseAuditRepository)
  {
    this.courseAuditRepository = courseAuditRepository;
  }

  @Override
  public Boolean execute(String input) throws UseCaseException
  {
    Validate.notBlank(input);

    GetCourseAudits getCourseAudits = new GetCourseAudits(courseAuditRepository);

    List<GetCourseAuditOutput> courseAudits = getCourseAudits.execute(input);

    DeleteCourseAudit deleteCourseAudit = new DeleteCourseAudit(courseAuditRepository);
    boolean deletedAll = true;
    for (GetCourseAuditOutput courseAudit : courseAudits)
    {
      if (!deleteCourseAudit.execute(courseAudit.getId()))
      {
        deletedAll = false;
      }
    }

    return deletedAll;
  }
}
