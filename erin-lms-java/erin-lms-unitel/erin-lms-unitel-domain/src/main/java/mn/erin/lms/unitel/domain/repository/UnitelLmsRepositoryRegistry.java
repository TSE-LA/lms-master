package mn.erin.lms.unitel.domain.repository;

import mn.erin.lms.base.domain.repository.LmsRepositoryRegistry;

/**
 * @author Bat-Erdene Tsogoo.
 */
public interface UnitelLmsRepositoryRegistry extends LmsRepositoryRegistry
{
  CourseReportRepository getCourseReportRepository();

  AssessmentReportRepository getAssessmentReportRepository();

}
