package mn.erin.aim.repository.mongo.user.document;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * @author Munkh
 */
@Document("profiles")
public class MongoUserProfile
{
  @Id
  private ObjectId id;
  @Indexed
  private ObjectId userId;
  @Indexed
  private MongoUserInfo info;
  private MongoUserContact contact;

  public MongoUserProfile()
  {
    // No default value
  }

  public ObjectId getId()
  {
    return id;
  }

  public MongoUserProfile setId(ObjectId id)
  {
    this.id = id;
    return this;
  }

  public ObjectId getUserId()
  {
    return userId;
  }

  public MongoUserProfile setUserId(ObjectId userId)
  {
    this.userId = userId;
    return this;
  }

  public MongoUserInfo getInfo()
  {
    return info;
  }

  public MongoUserProfile setInfo(MongoUserInfo info)
  {
    this.info = info;
    return this;
  }

  public MongoUserContact getContact()
  {
    return contact;
  }

  public MongoUserProfile setContact(MongoUserContact contact)
  {
    this.contact = contact;
    return this;
  }
}
