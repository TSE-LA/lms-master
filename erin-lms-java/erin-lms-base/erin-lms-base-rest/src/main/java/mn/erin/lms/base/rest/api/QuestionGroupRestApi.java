package mn.erin.lms.base.rest.api;

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
import mn.erin.lms.base.domain.repository.LmsRepositoryRegistry;
import mn.erin.lms.base.domain.service.LmsServiceRegistry;
import mn.erin.lms.base.domain.usecase.exam.dto.question.QuestionGroupInput;
import mn.erin.lms.base.domain.usecase.exam.question.CreateQuestionGroup;
import mn.erin.lms.base.domain.usecase.exam.question.DeleteQuestionGroup;
import mn.erin.lms.base.domain.usecase.exam.question.GetQuestionGroupByParentId;
import mn.erin.lms.base.domain.usecase.exam.question.UpdateQuestionGroup;
import mn.erin.lms.base.rest.model.exam.RestQuestionGroup;

/**
 * @author Galsan Bayart
 */

@Api
@RestController
@RequestMapping("lms/question-groups")
public class QuestionGroupRestApi extends BaseLmsRestApi
{
  public QuestionGroupRestApi(LmsRepositoryRegistry lmsRepositoryRegistry, LmsServiceRegistry lmsServiceRegistry)
  {
    super(lmsRepositoryRegistry, lmsServiceRegistry);
  }

  @ApiOperation("Creates new question group")
  @PostMapping("/create")
  public ResponseEntity<RestResult> create(@RequestBody RestQuestionGroup restQuestionGroup)
  {
    Validate.notNull(restQuestionGroup);

    QuestionGroupInput input = new QuestionGroupInput(restQuestionGroup.getParentId(), restQuestionGroup.getName(), restQuestionGroup.getDescription());

    CreateQuestionGroup createQuestionCategory = new CreateQuestionGroup(lmsRepositoryRegistry, lmsServiceRegistry);
    try
    {
      return RestResponse.success(createQuestionCategory.execute(input));
    }
    catch (UseCaseException e)
    {
      return RestResponse.internalError(e.getMessage());
    }
  }

  @GetMapping()
  public ResponseEntity<RestResult> getAllByParentId(@RequestParam(required = false) String parentGroupId)
  {
    try
    {
      GetQuestionGroupByParentId getQuestionGroupByParentId = new GetQuestionGroupByParentId(lmsRepositoryRegistry, lmsServiceRegistry);
      return RestResponse.success(getQuestionGroupByParentId.execute(parentGroupId));
    }
    catch (UseCaseException e)
    {
      return RestResponse.internalError(e.getMessage());
    }
  }

  @ApiOperation("Updates the question group")
  @PutMapping("/update/{id}")
  public ResponseEntity<RestResult> update(@PathVariable String id, @RequestBody RestQuestionGroup restQuestionGroup)
  {
    Validate.notNull(restQuestionGroup);

    if (!id.equals(restQuestionGroup.getId()))
    {
      return RestResponse.badRequest("Invalid group id: " + id);
    }

    QuestionGroupInput input = new QuestionGroupInput(restQuestionGroup.getId(), restQuestionGroup.getParentId(), restQuestionGroup.getName(), restQuestionGroup.getDescription());

    UpdateQuestionGroup updateQuestionGroup = new UpdateQuestionGroup(lmsRepositoryRegistry, lmsServiceRegistry);

    try
    {
      return RestResponse.success(updateQuestionGroup.execute(input));
    }
    catch (UseCaseException e)
    {
      return RestResponse.internalError(e.getMessage());
    }
  }

  @ApiOperation("Deletes the question group")
  @DeleteMapping("/delete/{id}")
  public ResponseEntity<RestResult> delete(@PathVariable String id)
  {
    Validate.notBlank(id);

    DeleteQuestionGroup deleteQuestionGroup = new DeleteQuestionGroup(lmsRepositoryRegistry, lmsServiceRegistry);

    try
    {
      return RestResponse.success(deleteQuestionGroup.execute(id));
    }
    catch (UseCaseException e)
    {
      return RestResponse.internalError(e.getMessage());
    }
  }
}
