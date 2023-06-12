package mn.erin.lms.base.mongo.document.exam.question;

import org.apache.commons.lang3.Validate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * @author Galsan Bayart
 */
@Document
public class MongoQuestionGroup
{
  @Id
  private String id;
  private String parentGroupId;
  private String name;
  private String organizationId;
  private String description;

  public MongoQuestionGroup(String id, String parentGroupId, String name, String organizationId)
  {
    this.id = Validate.notBlank(id);
    this.parentGroupId = parentGroupId;
    this.name = Validate.notBlank(name);
    this.organizationId = Validate.notBlank(organizationId);
  }

  public String getId()
  {
    return id;
  }

  public void setId(String id)
  {
    this.id = id;
  }

  public String getParentGroupId()
  {
    return parentGroupId;
  }

  public void setParentGroupId(String parentGroupId)
  {
    this.parentGroupId = parentGroupId;
  }

  public String getName()
  {
    return name;
  }

  public void setName(String name)
  {
    this.name = name;
  }

  public String getOrganizationId()
  {
    return organizationId;
  }

  public void setOrganizationId(String organizationId)
  {
    this.organizationId = organizationId;
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
