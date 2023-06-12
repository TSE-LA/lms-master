package mn.erin.domain.aim.usecase.group;

/**
 * @author Temuulen Naranbold
 */
public class UpdateGroupParentInput
{
  private String id;
  private String parentId;

  public UpdateGroupParentInput(String id, String parentId)
  {
    this.id = id;
    this.parentId = parentId;
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

}
