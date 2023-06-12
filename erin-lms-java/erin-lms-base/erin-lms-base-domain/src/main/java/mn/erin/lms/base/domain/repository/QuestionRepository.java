package mn.erin.lms.base.domain.repository;

import java.util.List;
import java.util.Set;

import mn.erin.lms.base.domain.model.category.QuestionCategoryId;
import mn.erin.lms.base.domain.model.exam.question.Answer;
import mn.erin.lms.base.domain.model.exam.question.Question;
import mn.erin.lms.base.domain.model.exam.question.QuestionDetail;
import mn.erin.lms.base.domain.model.exam.question.QuestionGroupId;
import mn.erin.lms.base.domain.model.exam.question.QuestionType;

/**
 * @author Galsan Bayart
 */
public interface QuestionRepository
{
  /**
   * Create a question
   *
   * @param value   Of the question
   * @param detail  The rest of the details
   * @param type    The question type
   * @param answers List of answers
   * @param score   The total score of the question
   * @throws LmsRepositoryException If failed to get list
   */
  Question create(String value, QuestionDetail detail, QuestionType type,
      List<Answer> answers, int score) throws LmsRepositoryException;

  /**
   * Gets all question
   *
   * @throws LmsRepositoryException If failed to get questions
   */
  List<Question> getAll() throws LmsRepositoryException;

  /**
   * Gets all active question
   */
  List<Question> getAllActive();

  /**
   * Gets all active question with category id
   *
   * @param categoryId Question category id
   * @throws LmsRepositoryException If failed to get questions
   */
  List<Question> getAllActive(QuestionCategoryId categoryId) throws LmsRepositoryException;

  /**
   * Gets all active question with group id
   *
   * @param groupId Question category id
   * @throws LmsRepositoryException If failed to get questions
   */
  List<Question> getAllActive(QuestionGroupId groupId) throws LmsRepositoryException;

  /**
   * Gets all active question with group id and category id
   *
   * @param categoryId Question category id
   * @param groupId    Question category id
   * @throws LmsRepositoryException If failed to get questions
   */
  List<Question> getAllActive(QuestionCategoryId categoryId, QuestionGroupId groupId) throws LmsRepositoryException;


  /**
   * Gets all question count
   */
  int getQuestionCount(int score);

  /**
   * Gets all active question count with category id
   *
   * @param categoryId Question category id
   * @throws LmsRepositoryException If failed to get questions
   */
  int getQuestionCount(QuestionCategoryId categoryId, int score) throws LmsRepositoryException;

  /**
   * Gets all active question count with group id
   *
   * @param groupId Question category id
   * @throws LmsRepositoryException If failed to get questions
   */
  int getQuestionCount(QuestionGroupId groupId, int score) throws LmsRepositoryException;

  /**
   * Gets all active question count with group id and category id
   *
   * @param categoryId Question category id
   * @param groupId    Question category id
   * @throws LmsRepositoryException If failed to get questions
   */
  int getQuestionCount(QuestionCategoryId categoryId, QuestionGroupId groupId, int score) throws LmsRepositoryException;

  /**
   * Gets question by ids
   *
   * @param questionIds Question
   * @return questions
   */
  List<Question> getByIds(Set<String> questionIds);

  /**
   * Updates the question status to used
   *
   * @param questionIds Updating question IDs
   */
  void updateStatus(Set<String> questionIds, String updatingStatus);

  /**
   * @param questionId Updating question ID
   */
  void archiveQuestion(String questionId);

  /**
   * Gets questions by group id category and score
   *
   * @param groupId    Question group id
   * @param categoryId Question category id
   * @param score      Question score
   */
  List<Question> findByGroupIdAndCategoryAndScore(String groupId, String categoryId, int score);

  /**
   * Gets questions by group id category and score
   *
   * @param question Question to update
   * @return updated question
   */
  Question update(Question question) throws LmsRepositoryException;

  /**
   * Deletes a question
   *
   * @param id The id of the question to delete
   */
  void deleteById(String id);

  /**
   * Get question by id
   *
   * @param id The id of the question
   * @return the found question
   */
  Question findById(String id) throws LmsRepositoryException;

  /**
   * @param categoryId The id of question category
   * @return A list of questions with specified parameter values. Returns empty list if no questions were found.
   */
  List<Question> listAll(QuestionCategoryId categoryId);
}
