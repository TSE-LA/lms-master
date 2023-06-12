package mn.erin.lms.jarvis.domain.report.model;

import mn.erin.lms.base.aim.organization.OrganizationId;
import mn.erin.lms.jarvis.domain.report.usecase.dto.FieldType;

/**
 * @author Temuulen Naranbold
 */
public class Field
{
  private final String id;
  private OrganizationId organizationId;
  private String name;
  private FieldType type;
  private boolean required;

  public Field(String id, OrganizationId organizationId, String name, FieldType type, boolean required)
  {
    this.id = id;
    this.organizationId = organizationId;
    this.name = name;
    this.type = type;
    this.required = required;
  }

  public String getId()
  {
    return id;
  }

  public OrganizationId getOrganizationId()
  {
    return organizationId;
  }

  public void setOrganizationId(OrganizationId organizationId)
  {
    this.organizationId = organizationId;
  }

  public String getName()
  {
    return name;
  }

  public void setName(String name)
  {
    this.name = name;
  }

  public FieldType getType()
  {
    return type;
  }

  public void setType(FieldType type)
  {
    this.type = type;
  }

  public boolean isRequired()
  {
    return required;
  }

  public void setRequired(boolean required)
  {
    this.required = required;
  }
}
