package mn.erin.lms.base.domain.util;

import java.util.ArrayList;
import java.util.List;

import mn.erin.domain.base.usecase.UseCaseException;
import mn.erin.lms.base.domain.model.assessment.Answer;
import mn.erin.lms.base.domain.model.assessment.Question;
import mn.erin.lms.base.domain.model.assessment.QuestionType;
import mn.erin.lms.base.domain.model.assessment.Quiz;
import mn.erin.lms.base.domain.usecase.assessment.dto.AnswerInfo;
import mn.erin.lms.base.domain.usecase.assessment.dto.QuestionInfo;
import mn.erin.lms.base.domain.usecase.assessment.dto.QuizDto;

/**
 * @author Bat-Erdene Tsogoo.
 */
public final class QuizUtils
{
  private QuizUtils()
  {
  }

  public static QuizDto toQuizDto(Quiz quiz)
  {
    QuizDto output = new QuizDto(quiz.getQuizId().getId(), quiz.getName(), quiz.isGraded(),
        quiz.getTimeLimit(), quiz.getDueDate());

    output.setMaxAttempts(quiz.getMaxAttempts());
    output.setThresholdScore(quiz.getThresholdScore());
    for (Question question : quiz.getQuestions())
    {
      QuestionInfo questionInfo = new QuestionInfo(question.getTitle(), question.getType().name(), question.isRequired());
      for (Answer answer : question.getAnswers())
      {
        questionInfo.addAnswers(new AnswerInfo(answer.getValue(), answer.isCorrect(), answer.getScore()));
      }
      output.addQuestions(questionInfo);
    }
    return output;
  }

  public static List<Question> createQuestionList(List<QuestionInfo> questions) throws UseCaseException
  {
    List<Question> questionList = new ArrayList<>();
    for (QuestionInfo questionInfo : questions)
    {
      String title = questionInfo.getTitle();
      if (title == null || title.isEmpty())
      {
        throw new UseCaseException("Question title cannot be empty!");
      }
      String type = questionInfo.getType();
      boolean isRequired = questionInfo.isRequired();
      List<AnswerInfo> answerInfos = questionInfo.getAnswers();
//      if ((type.equals(QuestionType.SINGLE_CHOICE.name()))
//          && !hasCorrectAnswer(answerInfos))
//      {
//        throw new UseCaseException("The question " + questionInfo.getTitle() + " has no correct answer!");
//      }
      Question question = new Question(questionInfo.getTitle(), QuestionType.valueOf(type), isRequired);
      for (AnswerInfo answerInfo : answerInfos)
      {
        String answerValue = answerInfo.getValue();
        if (question.getType() != QuestionType.FILL_IN_BLANK && (answerValue == null || answerValue.isEmpty()))
        {
          throw new UseCaseException("All answers must have a value!");
        }

        question.addAnswer(new Answer(answerInfo.getValue(), answerInfo.isCorrect(), answerInfo.getScore()));
      }
      questionList.add(question);
    }
    return questionList;
  }

  public static boolean hasCorrectAnswer(List<AnswerInfo> answers)
  {
    for (AnswerInfo answerInfo : answers)
    {
      if (answerInfo.isCorrect())
      {
        return true;
      }
    }
    return false;
  }
}
