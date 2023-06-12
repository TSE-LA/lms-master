package mn.erin.lms.jarvis.rest.api;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import mn.erin.domain.base.usecase.UseCaseException;
import mn.erin.infrastucture.rest.common.response.RestResponse;
import mn.erin.infrastucture.rest.common.response.RestResult;
import mn.erin.lms.base.domain.repository.LmsRepositoryRegistry;
import mn.erin.lms.base.domain.service.LmsServiceRegistry;
import mn.erin.lms.base.rest.api.BaseLmsRestApi;
import mn.erin.lms.jarvis.domain.report.repository.FieldRepository;
import mn.erin.lms.jarvis.domain.report.usecase.GetAllFields;
import mn.erin.lms.jarvis.domain.report.usecase.SetupJTRII;

/**
 * @author Temuulen Naranbold
 */
@Api("Field REST API")
@RequestMapping(value = "/lms/field", name = "Provides LMS field features")
@RestController
public class FieldRestApi extends BaseLmsRestApi
{
  private final FieldRepository fieldRepository;

  protected FieldRestApi(LmsRepositoryRegistry lmsRepositoryRegistry,
      LmsServiceRegistry lmsServiceRegistry, FieldRepository fieldRepository)
  {
    super(lmsRepositoryRegistry, lmsServiceRegistry);
    this.fieldRepository = fieldRepository;
  }

  @ApiOperation("Create JTRII fields")
  @PostMapping("/create-fields")
  public ResponseEntity<RestResult> createField(@RequestParam String organizationId)
  {
    try
    {
      SetupJTRII setupJTRII = new SetupJTRII(fieldRepository);
      return RestResponse.success(setupJTRII.execute(organizationId));
    }
    catch (UseCaseException e)
    {
      return RestResponse.internalError(e.getMessage());
    }
  }

  @ApiOperation("Gets fields")
  @GetMapping("/get-fields")
  public ResponseEntity<RestResult> getField(@RequestParam String organizationId)
  {
    try
    {
      GetAllFields getAllFields = new GetAllFields(fieldRepository);
      return RestResponse.success(getAllFields.execute(organizationId));
    }
    catch (UseCaseException e)
    {
      return RestResponse.internalError(e.getMessage());
    }
  }
}
