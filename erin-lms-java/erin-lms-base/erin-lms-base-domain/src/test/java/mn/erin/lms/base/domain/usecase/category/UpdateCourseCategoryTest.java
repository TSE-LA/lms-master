package mn.erin.lms.base.domain.usecase.category;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import mn.erin.domain.base.usecase.UseCaseException;
import mn.erin.lms.base.aim.organization.OrganizationId;
import mn.erin.lms.base.domain.model.category.CourseCategoryId;
import mn.erin.lms.base.domain.repository.CourseCategoryRepository;
import mn.erin.lms.base.domain.repository.LmsRepositoryException;
import mn.erin.lms.base.domain.service.OrganizationIdProvider;
import mn.erin.lms.base.domain.usecase.category.dto.UpdateCourseCategoryInput;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

/**
 * @author Dashnyam Bayarsaikhan
 */
public class UpdateCourseCategoryTest
{
  private static final String ORGANIZATION_ID = "organizationId";
  private CourseCategoryRepository courseCategoryRepository;
  private OrganizationIdProvider organizationIdProvider;
  private UpdateCourseCategory updateCourseCategory;

  @Before
  public void setUp() throws Exception
  {
    courseCategoryRepository = mock(CourseCategoryRepository.class);
    organizationIdProvider = mock(OrganizationIdProvider.class);

    updateCourseCategory = new UpdateCourseCategory(courseCategoryRepository, organizationIdProvider);
  }

  @Test(expected = NullPointerException.class)
  public void input_null_throws_null_pointer_exception() throws UseCaseException
  {
    updateCourseCategory.execute(null);
  }

  @Test(expected = NullPointerException.class)
  public void input_id_is_null_throws_null_pointer_exception() throws UseCaseException
  {
    UpdateCourseCategoryInput input = new UpdateCourseCategoryInput(null, "name", "aaa", false);
    updateCourseCategory.execute(input);
  }

  @Test(expected = NullPointerException.class)
  public void input_name_is_null_throws_null_pointer_exception() throws UseCaseException
  {
    UpdateCourseCategoryInput input = new UpdateCourseCategoryInput("id", null, "aaa", false);
    updateCourseCategory.execute(input);
  }

  @Test(expected = UseCaseException.class)
  public void category_not_found_throws_usecase_exception() throws UseCaseException
  {
    UpdateCourseCategoryInput input = new UpdateCourseCategoryInput("id", "name", "aaa", false);
    when(organizationIdProvider.getOrganizationId()).thenReturn(ORGANIZATION_ID);
    when(courseCategoryRepository.exists(any(CourseCategoryId.class))).thenReturn(false);
    updateCourseCategory.execute(input);
  }

  @Test
  public void assert_null_when_category_updated() throws UseCaseException
  {
    UpdateCourseCategoryInput input = new UpdateCourseCategoryInput("id", "name", "aaa", false);
    when(organizationIdProvider.getOrganizationId()).thenReturn(ORGANIZATION_ID);
    when(courseCategoryRepository.exists(any(CourseCategoryId.class))).thenReturn(true);
    assertNull(updateCourseCategory.execute(input));
  }

  @Test(expected = UseCaseException.class)
  public void a() throws LmsRepositoryException, UseCaseException
  {
    UpdateCourseCategoryInput input = new UpdateCourseCategoryInput("id", "name", "aaa", false);
    when(organizationIdProvider.getOrganizationId()).thenReturn(ORGANIZATION_ID);
    when(courseCategoryRepository.exists(any(CourseCategoryId.class))).thenReturn(true);
    doThrow(LmsRepositoryException.class).when(courseCategoryRepository)
        .update(any(OrganizationId.class), any(CourseCategoryId.class), anyString(), anyString(), anyBoolean());
    updateCourseCategory.execute(input);
  }
}