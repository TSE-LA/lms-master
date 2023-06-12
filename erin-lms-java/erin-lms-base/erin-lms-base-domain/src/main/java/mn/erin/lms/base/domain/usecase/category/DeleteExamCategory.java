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
import mn.erin.lms.base.domain.model.category.ExamCategoryId;
import mn.erin.lms.base.domain.model.exam.Exam;
import mn.erin.lms.base.domain.repository.LmsRepositoryException;
import mn.erin.lms.base.domain.repository.LmsRepositoryRegistry;
import mn.erin.lms.base.domain.service.LmsServiceRegistry;
import mn.erin.lms.base.domain.usecase.LmsUseCase;

/**
 * @author Temuulen Naranbold
 */
@Authorized(users = { Author.class, Instructor.class, Manager.class, Supervisor.class })
public class DeleteExamCategory extends LmsUseCase<String, Boolean>
{
  private static final Logger LOGGER = LoggerFactory.getLogger(DeleteExamCategory.class);

  public DeleteExamCategory(LmsRepositoryRegistry lmsRepositoryRegistry, LmsServiceRegistry lmsServiceRegistry)
  {
    super(Objects.requireNonNull(lmsRepositoryRegistry), Objects.requireNonNull(lmsServiceRegistry));
  }

  @Override
  protected Boolean executeImpl(String input) throws UseCaseException
  {
    try
    {
      Validate.notEmpty(input);

      ExamCategoryId categoryId = ExamCategoryId.valueOf(input);

      if (!lmsRepositoryRegistry.getExamCategoryRepository().exists(categoryId))
      {
        throw new UseCaseException("Category not found!");
      }

      List<Exam> examsUnderTheCategory = lmsRepositoryRegistry.getExamRepository().listAllByCategory(categoryId);

      if (examsUnderTheCategory != null && !examsUnderTheCategory.isEmpty())
      {
        throw new UseCaseException("Please delete all the exams under the category ID of: " + input);
      }

      lmsRepositoryRegistry.getExamCategoryRepository().delete(categoryId);
    }
    catch (LmsRepositoryException | NullPointerException e)
    {
      LOGGER.error(e.getMessage());
      return false;
    }

    return true;
  }
}
