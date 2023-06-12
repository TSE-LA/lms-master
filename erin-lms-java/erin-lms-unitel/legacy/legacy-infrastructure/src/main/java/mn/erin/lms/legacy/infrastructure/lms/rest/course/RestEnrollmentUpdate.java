/*
 * (C)opyright, 2019, ERIN SYSTEMS LLC
 * The source code is protected by copyright laws and international copyright
 * treaties, as well as other intellectual property laws and treaties.
 * All rights reserved.
 */

package mn.erin.lms.legacy.infrastructure.lms.rest.course;

/**
 * @author Oyungerel Chuluunsukh.
 */
public class RestEnrollmentUpdate
{
  private RestUserGroup userGroup = new RestUserGroup();
  private boolean deleteProgress = false;

  public RestUserGroup getUserGroup()
  {
    return userGroup;
  }

  public void setUserGroup(RestUserGroup userGroup)
  {
    this.userGroup = userGroup;
  }

  public boolean isDeleteProgress()
  {
    return deleteProgress;
  }

  public void setDeleteProgress(boolean deleteProgress)
  {
    this.deleteProgress = deleteProgress;
  }
}
