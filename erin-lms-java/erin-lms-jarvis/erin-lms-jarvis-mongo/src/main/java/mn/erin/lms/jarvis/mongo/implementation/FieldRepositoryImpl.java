package mn.erin.lms.jarvis.mongo.implementation;

import java.util.List;
import java.util.stream.Collectors;

import mn.erin.lms.base.aim.organization.OrganizationId;
import mn.erin.lms.jarvis.domain.report.model.Field;
import mn.erin.lms.jarvis.domain.report.repository.FieldRepository;
import mn.erin.lms.jarvis.domain.report.usecase.dto.FieldType;
import mn.erin.lms.jarvis.mongo.document.report.MongoField;
import mn.erin.lms.jarvis.mongo.repository.MongoFieldRepository;

/**
 * @author Temuulen Naranbold
 */
public class FieldRepositoryImpl implements FieldRepository
{
  private final MongoFieldRepository mongoFieldRepository;
  public FieldRepositoryImpl(MongoFieldRepository mongoFieldRepository)
  {
    this.mongoFieldRepository = mongoFieldRepository;
  }

  @Override
  public Field create(OrganizationId organizationId, String name, FieldType type, boolean required)
  {
    MongoField mongoField = new MongoField(organizationId.getId(), name, type.name(), required);
    return mapToField(mongoFieldRepository.save(mongoField));
  }

  @Override
  public List<Field> getAllFields(OrganizationId organizationId)
  {
    return mongoFieldRepository.findAllByOrganizationId(organizationId.getId()).stream().map(this::mapToField).collect(Collectors.toList());
  }

  @Override
  public boolean exists(OrganizationId organizationId, String name)
  {
    return !mongoFieldRepository.findAllByOrganizationIdAndName(organizationId.getId(), name).isEmpty();
  }

  private Field mapToField(MongoField mongoField)
  {
    return new Field(
        mongoField.getId().toHexString(),
        OrganizationId.valueOf(mongoField.getOrganizationId()),
        mongoField.getName(),
        FieldType.valueOf(mongoField.getType()),
        mongoField.isRequired()
    );
  }
}
