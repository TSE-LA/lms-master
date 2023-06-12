package mn.erin.lms.legacy.domain.lms.usecase.audit;

import java.util.ArrayList;
import java.util.List;

import mn.erin.domain.base.usecase.UseCase;
import mn.erin.lms.legacy.domain.lms.model.audit.CourseAudit;
import mn.erin.lms.legacy.domain.lms.model.enrollment.LearnerId;
import mn.erin.lms.legacy.domain.lms.repository.CourseAuditRepository;
import mn.erin.lms.legacy.domain.lms.usecase.audit.dto.GetCourseAuditOutput;

/**
 * @author Bat-Erdene Tsogoo.
 */
public class GetCourseAudits implements UseCase<String, List<GetCourseAuditOutput>>
{
  private final CourseAuditRepository courseAuditRepository;

  public GetCourseAudits(CourseAuditRepository courseAuditRepository)
  {
    this.courseAuditRepository = courseAuditRepository;
  }

  @Override
  public List<GetCourseAuditOutput> execute(String learnerId)
  {
    List<CourseAudit> courseAudits = courseAuditRepository.listAll(new LearnerId(learnerId));

    List<GetCourseAuditOutput> result = new ArrayList<>();
    for (CourseAudit courseAudit : courseAudits)
    {
      GetCourseAuditOutput output = new GetCourseAuditOutput(courseAudit.getId().getId(), courseAudit.getCourseId().getId());
      result.add(output);
    }

    return result;
  }
}
