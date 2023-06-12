package mn.erin.lms.base.domain.usecase.exam.dto.question;

/**
 * @author Temuulen Naranbold
 */
public class QuestionGroupInput
{
  private String id;
  private String parentId;
  private String name;
  private String description;

  public QuestionGroupInput(String parentId, String name, String description)
  {
    this.parentId = parentId;
    this.name = name;
    this.description = description;
  }

  public QuestionGroupInput(String id, String parentId, String name, String description)
  {
    this.id = id;
    this.parentId = parentId;
    this.name = name;
    this.description = description;
  }

  public String getId()
  {
    return id;
  }

  public void setId(String id)
  {
    this.id = id;
  }

  public String getParentId()
  {
    return parentId;
  }

  public void setParentId(String parentId)
  {
    this.parentId = parentId;
  }

  public String getName()
  {
    return name;
  }

  public void setName(String name)
  {
    this.name = name;
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
