package mn.erin.lms.jarvis.domain.report.repository;

import java.time.LocalDate;
import java.util.List;

import mn.erin.lms.base.domain.model.assessment.AssessmentId;
import mn.erin.lms.base.domain.repository.LmsRepositoryException;
import mn.erin.lms.jarvis.domain.report.model.AssessmentReport;

/**
 * @author Bat-Erdene Tsogoo.
 */
public interface AssessmentReportRepository
{
  void save(AssessmentReport report);

  AssessmentReport fetchById(String id) throws LmsRepositoryException;

  List<AssessmentReport> find(AssessmentId assessmentId, LocalDate startDate, LocalDate endDate);
}
