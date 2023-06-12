package mn.erin.lms.base.domain.repository;

import java.time.LocalDateTime;
import java.util.List;

import mn.erin.lms.base.domain.model.assessment.Question;
import mn.erin.lms.base.domain.model.assessment.Quiz;
import mn.erin.lms.base.domain.model.assessment.QuizId;

/**
 * @author Bat-Erdene Tsogoo.
 */
public interface QuizRepository
{
  /**
   * Creates a new quiz
   *
   * @param name The name of the quiz
   * @return A new quiz
   */
  Quiz create(String name);

  /**
   * Creates a new quiz
   *
   * @param questions The list of questions
   * @param name      The name of the quiz
   * @param graded    Whether the quiz is graded or not
   * @param timeLimit The time limit of the quiz
   * @param dueDate   The due date of the quiz
   * @return A new quiz
   * @throws LmsRepositoryException If failed to create a quiz
   */
  Quiz create(List<Question> questions, String name, boolean graded, Long timeLimit, LocalDateTime dueDate) throws LmsRepositoryException;

  /**
   * Creates a new quiz with max attempts and threshold score
   *
   * @param questions      The list of questions
   * @param name           The name of the quiz
   * @param graded         Whether the quiz is graded or not
   * @param timeLimit      The time limit of the quiz
   * @param dueDate        The due date of the quiz
   * @param maxAttempts    The number of attempts required for the quiz
   * @param thresholdScore The threshold score of the quiz
   * @return A new quiz
   * @throws LmsRepositoryException If failed to create a quiz
   */
  Quiz create(List<Question> questions, String name, boolean graded, Long timeLimit, LocalDateTime dueDate, Integer maxAttempts, Double thresholdScore)
      throws LmsRepositoryException;

  /**
   * Updates a quiz
   *
   * @param quizId    The ID of the quiz
   * @param questions The list of questions
   * @return An updated quiz
   * @throws LmsRepositoryException If failed to update the quiz or the quiz does not exist
   */
  Quiz update(QuizId quizId, List<Question> questions) throws LmsRepositoryException;

  /**
   * Updates a quiz including its max attempts and threshold score
   *
   * @param quizId         The ID of the quiz
   * @param questions      A list of questions
   * @param maxAttempts    The number of attempts required for the quiz
   * @param thresholdScore The threshold score of the quiz
   * @return An updated quiz
   * @throws LmsRepositoryException If failed to update a quiz or the quiz does not exist
   */
  Quiz update(QuizId quizId, List<Question> questions, Integer maxAttempts, Double thresholdScore) throws LmsRepositoryException;

  /**
   * Fetches a quiz by ID
   *
   * @param quizId The ID of the quiz
   * @return A quiz
   * @throws LmsRepositoryException If the quiz does not exist
   */
  Quiz fetchById(QuizId quizId) throws LmsRepositoryException;

  /**
   * Delets a quiz by ID
   *
   * @param quizId The ID of the quiz
   * @return true if deleted; otherwise, false
   */
  boolean delete(QuizId quizId);
}
