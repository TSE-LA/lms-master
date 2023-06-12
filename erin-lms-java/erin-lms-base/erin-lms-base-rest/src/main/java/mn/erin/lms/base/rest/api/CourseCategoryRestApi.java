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
import mn.erin.lms.base.domain.service.OrganizationIdProvider;
import mn.erin.lms.base.domain.usecase.UpdateAutoEnrollments;
import mn.erin.lms.base.domain.usecase.category.CreateCourseCategory;
import mn.erin.lms.base.domain.usecase.category.DeleteCourseCategory;
import mn.erin.lms.base.domain.usecase.category.GetCourseCategories;
import mn.erin.lms.base.domain.usecase.category.UpdateAutoEnrollment;
import mn.erin.lms.base.domain.usecase.category.UpdateCourseCategory;
import mn.erin.lms.base.domain.usecase.category.dto.CourseCategoryDto;
import mn.erin.lms.base.domain.usecase.category.dto.CreateCourseCategoryInput;
import mn.erin.lms.base.domain.usecase.category.dto.GetCourseCategoriesInput;
import mn.erin.lms.base.domain.usecase.category.dto.UpdateAutoEnrollmentInput;
import mn.erin.lms.base.domain.usecase.category.dto.UpdateAutoEnrollmentsInput;
import mn.erin.lms.base.domain.usecase.category.dto.UpdateCourseCategoryInput;
import mn.erin.lms.base.rest.model.RestCategoriesAutoEnroll;
import mn.erin.lms.base.rest.model.RestCategoryAutoEnroll;
import mn.erin.lms.base.rest.model.RestCourseCategory;

/**
 * @author Bat-Erdene Tsogoo.
 */
@Api("Course category REST API")
@RequestMapping(value = "/lms/course-categories")
@RestController
public class CourseCategoryRestApi extends BaseLmsRestApi
{
  public CourseCategoryRestApi(LmsRepositoryRegistry lmsRepositoryRegistry,
      LmsServiceRegistry lmsServiceRegistry)
  {
    super(lmsRepositoryRegistry, lmsServiceRegistry);
  }

  @ApiOperation("Lists all course categories")
  @GetMapping
  public ResponseEntity<RestResult> readAll(@RequestParam(required = false) String parentCategoryId)
  {
    OrganizationIdProvider organizationIdProvider = lmsServiceRegistry.getOrganizationIdProvider();
    GetCourseCategoriesInput input = new GetCourseCategoriesInput(organizationIdProvider.getOrganizationId(), parentCategoryId);
    GetCourseCategories getCourseCategories = new GetCourseCategories(lmsRepositoryRegistry.getCourseCategoryRepository());
    List<CourseCategoryDto> result = getCourseCategories.execute(input);
    return RestResponse.success(result);
  }

  @ApiOperation("Lists all course categories")
  @GetMapping("/auto-enroll")
  public ResponseEntity<RestResult> readAllByAutoEnroll(@RequestParam(required = false) String parentCategoryId)
  {
    OrganizationIdProvider organizationIdProvider = lmsServiceRegistry.getOrganizationIdProvider();
    GetCourseCategoriesInput input = new GetCourseCategoriesInput(organizationIdProvider.getOrganizationId(), parentCategoryId);
    input.setAutoEnroll(true);
    GetCourseCategories getCourseCategories = new GetCourseCategories(lmsRepositoryRegistry.getCourseCategoryRepository());
    List<CourseCategoryDto> result = getCourseCategories.execute(input);
    return RestResponse.success(result);
  }

  @ApiOperation("Creates course category")
  @PostMapping
  public ResponseEntity<RestResult> createCategory(@RequestBody RestCourseCategory body)
  {
    Validate.notNull(body);
    if (StringUtils.isBlank(body.getParentCategoryId()) || StringUtils.isBlank(body.getCategoryName()))
    {
      return RestResponse.badRequest("Parent category ID and category name cannot be null or blank!");
    }

    CreateCourseCategory createCourseCategory = new CreateCourseCategory(lmsRepositoryRegistry.getCourseCategoryRepository(),
        lmsServiceRegistry.getOrganizationIdProvider());

    CreateCourseCategoryInput input = new CreateCourseCategoryInput(body.getParentCategoryId(), body.getCategoryName());
    if (!StringUtils.isBlank(body.getDescription()))
    {
      input.setDescription(body.getDescription());
    }
    input.setAutoEnroll(body.isAutoEnroll());

    try
    {
      return RestResponse.success(createCourseCategory.execute(input));
    }
    catch (UseCaseException e)
    {
      return RestResponse.internalError(e.getMessage());
    }
  }

  @ApiOperation("Updates course category")
  @PutMapping
  public ResponseEntity<RestResult> updateCategory(@RequestBody RestCourseCategory body)
  {
    Validate.notNull(body);
    if (StringUtils.isBlank(body.getCategoryId()) || StringUtils.isBlank(body.getCategoryName()))
    {
      return RestResponse.badRequest("Category ID and category name cannot be null or blank!");
    }

    UpdateCourseCategory updateCourseCategory = new UpdateCourseCategory(lmsRepositoryRegistry.getCourseCategoryRepository(),
        lmsServiceRegistry.getOrganizationIdProvider());

    UpdateCourseCategoryInput input = new UpdateCourseCategoryInput(body.getCategoryId(), body.getCategoryName(), body.getDescription(), body.isAutoEnroll());
    if (!StringUtils.isBlank(body.getDescription()))
    {
      input.setDescription(body.getDescription());
    }

    try
    {
      updateCourseCategory.execute(input);
      return RestResponse.success();
    }
    catch (UseCaseException e)
    {
      return RestResponse.internalError(e.getMessage());
    }
  }

  @ApiOperation("Updates auto enrollment of a category")
  @PostMapping("/auto-enroll/{categoryId}")
  public ResponseEntity<RestResult> updateCategoryAutoEnrollment(@PathVariable String categoryId, @RequestBody RestCategoryAutoEnroll body)
  {
    Validate.notNull(body);
    if (StringUtils.isBlank(categoryId))
    {
      return RestResponse.badRequest("Category ID cannot be blank!");
    }

    UpdateAutoEnrollment updateAutoEnrollment = new UpdateAutoEnrollment(lmsRepositoryRegistry.getCourseCategoryRepository());

    try
    {
      updateAutoEnrollment.execute(new UpdateAutoEnrollmentInput(categoryId, body.isAutoEnroll()));
      return RestResponse.success();
    }
    catch (UseCaseException e)
    {
      return RestResponse.internalError(e.getMessage());
    }
  }

  @ApiOperation("Updates auto enrollment of a categories")
  @PostMapping("/auto-enroll")
  public ResponseEntity<RestResult> updateCategoriesAutoEnrollment(@RequestBody RestCategoriesAutoEnroll body)
  {
    Validate.notNull(body);
    if (body.getIds() == null || body.getIds().isEmpty())
    {
      return RestResponse.badRequest("Category ID or category IDs cannot be blank or empty!");
    }

    UpdateAutoEnrollments updateAutoEnrollments = new UpdateAutoEnrollments(lmsRepositoryRegistry.getCourseCategoryRepository());

    try
    {
      updateAutoEnrollments.execute(new UpdateAutoEnrollmentsInput(body.getIds(), body.isAutoEnroll()));
      return RestResponse.success();
    }
    catch (UseCaseException e)
    {
      return RestResponse.internalError(e.getMessage());
    }
  }

  @ApiOperation("Deletes course category")
  @DeleteMapping("/{categoryId}")
  public ResponseEntity<RestResult> delete(@PathVariable String categoryId)
  {
    Validate.notEmpty(categoryId);
    DeleteCourseCategory deleteCourseCategory = new DeleteCourseCategory(lmsRepositoryRegistry.getCourseCategoryRepository(),
        lmsRepositoryRegistry.getCourseRepository());
    try
    {
      return RestResponse.success(deleteCourseCategory.execute(categoryId));
    }
    catch (UseCaseException e)
    {
      return RestResponse.internalError(e.getMessage());
    }
  }
}
