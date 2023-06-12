/*
 * (C)opyright, 2019, ERIN SYSTEMS LLC
 * The source code is protected by copyright laws and international copyright
 * treaties, as well as other intellectual property laws and treaties.
 * All rights reserved.
 */

package mn.erin.domain.aim.usecase.authentication;

import org.apache.commons.lang3.Validate;

/**
 * @author EBazarragchaa
 */
public final class LoginUserInput
{
  private final String tenantId;
  private final String username;
  private final String password;

  public LoginUserInput(String tenantId, String username, String password)
  {
    this.tenantId = Validate.notBlank(tenantId, "Tenant ID is required!");
    this.username = Validate.notBlank(username, "User username is required!");
    this.password = Validate.notBlank(password, "User password is required!");
  }

  public String getTenantId()
  {
    return tenantId;
  }

  public String getUsername()
  {
    return username;
  }

  public String getPassword()
  {
    return password;
  }
}
