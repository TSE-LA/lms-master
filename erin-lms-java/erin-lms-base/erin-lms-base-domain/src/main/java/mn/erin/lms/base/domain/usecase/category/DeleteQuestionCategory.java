package mn.erin.lms.base.domain.usecase.category;

import java.util.List;
import java.util.Objects;

import org.apache.commons.lang3.Validate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import mn.erin.domain.base.usecase.UseCaseException;
import mn.erin.lms.base.aim.user.Author;
import mn.erin.lms.base.aim.user.Instructor;
import mn.erin.lms.base.aim.user.Manager;
import mn.erin.lms.base.aim.user.Supervisor;
import mn.erin.lms.base.domain.model.Authorized;
import mn.erin.lms.base.domain.model.category.QuestionCategoryId;
import mn.erin.lms.base.domain.model.exam.question.Question;
import mn.erin.lms.base.domain.repository.LmsRepositoryException;
import mn.erin.lms.base.domain.repository.LmsRepositoryRegistry;
import mn.erin.lms.base.domain.service.LmsServiceRegistry;
import mn.erin.lms.base.domain.usecase.LmsUseCase;

/**
 * @author Temuulen Naranbold
 */
@Authorized(users = { Author.class, Instructor.class, Manager.class, Supervisor.class })
public class DeleteQuestionCategory extends LmsUseCase<String, Boolean>
{
  private static final Logger LOGGER = LoggerFactory.getLogger(DeleteQuestionCategory.class);

  public DeleteQuestionCategory(LmsRepositoryRegistry lmsRepositoryRegistry, LmsServiceRegistry lmsServiceRegistry)
  {
    super(Objects.requireNonNull(lmsRepositoryRegistry), Objects.requireNonNull(lmsServiceRegistry));
  }

  @Override
  protected Boolean executeImpl(String input) throws UseCaseException
  {
    Validate.notEmpty(input);
    QuestionCategoryId deletingCategoryId = QuestionCategoryId.valueOf(input);

    if (!lmsRepositoryRegistry.getQuestionCategoryRepository().exists(deletingCategoryId))
    {
      return false;
    }

    List<Question> questionsUnderTheCategory = lmsRepositoryRegistry.getQuestionRepository().listAll(deletingCategoryId);

    if (questionsUnderTheCategory != null && !questionsUnderTheCategory.isEmpty())
    {
      throw new UseCaseException("Please delete all the questions under the category ID of: " + input);
    }

    try
    {
      lmsRepositoryRegistry.getQuestionCategoryRepository().delete(deletingCategoryId);
    }
    catch (LmsRepositoryException e)
    {
      LOGGER.error(e.getMessage());
      return false;
    }
    return true;
  }
}