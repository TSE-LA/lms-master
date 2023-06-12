package mn.erin.lms.base.domain.usecase.exam.dto.question;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Temuulen Naranbold
 */
public class QuestionInput
{
  private String id;
  private String value;
  private String categoryId;
  private String groupId;
  private String type;
  private List<AnswerInput> answers = new ArrayList<>();
  private int score;
  private boolean hasImage;
  private File file;
  private String correctAnswerText;
  private String wrongAnswerText;

  public QuestionInput()
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

  public List<AnswerInput> getAnswers()
  {
    return answers;
  }

  public void setAnswers(List<AnswerInput> answers)
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

  public File getFile()
  {
    return file;
  }

  public void setFile(File file)
  {
    this.file = file;
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

  public static class Builder
  {
    private String id;
    private String name;
    private String categoryId;
    private String groupId;
    private String type;
    private List<AnswerInput> answers = new ArrayList<>();
    private int score;
    private boolean hasImage;
    private File file;
    private String fileId;
    private String correctAnswerText;
    private String wrongAnswerText;

    public Builder withId(String id)
    {
      this.id = id;
      return this;
    }

    public Builder withName(String name)
    {
      this.name = name;
      return this;
    }

    public Builder withCategoryId(String categoryId)
    {
      this.categoryId = categoryId;
      return this;
    }

    public Builder withGroupId(String groupIds)
    {
      this.groupId = groupIds;
      return this;
    }

    public Builder withType(String type)
    {
      this.type = type;
      return this;
    }

    public Builder withAnswers(List<AnswerInput> answers)
    {
      this.answers = answers;
      return this;
    }

    public Builder withScore(int score)
    {
      this.score = score;
      return this;
    }

    public Builder withHasImage(boolean hasImage)
    {
      this.hasImage = hasImage;
      return this;
    }

    public Builder withFile(File file)
    {
      this.file = file;
      return this;
    }

    public Builder withCorrectAnswerText(String correctAnswerText)
    {
      this.correctAnswerText = correctAnswerText;
      return this;
    }

    public Builder withWrongAnswerText(String wrongAnswerText)
    {
      this.wrongAnswerText = wrongAnswerText;
      return this;
    }

    public Builder withFileId(String fileId)
    {
      this.fileId = fileId;
      return this;
    }

    public QuestionInput build()
    {
      QuestionInput input = new QuestionInput();
      input.id = this.id;
      input.value = this.name;
      input.categoryId = this.categoryId;
      input.groupId = this.groupId;
      input.type = this.type;
      input.answers = this.answers;
      input.score = this.score;
      input.hasImage = this.hasImage;
      input.file = this.file;
      input.correctAnswerText = this.correctAnswerText;
      input.wrongAnswerText = this.wrongAnswerText;
      return input;
    }
  }
}
