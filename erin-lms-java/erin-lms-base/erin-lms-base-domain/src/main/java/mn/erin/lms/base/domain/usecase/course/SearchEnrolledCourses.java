package mn.erin.lms.base.domain.usecase.course;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import mn.erin.domain.base.usecase.UseCaseException;
import mn.erin.lms.base.domain.model.Authorized;
import mn.erin.lms.base.domain.model.course.Course;
import mn.erin.lms.base.domain.model.course.CourseId;
import mn.erin.lms.base.domain.model.enrollment.CourseEnrollment;
import mn.erin.lms.base.aim.user.Learner;
import mn.erin.lms.base.aim.user.LearnerId;
import mn.erin.lms.base.aim.user.Supervisor;
import mn.erin.lms.base.domain.repository.LmsRepositoryRegistry;
import mn.erin.lms.base.domain.service.LmsServiceRegistry;
import mn.erin.lms.base.domain.usecase.course.dto.CourseDto;

/**
 * @author Erdenetulga
 */
@Authorized(users = { Supervisor.class, Learner.class })
public class SearchEnrolledCourses extends CourseUseCase<Map<String, Object>, List<CourseDto>>
{
  private String currentUser = "";

  public SearchEnrolledCourses(LmsRepositoryRegistry lmsRepositoryRegistry,
      LmsServiceRegistry lmsServiceRegistry)
  {
    super(lmsRepositoryRegistry, lmsServiceRegistry);
  }

  @Override
  public List<CourseDto> execute(Map<String, Object> queryParameters) throws UseCaseException
  {
    for (Map.Entry<String, Object> entry : queryParameters.entrySet())
    {
      if (entry.getKey().equals("currentUser"))
      {
        currentUser = entry.getValue().toString();
      }
    }
    List<CourseEnrollment> enrollments = courseEnrollmentRepository.listAll(LearnerId.valueOf(this.currentUser));
    List<Course> courses = getCourses(queryParameters);

    return toOutput(enrollments, courses);
  }

  private List<Course> getCourses(Map<String, Object> queryParameters)
  {
    boolean searchByDescription = false;
    boolean searchByName = false;
    String text = "";
    for (Map.Entry<String, Object> entry : queryParameters.entrySet())
    {
      if (entry.getKey().equals("description"))
      {
        searchByDescription = Boolean.parseBoolean(entry.getValue().toString());
      }
      if (entry.getKey().equals("name"))
      {
        searchByName = Boolean.parseBoolean(entry.getValue().toString());
      }
      if (entry.getKey().equals("query"))
      {
        text = entry.getValue().toString();
      }
      if (entry.getKey().equals("currentUser"))
      {
        currentUser = entry.getValue().toString();
      }
    }
    List<Course> searchedCourses = courseRepository.search(text, searchByName, searchByDescription);

    if (searchedCourses.isEmpty())
    {
      return Collections.emptyList();
    }

    Set<String> duplicateSet = new HashSet<>();
    List<Course> removedDuplicates = searchedCourses.stream().filter(e -> duplicateSet.add(e.getCourseId().getId())).collect(Collectors.toList());
    removedDuplicates = new ArrayList<>(removedDuplicates);
    return removedDuplicates;
  }

  private List<CourseDto> toOutput(List<CourseEnrollment> enrollments, List<Course> courses)
  {
    Map<CourseId, CourseEnrollment> courseEnrollmentMap = enrollments.stream()
        .collect(Collectors.toMap(CourseEnrollment::getCourseId, enrollment -> enrollment));
    Set<CourseId> enrolledCourseIds = enrollments.stream().map(CourseEnrollment::getCourseId).collect(Collectors.toSet());

    List<CourseDto> result = new ArrayList<>();

    for (Course course : courses)
    {
      if (enrolledCourseIds.contains(course.getCourseId()))
      {
        CourseDto courseDto = toCourseDto(course);
        courseDto.setEnrollmentState(courseEnrollmentMap.get(course.getCourseId()).getEnrollmentState().name());
        result.add(courseDto);
      }
    }

    return result;
  }
}
