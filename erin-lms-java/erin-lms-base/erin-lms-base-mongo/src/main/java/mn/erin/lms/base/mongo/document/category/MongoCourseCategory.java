package mn.erin.lms.base.mongo.document.category;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * @author Bat-Erdene Tsogoo.
 */
@Document
public class MongoCourseCategory
{
  @Id
  private String id;

  @Indexed
  private String organizationId;

  @Indexed
  private String parentCategoryId;

  @Indexed
  private String name;

  @Indexed
  private String description;

  @Indexed
  private boolean autoEnroll;

  public MongoCourseCategory()
  {
    autoEnroll = false;
  }

  public MongoCourseCategory(String id, String organizationId, String parentCategoryId, String name, boolean autoEnroll)
  {
    this.id = id;
    this.organizationId = organizationId;
    this.parentCategoryId = parentCategoryId;
    this.name = name;
    this.autoEnroll = autoEnroll;
  }

  public MongoCourseCategory(String id, String organizationId, String name)
  {
    this.id = id;
    this.organizationId = organizationId;
    this.name = name;
    autoEnroll = false;
  }

  public void setName(String name)
  {
    this.name = name;
  }

  public void setDescription(String description)
  {
    this.description = description;
  }

  public void setAutoEnroll(boolean autoEnroll)
  {
    this.autoEnroll = autoEnroll;
  }

  public String getId()
  {
    return id;
  }

  public String getOrganizationId()
  {
    return organizationId;
  }

  public String getParentCategoryId()
  {
    return parentCategoryId;
  }

  public String getName()
  {
    return name;
  }

  public String getDescription()
  {
    return description;
  }


  public boolean isAutoEnroll()
  {
    return autoEnroll;
  }
}
