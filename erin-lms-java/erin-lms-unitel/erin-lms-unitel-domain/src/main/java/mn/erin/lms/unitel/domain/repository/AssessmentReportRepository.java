package mn.erin.lms.unitel.domain.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

import mn.erin.lms.base.domain.model.assessment.AssessmentId;
import mn.erin.lms.base.domain.repository.LmsRepositoryException;
import mn.erin.lms.unitel.domain.model.report.AssessmentReport;

/**
 * @author Bat-Erdene Tsogoo.
 */
public interface AssessmentReportRepository
{
  void save(AssessmentReport report);

  AssessmentReport fetchById(String id) throws LmsRepositoryException;

  List<AssessmentReport> findAll(Set<String> ids);

  List<AssessmentReport> find(AssessmentId assessmentId, LocalDate startDate, LocalDate endDate);
}
