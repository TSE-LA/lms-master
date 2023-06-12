package mn.erin.lms.base.analytics.model.survey;

import mn.erin.lms.base.analytics.model.Analytic;
import mn.erin.lms.base.domain.model.assessment.QuestionType;

import java.util.List;

/**
 * @author Munkh
 */
public class SurveyAnalytic implements Analytic
{
  private int index;
  private String question;
  private QuestionType questionType;
  private List<SurveyAnswer> answers;

  public SurveyAnalytic(int index, String question, QuestionType questionType, List<SurveyAnswer> answers)
  {
    this.index = index;
    this.question = question;
    this.questionType = questionType;
    this.answers = answers;
  }

  public int getIndex()
  {
    return index;
  }

  public void setIndex(int index)
  {
    this.index = index;
  }

  public String getQuestion()
  {
    return question;
  }

  public void setQuestion(String question)
  {
    this.question = question;
  }

  public QuestionType getQuestionType()
  {
    return questionType;
  }

  public void setQuestionType(QuestionType questionType)
  {
    this.questionType = questionType;
  }

  public List<SurveyAnswer> getAnswers()
  {
    return answers;
  }

  public void setAnswers(List<SurveyAnswer> answers)
  {
    this.answers = answers;
  }
}
