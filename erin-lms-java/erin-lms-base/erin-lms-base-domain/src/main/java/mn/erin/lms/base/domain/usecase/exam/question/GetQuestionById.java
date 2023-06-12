package mn.erin.lms.base.domain.usecase.exam.question;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import mn.erin.domain.base.usecase.UseCaseException;
import mn.erin.lms.base.aim.user.Author;
import mn.erin.lms.base.aim.user.Instructor;
import mn.erin.lms.base.domain.model.Authorized;
import mn.erin.lms.base.domain.model.exam.question.Answer;
import mn.erin.lms.base.domain.model.exam.question.Question;
import mn.erin.lms.base.domain.repository.LmsRepositoryException;
import mn.erin.lms.base.domain.repository.LmsRepositoryRegistry;
import mn.erin.lms.base.domain.service.LmsServiceRegistry;
import mn.erin.lms.base.domain.usecase.exam.dto.question.AnswerDto;
import mn.erin.lms.base.domain.usecase.exam.dto.question.QuestionDetailedDto;

/**
 * @author Oyungerel Chuluunsukh
 **/
@Authorized(users = { Author.class, Instructor.class})
public class GetQuestionById extends QuestionUseCase<String, QuestionDetailedDto>
{
  public GetQuestionById(LmsRepositoryRegistry lmsRepositoryRegistry,
      LmsServiceRegistry lmsServiceRegistry)
  {
    super(lmsRepositoryRegistry, lmsServiceRegistry);
  }

  @Override
  protected QuestionDetailedDto executeImpl(String input) throws UseCaseException, LmsRepositoryException
  {
    if (input == null)
    {
      throw new UseCaseException("Question id cannot be null or blank!");
    }

    Question question = this.lmsRepositoryRegistry.getQuestionRepository().findById(input);
    return mapToDetailedQuestionDto(question);
  }

  private QuestionDetailedDto mapToDetailedQuestionDto(Question question)
  {
    QuestionDetailedDto dto = new QuestionDetailedDto(question.getId().getId(), question.getValue());
    dto.setAuthor(question.getAuthor());
    dto.setCategoryId(question.getDetail().getCategoryId().getId());
    dto.setGroupId(question.getDetail().getGroupId().getId());
    dto.setScore(question.getScore());
    dto.setAuthor(question.getAuthor());
    dto.setCreatedDate(question.getCreatedDate());
    dto.setType(question.getType().name());
    dto.setModifiedDate(question.getModifiedDate());
    dto.setModifiedUser(question.getModifiedUser());
    dto.setFileId(question.getDetail().getContentFolderId());
    dto.setFileName(question.getDetail().getImageName());
    dto.setHasImage(question.getDetail().isHasImage());
    dto.setCorrectText(question.getDetail().getCorrectAnswerText());
    dto.setWrongText(question.getDetail().getWrongAnswerText());
    dto.setAnswers(this.mapToAnswerDto(question.getAnswers()));
    return dto;
  }

  private List<AnswerDto> mapToAnswerDto(List<Answer> answers)
  {
    List<AnswerDto> answerDtoList = new ArrayList<>();
    answers.forEach(answer -> {
      AnswerDto answerDto = new AnswerDto();
      answerDto.setValue(answer.getValue());
      answerDto.setCorrect(answer.isCorrect());
      answerDto.setWeight(answer.getWeight());
      answerDto.setColumn(answer.getColumn());
      answerDto.setMatchIndex(answer.getMatchIndex());
      answerDto.setIndex(answer.getIndex());
      answerDtoList.add(answerDto);
    });

    return answerDtoList;
  }
}
