/*
 * (C)opyright, 2019, ERIN SYSTEMS LLC
 * The source code is protected by copyright laws and international copyright
 * treaties, as well as other intellectual property laws and treaties.
 * All rights reserved.
 */

package mn.erin.lms.legacy.infrastructure.lms.rest.course;

import java.util.Date;

/**
 * @author Bat-Erdene Tsogoo.
 */
public class PublishRequestModel
{
  private RestUserGroup userGroup;
  private NotificationRequestModel notificationRequestModel;
  private Date publishDate;

  public RestUserGroup getUserGroups()
  {
    return userGroup;
  }

  public void setUserGroup(RestUserGroup userGroup)
  {
    this.userGroup = userGroup;
  }

  public Date getPublishDate()
  {
    return publishDate;
  }

  public NotificationRequestModel getNotificationRequestModel()
  {
    return notificationRequestModel;
  }
}
