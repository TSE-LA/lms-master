package mn.erin.lms.base.domain.usecase.exam.question;

import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import mn.erin.domain.base.usecase.UseCaseException;
import mn.erin.domain.dms.repository.DMSRepositoryException;
import mn.erin.lms.base.aim.user.Author;
import mn.erin.lms.base.aim.user.Instructor;
import mn.erin.lms.base.aim.user.Manager;
import mn.erin.lms.base.aim.user.Supervisor;
import mn.erin.lms.base.domain.model.Authorized;
import mn.erin.lms.base.domain.model.category.QuestionCategoryId;
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
public class CreateQuestion extends QuestionUseCase<QuestionInput, String>
{
  public CreateQuestion(LmsRepositoryRegistry lmsRepositoryRegistry, LmsServiceRegistry lmsServiceRegistry)
  {
    super(lmsRepositoryRegistry, lmsServiceRegistry);
  }

  @Override
  protected String executeImpl(QuestionInput input) throws UseCaseException
  {
    try
    {
      Validate.notNull(input);
      Validate.notBlank(input.getValue());

      if (input.isHasImage() && input.getFile() != null && !isSupported(FilenameUtils.getExtension(input.getFile().getName())))
      {
        throw new UseCaseException("Unsupported image extension [" + input.getFile().getName() + "]");
      }

      QuestionType type = QuestionType.valueOf(input.getType());
      List<Answer> answers = input.getAnswers().stream().map(this::mapToAnswerEntity).collect(Collectors.toList());

      Question question = questionRepository.create(input.getValue(), mapToQuestionDetail(input), type, answers, input.getScore());

      if (input.isHasImage() && input.getFile() != null)
      {
        createQuestionFolder(question);
        String extension = Objects.requireNonNull(FilenameUtils.getExtension(input.getFile().getName()));
        String name = question.getId().getId().concat("." + extension);
        uploadImage(Files.readAllBytes(input.getFile().toPath()), name, question.getDetail().getContentFolderId());
        question.getDetail().setImageName(name);
        questionRepository.update(question);
      }

      return question.getId().getId();
    }
    catch (LmsRepositoryException | NullPointerException | IllegalArgumentException | DMSRepositoryException | IOException e)
    {
      throw new UseCaseException(e.getMessage(), e);
    }
  }

  private QuestionDetail mapToQuestionDetail(QuestionInput input) throws UseCaseException
  {
    String currentUsername = lmsServiceRegistry.getAccessIdentityManagement().getCurrentUsername();
    QuestionCategoryId categoryId = validateQuestionCategoryId(input.getCategoryId());
    QuestionGroupId groupId = QuestionGroupId.valueOf(input.getGroupId());

    HistoryOfModification modification = new HistoryOfModification(currentUsername, DATE);
    List<HistoryOfModification> historyOfModifications = new ArrayList<>();
    historyOfModifications.add(modification);

    return new QuestionDetail(categoryId, groupId, historyOfModifications, null, input.isHasImage(), null, input.getCorrectAnswerText(), input.getWrongAnswerText());
  }

  private void createQuestionFolder(Question question) throws LmsRepositoryException
  {
    String folderId = lmsServiceRegistry.getLmsFileSystemService().createQuestionFolder(question.getId().getId(), question.getValue());
    question.getDetail().setContentFolderId(folderId);
    questionRepository.update(question);
  }
}
