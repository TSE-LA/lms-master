package mn.erin.lms.base.domain.usecase.course;

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import mn.erin.domain.base.usecase.UseCaseException;
import mn.erin.lms.base.aim.LmsUserService;
import mn.erin.lms.base.domain.repository.LmsRepositoryRegistry;
import mn.erin.lms.base.domain.service.LmsServiceRegistry;
import mn.erin.lms.base.domain.usecase.UseCaseDelegator;
import mn.erin.lms.base.domain.usecase.course.dto.CourseDto;
import mn.erin.lms.base.domain.usecase.course.dto.GetCoursesInput;

/**
 * @author Dashnyam Bayarsaikhan
 */
public class GetCoursesTest
{
  public static final String COURSE_CATEGORY_ID = "CategoryId";

  private UseCaseDelegator<GetCoursesInput, List<CourseDto>> useCaseDelegator;
  private GetCourses getCourses;

  @Before
  public void setUp() throws Exception
  {
    LmsRepositoryRegistry lmsRepositoryRegistry = Mockito.mock(LmsRepositoryRegistry.class);
    LmsServiceRegistry lmsServiceRegistry = Mockito.mock(LmsServiceRegistry.class);
    useCaseDelegator = Mockito.mock(UseCaseDelegator.class);
    getCourses = new GetCourses(lmsRepositoryRegistry, lmsServiceRegistry, useCaseDelegator);
    LmsUserService lmsUserService = Mockito.mock(LmsUserService.class);
    Mockito.when(lmsServiceRegistry.getLmsUserService()).thenReturn(lmsUserService);
  }

  @Test(expected = NullPointerException.class)
  public void whenInputNull() throws UseCaseException
  {
    getCourses.execute(null);
  }

  @Test
  public void assertNull_whenCourseReturnNull() throws UseCaseException
  {
    GetCoursesInput getCoursesInput = new GetCoursesInput(COURSE_CATEGORY_ID);
    Mockito.when(useCaseDelegator.execute(Mockito.any(), Mockito.any())).thenReturn(null);
    Assert.assertNull(getCourses.execute(getCoursesInput));

  }

  @Test
  public void assertZero_whenReturnsEmptyList() throws UseCaseException
  {
    GetCoursesInput getCoursesInput = new GetCoursesInput(COURSE_CATEGORY_ID);
    List<CourseDto> courses = new ArrayList<>();
    Mockito.when(useCaseDelegator.execute(Mockito.any(), Mockito.any())).thenReturn(courses);
    List<CourseDto> returnedCourses = getCourses.execute(getCoursesInput);
    Assert.assertEquals(0, returnedCourses.size());
  }

  @Test
  public void assert_whenCourseIsReturned() throws UseCaseException
  {
    GetCoursesInput getCoursesInput = new GetCoursesInput(COURSE_CATEGORY_ID);
    List<CourseDto> courses = new ArrayList<>();
    for (int i = 0; i < 10; i++)
    {
      courses.add(new CourseDto.Builder("ID" + i).withTitle("title" + i).build());
    }
    Mockito.when(useCaseDelegator.execute(Mockito.any(), Mockito.any())).thenReturn(courses);
    List<CourseDto> returnedCourses = getCourses.execute(getCoursesInput);
    Assert.assertEquals(10, returnedCourses.size());
  }
}