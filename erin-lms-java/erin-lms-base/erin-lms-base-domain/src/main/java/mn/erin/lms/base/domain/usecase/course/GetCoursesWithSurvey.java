package mn.erin.lms.base.domain.usecase.course;

import java.util.ArrayList;
import java.util.List;

import mn.erin.domain.base.usecase.UseCaseException;
import mn.erin.lms.base.domain.model.assessment.AssessmentId;
import mn.erin.lms.base.domain.model.course.Course;
import mn.erin.lms.base.domain.repository.LmsRepositoryRegistry;
import mn.erin.lms.base.domain.service.LmsServiceRegistry;
import mn.erin.lms.base.domain.usecase.course.dto.CourseDto;

/**
 * @author Bat-Erdene Tsogoo.
 */
public class GetCoursesWithSurvey extends CourseUseCase<String, List<CourseDto>>
{
  public GetCoursesWithSurvey(LmsRepositoryRegistry lmsRepositoryRegistry,
      LmsServiceRegistry lmsServiceRegistry)
  {
    super(lmsRepositoryRegistry, lmsServiceRegistry);
  }

  @Override
  public List<CourseDto> execute(String assessmentId) throws UseCaseException
  {
    List<Course> courses = courseRepository.listAll(AssessmentId.valueOf(assessmentId));

    List<CourseDto> dtos = new ArrayList<>();
    for (Course course : courses)
    {
      dtos.add(toCourseDto(course));
    }
    return dtos;
  }
}
