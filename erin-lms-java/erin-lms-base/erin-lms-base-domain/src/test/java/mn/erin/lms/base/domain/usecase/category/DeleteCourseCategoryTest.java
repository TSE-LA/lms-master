package mn.erin.lms.base.domain.usecase.category;

import java.util.Collections;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import mn.erin.domain.base.usecase.UseCaseException;
import mn.erin.lms.base.aim.organization.DepartmentId;
import mn.erin.lms.base.aim.user.AuthorId;
import mn.erin.lms.base.domain.model.category.CourseCategoryId;
import mn.erin.lms.base.domain.model.course.Course;
import mn.erin.lms.base.domain.model.course.CourseDepartmentRelation;
import mn.erin.lms.base.domain.model.course.CourseDetail;
import mn.erin.lms.base.domain.model.course.CourseId;
import mn.erin.lms.base.domain.repository.CourseCategoryRepository;
import mn.erin.lms.base.domain.repository.CourseRepository;

/**
 * @author Munkh
 */
@RunWith(MockitoJUnitRunner.class)
public class DeleteCourseCategoryTest
{
  private static final String DEFAULT_COURSE_ID = "courseId";
  private static final String DEFAULT_CATEGORY_ID = "categoryId";
  private static final String DEFAULT_CATEGORY_ID_WITH_COURSE = "categoryIdWithCourses";
  private static final String DEFAULT_NOT_EXISTING_CATEGORY_ID = "nonExistentCategoryId";

  @Mock
  private CourseCategoryRepository courseCategoryRepository;

  @Mock
  private CourseRepository courseRepository;

  private DeleteCourseCategory deleteCourseCategory;

  @Before
  public void setUp()
  {
    deleteCourseCategory = new DeleteCourseCategory(courseCategoryRepository, courseRepository);
  }

  @Test
  public void execute_test() throws UseCaseException
  {
    Mockito.when(courseCategoryRepository.exists(CourseCategoryId.valueOf(DEFAULT_CATEGORY_ID))).thenReturn(true);
    Mockito.when(courseRepository.listAll(CourseCategoryId.valueOf(DEFAULT_CATEGORY_ID))).thenReturn(Collections.emptyList());

    boolean isDeleted = deleteCourseCategory.execute(DEFAULT_CATEGORY_ID);

    Assert.assertTrue(isDeleted);
  }

  @Test
  public void assert_false_if_category_not_found() throws UseCaseException
  {
    Mockito.when(courseCategoryRepository.exists(CourseCategoryId.valueOf(DEFAULT_NOT_EXISTING_CATEGORY_ID))).thenReturn(false);

    boolean isDeleted = deleteCourseCategory.execute(DEFAULT_NOT_EXISTING_CATEGORY_ID);

    Assert.assertFalse(isDeleted);
  }

  @Test(expected = NullPointerException.class)
  public void empty_string_input_throws_null_pointer_exception_test() throws UseCaseException
  {
    deleteCourseCategory.execute(null);
  }

  @Test(expected = IllegalArgumentException.class)
  public void empty_string_input_throws_illegal_argument_exception_test() throws UseCaseException
  {
    deleteCourseCategory.execute("");
  }

  @Test(expected = UseCaseException.class)
  public void category_with_course_throws_use_case_exception_test() throws UseCaseException
  {
    Mockito.when(courseCategoryRepository.exists(CourseCategoryId.valueOf(DEFAULT_CATEGORY_ID_WITH_COURSE))).thenReturn(true);
    Mockito.when(courseRepository.listAll(CourseCategoryId.valueOf(DEFAULT_CATEGORY_ID_WITH_COURSE))).thenReturn(
        Collections.singletonList(new Course(
            CourseId.valueOf(DEFAULT_COURSE_ID),
            CourseCategoryId.valueOf(DEFAULT_CATEGORY_ID_WITH_COURSE),
            new CourseDetail("detail"),
            AuthorId.valueOf("authorId"),
            new CourseDepartmentRelation(DepartmentId.valueOf("departmentId"))
        ))
    );

    deleteCourseCategory.execute(DEFAULT_CATEGORY_ID_WITH_COURSE);
  }
}
