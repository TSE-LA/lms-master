package mn.erin.lms.base.domain.usecase.exam.question;

import java.util.List;
import java.util.Set;
import mn.erin.domain.base.usecase.UseCaseException;
import mn.erin.lms.base.aim.user.Author;
import mn.erin.lms.base.aim.user.Instructor;
import mn.erin.lms.base.domain.model.Authorized;
import mn.erin.lms.base.domain.repository.LmsRepositoryException;
import mn.erin.lms.base.domain.repository.LmsRepositoryRegistry;
import mn.erin.lms.base.domain.service.LmsServiceRegistry;
import mn.erin.lms.base.domain.usecase.exam.dto.question.QuestionDto;

/**
 * @author Galsan Bayart.
 */
@Authorized(users = { Author.class, Instructor.class })
public class GetQuestionsByIds extends QuestionUseCase<Set<String>, List<QuestionDto>>
{
  public GetQuestionsByIds(LmsRepositoryRegistry lmsRepositoryRegistry,
      LmsServiceRegistry lmsServiceRegistry)
  {
    super(lmsRepositoryRegistry, lmsServiceRegistry);
  }

  @Override
  protected List<QuestionDto> executeImpl(Set<String> input) throws UseCaseException, LmsRepositoryException
  {
    return mapToQuestionDtoList(lmsRepositoryRegistry.getQuestionRepository().getByIds(input));
  }
}
