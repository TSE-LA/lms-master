package mn.erin.lms.base.domain.usecase.exam.question;

import java.util.Objects;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;

import mn.erin.domain.base.usecase.UseCaseException;
import mn.erin.lms.base.aim.user.Author;
import mn.erin.lms.base.aim.user.Instructor;
import mn.erin.lms.base.aim.user.Manager;
import mn.erin.lms.base.aim.user.Supervisor;
import mn.erin.lms.base.domain.model.Authorized;
import mn.erin.lms.base.domain.model.exam.question.QuestionGroup;
import mn.erin.lms.base.domain.model.exam.question.QuestionGroupId;
import mn.erin.lms.base.domain.repository.LmsRepositoryException;
import mn.erin.lms.base.domain.repository.LmsRepositoryRegistry;
import mn.erin.lms.base.domain.repository.QuestionGroupRepository;
import mn.erin.lms.base.domain.service.LmsServiceRegistry;
import mn.erin.lms.base.domain.usecase.LmsUseCase;
import mn.erin.lms.base.domain.usecase.exam.dto.question.QuestionGroupInput;

/**
 * @author Galsan Bayart.
 */
@Authorized(users = { Author.class, Manager.class, Instructor.class, Supervisor.class })
public class UpdateQuestionGroup extends LmsUseCase<QuestionGroupInput, String>
{
  private final QuestionGroupRepository questionGroupRepository;

  public UpdateQuestionGroup(LmsRepositoryRegistry lmsRepositoryRegistry, LmsServiceRegistry lmsServiceRegistry)
  {
    super(Objects.requireNonNull(lmsRepositoryRegistry), Objects.requireNonNull(lmsServiceRegistry));
    this.questionGroupRepository = lmsRepositoryRegistry.getQuestionGroupRepository();
  }

  @Override
  protected String executeImpl(QuestionGroupInput input) throws UseCaseException
  {
    try
    {
      Validate.notNull(input);

      QuestionGroup questionGroup = questionGroupRepository.findById(QuestionGroupId.valueOf(input.getId()));

      questionGroup.setParentGroupId(StringUtils.isBlank(input.getParentId()) ? null : QuestionGroupId.valueOf(input.getParentId()));

      if (!StringUtils.isBlank(input.getName()))
      {
        questionGroup.setName(input.getName());
      }
      if (!StringUtils.isBlank(input.getDescription()))
      {
        questionGroup.setDescription(input.getDescription());
      }

      return questionGroupRepository.update(questionGroup);
    }
    catch (LmsRepositoryException | NullPointerException e)
    {
      throw new UseCaseException(e.getMessage(), e);
    }
  }
}
