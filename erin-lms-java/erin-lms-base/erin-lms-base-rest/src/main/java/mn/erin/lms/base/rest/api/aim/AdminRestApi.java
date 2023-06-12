package mn.erin.lms.base.rest.api.aim;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import mn.erin.domain.base.usecase.UseCaseException;
import mn.erin.infrastucture.rest.common.response.RestResponse;
import mn.erin.infrastucture.rest.common.response.RestResult;
import mn.erin.lms.base.domain.repository.LmsRepositoryRegistry;
import mn.erin.lms.base.domain.service.LmsServiceRegistry;
import mn.erin.lms.base.rest.api.BaseLmsRestApi;
import mn.erin.lms.base.domain.usecase.user.GetCurrentGroupAdmins;

/**
 * @author Bat-Erdene Tsogoo.
 */
@Api("Admin REST API")
@RequestMapping(value = "/lms/admins")
@RestController
public class AdminRestApi extends BaseLmsRestApi
{
  public AdminRestApi(LmsRepositoryRegistry lmsRepositoryRegistry,
      LmsServiceRegistry lmsServiceRegistry)
  {
    super(lmsRepositoryRegistry, lmsServiceRegistry);
  }

  @ApiOperation("Fetches the admins of the current user's group")
  @GetMapping
  public ResponseEntity<RestResult> readAll()
  {
    GetCurrentGroupAdmins getCurrentGroupAdmins = new GetCurrentGroupAdmins(lmsRepositoryRegistry, lmsServiceRegistry);

    try
    {
      return RestResponse.success(getCurrentGroupAdmins.execute(null));
    }
    catch (UseCaseException e)
    {
      return RestResponse.internalError(e.getMessage());
    }
  }
}
