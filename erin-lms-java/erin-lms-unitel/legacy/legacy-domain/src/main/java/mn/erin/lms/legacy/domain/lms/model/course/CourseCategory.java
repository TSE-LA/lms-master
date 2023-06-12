/*
 * (C)opyright, 2019, ERIN SYSTEMS LLC
 * The source code is protected by copyright laws and international copyright
 * treaties, as well as other intellectual property laws and treaties.
 * All rights reserved.
 */

package mn.erin.lms.legacy.domain.lms.model.course;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.apache.commons.lang3.Validate;

import mn.erin.domain.base.model.Entity;

/**
 * @author Oyungerel Chuluunsukh.
 */
public class CourseCategory implements Entity<CourseCategory>
{
  public static final String ROOT_CATEGORY = "-root-";

  private final CompanyId companyId;
  private final CourseCategoryId parentCategoryId;
  private final CourseCategoryId id;
  private final String name;

  private List<CourseCategoryId> subCategories = new ArrayList<>();

  private String description;

  public CourseCategory(CompanyId companyId, CourseCategoryId parentCategoryId, CourseCategoryId id, String name)
  {
    this.companyId = Objects.requireNonNull(companyId, "Company ID cannot be null!");
    this.parentCategoryId = Objects.requireNonNull(parentCategoryId, "Parent category ID cannot be null!");
    this.id = Objects.requireNonNull(id, "Course category ID cannot be null!");
    this.name = Validate.notBlank(name, "Category name cannot be blank or null!");
  }

  @Override
  public boolean sameIdentityAs(CourseCategory other)
  {
    return other != null && this.id.equals(other.id);
  }

  public void addSubcategory(CourseCategoryId subCategoryId)
  {
    if (subCategoryId != null && !this.subCategories.contains(subCategoryId))
    {
      this.subCategories.add(subCategoryId);
    }
  }

  public boolean canHaveCourse()
  {
    return this.subCategories.isEmpty();
  }

  public CompanyId getCompanyId()
  {
    return companyId;
  }

  public CourseCategoryId getParentCategoryId()
  {
    return parentCategoryId;
  }

  public CourseCategoryId getCategoryId()
  {
    return id;
  }

  public String getName()
  {
    return name;
  }

  public List<CourseCategoryId> getSubCategories()
  {
    return subCategories;
  }

  public String getDescription()
  {
    return description;
  }

  public void setDescription(String description)
  {
    this.description = description;
  }
}
