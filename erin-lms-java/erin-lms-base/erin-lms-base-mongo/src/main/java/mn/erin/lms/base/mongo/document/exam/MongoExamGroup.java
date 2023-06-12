package mn.erin.lms.base.mongo.document.exam;

import org.apache.commons.lang3.Validate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * @author Galsan Bayart.
 */
@Document
public class MongoExamGroup
{
  @Id
  private String id;
  private String parentId;
  private String name;
  private String organizationId;
  private String description;

  public MongoExamGroup(String id, String parentId, String name, String organizationId, String description)
  {
    this.id = Validate.notBlank(id);
    this.parentId = parentId;
    this.name = Validate.notBlank(name);
    this.organizationId = Validate.notBlank(organizationId);
    this.description = description;
  }

  public MongoExamGroup()
  {
    //do nothing
  }

  public String getId()
  {
    return id;
  }

  public String getParentId()
  {
    return parentId;
  }

  public String getName()
  {
    return name;
  }

  public String getDescription()
  {
    return description;
  }

  public String getOrganizationId()
  {
    return organizationId;
  }

  public void setId(String id)
  {
    this.id = id;
  }

  public void setParentId(String parentId)
  {
    this.parentId = parentId;
  }

  public void setName(String name)
  {
    this.name = name;
  }

  public void setOrganizationId(String organizationId)
  {
    this.organizationId = organizationId;
  }

  public void setDescription(String description)
  {
    this.description = description;
  }
}
