package mn.erin.lms.base.rest.api;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.Validate;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import mn.erin.domain.base.usecase.UseCaseException;
import mn.erin.infrastucture.rest.common.response.RestResponse;
import mn.erin.infrastucture.rest.common.response.RestResult;
import mn.erin.lms.base.domain.model.exam.Exam;
import mn.erin.lms.base.domain.model.exam.UpdateExamRuntimeInput;
import mn.erin.lms.base.domain.model.exam.question.LearnerAnswerEntity;
import mn.erin.lms.base.domain.model.exam.question.LearnerQuestionDto;
import mn.erin.lms.base.domain.repository.LmsRepositoryRegistry;
import mn.erin.lms.base.domain.service.LmsServiceRegistry;
import mn.erin.lms.base.domain.usecase.exam.EndExam;
import mn.erin.lms.base.domain.usecase.exam.GetExamLaunchData;
import mn.erin.lms.base.domain.usecase.exam.GetLearnerExcelExamResult;
import mn.erin.lms.base.domain.usecase.exam.GetLearnerRuntime;
import mn.erin.lms.base.domain.usecase.exam.GetLearnerScoresForExam;
import mn.erin.lms.base.domain.usecase.exam.StartExam;
import mn.erin.lms.base.domain.usecase.exam.UpdateExamRuntime;
import mn.erin.lms.base.domain.usecase.exam.dto.LearnerExamResultInput;
import mn.erin.lms.base.rest.model.exam.RestExamRuntimeUpdate;
import mn.erin.lms.base.rest.model.exam.RestLearnerAnswer;
import mn.erin.lms.base.rest.model.exam.RestLearnerQuestion;
import mn.erin.lms.base.rest.model.exam.RestOngoingExam;

/**
 * @author Byambajav
 */
@Api
@RestController
@RequestMapping(value = "lms/exam-runtime", name = "Provides functions for take an exam as a learner")
public class ExamRuntimeRestApi extends BaseLmsRestApi
{
  private static final String EXCEL_MEDIA_TYPE = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
  private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss");

  protected ExamRuntimeRestApi(LmsRepositoryRegistry lmsRepositoryRegistry,
      LmsServiceRegistry lmsServiceRegistry)
  {
    super(lmsRepositoryRegistry, lmsServiceRegistry);
  }

  @ApiOperation("Launches an exam")
  @GetMapping("/launch/{id}")
  public ResponseEntity<RestResult> getExamLaunchData(@PathVariable String id) throws UseCaseException
  {
    GetExamLaunchData getExamLaunchData = new GetExamLaunchData(lmsRepositoryRegistry, lmsServiceRegistry);
    return RestResponse.success(getExamLaunchData.execute(id));
  }

  @ApiOperation("Starts an exam")
  @GetMapping("/start/{id}")
  public ResponseEntity<RestResult> startExam(@PathVariable String id)
  {
    StartExam startExam = new StartExam(lmsRepositoryRegistry, lmsServiceRegistry);
    try
    {
      return RestResponse.success(startExam.execute(id));
    }
    catch (UseCaseException e)
    {
      return RestResponse.internalError(e.getMessage());
    }
  }

  @ApiOperation("Updates an exam runtime")
  @PatchMapping("/update")
  public ResponseEntity<RestResult> updateExam(@RequestBody RestExamRuntimeUpdate examRuntimeUpdate)
  {
    Validate.notNull(examRuntimeUpdate);
    UpdateExamRuntime updateExamRuntime = new UpdateExamRuntime(lmsRepositoryRegistry, lmsServiceRegistry);
    try
    {
      UpdateExamRuntimeInput input = mapIntoInput(examRuntimeUpdate);
      return RestResponse.success(updateExamRuntime.execute(input));
    }
    catch (UseCaseException e)
    {
      return RestResponse.badRequest(e.getMessage());
    }
  }

  @ApiOperation("Ends an exam")
  @GetMapping("/end/{id}")
  public ResponseEntity<RestResult> endExam(@PathVariable String id)
  {
    EndExam endExam = new EndExam(lmsRepositoryRegistry, lmsServiceRegistry);
    try
    {
      return RestResponse.success(endExam.execute(id));
    }
    catch (UseCaseException e)
    {
      return RestResponse.badRequest(e.getMessage());
    }
  }

  @ApiOperation("Shows exam final scores")
  @GetMapping("/score/{examId}")
  public ResponseEntity<RestResult> showExamScore(@PathVariable String examId)
  {
    GetLearnerScoresForExam getScores = new GetLearnerScoresForExam(lmsRepositoryRegistry, lmsServiceRegistry);
    try
    {
      return RestResponse.success(getScores.execute(examId));
    }
    catch (UseCaseException e)
    {
      return RestResponse.badRequest(e.getMessage());
    }
  }

  @ApiOperation("Get learner runtime data")
  @GetMapping("/learner")
  public ResponseEntity<RestResult> getLearnerRuntime(@RequestParam() String learnerId)
  {
    GetLearnerRuntime getLearnerRuntime = new GetLearnerRuntime(lmsRepositoryRegistry, lmsServiceRegistry);
    try
    {
      return RestResponse.success(getLearnerRuntime.execute(learnerId));
    }
    catch (UseCaseException e)
    {
      return RestResponse.badRequest(e.getMessage());
    }
  }

  @ApiOperation("Returns user's current exam info")
  @GetMapping("/ongoing")
  public ResponseEntity<RestResult> getOngoingExam()
  {
    Optional<Exam> currentUserOngoingExam = lmsServiceRegistry.getExamInteractionService().getCurrentUserOngoingExam();
    if (currentUserOngoingExam.isPresent())
    {
      RestOngoingExam restOngoingExam = new RestOngoingExam();
      restOngoingExam.setExamId(currentUserOngoingExam.get().getId().getId());
      restOngoingExam.setExamName(currentUserOngoingExam.get().getName());
      restOngoingExam.setExamStartDate(currentUserOngoingExam.get().getActualStartDate());
      restOngoingExam.setExamEndDate(currentUserOngoingExam.get().getActualEndDate());
      return RestResponse.success(restOngoingExam);
    }
    return RestResponse.success(null);
  }

  @ApiOperation("Download excel exam result of learner")
  @GetMapping("/download")
  public ResponseEntity getExcelExamResult(@RequestParam String examId, @RequestParam String learnerId)
  {
    LearnerExamResultInput input = new LearnerExamResultInput(examId, learnerId);
    byte[] generatedExamResultExcel;
    GetLearnerExcelExamResult getLearnerExcelExamResult = new GetLearnerExcelExamResult(lmsRepositoryRegistry, lmsServiceRegistry);

    try
    {
      generatedExamResultExcel = getLearnerExcelExamResult.execute(input);
      ByteArrayResource resource;

      resource = new ByteArrayResource(generatedExamResultExcel);

      String formattedDate = LocalDateTime.now().format(dateFormatter);

      return ResponseEntity.ok()
          .contentLength(resource.contentLength())
          .contentType(MediaType.parseMediaType(EXCEL_MEDIA_TYPE))
          .header("Content-Disposition", "attachment; filename=\"Exam-Result-" + formattedDate + ".xlsx\"")
          .body(resource);
    }
    catch (UseCaseException e)
    {
      return RestResponse.internalError(e.getMessage());
    }
  }

  private UpdateExamRuntimeInput mapIntoInput(RestExamRuntimeUpdate runtimeUpdate)
  {
    return new UpdateExamRuntimeInput(runtimeUpdate.getExamId(), mapToLearnerQuestion(runtimeUpdate.getLearnerQuestion()),
        runtimeUpdate.getSpentTime());
  }

  private List<LearnerQuestionDto> mapToLearnerQuestion(List<RestLearnerQuestion> restLearnerQuestions)
  {
    List<LearnerQuestionDto> learnerQuestions = new ArrayList<>();
    for (RestLearnerQuestion restLearnerQuestion : restLearnerQuestions)
    {
      learnerQuestions.add(new LearnerQuestionDto(
          restLearnerQuestion.getId(),
          restLearnerQuestion.getValue(),
          restLearnerQuestion.getType(),
          mapToLearnerAnswer(restLearnerQuestion.getAnswers()),
          restLearnerQuestion.getImagePath()
      ));
    }
    return learnerQuestions;
  }

  private List<LearnerAnswerEntity> mapToLearnerAnswer(List<RestLearnerAnswer> restLearnerAnswers)
  {
    List<LearnerAnswerEntity> learnerAnswers = new ArrayList<>();
    for (RestLearnerAnswer restLearnerAnswer : restLearnerAnswers)
    {
      learnerAnswers.add(new LearnerAnswerEntity(
          restLearnerAnswer.getValue(), restLearnerAnswer.getIndex(), restLearnerAnswer.isSelected()
      ));
    }
    return learnerAnswers;
  }
}
