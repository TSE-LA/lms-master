package mn.erin.lms.base.domain.usecase.exam.question;

import java.util.List;

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
import mn.erin.lms.base.domain.usecase.exam.dto.question.QuestionDto;

/**
 * @author Oyungerel Chuluunsukh
 **/

@Authorized(users = { Author.class, Instructor.class })
public class GetQuestions extends QuestionUseCase<GetQuestionInput, List<QuestionDto>>
{
  public GetQuestions(LmsRepositoryRegistry lmsRepositoryRegistry, LmsServiceRegistry lmsServiceRegistry)
  {
    super(lmsRepositoryRegistry, lmsServiceRegistry);
  }

  @Override
  protected List<QuestionDto> executeImpl(GetQuestionInput input) throws UseCaseException
  {
    try
    {
      Validate.notNull(input);

      if (input.getCategoryId() != null && input.getGroupId() != null)
      {
        return this.mapToQuestionDtoList(lmsRepositoryRegistry.getQuestionRepository().getAllActive(new QuestionCategoryId(input.getCategoryId()),
            new QuestionGroupId(input.getGroupId())));
      }

      if (input.getCategoryId() != null && input.getGroupId() == null)
      {
        return this.mapToQuestionDtoList(lmsRepositoryRegistry.getQuestionRepository().getAllActive(new QuestionCategoryId(input.getCategoryId())));
      }

      if (input.getGroupId() != null && input.getCategoryId() == null)
      {
        return this.mapToQuestionDtoList(lmsRepositoryRegistry.getQuestionRepository().getAllActive(new QuestionGroupId(input.getGroupId())));
      }

      return this.mapToQuestionDtoList(lmsRepositoryRegistry.getQuestionRepository().getAllActive());
    }
    catch (NullPointerException | LmsRepositoryException e)
    {
      throw new UseCaseException(e.getMessage(), e);
    }
  }
}