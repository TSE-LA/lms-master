/*
 * (C)opyright, 2019, ERIN SYSTEMS LLC
 * The source code is protected by copyright laws and international copyright
 * treaties, as well as other intellectual property laws and treaties.
 * All rights reserved.
 */

package mn.erin.lms.base.rest.api.aim.model;

/**
 * @author Bat-Erdene Tsogoo.
 */
public class RestGroupUser
{
  private String userId;
  private String firstName;
  private String lastName;

  public RestGroupUser(String userId, String firstName, String lastName)
  {
    this.userId = userId;
    this.firstName = firstName;
    this.lastName = lastName;
  }

  public String getUserId()
  {
    return userId;
  }

  public String getFirstName()
  {
    return firstName;
  }

  public String getLastName()
  {
    return lastName;
  }
}
