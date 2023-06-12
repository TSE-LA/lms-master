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
import mn.erin.lms.base.domain.usecase.category.CreateQuestionCategory;
import mn.erin.lms.base.domain.usecase.category.DeleteQuestionCategory;
import mn.erin.lms.base.domain.usecase.category.GetQuestionCategories;
import mn.erin.lms.base.domain.usecase.category.UpdateQuestionCategory;
import mn.erin.lms.base.domain.usecase.category.dto.QuestionCategoryDto;
import mn.erin.lms.base.domain.usecase.category.dto.QuestionCategoryInput;
import mn.erin.lms.base.rest.model.exam.RestQuestionCategory;

/**
 * @author Galsan Bayart
 */

@Api
@RestController
@RequestMapping("lms/question-categories")
public class QuestionCategoryRestApi extends BaseLmsRestApi
{

  public QuestionCategoryRestApi(LmsRepositoryRegistry lmsRepositoryRegistry, LmsServiceRegistry lmsServiceRegistry)
  {
    super(lmsRepositoryRegistry, lmsServiceRegistry);
  }

  @ApiOperation("Creates question category")
  @PostMapping("/create")
  public ResponseEntity<RestResult> createQuestionCategory(@RequestBody RestQuestionCategory body)
  {
    Validate.notNull(body);

    if (StringUtils.isBlank(body.getName()))
    {
      return RestResponse.badRequest("Question category name cannot be null or blank!");
    }

    QuestionCategoryInput input = new QuestionCategoryInput(
        body.getParentCategoryId(),
        body.getIndex(),
        body.getName()
    );
    input.setDescription(body.getDescription());

    CreateQuestionCategory createQuestionCategory = new CreateQuestionCategory(lmsRepositoryRegistry, lmsServiceRegistry);

    try
    {
      return RestResponse.success(createQuestionCategory.execute(input));
    }
    catch (UseCaseException e)
    {
      return RestResponse.internalError(e.getMessage());
    }
  }

  @ApiOperation("Updates question category")
  @PutMapping("/update/{id}")
  public ResponseEntity<RestResult> updateQuestionCategory(@PathVariable String id, @RequestBody RestQuestionCategory body)
  {
    Validate.notNull(body);
    Validate.notBlank(id);
    if (!id.equals(body.getCategoryId()))
    {
      return RestResponse.badRequest("Invalid category id: " + id);
    }
    QuestionCategoryInput input = new QuestionCategoryInput(
        body.getIndex(),
        body.getName(),
        body.getCategoryId()
    );
    input.setDescription(body.getDescription());

    UpdateQuestionCategory updateQuestionCategory = new UpdateQuestionCategory(lmsRepositoryRegistry, lmsServiceRegistry);
    try
    {
      return RestResponse.success(updateQuestionCategory.execute(input));
    }
    catch (UseCaseException e)
    {
      return RestResponse.internalError(e.getMessage());
    }
  }

  @ApiOperation("Lists all question categories")
  @GetMapping()
  public ResponseEntity<RestResult> readAll(@RequestParam(required = false) String parentCategoryId) throws UseCaseException
  {
    GetQuestionCategories getQuestionCategories = new GetQuestionCategories(lmsRepositoryRegistry, lmsServiceRegistry);
    List<QuestionCategoryDto> result = getQuestionCategories.execute(parentCategoryId);
    return RestResponse.success(result);
  }

  @ApiOperation("Deletes question category")
  @DeleteMapping("/delete/{categoryId}")
  public ResponseEntity<RestResult> delete(@PathVariable String categoryId)
  {
    Validate.notBlank(categoryId);
    DeleteQuestionCategory deleteQuestionCategory = new DeleteQuestionCategory(lmsRepositoryRegistry, lmsServiceRegistry);

    try
    {
      return RestResponse.success(deleteQuestionCategory.execute(categoryId));
    }
    catch (UseCaseException e)
    {
      return RestResponse.internalError(e.getMessage());
    }
  }
}
