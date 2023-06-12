package mn.erin.lms.base.domain.usecase.exam.question;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import mn.erin.domain.base.usecase.UseCaseException;
import mn.erin.domain.dms.repository.DMSRepositoryException;
import mn.erin.lms.base.domain.model.category.QuestionCategoryId;
import mn.erin.lms.base.domain.model.exam.question.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import mn.erin.lms.base.domain.repository.LmsRepositoryException;
import mn.erin.lms.base.domain.repository.LmsRepositoryRegistry;
import mn.erin.lms.base.domain.repository.QuestionCategoryRepository;
import mn.erin.lms.base.domain.repository.QuestionGroupRepository;
import mn.erin.lms.base.domain.repository.QuestionRepository;
import mn.erin.lms.base.domain.repository.QuestionStateRepository;
import mn.erin.lms.base.domain.service.LmsServiceRegistry;
import mn.erin.lms.base.domain.usecase.LmsUseCase;
import mn.erin.lms.base.domain.usecase.exam.dto.question.AnswerInput;
import mn.erin.lms.base.domain.usecase.exam.dto.question.QuestionDto;

/**
 * @author Temuulen Naranbold
 */
public abstract class QuestionUseCase<I, O> extends LmsUseCase<I, O>
{
  protected static final Date DATE = Date.from(LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant());

  protected static final Logger LOGGER = LoggerFactory.getLogger(QuestionUseCase.class);

  protected QuestionCategoryRepository questionCategoryRepository;
  protected QuestionGroupRepository questionGroupRepository;
  protected QuestionRepository questionRepository;
  protected QuestionStateRepository questionStateRepository;

  protected QuestionUseCase(LmsRepositoryRegistry lmsRepositoryRegistry, LmsServiceRegistry lmsServiceRegistry)
  {
    super(lmsRepositoryRegistry, lmsServiceRegistry);

    this.questionCategoryRepository = lmsRepositoryRegistry.getQuestionCategoryRepository();
    this.questionGroupRepository = lmsRepositoryRegistry.getQuestionGroupRepository();
    this.questionRepository = lmsRepositoryRegistry.getQuestionRepository();
    this.questionStateRepository = lmsRepositoryRegistry.getQuestionStateRepository();
  }

  protected Question getQuestion(String id) throws UseCaseException
  {
    try
    {
      return questionRepository.findById(id);
    }
    catch (LmsRepositoryException e)
    {
      throw new UseCaseException("The question with the ID: [" + id + "] does not exist!");
    }
  }

  protected void uploadImage(byte[] image, String name, String folderId) throws DMSRepositoryException
  {
    lmsServiceRegistry.getLmsFileSystemService().createQuestionImage(folderId, name, image);
  }

  protected QuestionCategoryId validateQuestionCategoryId(String categoryId) throws UseCaseException
  {
    QuestionCategoryId questionCategoryId = QuestionCategoryId.valueOf(categoryId);
    boolean categoryExists = questionCategoryRepository.exists(questionCategoryId);

    if (!categoryExists)
    {
      throw new UseCaseException("Question category with the ID: [" + questionCategoryId.getId() + "] does not exist");
    }
    return questionCategoryId;
  }

  protected Answer mapToAnswerEntity(AnswerInput input)
  {
    return new Answer(input.getValue(), input.getIndex(), input.getMatchIndex(), input.getColumn(), input.isCorrect(), input.getWeight());
  }

  protected List<QuestionDto> mapToQuestionDtoList(List<Question> questions){
    List<QuestionDto> questionDtoList = new ArrayList<>();
    for (Question question: questions){
      QuestionDto questionDto = new QuestionDto(question.getId().getId(), question.getValue());
      questionDto.setAuthor(question.getAuthor());
      questionDto.setCategoryId(question.getDetail().getCategoryId().getId());
      questionDto.setGroupId(question.getDetail().getGroupId().getId());
      questionDto.setModifiedDate(question.getModifiedDate());
      questionDto.setCreatedDate(question.getCreatedDate());
      questionDto.setModifiedUser(question.getModifiedUser());
      questionDto.setScore(question.getScore());
      questionDto.setType(question.getType().name());
      questionDto.setHasImage(question.getDetail().isHasImage());
      questionDtoList.add(questionDto);
    }
    return questionDtoList;
  }
}
