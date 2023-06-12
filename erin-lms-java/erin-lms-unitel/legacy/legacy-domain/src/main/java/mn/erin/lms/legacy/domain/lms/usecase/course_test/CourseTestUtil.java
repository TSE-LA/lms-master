/*
 * (C)opyright, 2019, ERIN SYSTEMS LLC
 * The source code is protected by copyright laws and international copyright
 * treaties, as well as other intellectual property laws and treaties.
 * All rights reserved.
 */

package mn.erin.lms.legacy.domain.lms.usecase.course_test;

import java.util.ArrayList;
import java.util.List;

import mn.erin.domain.base.usecase.UseCaseException;
import mn.erin.lms.legacy.domain.lms.model.assessment.Answer;
import mn.erin.lms.legacy.domain.lms.model.assessment.Question;
import mn.erin.lms.legacy.domain.lms.model.assessment.QuestionType;

/**
 * @author Oyungerel Chuluunsukh.
 */
public class CourseTestUtil
{
  private CourseTestUtil()
  {

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
      List<AnswerInfo> answerInfos = questionInfo.getAnswers();
      if ((type.equals(QuestionType.MULTI_CHOICE.name()) || type.equals(QuestionType.SINGLE_CHOICE.name()))
          && !hasCorrectAnswer(answerInfos))
      {
        throw new UseCaseException("The question " + questionInfo.getTitle() + " has no correct answer!");
      }
      Question question = new Question(questionInfo.getTitle(), QuestionType.valueOf(type));
      for (AnswerInfo answerInfo : answerInfos)
      {
        String answerValue = answerInfo.getValue();
        if (answerValue == null || answerValue.isEmpty())
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
