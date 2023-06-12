/*
 * (C)opyright, 2019, ERIN SYSTEMS LLC
 * The source code is protected by copyright laws and international copyright
 * treaties, as well as other intellectual property laws and treaties.
 * All rights reserved.
 */

package mn.erin.lms.legacy.domain.lms.usecase.course.create_course;

import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import org.apache.commons.lang3.Validate;

public class CreateCourseInput
{
  private final String title;
  private final String categoryId;
  private final Map<String, Object> properties;

  private Set<String> users = new HashSet<>();
  private Set<String> groups = new HashSet<>();

  private String description;

  public CreateCourseInput(String title, String categoryId, Map<String, Object> properties)
  {
    this.title = Validate.notBlank(title, "Course title cannot be null or blank!");
    this.categoryId = Validate.notBlank(categoryId, "Course category ID cannot be null or blank!");
    this.properties = Objects.requireNonNull(properties, "Properties cannot be null!");
  }

  public String getTitle()
  {
    return title;
  }

  public Map<String, Object> getProperties()
  {
    return Collections.unmodifiableMap(properties);
  }

  public String getDescription()
  {
    return description;
  }

  public String getCategoryId()
  {
    return categoryId;
  }

  public void setDescription(String description)
  {
    this.description = description;
  }

  public void setUsers(Set<String> users)
  {
    this.users = users;
  }

  public void setGroups(Set<String> groups)
  {
    this.groups = groups;
  }

  public Set<String> getUsers()
  {
    return users;
  }

  public Set<String> getGroups()
  {
    return groups;
  }
}
