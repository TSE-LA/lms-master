package mn.erin.lms.base.domain.usecase.exam.question;

import java.util.Objects;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import mn.erin.domain.base.usecase.UseCaseException;
import mn.erin.lms.base.aim.user.Author;
import mn.erin.lms.base.aim.user.Instructor;
import mn.erin.lms.base.domain.model.Authorized;
import mn.erin.lms.base.domain.model.exam.question.Question;
import mn.erin.lms.base.domain.model.exam.question.QuestionStatus;
import mn.erin.lms.base.domain.repository.LmsRepositoryException;
import mn.erin.lms.base.domain.repository.LmsRepositoryRegistry;
import mn.erin.lms.base.domain.service.LmsServiceRegistry;

/**
 * @author Temuulen Naranbold
 */
@Authorized(users = { Author.class, Instructor.class})
public class DeleteQuestion extends QuestionUseCase<String, Void>
{
  private static final Logger LOGGER = LoggerFactory.getLogger(DeleteQuestion.class);

  public DeleteQuestion(LmsRepositoryRegistry lmsRepositoryRegistry, LmsServiceRegistry lmsServiceRegistry)
  {
    super(Objects.requireNonNull(lmsRepositoryRegistry), Objects.requireNonNull(lmsServiceRegistry));
  }

  @Override
  protected Void executeImpl(String input) throws UseCaseException
  {
    try
    {
      Validate.notBlank(input);

      Question deletingQuestion = lmsRepositoryRegistry.getQuestionRepository().findById(input);



      if (deletingQuestion.getStatus().equals(QuestionStatus.USED))
      {
        lmsRepositoryRegistry.getQuestionRepository().archiveQuestion(deletingQuestion.getId().getId());
      }
      else
      {
        lmsRepositoryRegistry.getQuestionRepository().deleteById(deletingQuestion.getId().getId());
        if (!StringUtils.isBlank(deletingQuestion.getDetail().getContentFolderId()))
        {
          boolean isDeleted = lmsServiceRegistry.getLmsFileSystemService().deleteFolder(deletingQuestion.getDetail().getContentFolderId());
          if (!isDeleted)
          {
            LOGGER.error("Could not delete the folder");
          }
        }
      }

      return null;
    }
    catch (IllegalArgumentException | LmsRepositoryException e)
    {
      throw new UseCaseException(e.getMessage(), e);
    }
  }
}
