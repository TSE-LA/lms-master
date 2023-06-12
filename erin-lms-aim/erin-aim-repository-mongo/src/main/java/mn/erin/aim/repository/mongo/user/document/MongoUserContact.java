package mn.erin.aim.repository.mongo.user.document;

import org.springframework.data.mongodb.core.index.Indexed;

/**
 * @author Munkh
 */
public class MongoUserContact
{
  @Indexed
  private String email;
  @Indexed
  private String phoneNumber;

  public String getEmail()
  {
    return email;
  }

  public MongoUserContact setEmail(String email)
  {
    this.email = email;
    return this;
  }

  public String getPhoneNumber()
  {
    return phoneNumber;
  }

  public MongoUserContact setPhoneNumber(String phoneNumber)
  {
    this.phoneNumber = phoneNumber;
    return this;
  }
}
