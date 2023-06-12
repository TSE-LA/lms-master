package mn.erin.lms.base.domain.usecase.exam.question;

import org.apache.commons.lang3.Validate;

import mn.erin.domain.base.usecase.UseCaseException;
import mn.erin.lms.base.aim.user.Author;
import mn.erin.lms.base.aim.user.Instructor;
import mn.erin.lms.base.domain.model.Authorized;
import mn.erin.lms.base.domain.model.category.QuestionCategoryId;
import mn.erin.lms.base.domain.model.exam.question.QuestionGroupId;
import mn.erin.lms.base.domain.repository.LmsRepositoryException;
import mn.erin.lms.base.domain.repository.LmsRepositoryRegistry;
import mn.erin.lms.base.domain.service.LmsServiceRegistry;
import mn.erin.lms.base.domain.usecase.exam.dto.question.GetQuestionInput;

/**
 * @author Temuulen Naranbold
 **/

@Authorized(users = { Author.class, Instructor.class })
public class GetQuestionCount extends QuestionUseCase<GetQuestionInput, Integer>
{
  public GetQuestionCount(LmsRepositoryRegistry lmsRepositoryRegistry, LmsServiceRegistry lmsServiceRegistry)
  {
    super(lmsRepositoryRegistry, lmsServiceRegistry);
  }

  @Override
  protected Integer executeImpl(GetQuestionInput input) throws UseCaseException
  {
    try
    {
      Validate.notNull(input);

      if (input.getCategoryId() != null && input.getGroupId() != null)
      {
        return lmsRepositoryRegistry.getQuestionRepository().getQuestionCount(new QuestionCategoryId(input.getCategoryId()),
            new QuestionGroupId(input.getGroupId()), input.getScore());
      }

      if (input.getCategoryId() != null && input.getGroupId() == null)
      {
        return lmsRepositoryRegistry.getQuestionRepository().getQuestionCount(new QuestionCategoryId(input.getCategoryId()), input.getScore());
      }

      if (input.getGroupId() != null && input.getCategoryId() == null)
      {
        return lmsRepositoryRegistry.getQuestionRepository().getQuestionCount(new QuestionGroupId(input.getGroupId()), input.getScore());
      }

      return lmsRepositoryRegistry.getQuestionRepository().getQuestionCount(input.getScore());
    }
    catch (NullPointerException | LmsRepositoryException e)
    {
      throw new UseCaseException(e.getMessage(), e);
    }
  }
}