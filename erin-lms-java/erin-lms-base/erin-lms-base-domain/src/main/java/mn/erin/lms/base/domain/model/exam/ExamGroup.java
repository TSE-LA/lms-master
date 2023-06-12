package mn.erin.lms.base.domain.model.exam;

import org.apache.commons.lang3.Validate;

import mn.erin.domain.base.model.Entity;
import mn.erin.lms.base.aim.organization.OrganizationId;

/**
 * @author Galsan Bayart.
 */
public class ExamGroup implements Entity<ExamGroup>
{
  private final ExamGroupId id;
  private String parentId;
  private String name;
  private OrganizationId organizationId;
  private String description;

  public ExamGroup(ExamGroupId id, String parentId, String name, OrganizationId organizationId, String description)
  {
    this.id = Validate.notNull(id);
    this.parentId = parentId;
    this.name = Validate.notBlank(name);
    this.organizationId = Validate.notNull(organizationId);
    this.description = description;
  }

  public ExamGroupId getId()
  {
    return id;
  }

  public String getParentId()
  {
    return parentId;
  }

  public void setParentId(String parentId)
  {
    this.parentId = parentId;
  }

  public String getName()
  {
    return name;
  }

  public void setName(String name)
  {
    this.name = Validate.notBlank(name);
  }

  public OrganizationId getOrganizationId()
  {
    return organizationId;
  }

  public void setOrganizationId(OrganizationId organizationId)
  {
    this.organizationId = Validate.notNull(organizationId);
  }

  public String getDescription()
  {
    return description;
  }

  public void setDescription(String description)
  {
    this.description = description;
  }

  public boolean isRoot()
  {
    return this.parentId == null;
  }

  @Override
  public boolean sameIdentityAs(ExamGroup other)
  {
    return other != null && this.id.equals(other.id);
  }

}
