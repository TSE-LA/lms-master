package mn.erin.lms.base.rest.api;

import io.swagger.annotations.Api;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import mn.erin.domain.aim.service.TenantIdProvider;
import mn.erin.domain.base.usecase.UseCaseException;
import mn.erin.infrastucture.rest.common.response.RestResponse;
import mn.erin.infrastucture.rest.common.response.RestResult;
import mn.erin.lms.base.domain.model.exam.question.QuestionState;
import mn.erin.lms.base.domain.model.exam.question.QuestionStateId;
import mn.erin.lms.base.domain.repository.LmsRepositoryRegistry;
import mn.erin.lms.base.domain.service.LmsServiceRegistry;
import mn.erin.lms.base.domain.usecase.exam.question.CreateQuestionState;

/**
 * @author Galsan Bayart
 */

@Api
@RestController
@RequestMapping("lms/question-state")
public class QuestionStateRestApi extends BaseLmsRestApi
{
  private final TenantIdProvider tenantIdProvider;

  public QuestionStateRestApi(LmsRepositoryRegistry lmsRepositoryRegistry, LmsServiceRegistry lmsServiceRegistry, TenantIdProvider tenantIdProvider)
  {
    super(lmsRepositoryRegistry, lmsServiceRegistry);
    this.tenantIdProvider = tenantIdProvider;
  }

  @PostMapping("/save")
  public ResponseEntity<RestResult> saveQuestionState(@RequestParam String name){
    CreateQuestionState createQuestionState = new CreateQuestionState(lmsRepositoryRegistry, lmsServiceRegistry);
    try
    {
      return RestResponse.success(createQuestionState.execute(new QuestionState(name, tenantIdProvider.getCurrentUserTenantId())));
    }
    catch (UseCaseException e)
    {
      return RestResponse.internalError(e.getMessage());
    }
  }


  @GetMapping("/getAll/{tenantId}")
  public ResponseEntity<RestResult> getAllQuestionStateByTenantId(@PathVariable String tenantId){
    return RestResponse.success(lmsRepositoryRegistry.getQuestionStateRepository().getAllByTenantId(tenantId));
  }

  @PutMapping("/update/{questionStateId}")
  public ResponseEntity<RestResult> updateQuestionState(@PathVariable String questionStateId, @RequestParam String name){
    return RestResponse.success(lmsRepositoryRegistry.getQuestionStateRepository().update(new QuestionState(QuestionStateId.valueOf(questionStateId), name, tenantIdProvider.getCurrentUserTenantId())));
  }

  @DeleteMapping("/delete/{questionCategoryId}")
  public ResponseEntity<RestResult> deleteById(@PathVariable String questionCategoryId){
    return RestResponse.success(lmsRepositoryRegistry.getQuestionStateRepository().delete(questionCategoryId));
  }
}
