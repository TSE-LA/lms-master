package mn.erin.lms.base.rest.api;

import java.util.List;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
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
import mn.erin.lms.base.domain.usecase.category.CreateExamCategory;
import mn.erin.lms.base.domain.usecase.category.DeleteExamCategory;
import mn.erin.lms.base.domain.usecase.category.GetExamCategories;
import mn.erin.lms.base.domain.usecase.category.UpdateExamCategory;
import mn.erin.lms.base.domain.usecase.category.dto.ExamCategoryDto;
import mn.erin.lms.base.domain.usecase.category.dto.ExamCategoryInput;
import mn.erin.lms.base.rest.model.exam.RestExamCategory;

/**
 * @author Temuulen Naranbold
 */
@Api("Exam category REST API")
@RequestMapping(value = "/lms/exam-categories")
@RestController
public class ExamCategoryRestApi extends BaseLmsRestApi
{
  protected ExamCategoryRestApi(LmsRepositoryRegistry lmsRepositoryRegistry, LmsServiceRegistry lmsServiceRegistry)
  {
    super(lmsRepositoryRegistry, lmsServiceRegistry);
  }

  @ApiOperation("Creates the exam category")
  @PostMapping
  public ResponseEntity<RestResult> createExamCategory(@RequestBody RestExamCategory body)
  {
    Validate.notNull(body);
    if (StringUtils.isBlank(body.getName()))
    {
      return RestResponse.badRequest("Exam category name cannot be null or blank!");
    }

    CreateExamCategory createExamCategory = new CreateExamCategory(lmsRepositoryRegistry, lmsServiceRegistry);

    ExamCategoryInput examCategoryInput = new ExamCategoryInput(null, body.getIndex(), body.getParentCategoryId(), body.getName(), body.getDescription());
     try
     {
       return RestResponse.success(createExamCategory.execute(examCategoryInput));
     }
     catch (UseCaseException e)
     {
       return RestResponse.internalError(e.getMessage());
     }
  }

  @ApiOperation("Updates the exam category")
  @PutMapping
  public ResponseEntity<RestResult> updateExamCategory(@RequestBody RestExamCategory body)
  {
    Validate.notNull(body);
    if (StringUtils.isBlank(body.getId()) || StringUtils.isBlank(body.getName()))
    {
      return RestResponse.badRequest("Category ID and category name cannot be null or blank!");
    }

    UpdateExamCategory updateExamCategory = new UpdateExamCategory(lmsRepositoryRegistry, lmsServiceRegistry);

    ExamCategoryInput input = new ExamCategoryInput(body.getId(), body.getIndex(), body.getName(), body.getDescription());
    try
    {
      return RestResponse.success(updateExamCategory.execute(input));
    }
    catch (UseCaseException e)
    {
      return RestResponse.internalError(e.getMessage());
    }
  }

  @ApiOperation("Lists all exam categories")
  @GetMapping
  public ResponseEntity<RestResult> readAll(@RequestParam(required = false) String parentCategoryId)
  {
    GetExamCategories getExamCategories = new GetExamCategories(lmsRepositoryRegistry, lmsServiceRegistry);
    List<ExamCategoryDto> result = getExamCategories.execute(parentCategoryId);
    return RestResponse.success(result);
  }

  @ApiOperation("Deletes the exam category")
  @DeleteMapping("/{id}")
  public ResponseEntity<RestResult> delete(@PathVariable String id)
  {
    Validate.notBlank(id);

    DeleteExamCategory deleteExamCategory = new DeleteExamCategory(lmsRepositoryRegistry, lmsServiceRegistry);

    try
    {
      return RestResponse.success(deleteExamCategory.execute(id));
    }
    catch (UseCaseException e)
    {
      return RestResponse.internalError(e.getMessage());
    }
  }
}
