/*
 * (C)opyright, 2020, ERIN SYSTEMS LLC
 * The source code is protected by copyright laws and international copyright
 * treaties, as well as other intellectual property laws and treaties.
 * All rights reserved.
 */

package mn.erin.domain.aim.model;

import mn.erin.domain.aim.model.permission.Permission;
import org.junit.Assert;
import org.junit.Test;

/**
 * @author EBazarragchaa
 */
public class PermissionTest
{
  private static final String APPLICATION_ID = "ees";
  private static final String MODULE_ID = "dms";
  private static final String ACTION_ID = "createFolder";

  @Test(expected = NullPointerException.class)
  public void ctorThrowsExceptionApplicationIdNull()
  {
    Permission.valueOf(null, MODULE_ID, ACTION_ID);
  }

  @Test(expected = IllegalArgumentException.class)
  public void ctorThrowsExceptionApplicationIdEmpty()
  {
    Permission.valueOf("", MODULE_ID, ACTION_ID);
  }

  @Test(expected = NullPointerException.class)
  public void ctorThrowsExceptionModuleIdNull()
  {
    Permission.valueOf(APPLICATION_ID, null, ACTION_ID);
  }

  @Test(expected = IllegalArgumentException.class)
  public void ctorThrowsExceptionModuleIdEmpty()
  {
    Permission.valueOf(APPLICATION_ID, "", ACTION_ID);
  }

  @Test(expected = NullPointerException.class)
  public void ctorThrowsExceptionActionIdNull()
  {
    Permission.valueOf(APPLICATION_ID, MODULE_ID, null);
  }

  @Test(expected = IllegalArgumentException.class)
  public void ctorThrowsExceptionActionIdEmpty()
  {
    Permission.valueOf(APPLICATION_ID, MODULE_ID, "");
  }

  @Test
  public void getPermissionStringReturnsValidString()
  {
    Permission permission = Permission.valueOf(APPLICATION_ID, MODULE_ID, ACTION_ID);

    Assert.assertEquals("ees.dms.createFolder", permission.getPermissionString());
  }

  @Test
  public void wildcardActionIdAllowed()
  {
    Permission permission = Permission.valueOf(APPLICATION_ID, MODULE_ID, "*");

    Assert.assertEquals("ees.dms.*", permission.getPermissionString());
  }

  @Test
  public void wildcardModuleIdAllowed()
  {
    Permission permission = Permission.valueOf(APPLICATION_ID, "*", "*");

    Assert.assertEquals("ees.*.*", permission.getPermissionString());
  }

  @Test
  public void checkSamePermissionValue()
  {
    Permission permission1 = Permission.valueOf(APPLICATION_ID, MODULE_ID, ACTION_ID);
    Permission permission2 = Permission.valueOf(APPLICATION_ID, MODULE_ID, ACTION_ID);

    Assert.assertTrue(permission1.sameValueAs(permission2));
  }
}
