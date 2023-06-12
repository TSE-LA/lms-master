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
import mn.erin.lms.base.domain.usecase.exam.CreateExamGroup;
import mn.erin.lms.base.domain.usecase.exam.DeleteExamGroup;
import mn.erin.lms.base.domain.usecase.exam.GetAllExamGroup;
import mn.erin.lms.base.domain.usecase.exam.UpdateExamGroup;
import mn.erin.lms.base.domain.usecase.exam.dto.ExamGroupInput;
import mn.erin.lms.base.rest.model.exam.RestExamGroup;

/**
 * @author Temuulen Naranbold
 */
@Api
@RestController
@RequestMapping("lms/exam-group")
public class ExamGroupRestApi extends BaseLmsRestApi
{
  protected ExamGroupRestApi(LmsRepositoryRegistry lmsRepositoryRegistry, LmsServiceRegistry lmsServiceRegistry)
  {
    super(lmsRepositoryRegistry, lmsServiceRegistry);
  }

  @ApiOperation("Creates exam group")
  @PostMapping
  public ResponseEntity<RestResult> create(@RequestBody RestExamGroup body)
  {
    Validate.notNull(body);

    ExamGroupInput input = new ExamGroupInput(body.getParentId(), body.getName(), body.getDescription());

    CreateExamGroup createExamGroup = new CreateExamGroup(lmsRepositoryRegistry, lmsServiceRegistry);
    try
    {
      return RestResponse.success(createExamGroup.execute(input));
    }
    catch (UseCaseException e)
    {
      return RestResponse.internalError(e.getMessage());
    }
  }

  @ApiOperation("Get all exam group")
  @GetMapping
  public ResponseEntity<RestResult> getExamGroup(@RequestParam(required = false) String parentId)
  {
    GetAllExamGroup getAllExamGroup = new GetAllExamGroup(lmsRepositoryRegistry, lmsServiceRegistry);
    try
    {
      return RestResponse.success(getAllExamGroup.execute(parentId));
    }
    catch (UseCaseException e)
    {
      return RestResponse.internalError(e.getMessage());
    }
  }

  @ApiOperation("Updates an exam group")
  @PutMapping
  public ResponseEntity<RestResult> update(@RequestBody RestExamGroup body)
  {
    Validate.notNull(body);

    ExamGroupInput input = new ExamGroupInput(body.getId(), body.getParentId(), body.getName(), body.getDescription());

    UpdateExamGroup updateExamGroup = new UpdateExamGroup(lmsRepositoryRegistry, lmsServiceRegistry);
    try
    {
      return RestResponse.success(updateExamGroup.execute(input));
    }
    catch (UseCaseException e)
    {
      return RestResponse.internalError(e.getMessage());
    }
  }

  @ApiOperation("Deletes an exam group")
  @DeleteMapping("/{id}")
  public ResponseEntity<RestResult> delete(@PathVariable String id)
  {
    DeleteExamGroup deleteExamGroup = new DeleteExamGroup(lmsRepositoryRegistry, lmsServiceRegistry);

    try
    {
      return RestResponse.success(deleteExamGroup.execute(id));
    }
    catch (UseCaseException e)
    {
      return RestResponse.internalError(e.getMessage());
    }
  }


}
