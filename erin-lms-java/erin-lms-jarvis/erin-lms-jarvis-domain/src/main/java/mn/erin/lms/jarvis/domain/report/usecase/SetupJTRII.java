package mn.erin.lms.jarvis.domain.report.usecase;

import org.apache.commons.lang3.Validate;

import mn.erin.domain.base.usecase.UseCase;
import mn.erin.domain.base.usecase.UseCaseException;
import mn.erin.lms.base.aim.organization.OrganizationId;
import mn.erin.lms.jarvis.domain.report.repository.FieldRepository;
import mn.erin.lms.jarvis.domain.report.usecase.dto.FieldType;

/**
 * @author Temuulen Naranbold
 */
public class SetupJTRII implements UseCase<String, Boolean>
{
  private static final String JURISDICTIONAL_COURT_FIELD = "jurisdictionalCourt";
  private static final String JOB_TITLE = "jobTitle";
  private static final String APPOINTED_DATE = "appointedDate";
  private static final String JOB_YEAR = "jobYear";
  private final FieldRepository fieldRepository;

  public SetupJTRII(FieldRepository fieldRepository)
  {
    this.fieldRepository = fieldRepository;
  }

  @Override
  public Boolean execute(String input) throws UseCaseException
  {
    try
    {
      Validate.notBlank(input);
      OrganizationId organizationId = OrganizationId.valueOf(input);
      if (!fieldRepository.exists(organizationId, JURISDICTIONAL_COURT_FIELD))
      {
        fieldRepository.create(organizationId, JURISDICTIONAL_COURT_FIELD, FieldType.INPUT, true);
      }
      if (!fieldRepository.exists(organizationId, JOB_TITLE))
      {
        fieldRepository.create(organizationId, JOB_TITLE, FieldType.SELECT, false);
      }
      if (!fieldRepository.exists(organizationId, APPOINTED_DATE))
      {
        fieldRepository.create(organizationId, APPOINTED_DATE, FieldType.DATE, true);
      }
      if (!fieldRepository.exists(organizationId, JOB_YEAR))
      {
        fieldRepository.create(organizationId, JOB_YEAR, FieldType.INPUT, false);
      }

      return true;
    }
    catch (IllegalArgumentException | NullPointerException e)
    {
      throw new UseCaseException(e.getMessage());
    }
  }
}
