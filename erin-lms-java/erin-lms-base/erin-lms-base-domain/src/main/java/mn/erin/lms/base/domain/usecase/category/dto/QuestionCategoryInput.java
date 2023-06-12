package mn.erin.lms.base.domain.usecase.category.dto;

/**
 * @author Temuulen Naranbold
 */
public class QuestionCategoryInput
{
  private final int index;
  private final String name;
  private String parentCategoryId;
  private String questionCategoryId;
  private String description;

  public QuestionCategoryInput(String parentCategoryId, int index, String name)
  {
    this.parentCategoryId = parentCategoryId;
    this.index = index;
    this.name = name;
  }

  public QuestionCategoryInput(int index, String name, String questionCategoryId)
  {
    this.index = index;
    this.name = name;
    this.questionCategoryId = questionCategoryId;
  }

  public int getIndex()
  {
    return index;
  }

  public String getName()
  {
    return name;
  }

  public String getParentCategoryId()
  {
    return parentCategoryId;
  }

  public void setParentCategoryId(String parentCategoryId)
  {
    this.parentCategoryId = parentCategoryId;
  }

  public String getQuestionCategoryId()
  {
    return questionCategoryId;
  }

  public void setQuestionCategoryId(String questionCategoryId)
  {
    this.questionCategoryId = questionCategoryId;
  }

  public String getDescription()
  {
    return description;
  }

  public void setDescription(String description)
  {
    this.description = description;
  }
}
