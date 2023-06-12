package mn.erin.lms.base.domain.repository;

import java.util.List;
import java.util.Set;

import mn.erin.lms.base.domain.model.category.ExamCategoryId;
import mn.erin.lms.base.domain.model.exam.Exam;
import mn.erin.lms.base.domain.model.exam.ExamConfig;
import mn.erin.lms.base.domain.model.exam.ExamGroupId;
import mn.erin.lms.base.domain.model.exam.ExamId;
import mn.erin.lms.base.domain.model.exam.ExamPublishConfig;
import mn.erin.lms.base.domain.model.exam.ExamPublishStatus;
import mn.erin.lms.base.domain.model.exam.ExamStatus;
import mn.erin.lms.base.domain.model.exam.ExamType;
import mn.erin.lms.base.domain.model.exam.HistoryOfModification;

/**
 * @author Galsan Bayart.
 */
public interface ExamRepository
{
  /**
   * Creates a new exam
   *
   * @param name                  The name of the exam
   * @param description           The description of the exam
   * @param categoryId            The unique ID of the exam category
   * @param examType              The type of the exam
   * @param historyOfModification The history of modification
   * @param publishConfig         The publishing configuration of exam
   * @param configure             The configuration of exam
   * @return Returns a new exam
   * @throws LmsRepositoryException If failed to create exam
   */
  Exam create(String name, String description, ExamCategoryId categoryId, ExamGroupId groupId, ExamType examType,
      List<HistoryOfModification> historyOfModification, ExamPublishConfig publishConfig, ExamConfig configure) throws LmsRepositoryException;

  /**
   * Updates an exam
   *
   * @param exam The updating exam
   * @return Returns exam ID
   * @throws LmsRepositoryException If failed to update the exam
   */
  String update(Exam exam) throws LmsRepositoryException;

  void updateExamStatus(String examId, ExamStatus newStatus) throws LmsRepositoryException;

  void updateExamPublishStatus(String examId, ExamPublishStatus newStatus) throws LmsRepositoryException;

  /**
   * Update the enrollment part of exams
   *
   * @param enrolledLearners A set of enrolled learners
   * @param enrolledGroups   A set of enrolled groups
   * @return Returns the exam
   * @throws LmsRepositoryException If failed to create exam
   */
  Exam updateEnrollments(String id, Set<String> enrolledLearners, Set<String> enrolledGroups) throws LmsRepositoryException;

  /**
   * Finds all existing exams
   *
   * @return Returns list of existing exams
   */
  List<Exam> getAllExam();

  /**
   * Checks whether an exam exists or not
   *
   * @param categoryId The unique ID of the exam category
   * @return a list of exams
   */
  List<Exam> listAllByCategory(ExamCategoryId categoryId);

  /**
   * Finds an exam by exam ID
   *
   * @param id The unique ID of the exam
   * @return Returns an existing exam
   * @throws LmsRepositoryException If failed to find an exam
   */
  Exam findById(ExamId id) throws LmsRepositoryException;

  /**
   * Get exams by given IDs
   *
   * @param examIds The unique IDs of the exam
   * @return Returns the list of exam
   */
  List<Exam> getAllByIds(Set<String> examIds);

  /**
   * @param examGroupId The unique ID of the exam group
   * @return Returns the list of exam
   */
  List<Exam> listAllByGroup(ExamGroupId examGroupId);

  List<Exam> listAllByCategoryAndGroup(ExamCategoryId categoryId, ExamGroupId groupId);

  /**
   * Deletes an exam
   *
   * @param id The deleting ID of the exam
   * @return If exam deleted returns true, otherwise false
   * @throws LmsRepositoryException If failed to delete the exam
   */
  boolean delete(String id) throws LmsRepositoryException;

  /**
   * Checks whether an exam exists or not
   *
   * @param examId The unique ID of the exam
   * @return If exam exists returns true, otherwise false
   */
  boolean exists(ExamId examId);

  void updateExamScore(String id, int score) throws LmsRepositoryException;
}
