/*
 * (C)opyright, 2019, ERIN SYSTEMS LLC
 * The source code is protected by copyright laws and international copyright
 * treaties, as well as other intellectual property laws and treaties.
 * All rights reserved.
 */

package mn.erin.lms.legacy.infrastructure.lms.rest.course;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Bat-Erdene Tsogoo.
 */
public class RestCourseDetail
{
  private String title;
  private String description;
  private String note;
  private Map<String, Object> courseProperties = new HashMap<>();
  private RestUserGroup userGroup;

  public String getTitle()
  {
    return title;
  }

  public String getDescription()
  {
    return description;
  }

  public String getNote()
  {
    return note;
  }

  public Map<String, Object> getCourseProperties()
  {
    return Collections.unmodifiableMap(courseProperties);
  }

  public RestUserGroup getUserGroup()
  {
    return userGroup;
  }

  public void setUserGroup(RestUserGroup userGroup)
  {
    this.userGroup = userGroup;
  }
}
