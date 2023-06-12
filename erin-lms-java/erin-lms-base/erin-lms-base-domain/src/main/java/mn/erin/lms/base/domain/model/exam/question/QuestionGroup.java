package mn.erin.lms.base.domain.model.exam.question;

import mn.erin.domain.base.model.Entity;
import mn.erin.lms.base.aim.organization.OrganizationId;

/**
 * @author Galsan Bayart
 */
public class QuestionGroup implements Entity<QuestionGroup>
{
  private final QuestionGroupId id;
  private QuestionGroupId parentGroupId;
  private String name;
  private OrganizationId organizationId;
  private String description;

  public QuestionGroup(QuestionGroupId id, QuestionGroupId parentGroupId, String name, OrganizationId organizationId)
  {
    this.id = id;
    this.parentGroupId = parentGroupId;
    this.name = name;
    this.organizationId = organizationId;
  }

  public QuestionGroup(QuestionGroupId id, QuestionGroupId parentGroupId, String name, OrganizationId organizationId, String description)
  {
    this.id = id;
    this.parentGroupId = parentGroupId;
    this.name = name;
    this.organizationId = organizationId;
    this.description = description;
  }

  public QuestionGroupId getId()
  {
    return id;
  }

  public QuestionGroupId getParentGroupId()
  {
    return parentGroupId;
  }

  public void setParentGroupId(QuestionGroupId parentGroupId)
  {
    this.parentGroupId = parentGroupId;
  }

  public String getName()
  {
    return name;
  }

  public void setName(String name)
  {
    this.name = name;
  }

  public OrganizationId getOrganizationId()
  {
    return organizationId;
  }

  public void setOrganizationId(OrganizationId organizationId)
  {
    this.organizationId = organizationId;
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
  public boolean sameIdentityAs(QuestionGroup other)
  {
    return this.id.sameValueAs(other.id);
  }
}
