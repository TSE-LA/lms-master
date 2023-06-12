package mn.erin.lms.base.domain.model.category;

import java.util.Objects;

import org.apache.commons.lang3.Validate;

import mn.erin.domain.base.model.Entity;
import mn.erin.lms.base.aim.organization.OrganizationId;

/**
 * @author Temuulen Naranbold
 */
public class ExamCategory implements Entity<ExamCategory>
{
  private final ExamCategoryId id;
  private final int index;
  private ExamCategoryId parentCategoryId;
  private OrganizationId organizationId;
  private String name;
  private String description;

  public ExamCategory(ExamCategoryId id, int index, ExamCategoryId parentCategoryId, OrganizationId organizationId, String name, String description)
  {
    this.id = Objects.requireNonNull(id);
    this.index = index;
    this.parentCategoryId = parentCategoryId;
    this.organizationId = Objects.requireNonNull(organizationId);
    this.name = Validate.notBlank(name);
    this.description = description;
  }

  public ExamCategory(ExamCategoryId id, int index, OrganizationId organizationId, String name, String description)
  {
    this.id = Objects.requireNonNull(id);
    this.index = index;
    this.organizationId = Objects.requireNonNull(organizationId);
    this.name = Validate.notBlank(name);
    this.description = description;
  }

  public ExamCategoryId getId()
  {
    return id;
  }

  public int getIndex()
  {
    return index;
  }

  public ExamCategoryId getParentCategoryId()
  {
    return parentCategoryId;
  }

  public void setParentCategoryId(ExamCategoryId parentCategoryId)
  {
    this.parentCategoryId = parentCategoryId;
  }

  public OrganizationId getOrganizationId()
  {
    return organizationId;
  }

  public void setOrganizationId(OrganizationId organizationId)
  {
    this.organizationId = Objects.requireNonNull(organizationId);
  }

  public String getName()
  {
    return name;
  }

  public void setName(String name)
  {
    this.name = Validate.notBlank(name);
  }

  public String getDescription()
  {
    return description;
  }

  public void setDescription(String description)
  {
    this.description = description;
  }

  @Override
  public boolean sameIdentityAs(ExamCategory other)
  {
    return this.id.sameValueAs(other.id);
  }
}
