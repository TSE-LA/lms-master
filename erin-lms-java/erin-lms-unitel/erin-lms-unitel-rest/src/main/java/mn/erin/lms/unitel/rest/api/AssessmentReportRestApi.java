package mn.erin.lms.unitel.rest.api;

import java.time.LocalDate;
import java.util.List;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import mn.erin.domain.base.usecase.UseCaseException;
import mn.erin.infrastucture.rest.common.response.RestResponse;
import mn.erin.infrastucture.rest.common.response.RestResult;
import mn.erin.lms.base.domain.repository.LmsRepositoryRegistry;
import mn.erin.lms.base.domain.service.LmsServiceRegistry;
import mn.erin.lms.base.rest.api.BaseLmsRestApi;
import mn.erin.lms.unitel.domain.model.report.AssessmentReportItem;
import mn.erin.lms.unitel.domain.repository.UnitelLmsRepositoryRegistry;
import mn.erin.lms.unitel.domain.service.UnitelLmsServiceRegistry;
import mn.erin.lms.unitel.domain.usecase.GetAssessmentReport;
import mn.erin.lms.unitel.domain.usecase.dto.GetAssessmentReportInput;

/**
 * @author Bat-Erdene Tsogoo.
 */
@Api("Assessment report REST API")
@RequestMapping(value = "/lms/assessments-report", name = "Provides LMS assessment report features")
@RestController
public class AssessmentReportRestApi extends BaseLmsRestApi
{
  public AssessmentReportRestApi(LmsRepositoryRegistry lmsRepositoryRegistry, LmsServiceRegistry lmsServiceRegistry)
  {
    super(lmsRepositoryRegistry, lmsServiceRegistry);
  }

  @ApiOperation("Fetches assessment reports")
  @GetMapping
  public ResponseEntity<RestResult> readAll(
      @RequestParam String assessmentId,
      @RequestParam String courseId,
      @RequestParam String requestType,
      @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
      @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate
  )
  {
    GetAssessmentReport getAssessmentReport = new GetAssessmentReport((UnitelLmsRepositoryRegistry) lmsRepositoryRegistry,(UnitelLmsServiceRegistry) lmsServiceRegistry);
    GetAssessmentReportInput input = new GetAssessmentReportInput(assessmentId, requestType, courseId, startDate, endDate);

    try
    {
      List<AssessmentReportItem> result = getAssessmentReport.execute(input);
      return RestResponse.success(result);
    }
    catch (UseCaseException e)
    {
      return RestResponse.internalError(e.getMessage());
    }
  }
}
