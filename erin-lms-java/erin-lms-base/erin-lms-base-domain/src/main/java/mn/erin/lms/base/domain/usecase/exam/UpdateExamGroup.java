package mn.erin.lms.base.domain.usecase.exam;

import java.util.Objects;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;

import mn.erin.domain.base.usecase.UseCaseException;
import mn.erin.lms.base.aim.user.Author;
import mn.erin.lms.base.aim.user.Instructor;
import mn.erin.lms.base.aim.user.Manager;
import mn.erin.lms.base.aim.user.Supervisor;
import mn.erin.lms.base.domain.model.Authorized;
import mn.erin.lms.base.domain.model.exam.ExamGroup;
import mn.erin.lms.base.domain.model.exam.ExamGroupId;
import mn.erin.lms.base.domain.repository.LmsRepositoryException;
import mn.erin.lms.base.domain.repository.LmsRepositoryRegistry;
import mn.erin.lms.base.domain.service.LmsServiceRegistry;
import mn.erin.lms.base.domain.usecase.LmsUseCase;
import mn.erin.lms.base.domain.usecase.exam.dto.ExamGroupInput;

/**
 * @author Galsan Bayart.
 */
@Authorized(users = { Author.class, Manager.class, Instructor.class, Supervisor.class })
public class UpdateExamGroup extends LmsUseCase<ExamGroupInput, String>
{
  public UpdateExamGroup(LmsRepositoryRegistry lmsRepositoryRegistry, LmsServiceRegistry lmsServiceRegistry)
  {
    super(Objects.requireNonNull(lmsRepositoryRegistry), Objects.requireNonNull(lmsServiceRegistry));
  }

  @Override
  protected String executeImpl(ExamGroupInput input) throws UseCaseException
  {
    try
    {
      Validate.notNull(input);
      Validate.notBlank(input.getName());

      ExamGroup examGroup = lmsRepositoryRegistry.getExamGroupRepository().findById(ExamGroupId.valueOf(input.getId()));

      examGroup.setParentId(input.getParentId());

      if (!StringUtils.isBlank(input.getName()))
      {
        examGroup.setName(input.getName());
      }
      else
      {
        throw new UseCaseException("Group name cannot be null!!");
      }
      if (!StringUtils.isBlank(input.getDescription()))
      {
        examGroup.setDescription(input.getDescription());
      }

      return lmsRepositoryRegistry.getExamGroupRepository().update(examGroup);
    }
    catch (NullPointerException | IllegalArgumentException | LmsRepositoryException e)
    {
      throw new UseCaseException(e.getMessage(), e);
    }
  }
}
