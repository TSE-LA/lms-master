/*
 * (C)opyright, 2019, ERIN SYSTEMS LLC
 * The source code is protected by copyright laws and international copyright
 * treaties, as well as other intellectual property laws and treaties.
 * All rights reserved.
 */

package mn.erin.lms.legacy.domain.lms.usecase.course.get_course_list;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * @author Oyungerel Chuluunsukh.
 */
public class GetCourseListInput
{
  private final String categoryId;
  private String publishStatus = "";
  private Map<String, Object> properties = new HashMap<>();

  public GetCourseListInput(String categoryId)
  {
    this.categoryId = Objects.requireNonNull(categoryId, "Course category ID cannot be null or blank!");
  }

  public String getCategoryId()
  {
    return categoryId;
  }

  public String getPublishStatus()
  {
    return publishStatus;
  }

  public void setPublishStatus(String publishStatus)
  {
    this.publishStatus = publishStatus;
  }

  public Map<String, Object> getProperties()
  {
    return Collections.unmodifiableMap(properties);
  }

  public void setProperties(final Map<String, Object> properties)
  {
    this.properties = Objects.requireNonNull(properties, "Course properties cannot be null!");
  }
}
