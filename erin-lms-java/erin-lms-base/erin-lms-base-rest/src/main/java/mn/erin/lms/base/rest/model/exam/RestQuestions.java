package mn.erin.lms.base.rest.model.exam;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Galsan Bayart
 */
public class RestQuestions
{
  private String id;
  private String value;
  private String categoryId;
  private String groupId;
  private String type;
  private List<RestAnswer> answers = new ArrayList<>();
  private int score;
  private boolean hasImage;
  private String fileId;
  private String correctAnswerText;
  private String wrongAnswerText;

  public RestQuestions()
  {
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

  public String getCategoryId()
  {
    return categoryId;
  }

  public void setCategoryId(String categoryId)
  {
    this.categoryId = categoryId;
  }

  public String getGroupId()
  {
    return groupId;
  }

  public void setGroupId(String groupId)
  {
    this.groupId = groupId;
  }

  public String getType()
  {
    return type;
  }

  public void setType(String type)
  {
    this.type = type;
  }

  public List<RestAnswer> getAnswers()
  {
    return answers;
  }

  public void setAnswers(List<RestAnswer> answers)
  {
    this.answers = answers;
  }

  public int getScore()
  {
    return score;
  }

  public void setScore(int score)
  {
    this.score = score;
  }

  public boolean isHasImage()
  {
    return hasImage;
  }

  public void setHasImage(boolean hasImage)
  {
    this.hasImage = hasImage;
  }

  public String getFileId()
  {
    return fileId;
  }

  public void setFileId(String fileId)
  {
    this.fileId = fileId;
  }

  public String getCorrectAnswerText()
  {
    return correctAnswerText;
  }

  public void setCorrectAnswerText(String correctAnswerText)
  {
    this.correctAnswerText = correctAnswerText;
  }

  public String getWrongAnswerText()
  {
    return wrongAnswerText;
  }

  public void setWrongAnswerText(String wrongAnswerText)
  {
    this.wrongAnswerText = wrongAnswerText;
  }
}
