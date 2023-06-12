/*
 * (C)opyright, 2019, ERIN SYSTEMS LLC
 * The source code is protected by copyright laws and international copyright
 * treaties, as well as other intellectual property laws and treaties.
 * All rights reserved.
 */

package mn.erin.lms.legacy.domain.lms.usecase.course.get_enrolled_course_list;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import org.apache.commons.lang3.Validate;

/**
 * @author Oyungerel Chuluunsukh.
 */
public class GetEnrolledListInput
{
  private final String categoryId;
  private String publishStatus;
  private Map<String, Object> properties = new HashMap<>();
  private String enrollmentState;
  private String parentCategoryId;

  public GetEnrolledListInput(String categoryId){
    this.categoryId = categoryId;
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
    this.publishStatus = Validate.notBlank(publishStatus, "Publish status cannot be null or blank!");
  }

  public Map<String, Object> getProperties()
  {
    return Collections.unmodifiableMap(properties);
  }

  public void setProperties(final Map<String, Object> properties)
  {
    this.properties = Objects.requireNonNull(properties, "Course properties cannot be null!");
  }

  public String getEnrollmentState()
  {
    return enrollmentState;
  }

  public void setEnrollmentState(String enrollmentState)
  {
    this.enrollmentState = Validate.notBlank(enrollmentState, "Course enrollment state cannot be null or blank!");
  }

  public String getParentCategoryId()
  {
    return parentCategoryId;
  }

  public void setParentCategoryId(String parentCategoryId)
  {
    this.parentCategoryId = parentCategoryId;
  }
}
