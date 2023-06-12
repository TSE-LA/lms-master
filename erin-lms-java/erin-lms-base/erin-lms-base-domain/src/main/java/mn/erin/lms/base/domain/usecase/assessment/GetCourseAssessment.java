package mn.erin.lms.base.domain.usecase.assessment;

import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import mn.erin.domain.base.model.EntityId;
import mn.erin.domain.base.usecase.UseCase;
import mn.erin.domain.base.usecase.UseCaseException;
import mn.erin.lms.base.domain.model.Authorized;
import mn.erin.lms.base.domain.model.assessment.CourseAssessment;
import mn.erin.lms.base.domain.model.course.CourseId;
import mn.erin.lms.base.aim.user.Author;
import mn.erin.lms.base.domain.repository.CourseAssessmentRepository;
import mn.erin.lms.base.domain.repository.LmsRepositoryException;
import mn.erin.lms.base.domain.usecase.assessment.dto.CourseAssessmentDto;

/**
 * @author Bat-Erdene Tsogoo.
 */
@Authorized(users = { Author.class })
public class GetCourseAssessment implements UseCase<String, CourseAssessmentDto>
{
  private final CourseAssessmentRepository courseAssessmentRepository;

  public GetCourseAssessment(CourseAssessmentRepository courseAssessmentRepository)
  {
    this.courseAssessmentRepository = Objects.requireNonNull(courseAssessmentRepository);
  }

  @Override
  public CourseAssessmentDto execute(String courseId) throws UseCaseException
  {
    try
    {
      CourseAssessment courseAssessment = courseAssessmentRepository.fetchById(CourseId.valueOf(courseId));
      Set<String> quizIdList = courseAssessment.getQuizzes().stream().map(EntityId::getId).collect(Collectors.toSet());
      return new CourseAssessmentDto(courseAssessment.getCourseId().getId(), quizIdList);
    }
    catch (LmsRepositoryException e)
    {
      throw new UseCaseException(e.getMessage(), e);
    }
  }
}
