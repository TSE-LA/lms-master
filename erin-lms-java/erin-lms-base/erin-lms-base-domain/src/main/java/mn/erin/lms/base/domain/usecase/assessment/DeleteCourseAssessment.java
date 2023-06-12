package mn.erin.lms.base.domain.usecase.assessment;

import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import mn.erin.domain.base.usecase.UseCase;
import mn.erin.domain.base.usecase.UseCaseException;
import mn.erin.lms.base.domain.model.Authorized;
import mn.erin.lms.base.domain.model.assessment.CourseAssessment;
import mn.erin.lms.base.domain.model.assessment.QuizId;
import mn.erin.lms.base.domain.model.course.CourseId;
import mn.erin.lms.base.aim.user.Author;
import mn.erin.lms.base.domain.repository.CourseAssessmentRepository;
import mn.erin.lms.base.domain.repository.LmsRepositoryException;
import mn.erin.lms.base.domain.repository.QuizRepository;

/**
 * @author Bat-Erdene Tsogoo.
 */
@Authorized(users = { Author.class })
public class DeleteCourseAssessment implements UseCase<String, Boolean>
{
  private static final Logger LOGGER = LoggerFactory.getLogger(DeleteCourseAssessment.class);

  private final CourseAssessmentRepository courseAssessmentRepository;
  private final QuizRepository quizRepository;

  public DeleteCourseAssessment(CourseAssessmentRepository courseAssessmentRepository, QuizRepository quizRepository)
  {
    this.courseAssessmentRepository = Objects.requireNonNull(courseAssessmentRepository);
    this.quizRepository = Objects.requireNonNull(quizRepository);
  }

  @Override
  public Boolean execute(String input) throws UseCaseException
  {
    CourseId courseId = CourseId.valueOf(input);
    CourseAssessment courseAssessment = getCourseAssessment(courseId);

    if(courseAssessment == null)
    {
      return false;
    }

    for (QuizId quizId : courseAssessment.getQuizzes())
    {
      quizRepository.delete(quizId);
    }
    courseAssessmentRepository.delete(courseId);
    return true;
  }

  private CourseAssessment getCourseAssessment(CourseId courseId)
  {
    try
    {
      return courseAssessmentRepository.fetchById(courseId);
    }
    catch (LmsRepositoryException e)
    {
//      throw new UseCaseException("Failed to fetch the assessment of the course with the ID: [" + courseId.getId() + "]", e);
      LOGGER.warn("Failed to fetch the assessment of the course with the ID: [{}]", courseId.getId());
      return null;
    }
  }
}
