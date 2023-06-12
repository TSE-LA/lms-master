/*
 * (C)opyright, 2019, ERIN SYSTEMS LLC
 * The source code is protected by copyright laws and international copyright
 * treaties, as well as other intellectual property laws and treaties.
 * All rights reserved.
 */

package mn.erin.lms.base.rest.api.aim.model;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Bat-Erdene Tsogoo.
 */
public class RestUserBody
{
  private String username;
  private String password;
  private String firstName;
  private String lastName;
  private String email;
  private String phoneNumber;
  private String gender;
  private String jobTitle;
  private String birthday;
  private Map<String, String> properties = new HashMap<>();

  private String newPassword;
  private String oldPassword;

  public String getFirstName()
  {
    return firstName;
  }

  public void setFirstName(String firstName)
  {
    this.firstName = firstName;
  }

  public String getLastName()
  {
    return lastName;
  }

  public void setLastName(String lastName)
  {
    this.lastName = lastName;
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

  public String getUsername()
  {
    return username;
  }

  public void setUsername(String username)
  {
    this.username = username;
  }

  public String getPassword()
  {
    return password;
  }

  public void setPassword(String password)
  {
    this.password = password;
  }

  public String getGender()
  {
    return gender;
  }

  public void setGender(String gender)
  {
    this.gender = gender;
  }

  public String getJobTitle() { return jobTitle; }

  public void setJobTitle(String jobTitle) { this.jobTitle = jobTitle; }

  public String getBirthday()
  {
    return birthday;
  }

  public void setBirthday(String birthday)
  {
    this.birthday = birthday;
  }

  public Map<String, String> getProperties()
  {
    return properties;
  }

  public void setProperties(Map<String, String> properties)
  {
    this.properties = properties;
  }

  public String getNewPassword()
  {
    return newPassword;
  }

  public void setNewPassword(String newPassword)
  {
    this.newPassword = newPassword;
  }

  public String getOldPassword()
  {
    return oldPassword;
  }

  public void setOldPassword(String oldPassword)
  {
    this.oldPassword = oldPassword;
  }
}
