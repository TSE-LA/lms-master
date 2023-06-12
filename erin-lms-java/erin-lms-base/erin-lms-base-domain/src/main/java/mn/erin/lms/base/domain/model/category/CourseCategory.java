package mn.erin.lms.base.domain.model.category;

import java.util.Objects;

import org.apache.commons.lang3.Validate;

import mn.erin.domain.base.model.Entity;
import mn.erin.lms.base.aim.organization.OrganizationId;

/**
 * @author Bat-Erdene Tsogoo.
 */
public class CourseCategory implements Entity<CourseCategory>
{
  private final OrganizationId organizationId;
  private final CourseCategoryId courseCategoryId;
  private final CourseCategoryId parentCategoryId;
  private String name;

  private String description;
  private boolean autoEnroll;

  public CourseCategory(OrganizationId organizationId, CourseCategoryId courseCategoryId, CourseCategoryId parentCategoryId, String name, boolean autoEnroll)
  {
    this.organizationId = Objects.requireNonNull(organizationId, "Organization ID cannot be null!");
    this.courseCategoryId = Objects.requireNonNull(courseCategoryId, "Course category ID cannot be null!");
    this.name = Validate.notBlank(name, "Category name cannot be null or blank!");
    this.parentCategoryId = parentCategoryId;
    this.autoEnroll = autoEnroll;
  }

  public String getDescription()
  {
    return description;
  }

  public void setDescription(String description)
  {
    this.description = description;
  }

  public boolean isAutoEnroll()
  {
    return autoEnroll;
  }

  public void setAutoEnroll(boolean autoEnroll)
  {
    this.autoEnroll = autoEnroll;
  }

  public OrganizationId getOrganizationId()
  {
    return organizationId;
  }

  public CourseCategoryId getCourseCategoryId()
  {
    return courseCategoryId;
  }

  public CourseCategoryId getParentCategoryId()
  {
    return parentCategoryId;
  }

  public void setName(String name)
  {
    this.name = name;
  }

  public String getName()
  {
    return name;
  }

  @Override
  public boolean sameIdentityAs(CourseCategory other)
  {
    return this.courseCategoryId.equals(other.courseCategoryId);
  }
}
