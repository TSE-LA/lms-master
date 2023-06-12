package mn.erin.lms.legacy.domain.lms.usecase.audit;

import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import mn.erin.domain.base.usecase.UseCase;
import mn.erin.domain.base.usecase.UseCaseException;
import mn.erin.lms.legacy.domain.lms.model.audit.CourseAudit;
import mn.erin.lms.legacy.domain.lms.model.enrollment.CourseEnrollment;
import mn.erin.lms.legacy.domain.lms.repository.CourseAuditRepository;
import mn.erin.lms.legacy.domain.lms.repository.CourseEnrollmentRepository;
import mn.erin.lms.legacy.domain.lms.repository.LMSRepositoryException;

/**
 * @author Temuulen Naranbold
 */
public class FixCourseReadership implements UseCase<Map<String, Date>, Boolean>
{
  private final CourseEnrollmentRepository courseEnrollmentRepository;
  private final CourseAuditRepository courseAuditRepository;

  public FixCourseReadership(CourseEnrollmentRepository courseEnrollmentRepository, CourseAuditRepository courseAuditRepository)
  {
    this.courseEnrollmentRepository = courseEnrollmentRepository;
    this.courseAuditRepository = courseAuditRepository;
  }

  @Override
  public Boolean execute(Map<String, Date> input) throws UseCaseException
  {
    List<CourseEnrollment> enrollments = courseEnrollmentRepository.getEnrollmentList(input.get("startDate"), input.get("endDate"));
    List<CourseAudit> courseAudits = courseAuditRepository.listAll();

    Set<String> enrollmentCourseAndLearnerConnections = enrollments.stream()
        .map(enrollment -> enrollment.getCourseId().getId() + "_" + enrollment.getLearnerId().getId())
        .collect(Collectors.toSet());

    Map<String, String> enrollmentMap = enrollments.stream().collect(Collectors.toMap(
        enrollment -> enrollment.getCourseId().getId() + "_" + enrollment.getLearnerId().getId(),
        enrollment -> enrollment.getId().getId()
    ));

    Set<String> auditCourseAndLearnerConnections = courseAudits.stream()
        .map(enrollment -> enrollment.getCourseId().getId() + "_" + enrollment.getLearnerId().getId())
        .collect(Collectors.toSet());

    Set<String> intersections = new HashSet<>(enrollmentCourseAndLearnerConnections);
    intersections.retainAll(auditCourseAndLearnerConnections);

    Set<String> duplicatedEnrollmentIds = intersections.stream().map(enrollmentMap::get).collect(Collectors.toSet());

    try
    {
      courseEnrollmentRepository.deleteEnrollments(duplicatedEnrollmentIds);
    }
    catch (LMSRepositoryException e)
    {
      throw new UseCaseException(e.getMessage());
    }

    return true;
  }
}
