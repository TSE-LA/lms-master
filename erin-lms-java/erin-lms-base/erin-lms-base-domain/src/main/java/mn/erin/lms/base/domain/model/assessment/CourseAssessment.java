package mn.erin.lms.base.domain.model.assessment;

import java.util.Collections;
import java.util.Objects;
import java.util.Set;

import mn.erin.lms.base.domain.model.course.CourseId;

/**
 * @author Bat-Erdene Tsogoo.
 */
public class CourseAssessment
{
  private final Set<QuizId> quizzes;
  private final CourseId courseId;

  public CourseAssessment(CourseId courseId, Set<QuizId> quizzes)
  {
    this.quizzes = Objects.requireNonNull(quizzes, "Quizzes cannot be null!");
    this.courseId = Objects.requireNonNull(courseId, "Course ID cannot be null!");
  }

  public CourseId getCourseId()
  {
    return courseId;
  }

  public Set<QuizId> getQuizzes()
  {
    return Collections.unmodifiableSet(quizzes);
  }
}
