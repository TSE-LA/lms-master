package mn.erin.lms.base.domain.repository;

import java.time.LocalDate;
import java.util.List;

import mn.erin.lms.base.domain.model.assessment.Assessment;
import mn.erin.lms.base.domain.model.assessment.AssessmentId;
import mn.erin.lms.base.domain.model.assessment.QuizId;
import mn.erin.lms.base.aim.user.AuthorId;

/**
 * @author Bat-Erdene Tsogoo.
 */
public interface AssessmentRepository
{
  Assessment createAssessment(QuizId quizId, String name, AuthorId authorId) throws LmsRepositoryException;

  Assessment createAssessment(QuizId quizId, String name, AuthorId authorId, String description) throws LmsRepositoryException;

  List<Assessment> listAll();

  List<Assessment> listAll(LocalDate startDate, LocalDate endDate);

  Assessment findById(AssessmentId assessmentId) throws LmsRepositoryException;

  Assessment update(AssessmentId assessmentId, String name, String description) throws LmsRepositoryException;

  Assessment updateModifiedDate(AssessmentId assessmentId) throws LmsRepositoryException;

  boolean delete(AssessmentId assessmentId);

  void updateStatus(AssessmentId assessmentId, boolean activate) throws LmsRepositoryException;
}
