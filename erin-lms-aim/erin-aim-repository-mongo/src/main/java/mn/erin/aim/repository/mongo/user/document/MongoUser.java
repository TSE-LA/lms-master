package mn.erin.aim.repository.mongo.user.document;

import java.time.LocalDateTime;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * @author Munkh
 */
@Document("users")
public class MongoUser
{
  @Id
  private ObjectId id;
  @Indexed
  private String tenantId;
  @Indexed
  private String status;
  @Indexed
  private String source;
  private LocalDateTime lastModified;

  public MongoUser()
  {
    // No default value
  }

  public ObjectId getId()
  {
    return id;
  }

  public MongoUser setId(ObjectId id)
  {
    this.id = id;
    return this;
  }

  public String getTenantId()
  {
    return tenantId;
  }

  public MongoUser setTenantId(String tenantId)
  {
    this.tenantId = tenantId;
    return this;
  }

  public String getStatus()
  {
    return status;
  }

  public MongoUser setStatus(String status)
  {
    this.status = status;
    return this;
  }

  public String getSource()
  {
    return source;
  }

  public MongoUser setSource(String source)
  {
    this.source = source;
    return this;
  }

  public LocalDateTime getLastModified()
  {
    return lastModified;
  }

  public MongoUser setLastModified(LocalDateTime lastModified)
  {
    this.lastModified = lastModified;
    return this;
  }

  public boolean hasState()
  {
    return status != null && source != null && lastModified != null;
  }
}
