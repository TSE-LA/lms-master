package mn.erin.lms.base.mongo.document.category;

import org.apache.commons.lang3.Validate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * @author Galsan Bayart
 */
@Document
public class MongoQuestionCategory
{
  @Id
  private String id;

  @Indexed
  private String parentCategoryId;

  @Indexed
  private String organizationId;

  private int index;

  @Indexed
  private String name;

  @Indexed
  private String description;

  public MongoQuestionCategory()
  {
  }

  public MongoQuestionCategory(String id, String parentCategoryId, String organizationId, int index, String name, String description)
  {
    this.id = Validate.notBlank(id);
    this.parentCategoryId = Validate.notBlank(parentCategoryId);
    this.organizationId = Validate.notBlank(organizationId);
    this.index = index;
    this.name = Validate.notBlank(name);
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

  public String getParentCategoryId()
  {
    return parentCategoryId;
  }

  public void setParentCategoryId(String parentCategoryId)
  {
    this.parentCategoryId = parentCategoryId;
  }

  public String getOrganizationId()
  {
    return organizationId;
  }

  public void setOrganizationId(String organizationId)
  {
    this.organizationId = organizationId;
  }

  public int getIndex()
  {
    return index;
  }

  public void setIndex(int index)
  {
    this.index = index;
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
