package mn.erin.aim.repository.mongo.user.document;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * @author Munkh
 */
@Document("identities")
public class MongoUserIdentity
{
  @Id
  private ObjectId id;
  @Indexed
  private ObjectId userId;
  @Indexed
  private String username;
  private String password;
  @Indexed
  private String source;

  public MongoUserIdentity()
  {
  }

  public ObjectId getId()
  {
    return id;
  }

  public MongoUserIdentity setId(ObjectId id)
  {
    this.id = id;
    return this;
  }

  public ObjectId getUserId()
  {
    return userId;
  }

  public MongoUserIdentity setUserId(ObjectId userId)
  {
    this.userId = userId;
    return this;
  }

  public String getUsername()
  {
    return username;
  }

  public MongoUserIdentity setUsername(String username)
  {
    this.username = username;
    return this;
  }

  public String getPassword()
  {
    return password;
  }

  public MongoUserIdentity setPassword(String password)
  {
    this.password = password;
    return this;
  }

  public String getSource()
  {
    return source;
  }

  public MongoUserIdentity setSource(String source)
  {
    this.source = source;
    return this;
  }
}
