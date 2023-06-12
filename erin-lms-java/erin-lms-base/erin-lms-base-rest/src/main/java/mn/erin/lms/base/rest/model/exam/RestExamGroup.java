package mn.erin.lms.base.rest.model.exam;

/**
 * @author Temuulen Naranbold
 */
public class RestExamGroup
{
  String id;
  String parentId;
  String name;
  String description;

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

  public RestExamGroup createDummy()
  {
    RestExamGroup dummy = new RestExamGroup();
    dummy.setParentId("");
    dummy.setName("examGroup");
    dummy.setDescription("dummy");
    return dummy;
  }
}
