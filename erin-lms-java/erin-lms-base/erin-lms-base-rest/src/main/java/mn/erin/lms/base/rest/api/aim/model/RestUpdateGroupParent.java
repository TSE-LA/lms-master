package mn.erin.lms.base.rest.api.aim.model;

/**
 * @author Temuulen Naranbold
 */
public class RestUpdateGroupParent
{
  private String id;
  private String parentId;

  public RestUpdateGroupParent()
  {
  }

  public String getId()
  {
    return id;
  }

  public String getParentId()
  {
    return parentId;
  }

  public void setId(String id)
  {
    this.id = id;
  }

  public void setParentId(String parentId)
  {
    this.parentId = parentId;
  }
}
