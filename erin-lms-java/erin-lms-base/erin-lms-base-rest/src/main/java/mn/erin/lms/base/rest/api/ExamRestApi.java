package mn.erin.lms.base.rest.api;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.Validate;
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

import mn.erin.domain.base.usecase.UseCaseException;
import mn.erin.infrastucture.rest.common.response.RestResponse;
import mn.erin.infrastucture.rest.common.response.RestResult;
import mn.erin.lms.base.domain.model.exam.Exam;
import mn.erin.lms.base.domain.model.exam.RandomQuestionConfig;
import mn.erin.lms.base.domain.model.exam.question.Question;
import mn.erin.lms.base.domain.repository.LmsRepositoryException;
import mn.erin.lms.base.domain.repository.LmsRepositoryRegistry;
import mn.erin.lms.base.domain.service.LmsServiceRegistry;
import mn.erin.lms.base.domain.usecase.exam.AssignInstructor;
import mn.erin.lms.base.domain.usecase.exam.CreateExam;
import mn.erin.lms.base.domain.usecase.exam.DeleteExam;
import mn.erin.lms.base.domain.usecase.exam.GetAllExamInfo;
import mn.erin.lms.base.domain.usecase.exam.GetDetailedExam;
import mn.erin.lms.base.domain.usecase.exam.GetLearnerExam;
import mn.erin.lms.base.domain.usecase.exam.GetLearnerExamList;
import mn.erin.lms.base.domain.usecase.exam.PublishExam;
import mn.erin.lms.base.domain.usecase.exam.UpdateExam;
import mn.erin.lms.base.domain.usecase.exam.dto.CreateUpdateExamOutput;
import mn.erin.lms.base.domain.usecase.exam.dto.ExamInput;
import mn.erin.lms.base.domain.usecase.exam.dto.ExamRuntimeData;
import mn.erin.lms.base.domain.usecase.exam.dto.GetExamInput;
import mn.erin.lms.base.rest.model.exam.RestExam;
import mn.erin.lms.base.rest.model.exam.RestRandomQuestion;

/**
 * @author Galsan Bayart.
 */
@Api
@RestController
@RequestMapping("lms/exams")
public class ExamRestApi extends BaseLmsRestApi
{

  public ExamRestApi(LmsRepositoryRegistry lmsRepositoryRegistry, LmsServiceRegistry lmsServiceRegistry)
  {
    super(lmsRepositoryRegistry, lmsServiceRegistry);
  }

  @ApiOperation("Creates a new exam")
  @PostMapping
  public ResponseEntity<RestResult> createExam(@RequestBody RestExam restExam)
  {
    Validate.notNull(restExam);
    CreateExam createExam = new CreateExam(lmsRepositoryRegistry, lmsServiceRegistry);

    try
    {
      CreateUpdateExamOutput examResult = createExam.execute(mapToInput(restExam));
      return RestResponse.success(new RestCreateExamResult(examResult.exam.getId().getId()));
    }
    catch (UseCaseException e)
    {
      return RestResponse.internalError(e.getMessage());
    }
  }

  @ApiOperation("Publish exam")
  @PutMapping("/publish/{id}")
  public ResponseEntity<RestResult> updateExam(@PathVariable String id)
  {
    PublishExam publish = new PublishExam(lmsRepositoryRegistry, lmsServiceRegistry);
    try
    {
      return RestResponse.success(publish.execute(id));
    }
    catch (UseCaseException e)
    {
      return RestResponse.internalError(e.getMessage());
    }
  }

  @ApiOperation("Updates exam")
  @PutMapping("/{id}")
  public ResponseEntity<RestResult> updateExam(@PathVariable String id, @RequestBody RestExam restExam)
  {
    Validate.notNull(restExam);
    if (!id.equals(restExam.getId()))
    {
      return RestResponse.badRequest("Invalid exam id: " + id);
    }

    UpdateExam updateExam = new UpdateExam(lmsRepositoryRegistry, lmsServiceRegistry);

    try
    {
      ExamInput input = mapToInput(restExam);
      input.setId(restExam.getId());

      return RestResponse.success(updateExam.execute(input));
    }
    catch (UseCaseException e)
    {
      return RestResponse.internalError(e.getMessage());
    }
  }

  @ApiOperation("Get filtered exams")
  @GetMapping
  public ResponseEntity<RestResult> getExamList(@RequestParam(required = false) String categoryId,
      @RequestParam(required = false) String groupId)
  {
    GetExamInput input = new GetExamInput(categoryId, groupId);
    GetAllExamInfo getExamInfoList = new GetAllExamInfo(lmsRepositoryRegistry, lmsServiceRegistry);
    try
    {
      return RestResponse.success(getExamInfoList.execute(input));
    }
    catch (UseCaseException e)
    {
      return RestResponse.internalError(e.getMessage());
    }
  }

  @ApiOperation("Get learner exam list")
  @GetMapping("/learner")
  public ResponseEntity<RestResult> getLearnerExamList()
  {
    GetLearnerExamList getLearnerExamList = new GetLearnerExamList(lmsRepositoryRegistry, lmsServiceRegistry);

    try
    {
      return RestResponse.success(getLearnerExamList.execute(null));
    }
    catch (UseCaseException e)
    {
      return RestResponse.internalError(e.getMessage());
    }
  }

  @ApiOperation("Get learner exam by id")
  @GetMapping("/learner/{id}")
  public ResponseEntity<RestResult> getLearnerExamById(@PathVariable String id)
  {
    GetLearnerExam getLearnerExam = new GetLearnerExam(lmsRepositoryRegistry, lmsServiceRegistry);

    try
    {
      return RestResponse.success(getLearnerExam.execute(id));
    }
    catch (UseCaseException e)
    {
      return RestResponse.internalError(e.getMessage());
    }
  }

  @ApiOperation("Get detailed exam by id")
  @GetMapping("/detailed/{id}")
  public ResponseEntity<RestResult> getDetailedExamById(@PathVariable String id)
  {
    GetDetailedExam getDetailedExam = new GetDetailedExam(lmsRepositoryRegistry, lmsServiceRegistry);

    try
    {
      return RestResponse.success(getDetailedExam.execute(id));
    }
    catch (UseCaseException e)
    {
      return RestResponse.internalError(e.getMessage());
    }
  }

  @ApiOperation("FIX all exam")
  @GetMapping("/fix")
  public ResponseEntity<RestResult> fixExam()
  {
    List<Exam> examList = this.lmsRepositoryRegistry.getExamRepository().getAllExam();
    examList.forEach(exam -> {
      Set<String> questionIds = exam.getExamConfig().getQuestionIds();
      List<Question> questions = this.lmsRepositoryRegistry.getQuestionRepository().getByIds(questionIds);
      Set<RandomQuestionConfig> randomQuestionConfigs = exam.getExamConfig().getRandomQuestionConfigs();
      int score = questions.stream().mapToInt(Question::getScore).sum() +
          randomQuestionConfigs.stream().mapToInt(rand -> rand.getScore() * rand.getAmount()).sum();
      try
      {
        //fix current exam max scores
        this.lmsRepositoryRegistry.getExamRepository().updateExamScore(exam.getId().getId(), score);
      }
      catch (LmsRepositoryException e)
      {
      }
      try
      {
        List<ExamRuntimeData> examRuntimeDataList = this.lmsRepositoryRegistry.getExamRuntimeDataRepository().listExamRuntimeData(exam.getId().getId());
        examRuntimeDataList.forEach(data -> {
          try
          {
            this.lmsRepositoryRegistry.getExamRuntimeDataRepository().updateMaxScore(data.getId(), score);
          }
          catch (LmsRepositoryException e)
          {
          }
        });
      }
      catch (LmsRepositoryException e)
      {
      }
    });
    return RestResponse.success();
  }

  @ApiOperation("Deletes an exam")
  @DeleteMapping("/{id}")
  public ResponseEntity<RestResult> deleteExam(@PathVariable String id)
  {
    Validate.notBlank(id);
    DeleteExam deleteExam = new DeleteExam(lmsRepositoryRegistry, lmsServiceRegistry);
    try
    {
      return RestResponse.success(deleteExam.execute(id));
    }
    catch (UseCaseException e)
    {
      return RestResponse.internalError(e.getMessage());
    }
  }

  @ApiOperation("Assign instructor")
  @GetMapping("/assign-instructor")
  public ResponseEntity<RestResult> assignInstructors(@RequestParam String instructor)
  {
    Validate.notNull(instructor);
    AssignInstructor assignInstructor = new AssignInstructor(lmsRepositoryRegistry, lmsServiceRegistry);

    try
    {
      return RestResponse.success(assignInstructor.execute(instructor));
    }
    catch (UseCaseException e)
    {
      return RestResponse.internalError(e.getMessage());
    }
  }

  private RandomQuestionConfig mapIntoRandomQuestion(RestRandomQuestion restRandomQuestion)
  {
    return new RandomQuestionConfig(restRandomQuestion.getGroupId(), restRandomQuestion.getCategoryId(), restRandomQuestion.getScore(),
        restRandomQuestion.getAmount());
  }

  private ExamInput mapToInput(RestExam restExam)
  {
    ExamInput.Builder builder = new ExamInput.Builder()
        .withName(restExam.getName())
        .withDescription(restExam.getDescription())
        .withExamCategoryId(restExam.getCategoryId())
        .withExamGroupId(restExam.getGroupId())
        .withExamType(restExam.getExamType())
        .withPublishDate(restExam.getPublishConfig().getPublishDate())
        .withPublishTime(restExam.getPublishConfig().getPublishTime())
        .withSendEmail(restExam.getPublishConfig().isSendEmail())
        .withSendSMS(restExam.getPublishConfig().isSendSMS())
        .withMailText(restExam.getPublishConfig().getMailText())
        .withSMSText(restExam.getPublishConfig().getSmsText())
        .withQuestionIds(restExam.getExamConfigure().getQuestionIds())
        .withRandomQuestionConfigs(restExam.getExamConfigure().getRandomQuestions().stream().map(this::mapIntoRandomQuestion).collect(Collectors.toSet()))
        .withShowAnswerResult(restExam.getExamConfigure().getAnswerResult())
        .withShuffleQuestions(restExam.getExamConfigure().isShuffleQuestion())
        .withShuffleAnswers(restExam.getExamConfigure().isShuffleAnswer())
        .withQuestionsPerPage(restExam.getExamConfigure().isQuestionsPerPage())
        .withThresholdScore(restExam.getExamConfigure().getThresholdScore())
        .withAutoStart(restExam.getExamConfigure().isAutoStart())
        .withMaxScore(restExam.getExamConfigure().getMaxScore())
        .withAttempt(restExam.getExamConfigure().getAttempt())
        .withCertificateId(restExam.getExamConfigure().getCertificateId())
        .withStartDate(restExam.getExamConfigure().getStartDate())
        .withEndDate(restExam.getExamConfigure().getEndDate())
        .withStartTime(restExam.getExamConfigure().getStartTime())
        .withEndTime(restExam.getExamConfigure().getEndTime())
        .withDuration(restExam.getExamConfigure().getDuration())
        .withEnrolledLearners(restExam.getEnrolledLearners())
        .withEnrolledGroups(restExam.getEnrolledGroups());
    return builder.build();
  }
}
