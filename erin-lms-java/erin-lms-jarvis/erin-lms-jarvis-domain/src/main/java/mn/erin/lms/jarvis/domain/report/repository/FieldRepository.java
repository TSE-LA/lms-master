package mn.erin.lms.jarvis.domain.report.repository;

import java.util.List;

import mn.erin.lms.base.aim.organization.OrganizationId;
import mn.erin.lms.jarvis.domain.report.model.Field;
import mn.erin.lms.jarvis.domain.report.usecase.dto.FieldType;

/**
 * @author Temuulen Naranbold
 */
public interface FieldRepository
{
  Field create(OrganizationId organizationId, String name, FieldType type, boolean required);

  List<Field> getAllFields(OrganizationId organizationId);

  boolean exists(OrganizationId organizationId, String name);
}
