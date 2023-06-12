package mn.erin.domain.aim.model.user;

import mn.erin.domain.base.model.ValueObject;

/**
 * @author Zorig
 */
public class UserContact implements ValueObject<UserContact>
{
  private String email;
  private String phoneNumber;

  public UserContact()
  {
    this("", "");
  }

  public UserContact(String email, String phoneNumber)
  {
    this.email = email;
    this.phoneNumber = phoneNumber;
  }

  public String getEmail()
  {
    return email;
  }

  public void setEmail(String email)
  {
    this.email = email;
  }

  public String getPhoneNumber()
  {
    return phoneNumber;
  }

  public void setPhoneNumber(String phoneNumber)
  {
    this.phoneNumber = phoneNumber;
  }

  @Override
  public boolean sameValueAs(UserContact other)
  {
    return false;
  }
}
