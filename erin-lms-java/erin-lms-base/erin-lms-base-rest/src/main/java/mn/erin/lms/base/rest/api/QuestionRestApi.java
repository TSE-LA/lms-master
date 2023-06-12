package mn.erin.lms.base.rest.api;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;


import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

import mn.erin.lms.base.domain.usecase.exam.dto.question.AnswerInput;
import mn.erin.lms.base.domain.usecase.exam.dto.question.GetQuestionInput;
import mn.erin.lms.base.domain.usecase.exam.dto.question.QuestionInput;

import org.apache.commons.lang3.Validate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import mn.erin.domain.base.usecase.UseCaseException;
import mn.erin.infrastucture.rest.common.response.RestResponse;
import mn.erin.infrastucture.rest.common.response.RestResult;
import mn.erin.lms.base.domain.model.exam.question.Question;
import mn.erin.lms.base.domain.repository.LmsRepositoryRegistry;
import mn.erin.lms.base.domain.service.LmsServiceRegistry;
import mn.erin.lms.base.domain.usecase.exam.question.CreateQuestion;
import mn.erin.lms.base.domain.usecase.exam.question.DeleteQuestion;
import mn.erin.lms.base.domain.usecase.exam.question.GetQuestionCount;
import mn.erin.lms.base.domain.usecase.exam.question.GetQuestionById;
import mn.erin.lms.base.domain.usecase.exam.question.GetQuestions;
import mn.erin.lms.base.domain.usecase.exam.question.GetQuestionsByIds;
import mn.erin.lms.base.domain.usecase.exam.question.UpdateQuestion;
import mn.erin.lms.base.rest.model.exam.RestAnswer;
import mn.erin.lms.base.rest.model.exam.RestQuestions;

/**
 * @author Galsan Bayart
 */
@Api
@RequestMapping("lms/questions")
@RestController
public class QuestionRestApi extends BaseLmsRestApi
{
  public static final Logger LOGGER = LoggerFactory.getLogger(QuestionRestApi.class);

  private Map<String, File> attachmentFiles = new HashMap<>();

  public QuestionRestApi(LmsRepositoryRegistry lmsRepositoryRegistry, LmsServiceRegistry lmsServiceRegistry)
  {
    super(lmsRepositoryRegistry, lmsServiceRegistry);
  }

  @ApiOperation("Create question")
  @PostMapping
  public ResponseEntity<RestResult> create(@RequestBody RestQuestions restQuestions)
  {
    Validate.notNull(restQuestions);
    QuestionInput input = mapToQuestionInput(restQuestions);
    attachmentFiles.remove(restQuestions.getFileId());

    CreateQuestion createQuestion = new CreateQuestion(lmsRepositoryRegistry, lmsServiceRegistry);
    try
    {
      return RestResponse.success(createQuestion.execute(input));
    }
    catch (UseCaseException e)
    {
      return RestResponse.internalError(e.getMessage());
    }
  }

  @ApiOperation("Uploads an attachment")
  @PostMapping("/attachment")
  public ResponseEntity<RestResult> uploadAttachment(@RequestParam("file") MultipartFile multipartFile)
  {
    if (multipartFile.isEmpty())
    {
      return RestResponse.badRequest("File is empty!");
    }

    try
    {
      String uuid = UUID.randomUUID().toString();
      File file = lmsServiceRegistry.getTemporaryFileApi().store(multipartFile.getOriginalFilename(), multipartFile.getBytes());
      this.attachmentFiles.put(uuid, file);
      return RestResponse.success(uuid);
    }
    catch (IOException e)
    {
      return RestResponse.internalError(e.getMessage());
    }
  }

  @GetMapping
  public ResponseEntity<RestResult> getQuestions(
      @RequestParam(required = false) String categoryId,
      @RequestParam(required = false) String groupId)
  {
    GetQuestions getQuestions = new GetQuestions(lmsRepositoryRegistry, lmsServiceRegistry);
    try
    {
      return RestResponse.success(getQuestions.execute(new GetQuestionInput(categoryId, groupId)));
    }
    catch (UseCaseException e)
    {
      return RestResponse.internalError(e.getMessage());
    }
  }

  @ApiOperation("Gets the question count")
  @GetMapping(value = "/get-question-count")
  public ResponseEntity<RestResult> getQuestionCount(
      @RequestParam(required = false) String categoryId,
      @RequestParam(required = false) String groupId,
      @RequestParam int score)
  {
    GetQuestionCount getQuestionCount = new GetQuestionCount(lmsRepositoryRegistry, lmsServiceRegistry);
    try
    {
      return RestResponse.success(getQuestionCount.execute(new GetQuestionInput(categoryId, groupId, score)));
    }
    catch (UseCaseException e)
    {
      return RestResponse.internalError(e.getMessage());
    }
  }

  @ApiOperation("Gets active questions")
  @GetMapping("/active")
  public ResponseEntity<RestResult> getActiveQuestions()
  {
    List<Question> question = lmsRepositoryRegistry.getQuestionRepository().getAllActive();
    return RestResponse.success(question);
  }

  @ApiOperation("Gets question by id")
  @GetMapping("/{id}")
  public ResponseEntity<RestResult> getQuestionById(@PathVariable String id)
  {
    GetQuestionById getQuestionById = new GetQuestionById(lmsRepositoryRegistry, lmsServiceRegistry);
    try
    {
      return RestResponse.success(getQuestionById.execute(id));
    }
    catch (UseCaseException e)
    {
      return RestResponse.internalError(e.getMessage());
    }
  }

  @ApiOperation("Gets question list by ids")
  @PutMapping("/get-by-ids")
  public ResponseEntity<RestResult> getActiveQuestionByIds(@RequestBody Set<String> ids)
  {
    GetQuestionsByIds getQuestionsByIds = new GetQuestionsByIds(lmsRepositoryRegistry, lmsServiceRegistry);
    try
    {
      return RestResponse.success(getQuestionsByIds.execute(ids));
    }
    catch (UseCaseException e)
    {
      return RestResponse.internalError(e.getMessage());
    }
  }

  @ApiOperation("Updates a question")
  @PutMapping("/{id}")
  public ResponseEntity<RestResult> updateQuestion(@PathVariable String id, @RequestBody RestQuestions restQuestions)
  {
    Validate.notNull(restQuestions);
    Validate.notBlank(id);

    if (!id.equals(restQuestions.getId()))
    {
      return RestResponse.badRequest("Invalid question id: " + id);
    }

    QuestionInput input = mapToQuestionInput(restQuestions);
    input.setId(id);

    UpdateQuestion updateQuestion = new UpdateQuestion(lmsRepositoryRegistry, lmsServiceRegistry);
    try
    {
      return RestResponse.success(updateQuestion.execute(input));
    }
    catch (UseCaseException e)
    {
      return RestResponse.internalError(e.getMessage());
    }
  }

  @ApiOperation("Deletes a question")
  @DeleteMapping("/{questionId}")
  public ResponseEntity<RestResult> delete(@PathVariable String questionId)
  {
    DeleteQuestion deleteQuestion = new DeleteQuestion(lmsRepositoryRegistry, lmsServiceRegistry);
    try
    {
      return RestResponse.success(deleteQuestion.execute(questionId));
    }
    catch (UseCaseException e)
    {
      return RestResponse.internalError(e.getMessage());
    }
  }

  private QuestionInput mapToQuestionInput(RestQuestions restQuestions)
  {
    return new QuestionInput.Builder()
        .withName(restQuestions.getValue())
        .withCategoryId(restQuestions.getCategoryId())
        .withGroupId(restQuestions.getGroupId())
        .withType(restQuestions.getType())
        .withAnswers(restQuestions.getAnswers().stream().map(this::mapToAnswerInput).collect(Collectors.toList()))
        .withScore(restQuestions.getScore())
        .withFile(attachmentFiles.get(restQuestions.getFileId()))
        .withFileId(restQuestions.getFileId())
        .withHasImage(restQuestions.isHasImage())
        .withCorrectAnswerText(restQuestions.getCorrectAnswerText())
        .withWrongAnswerText(restQuestions.getWrongAnswerText()).build();
  }

  private AnswerInput mapToAnswerInput(RestAnswer answers)
  {
    AnswerInput input = new AnswerInput();
    input.setValue(answers.getValue());
    input.setIndex(answers.getIndex());
    input.setMatchIndex(answers.getMatchIndex());
    input.setColumn(answers.getColumn());
    input.setCorrect(answers.isCorrect());
    input.setWeight(answers.getWeight());
    return input;
  }
}
