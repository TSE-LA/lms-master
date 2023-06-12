package mn.erin.lms.base.domain.usecase.exam.question;

import java.io.IOException;
import java.nio.file.Files;
import java.util.Objects;
import java.util.stream.Collectors;

import mn.erin.domain.base.model.Content;
import mn.erin.domain.base.usecase.UseCaseException;
import mn.erin.domain.dms.repository.DMSRepositoryException;
import mn.erin.lms.base.aim.user.Author;
import mn.erin.lms.base.aim.user.Instructor;
import mn.erin.lms.base.aim.user.Manager;
import mn.erin.lms.base.aim.user.Supervisor;
import mn.erin.lms.base.domain.model.Authorized;
import mn.erin.lms.base.domain.model.exam.HistoryOfModification;
import mn.erin.lms.base.domain.model.exam.question.*;
import mn.erin.lms.base.domain.repository.LmsRepositoryException;
import mn.erin.lms.base.domain.repository.LmsRepositoryRegistry;
import mn.erin.lms.base.domain.service.LmsServiceRegistry;
import mn.erin.lms.base.domain.usecase.exam.dto.question.QuestionInput;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.Validate;

import static mn.erin.lms.base.domain.usecase.content.dto.ImageExtension.isSupported;

/**
 * @author Galsan Bayart
 */
@Authorized(users = { Author.class, Manager.class, Instructor.class, Supervisor.class })
public class UpdateQuestion extends QuestionUseCase<QuestionInput, String>
{
  public UpdateQuestion(LmsRepositoryRegistry lmsRepositoryRegistry, LmsServiceRegistry lmsServiceRegistry)
  {
    super(lmsRepositoryRegistry, lmsServiceRegistry);
  }

  @Override
  protected String executeImpl(QuestionInput input) throws UseCaseException
  {
    try
    {
      Validate.notNull(input);
      Validate.notBlank(input.getId());

      if (input.isHasImage() && input.getFile() != null && !isSupported(FilenameUtils.getExtension(input.getFile().getName())))
      {
        throw new UseCaseException("Unsupported image extension [" + input.getFile().getName() + "]");
      }

      Question updatingQuestion = getQuestion(input.getId());
      mapToQuestion(input, updatingQuestion);

      Question updatedQuestion = questionRepository.update(updatingQuestion);

      if (!updatingQuestion.sameIdentityAs(updatedQuestion))
      {
        if (updatedQuestion.getDetail().isHasImage())
        {
          createQuestionFolder(updatedQuestion);
          if (!input.isHasImage())
          {
            Content content = lmsServiceRegistry.getLmsFileSystemService().getDocumentContent(updatingQuestion.getDetail().getContentFolderId(), updatingQuestion.getDetail().getImageName());
            String name = updatedQuestion.getId().getId().concat("." + content.getMimeType().replace("image/", "").trim());
            uploadImage(content.getContent(), name, updatedQuestion.getDetail().getContentFolderId());
            updatedQuestion.getDetail().setImageName(name);
          }
        }
        else
        {
          updatedQuestion.getDetail().setContentFolderId(null);
          updatedQuestion.getDetail().setImageName(null);
        }
      }

      updateImage(input, updatedQuestion);
      return questionRepository.update(updatedQuestion).getId().getId();
    }
    catch (LmsRepositoryException | NullPointerException | IllegalArgumentException | DMSRepositoryException | IOException e)
    {
      throw new UseCaseException(e.getMessage(), e);
    }
  }

  private void createQuestionFolder(Question question) throws LmsRepositoryException
  {
    lmsServiceRegistry.getLmsFileSystemService().deleteFolder(question.getId().getId());
    String folderId = lmsServiceRegistry.getLmsFileSystemService().createQuestionFolder(question.getId().getId(), question.getValue());
    question.getDetail().setContentFolderId(folderId);
    questionRepository.update(question);
  }

  private void updateImage(QuestionInput input, Question updatedQuestion) throws LmsRepositoryException, IOException, DMSRepositoryException
  {
    String folderId = updatedQuestion.getDetail().getContentFolderId();

    if (input.getFile() != null)
    {
      String extension = Objects.requireNonNull(FilenameUtils.getExtension(input.getFile().getName()));
      String name = updatedQuestion.getId().getId().concat("." + extension);

      if (folderId != null && updatedQuestion.getDetail().getImageName() != null)
      {
        lmsServiceRegistry.getLmsFileSystemService()
            .deleteQuestionImage(updatedQuestion.getId().getId(), folderId, updatedQuestion.getDetail().getImageName());
      }
      else if (folderId == null)
      {
        createQuestionFolder(updatedQuestion);
      }
      uploadImage(Files.readAllBytes(input.getFile().toPath()), name, updatedQuestion.getDetail().getContentFolderId());
      updatedQuestion.getDetail().setImageName(name);
      updatedQuestion.getDetail().setHasImage(true);
    }
    else if (!input.isHasImage() && folderId != null && updatedQuestion.getDetail().getImageName() != null)
    {
      lmsServiceRegistry.getLmsFileSystemService()
          .deleteQuestionImage(updatedQuestion.getId().getId(), folderId, updatedQuestion.getDetail().getImageName());
      updatedQuestion.getDetail().setImageName(null);
      updatedQuestion.getDetail().setHasImage(false);
    }
  }

  private void mapToQuestion(QuestionInput input, Question updatingQuestion) throws UseCaseException
  {
    String currentUsername = lmsServiceRegistry.getAccessIdentityManagement().getCurrentUsername();
    updatingQuestion.getDetail().setCategoryId(validateQuestionCategoryId(input.getCategoryId()));
    updatingQuestion.getDetail().setGroupId(QuestionGroupId.valueOf(input.getGroupId()));
    updatingQuestion.getDetail().addModifyInfo(new HistoryOfModification(currentUsername, DATE));
    updatingQuestion.getDetail().setCorrectAnswerText(input.getCorrectAnswerText());
    updatingQuestion.getDetail().setWrongAnswerText(input.getWrongAnswerText());
    updatingQuestion.setValue(input.getValue());
    updatingQuestion.setType(QuestionType.valueOf(input.getType()));
    updatingQuestion.setAnswers(input.getAnswers().stream().map(this::mapToAnswerEntity).collect(Collectors.toList()));
    updatingQuestion.setModifiedUser(currentUsername);
    updatingQuestion.setModifiedDate(DATE);
    updatingQuestion.setScore(input.getScore());
  }
}
