package mn.erin.lms.base.mongo.document.category;

import org.apache.commons.lang3.Validate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;

/**
 * @author Temuulen Naranbold
 */
public class MongoExamCategory
{
  @Id
  private String id;

  private int index;

  @Indexed
  private String parentCategoryId;

  @Indexed
  private String organizationId;

  @Indexed
  private String name;

  private String description;

  public MongoExamCategory()
  {
  }

  public MongoExamCategory(String id, int index, String parentCategoryId, String organizationId, String name, String description)
  {
    this.id = Validate.notBlank(id);
    this.index = index;
    this.parentCategoryId = Validate.notBlank(parentCategoryId);
    this.organizationId = Validate.notBlank(organizationId);
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

  public int getIndex()
  {
    return index;
  }

  public void setIndex(int index)
  {
    this.index = index;
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
