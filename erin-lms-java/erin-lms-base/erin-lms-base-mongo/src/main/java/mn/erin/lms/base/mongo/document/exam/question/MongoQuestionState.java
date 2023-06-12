package mn.erin.lms.base.mongo.document.exam.question;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * @author Galsan Bayart
 */
@Document
public class MongoQuestionState
{
  @Id
  private String id;
  private String name;
  private String tenantId;

  public MongoQuestionState(String name, String tenantId)
  {
    this.name = name;
    this.tenantId = tenantId;
  }

  public MongoQuestionState(String id, String name, String tenantId)
  {
    this.id = id;
    this.name = name;
    this.tenantId = tenantId;
  }

  public String getId()
  {
    return id;
  }

  public void setId(String id)
  {
    this.id = id;
  }

  public String getName()
  {
    return name;
  }

  public void setName(String name)
  {
    this.name = name;
  }

  public String getTenantId()
  {
    return tenantId;
  }

  public void setTenantId(String tenantId)
  {
    this.tenantId = tenantId;
  }
}
