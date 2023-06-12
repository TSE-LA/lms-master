package mn.erin.lms.base.domain.model.exam.question;

import java.util.List;

/**
 * @author Byambajav
 */
public class LearnerQuestionDto
{
  private String id;
  private String value;
  private String imagePath;
  private QuestionType type;
  private List<Integer> selectedAnswers;
  private List<LearnerAnswerEntity> answers;

  public LearnerQuestionDto(String id, String value, QuestionType type, List<LearnerAnswerEntity> answers, String imagePath)
  {
    this.id = id;
    this.value = value;
    this.type = type;
    this.answers = answers;
    this.imagePath = imagePath;
  }

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

  public List<LearnerAnswerEntity> getAnswers()
  {
    return answers;
  }

  public void setAnswers(List<LearnerAnswerEntity> answers)
  {
    this.answers = answers;
  }
}
