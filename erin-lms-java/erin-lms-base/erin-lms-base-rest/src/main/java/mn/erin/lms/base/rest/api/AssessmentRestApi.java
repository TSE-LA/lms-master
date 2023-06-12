package mn.erin.lms.base.rest.api;

import java.time.LocalDate;
import java.util.List;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.format.annotation.DateTimeFormat;
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
import mn.erin.lms.base.domain.repository.LmsRepositoryRegistry;
import mn.erin.lms.base.domain.service.LmsServiceRegistry;
import mn.erin.lms.base.domain.usecase.assessment.CloneAssessment;
import mn.erin.lms.base.domain.usecase.assessment.CreateAssessment;
import mn.erin.lms.base.domain.usecase.assessment.DeleteAssessment;
import mn.erin.lms.base.domain.usecase.assessment.GetAssessmentById;
import mn.erin.lms.base.domain.usecase.assessment.GetAssessments;
import mn.erin.lms.base.domain.usecase.assessment.UpdateAssessment;
import mn.erin.lms.base.domain.usecase.assessment.dto.AssessmentDto;
import mn.erin.lms.base.domain.usecase.assessment.dto.CloneAssessmentInput;
import mn.erin.lms.base.domain.usecase.assessment.dto.CreateAssessmentInput;
import mn.erin.lms.base.domain.usecase.assessment.dto.GetAssessmentsInput;
import mn.erin.lms.base.domain.usecase.assessment.dto.UpdateAssessmentInput;
import mn.erin.lms.base.rest.model.RestAssessment;

/**
 * @author Bat-Erdene Tsogoo.
 */
@Api("Assessment REST API")
@RequestMapping(value = "/lms/assessments", name = "Provides LMS assessment features")
@RestController
public class AssessmentRestApi extends BaseLmsRestApi
{
  public AssessmentRestApi(LmsRepositoryRegistry lmsRepositoryRegistry,
      LmsServiceRegistry lmsServiceRegistry)
  {
    super(lmsRepositoryRegistry, lmsServiceRegistry);
  }

  @ApiOperation("Creates a new assessment")
  @PostMapping
  public ResponseEntity<RestResult> create(@RequestBody RestAssessment body)
  {
    CreateAssessmentInput input = new CreateAssessmentInput(body.getName());
    input.setDescription(body.getDescription());
    CreateAssessment createAssessment = new CreateAssessment(lmsRepositoryRegistry, lmsServiceRegistry);

    try
    {
      String id = createAssessment.execute(input);
      return RestResponse.success(id);
    }
    catch (UseCaseException e)
    {
      return RestResponse.internalError(e.getMessage());
    }
  }

  @ApiOperation("Fetches all assessments")
  @GetMapping
  public ResponseEntity<RestResult> readAll(
      @RequestParam Boolean onlyActive,
      @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
      @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate
  )
  {
    GetAssessments getAssessments = new GetAssessments(lmsRepositoryRegistry, lmsServiceRegistry);
    GetAssessmentsInput input = new GetAssessmentsInput(startDate, endDate, onlyActive);

    try
    {
      List<AssessmentDto> assessments = getAssessments.execute(input);
      return RestResponse.success(assessments);
    }
    catch (UseCaseException e)
    {
      return RestResponse.success(e.getMessage());
    }
  }

  @ApiOperation("Fetches an assessment by ID")
  @GetMapping("/{assessmentId}")
  public ResponseEntity<RestResult> readById(@PathVariable String assessmentId)
  {
    GetAssessmentById getAssessmentById = new GetAssessmentById(lmsRepositoryRegistry, lmsServiceRegistry);

    try
    {
      AssessmentDto dto = getAssessmentById.execute(assessmentId);
      return RestResponse.success(dto);
    }
    catch (UseCaseException e)
    {
      return RestResponse.internalError(e.getMessage());
    }
  }

  @ApiOperation("Updates an assessment")
  @PutMapping("/{assessmentId}")
  public ResponseEntity<RestResult> update(@PathVariable String assessmentId,
      @RequestBody RestAssessment requestBody)
  {
    UpdateAssessment updateAssessment = new UpdateAssessment(lmsRepositoryRegistry, lmsServiceRegistry);
    UpdateAssessmentInput input = new UpdateAssessmentInput(assessmentId, requestBody.getName(), requestBody.getDescription());

    try
    {
      AssessmentDto dto = updateAssessment.execute(input);
      return RestResponse.success(dto);
    }
    catch (UseCaseException e)
    {
      return RestResponse.internalError(e.getMessage());
    }
  }

  @ApiOperation("Deletes an assessment")
  @DeleteMapping("/{assessmentId}")
  public ResponseEntity<RestResult> delete(@PathVariable String assessmentId)
  {
    DeleteAssessment deleteAssessment = new DeleteAssessment(lmsRepositoryRegistry, lmsServiceRegistry);

    try
    {
      boolean isDeleted = deleteAssessment.execute(assessmentId);
      return RestResponse.success(isDeleted);
    }
    catch (UseCaseException e)
    {
      return RestResponse.internalError(e.getMessage());
    }
  }

  @ApiOperation("Clones an assessment")
  @PostMapping("/{assessmentId}/clone")
  public ResponseEntity<RestResult> clone(@PathVariable String assessmentId, @RequestBody RestAssessment body) {
    CreateAssessmentInput createAssessmentInput = new CreateAssessmentInput(body.getName());
    createAssessmentInput.setDescription(body.getDescription());
    CloneAssessmentInput input = new CloneAssessmentInput(assessmentId, createAssessmentInput);

    CloneAssessment cloneAssessment = new CloneAssessment(lmsRepositoryRegistry, lmsServiceRegistry);
    AssessmentDto assessmentDto;
    try {
      assessmentDto = cloneAssessment.execute(input);
    } catch (UseCaseException e) {
      return RestResponse.internalError(e.getMessage());
    }
    return RestResponse.success(assessmentDto);
  }

  @ApiOperation("Launch assessment")
  @PostMapping("/{assessmentId}/launch")
  public ResponseEntity<RestResult> launch(@PathVariable String assessmentId)
  {
    return RestResponse.success();
  }

  @ApiOperation("Publish assessment")
  @PostMapping("/{assessmentId}/publish")
  public ResponseEntity<RestResult> publish(@PathVariable String assessmentId)
  {
    return RestResponse.success();
  }
}
