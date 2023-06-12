package mn.erin.lms.base.domain.usecase.exam.question;

import java.util.List;

import org.apache.commons.lang3.Validate;

import mn.erin.domain.base.usecase.UseCaseException;
import mn.erin.lms.base.aim.user.Author;
import mn.erin.lms.base.aim.user.Instructor;
import mn.erin.lms.base.aim.user.Manager;
import mn.erin.lms.base.aim.user.Supervisor;
import mn.erin.lms.base.domain.model.Authorized;
import mn.erin.lms.base.domain.model.exam.question.Question;
import mn.erin.lms.base.domain.model.exam.question.QuestionGroup;
import mn.erin.lms.base.domain.model.exam.question.QuestionGroupId;
import mn.erin.lms.base.domain.repository.LmsRepositoryException;
import mn.erin.lms.base.domain.repository.LmsRepositoryRegistry;
import mn.erin.lms.base.domain.service.LmsServiceRegistry;
import mn.erin.lms.base.domain.usecase.LmsUseCase;

/**
 * @author Temuulen Naranbold
 */
@Authorized(users = { Author.class, Manager.class, Instructor.class, Supervisor.class })
public class DeleteQuestionGroup extends LmsUseCase<String, Boolean>
{
  public DeleteQuestionGroup(LmsRepositoryRegistry lmsRepositoryRegistry, LmsServiceRegistry lmsServiceRegistry)
  {
    super(lmsRepositoryRegistry, lmsServiceRegistry);
  }

  @Override
  protected Boolean executeImpl(String input) throws UseCaseException
  {
    try
    {
      Validate.notBlank(input);

      QuestionGroup groupToDelete = lmsRepositoryRegistry.getQuestionGroupRepository().findById(QuestionGroupId.valueOf(input));

      if (groupToDelete == null)
      {
        throw new UseCaseException("Could not find question group with ID: " + input);
      }

      List<Question> questionsUnderGroup = lmsRepositoryRegistry.getQuestionRepository().getAllActive(groupToDelete.getId());
      if (questionsUnderGroup != null && !questionsUnderGroup.isEmpty())
      {
        throw new UseCaseException("Please delete all the questions under the group ID of: " + groupToDelete.getId().getId());
      }

      List<QuestionGroup> childGroups = lmsRepositoryRegistry.getQuestionGroupRepository().getAll(groupToDelete.getId().getId(), lmsServiceRegistry.getOrganizationIdProvider().getOrganizationId());
      if (childGroups != null && !childGroups.isEmpty())
      {
        throw new UseCaseException("Deleting group has child groups!");
      }

      return lmsRepositoryRegistry.getQuestionGroupRepository().delete(input);
    }
    catch (UseCaseException | NullPointerException | IllegalArgumentException | LmsRepositoryException e)
    {
      throw new UseCaseException(e.getMessage(), e);
    }
  }
}
