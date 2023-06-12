/*
 * (C)opyright, 2018, ERIN SYSTEMS LLC
 * The source code is protected by copyright laws and international copyright
 * treaties, as well as other intellectual property laws and treaties.
 * All rights reserved.
 */

package mn.erin.lms.legacy.domain.lms.usecase.course.update_course;

import java.util.Map;
import java.util.Objects;

import org.apache.commons.lang3.Validate;

/**
 * author Tamir Batmagnai.
 */
public class UpdateCourseInput
{
  private final String courseId;
  private final String categoryId;

  private final String title;
  private final Map<String, Object> properties;
  private String description;
  private String note;

  public UpdateCourseInput(String courseId, String categoryId, String title, Map<String, Object> properties)
  {
    this.courseId = Validate.notBlank(courseId, "Course id cannot be null!");
    this.categoryId = Validate.notBlank(categoryId, "Course category id cannot be null!");
    this.title = Validate.notBlank(title, "Title cannot be null!");
    this.properties = Objects.requireNonNull(properties, "Properties cannot be null!");
  }

  public String getCourseId()
  {
    return courseId;
  }

  public String getCategoryId()
  {
    return categoryId;
  }

  public String getTitle()
  {
    return title;
  }

  public Map<String, Object> getProperties()
  {
    return properties;
  }

  public String getDescription()
  {
    return description;
  }

  public String getNote()
  {
    return note;
  }

  public void setNote(String note)
  {
    this.note = note;
  }

  public void setDescription(String description)
  {
    this.description = description;
  }
}
