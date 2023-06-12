package mn.erin.lms.jarvis.domain.repository;

import mn.erin.lms.base.domain.repository.LmsRepositoryRegistry;
import mn.erin.lms.jarvis.domain.report.repository.AssessmentReportRepository;
import mn.erin.lms.jarvis.domain.report.repository.CourseReportRepository;
import mn.erin.lms.jarvis.domain.report.repository.FieldRepository;
import mn.erin.lms.jarvis.domain.report.repository.LearnerSuccessRepository;

/**
 * @author Bat-Erdene Tsogoo.
 */
public interface JarvisLmsRepositoryRegistry extends LmsRepositoryRegistry
{
  CourseReportRepository getCourseReportRepository();

  AssessmentReportRepository getAssessmentReportRepository();

  LearnerSuccessRepository getLearnerSuccessRepository();

  FieldRepository getFieldRepository();
}
