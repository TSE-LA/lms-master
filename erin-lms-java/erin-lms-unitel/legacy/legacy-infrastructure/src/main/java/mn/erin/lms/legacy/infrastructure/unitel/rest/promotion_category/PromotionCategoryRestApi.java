package mn.erin.lms.legacy.infrastructure.unitel.rest.promotion_category;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import mn.erin.domain.base.usecase.UseCaseException;
import mn.erin.infrastucture.rest.common.response.RestResponse;
import mn.erin.lms.legacy.domain.lms.repository.CourseCategoryRepository;
import mn.erin.lms.legacy.domain.unitel.usecase.category.create_category.CreatePromotionCategory;
import mn.erin.lms.legacy.domain.unitel.usecase.category.create_category.CreatePromotionCategoryInput;
import mn.erin.lms.legacy.domain.unitel.usecase.category.create_category.CreatePromotionCategoryOutput;
import mn.erin.lms.legacy.domain.unitel.usecase.category.delete_category.DeletePromotionCategory;
import mn.erin.lms.legacy.domain.unitel.usecase.category.delete_category.DeletePromotionCategoryInput;
import mn.erin.lms.legacy.domain.unitel.usecase.category.get_category.GetPromotionCategories;
import mn.erin.lms.legacy.domain.unitel.usecase.category.get_category.GetPromotionCategoryOutput;

/**
 * @author Bat-Erdene Tsogoo.
 */
@Api("Promotion Category")
@RequestMapping(value = "/legacy/promotion-category", name = "Provides 'UNITEL' promotion category features")
public class PromotionCategoryRestApi
{
  private static final Logger LOGGER = LoggerFactory.getLogger(PromotionCategoryRestApi.class);

  private final CourseCategoryRepository courseCategoryRepository;

  public PromotionCategoryRestApi(CourseCategoryRepository courseCategoryRepository)
  {
    this.courseCategoryRepository = Objects.requireNonNull(courseCategoryRepository, "CourseCategoryRepository cannot be null!");
  }

  @ApiOperation("Create Promotion Category")
  @PostMapping
  public ResponseEntity create(@RequestBody RestPromotionCategory request)
  {
    CreatePromotionCategoryInput input = new CreatePromotionCategoryInput(request.getCategoryName());
    input.setDescription(request.getDescription());

    CreatePromotionCategory createPromotionCategory = new CreatePromotionCategory(courseCategoryRepository);
    try
    {
      CreatePromotionCategoryOutput output = createPromotionCategory.execute(input);
      return RestResponse.success(output);
    }
    catch (UseCaseException e)
    {
      LOGGER.error(e.getMessage(), e);
      return RestResponse.internalError("Failed to create a promotion category. The cause: " + e.getMessage());
    }
  }

  @ApiOperation("Get All Promotion Categories")
  @GetMapping
  public ResponseEntity read()
  {
    GetPromotionCategories getPromotionCategories = new GetPromotionCategories(courseCategoryRepository);
    Set<GetPromotionCategoryOutput> result = getPromotionCategories.execute(null);

    return RestResponse.success(result);
  }

  @ApiOperation("Delete Promotion Category")
  @DeleteMapping
  public ResponseEntity delete(@RequestParam String categoryName)
  {
    DeletePromotionCategoryInput input = new DeletePromotionCategoryInput(categoryName);

    DeletePromotionCategory deletePromotionCategory = new DeletePromotionCategory(courseCategoryRepository);
    try
    {
      deletePromotionCategory.execute(input);
      return RestResponse.success();
    }
    catch (UseCaseException e)
    {
      LOGGER.error(e.getMessage(), e);
      return RestResponse.internalError("Failed to delete promotion category [" + categoryName + "]");
    }
  }

  @ApiOperation("Create default 'UNITEL' promotion categories")
  @PostMapping("/unitel")
  public ResponseEntity createUnitelCategories()
  {
    String[] defaultPromotionCategories = { "Мобайл", "Юнивишн", "Гэр", "LookTV", "Upoint", "Байгууллага", "Ддэш", "Гар утас", "Bundle" };

    try
    {
      List<CreatePromotionCategoryOutput> result = createCategory(defaultPromotionCategories);
      return RestResponse.success(result);
    }
      catch (UseCaseException e)
    {
      LOGGER.error(e.getMessage(), e);
      return RestResponse.internalError(e.getMessage());
    }
  }

  private List<CreatePromotionCategoryOutput> createCategory(String[] categories) throws UseCaseException
  {
    CreatePromotionCategory createPromotionCategory = new CreatePromotionCategory(courseCategoryRepository);

    List<CreatePromotionCategoryOutput> result = new ArrayList<>();

    for (String promotionCategory : categories)
    {
      CreatePromotionCategoryInput input = new CreatePromotionCategoryInput(promotionCategory);
      CreatePromotionCategoryOutput output = createPromotionCategory.execute(input);
      result.add(output);
    }

    return result;
  }
}
