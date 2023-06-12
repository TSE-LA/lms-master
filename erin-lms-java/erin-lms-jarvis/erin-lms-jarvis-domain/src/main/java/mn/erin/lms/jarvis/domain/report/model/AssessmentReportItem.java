package mn.erin.lms.jarvis.domain.report.model;

import java.util.ArrayList;
import java.util.List;

import mn.erin.lms.base.domain.model.assessment.QuestionType;

/**
 * @author Bat-Erdene Tsogoo.
 */
public class AssessmentReportItem
{
  private final Integer index;
  private final String question;
  private final QuestionType questionType;
  private List<Answer> answers = new ArrayList<>();

  public AssessmentReportItem(Integer index, String question, QuestionType questionType)
  {
    this.index = index;
    this.question = question;
    this.questionType = questionType;
  }

  public AssessmentReportItem(Integer index, String question, QuestionType questionType,
      List<Answer> answers)
  {
    this.index = index;
    this.question = question;
    this.questionType = questionType;
    this.answers = answers;
  }

  public void addAnswer(Answer answer)
  {
    this.answers.add(answer);
  }

  public void setAnswers(List<Answer> answers)
  {
    this.answers = answers;
  }

  public String getQuestion()
  {
    return question;
  }

  public QuestionType getQuestionType()
  {
    return questionType;
  }

  public List<Answer> getAnswers()
  {
    return answers;
  }

  public Integer getIndex()
  {
    return index;
  }

  public static class Answer
  {
    private final String value;
    private Integer count;

    public Answer(String value)
    {
      this.value = value;
    }

    public Answer(String value, Integer count)
    {
      this.value = value;
      this.count = count;
    }

    public String getValue()
    {
      return value;
    }

    public Integer getCount()
    {
      return count;
    }

    public void increment()
    {
      if (count != null)
      {
        this.count += 1;
      }
    }

    public void add(Integer count)
    {
      this.count += count;
    }
  }
}
