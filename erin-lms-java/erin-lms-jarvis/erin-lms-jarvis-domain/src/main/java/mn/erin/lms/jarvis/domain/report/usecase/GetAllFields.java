package mn.erin.lms.jarvis.domain.report.usecase;

import java.util.List;

import org.apache.commons.lang3.Validate;

import mn.erin.domain.base.usecase.UseCase;
import mn.erin.domain.base.usecase.UseCaseException;
import mn.erin.lms.base.aim.organization.OrganizationId;
import mn.erin.lms.jarvis.domain.report.model.Field;
import mn.erin.lms.jarvis.domain.report.repository.FieldRepository;

/**
 * @author Temuulen Naranbold
 */
public class GetAllFields implements UseCase<String, List<Field>>
{
  private final FieldRepository fieldRepository;

  public GetAllFields(FieldRepository fieldRepository)
  {
    this.fieldRepository = fieldRepository;
  }

  @Override
  public List<Field> execute(String organizationId) throws UseCaseException
  {
    try
    {
      Validate.notBlank(organizationId);
      return fieldRepository.getAllFields(OrganizationId.valueOf(organizationId));
    }
    catch (IllegalArgumentException | NullPointerException e)
    {
      throw new UseCaseException(e.getMessage());
    }
  }
}
