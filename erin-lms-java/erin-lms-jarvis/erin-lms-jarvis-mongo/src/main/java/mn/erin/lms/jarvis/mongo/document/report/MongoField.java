package mn.erin.lms.jarvis.mongo.document.report;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * @author Temuulen Naranbold
 */
@Document
public class MongoField
{
  @Id
  private ObjectId id;
  @Indexed
  private String organizationId;
  @Indexed
  private String name;
  @Indexed
  private String type;
  @Indexed
  private boolean required;

  public MongoField()
  {
  }

  public MongoField(String organizationId, String name, String type, boolean required)
  {
    this.organizationId = organizationId;
    this.name = name;
    this.type = type;
    this.required = required;
  }

  public MongoField(ObjectId id, String organizationId, String name, String type, boolean required)
  {
    this.id = id;
    this.organizationId = organizationId;
    this.name = name;
    this.type = type;
    this.required = required;
  }

  public ObjectId getId()
  {
    return id;
  }

  public void setId(ObjectId id)
  {
    this.id = id;
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

  public String getType()
  {
    return type;
  }

  public void setType(String type)
  {
    this.type = type;
  }

  public boolean isRequired()
  {
    return required;
  }

  public void setRequired(boolean required)
  {
    this.required = required;
  }
}
