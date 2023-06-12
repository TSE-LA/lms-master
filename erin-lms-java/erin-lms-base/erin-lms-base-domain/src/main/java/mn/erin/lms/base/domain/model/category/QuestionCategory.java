package mn.erin.lms.base.domain.model.category;

import java.util.Objects;

import org.apache.commons.lang3.Validate;

import mn.erin.domain.base.model.Entity;
import mn.erin.lms.base.aim.organization.OrganizationId;

/**
 * @author Temuulen Naranbold
 */
public class QuestionCategory implements Entity<QuestionCategory>
{
  private final QuestionCategoryId id;
  private final QuestionCategoryId parentCategoryId;
  private final OrganizationId organizationId;
  private int index;
  private String name;
  private String description;

  public QuestionCategory(QuestionCategoryId id, QuestionCategoryId parentCategoryId,
      OrganizationId organizationId, int index, String name, String description)
  {
    this.id = Objects.requireNonNull(id);
    this.parentCategoryId = parentCategoryId;
    this.organizationId = Objects.requireNonNull(organizationId);
    this.index = index;
    this.name = Validate.notBlank(name);
    this.description = description;
  }

  public QuestionCategoryId getId()
  {
    return id;
  }

  public QuestionCategoryId getParentCategoryId()
  {
    return parentCategoryId;
  }

  public OrganizationId getOrganizationId()
  {
    return organizationId;
  }

  public int getIndex()
  {
    return index;
  }

  public void setIndex(int index)
  {
    this.index = index;
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
  public boolean sameIdentityAs(QuestionCategory other)
  {
    return this.id.sameValueAs(other.id);
  }
}
