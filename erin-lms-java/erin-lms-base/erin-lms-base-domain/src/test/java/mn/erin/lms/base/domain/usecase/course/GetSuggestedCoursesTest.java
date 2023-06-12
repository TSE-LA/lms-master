package mn.erin.lms.base.domain.usecase.course;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;

import mn.erin.domain.base.model.person.PersonId;
import mn.erin.domain.base.usecase.UseCaseException;
import mn.erin.lms.base.aim.LmsUserService;
import mn.erin.lms.base.aim.organization.DepartmentId;
import mn.erin.lms.base.aim.user.AuthorId;
import mn.erin.lms.base.aim.user.LmsAdmin;
import mn.erin.lms.base.aim.user.LmsUser;
import mn.erin.lms.base.domain.model.category.CourseCategoryId;
import mn.erin.lms.base.domain.model.course.Course;
import mn.erin.lms.base.domain.model.course.CourseDepartmentRelation;
import mn.erin.lms.base.domain.model.course.CourseDetail;
import mn.erin.lms.base.domain.model.course.CourseId;
import mn.erin.lms.base.domain.model.course.DateInfo;
import mn.erin.lms.base.domain.model.online_course.EmployeeType;
import mn.erin.lms.base.domain.repository.LmsRepositoryRegistry;
import mn.erin.lms.base.domain.service.LmsServiceRegistry;
import mn.erin.lms.base.domain.usecase.UseCaseDelegator;
import mn.erin.lms.base.domain.usecase.course.dto.CourseDto;
import mn.erin.lms.base.domain.usecase.course.dto.GetCoursesInput;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

/**
 * @author Dashnyam Bayarsaikhan
 */
public class GetSuggestedCoursesTest
{
  private static final int TOTAL = 10;
  private UseCaseDelegator<GetCoursesInput, List<CourseDto>> useCaseDelegator;

  private GetSuggestedCourses getSuggestedCourses;

  private List<Course> courses = new ArrayList<>();
  private List<CourseDto> courseDtos = new ArrayList<>();
  private Set<String> departments = new HashSet<>();

  @Before
  public void setUp() throws Exception
  {
    LmsRepositoryRegistry lmsRepositoryRegistry = mock(LmsRepositoryRegistry.class);
    LmsServiceRegistry lmsServiceRegistry = mock(LmsServiceRegistry.class);
    LmsUserService lmsUserService = mock(LmsUserService.class);
    useCaseDelegator = mock(UseCaseDelegator.class);

    LmsUser lmsUser = new LmsAdmin(PersonId.valueOf("personId"));

    when(lmsServiceRegistry.getLmsUserService()).thenReturn(lmsUserService);
    when(lmsUserService.getCurrentUser()).thenReturn(lmsUser);

    courses = new ArrayList<>();
    courseDtos = new ArrayList<>();
    departments = new HashSet<>();
    for (int i = 0; i < TOTAL; i++)
    {
      Course course = new Course(CourseId.valueOf("courseId" + i), CourseCategoryId.valueOf("categoryId"), new CourseDetail("Title"),
          AuthorId.valueOf("authorId"),
          new CourseDepartmentRelation(DepartmentId.valueOf("departmentId")));

      DateInfo dateInfo = new DateInfo();
      dateInfo.setCreatedDate(LocalDateTime.now());
      dateInfo.setModifiedDate(LocalDateTime.now());
      dateInfo.setPublishDate(LocalDateTime.now());
      course.getCourseDetail().setDateInfo(dateInfo);
      course.setCourseCategoryName("CategoryName");
      course.setCourseType(new EmployeeType());
      courses.add(course);
      departments.add(course.getCourseDepartmentRelation().getCourseDepartment().getId());
      courseDtos.add(CourseTestUtils.generateCourseDto(course, course.getCourseDetail()));
    }

    getSuggestedCourses = new GetSuggestedCourses(lmsRepositoryRegistry, lmsServiceRegistry,useCaseDelegator);
  }

  @Test(expected = NullPointerException.class)
  public void inputNull_throwsNullPointerException() throws UseCaseException
  {
    getSuggestedCourses.execute(null);
  }

  @Test
  public void getSuggestedCourses_success() throws UseCaseException
  {
    GetCoursesInput getCoursesInput = new GetCoursesInput("categoryId");
    when(useCaseDelegator.execute(any(),any())).thenReturn(courseDtos);
    List<CourseDto> returnedCourses = getSuggestedCourses.execute(getCoursesInput);
    assertEquals(10, returnedCourses.size());
  }
}