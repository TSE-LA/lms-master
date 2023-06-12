package mn.erin.lms.base.rest.api;

import java.util.List;

import io.swagger.annotations.Api;
import org.apache.commons.lang3.Validate;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import mn.erin.domain.aim.repository.AimRepositoryRegistry;
import mn.erin.domain.base.usecase.UseCaseException;
import mn.erin.infrastucture.rest.common.response.RestResponse;
import mn.erin.infrastucture.rest.common.response.RestResult;
import mn.erin.lms.base.domain.model.search.SearchInput;
import mn.erin.lms.base.domain.repository.LmsRepositoryRegistry;
import mn.erin.lms.base.domain.service.LmsServiceRegistry;
import mn.erin.lms.base.domain.usecase.SearchUser;

/**
 * @author Galsan Bayart.
 */
@Api
@RestController
@RequestMapping("lms/search")
public class SearchRestApi
{
  private final LmsRepositoryRegistry lmsRepositoryRegistry;
  private final LmsServiceRegistry lmsServiceRegistry;
  private final AimRepositoryRegistry aimRepositoryRegistry;

  public SearchRestApi(LmsRepositoryRegistry lmsRepositoryRegistry, LmsServiceRegistry lmsServiceRegistry,
      AimRepositoryRegistry aimRepositoryRegistry)
  {
    this.lmsRepositoryRegistry = lmsRepositoryRegistry;
    this.lmsServiceRegistry = lmsServiceRegistry;
    this.aimRepositoryRegistry = aimRepositoryRegistry;
  }

  @GetMapping("find")
  public ResponseEntity<RestResult> get(@RequestParam String searchString, @RequestParam List<String> selectedUsers)
  {
    Validate.notNull(searchString);
    SearchUser searchUser = new SearchUser(lmsRepositoryRegistry, lmsServiceRegistry, aimRepositoryRegistry);
    try
    {
      return RestResponse.success(
          searchUser.execute(new SearchInput(searchString, selectedUsers)));
    }
    catch (UseCaseException e)
    {
      return RestResponse.internalError(e.getMessage());
    }
  }
}
