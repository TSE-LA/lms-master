package mn.erin.lms.base.domain.repository;

import java.util.List;

import mn.erin.lms.base.domain.usecase.exam.dto.ExamInteractionDto;
import mn.erin.lms.base.domain.usecase.exam.dto.ExamRuntimeData;
import mn.erin.lms.base.domain.usecase.exam.dto.ExamRuntimeStatus;

/**
 * @author Byambajav
 */
public interface ExamRuntimeDataRepository
{
  /**
   * Lists the all runtime data associated to a exam
   *
   * @param examId The id of the exam
   * @return A list of runtime data
   */
  List<ExamRuntimeData> listExamRuntimeData(String examId) throws LmsRepositoryException;

  ExamRuntimeData findRuntimeDataWithOngoingInteraction(String learnerId);

  /**
   * Gets the learner's runtime data on a particular Exam
   *
   * @param examId    The id of the exam
   * @param learnerId The id of the learner
   * @return The runtime data of the Exam
   */
  ExamRuntimeData getRuntimeData(String examId, String learnerId) throws LmsRepositoryException;

  /**
   * Checks if user has runtime data on a particular Exam
   *
   * @param examId    The id of the exam
   * @param learnerId The id of the learner
   * @return The runtime data of the Exam
   */
  boolean checkIfExists(String examId, String learnerId) throws LmsRepositoryException;

  /**
   * Creates a new instance of runtime data when a learner launches the exam
   *
   * @param learnerId      The id of the learner
   * @param examId         The id of the exam
   * @param maxScore       The max score of the exam
   * @param maxAttempt     The max attempt of the exam
   * @param duration       The duration of the exam
   * @param thresholdScore The threshold of the exam
   * @return Returns a new runtime data
   */
  ExamRuntimeData create(String learnerId, String examId, double maxScore, int maxAttempt, int duration, double thresholdScore) throws LmsRepositoryException;

  /**
   * Updates the learner's runtime data on a particular exam
   *
   * @param examId      The id of the exam
   * @param learnerId   The id of the learner
   * @param interaction The interaction data of the learner
   * @return Returns updated runtime data
   */
  ExamRuntimeData update(String examId, String learnerId, List<ExamInteractionDto> interaction) throws LmsRepositoryException;

  void update(String examId, String learnerId, ExamRuntimeStatus status) throws LmsRepositoryException;

  /**
   * Deletes the learner's runtime data on a particular exam
   *
   * @param learnerId The id of the learner
   * @param examId    The id of the exam
   */
  boolean delete(String examId, String learnerId) throws LmsRepositoryException;

  void updateMaxScore(String id, int score) throws LmsRepositoryException;

  List<ExamRuntimeData> getRuntimeData(String learnerId) throws  LmsRepositoryException;
}
