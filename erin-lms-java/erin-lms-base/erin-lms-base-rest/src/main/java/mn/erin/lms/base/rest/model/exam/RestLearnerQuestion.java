package mn.erin.lms.base.rest.model.exam;

import java.util.List;

import mn.erin.lms.base.domain.model.exam.question.QuestionType;

/**
 * @author Byambajav
 */
public class RestLearnerQuestion
{
  private String id;
  private String value;
  private String imagePath;
  private QuestionType type;
  private List<Integer> selectedAnswers;
  private List<RestLearnerAnswer> answers;

  public RestLearnerQuestion(){}


  public String getId()
  {
    return id;
  }

  public void setId(String id)
  {
    this.id = id;
  }

  public String getValue()
  {
    return value;
  }

  public void setValue(String value)
  {
    this.value = value;
  }

  public String getImagePath()
  {
    return imagePath;
  }

  public void setImagePath(String imagePath)
  {
    this.imagePath = imagePath;
  }

  public QuestionType getType()
  {
    return type;
  }

  public void setType(QuestionType type)
  {
    this.type = type;
  }

  public List<Integer> getSelectedAnswers()
  {
    return selectedAnswers;
  }

  public void setSelectedAnswers(List<Integer> selectedAnswers)
  {
    this.selectedAnswers = selectedAnswers;
  }

  public List<RestLearnerAnswer> getAnswers()
  {
    return answers;
  }

  public void setAnswers(List<RestLearnerAnswer> answers)
  {
    this.answers = answers;
  }
}
