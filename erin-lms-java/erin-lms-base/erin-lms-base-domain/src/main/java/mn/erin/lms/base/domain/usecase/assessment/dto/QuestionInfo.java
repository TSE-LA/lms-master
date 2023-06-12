package mn.erin.lms.base.domain.usecase.assessment.dto;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Bat-Erdene Tsogoo.
 */
public class QuestionInfo
{
  private String title;
  private String type;
  private boolean isRequired;
  private List<AnswerInfo> answers = new ArrayList<>();

  public QuestionInfo(String title, String type, boolean isRequired)
  {
    this.title = title;
    this.type = type;
    this.isRequired = isRequired;
  }

  public String getTitle()
  {
    return title;
  }

  public void setTitle(String title)
  {
    this.title = title;
  }

  public List<AnswerInfo> getAnswers()
  {
    return answers;
  }

  public void addAnswers(AnswerInfo answer)
  {
    this.answers.add(answer);
  }

  public String getType()
  {
    return type;
  }

  public void setType(String type)
  {
    this.type = type;
  }

  public boolean isRequired() {
    return isRequired;
  }

  public void setRequired(boolean required) {
    isRequired = required;
  }
}
