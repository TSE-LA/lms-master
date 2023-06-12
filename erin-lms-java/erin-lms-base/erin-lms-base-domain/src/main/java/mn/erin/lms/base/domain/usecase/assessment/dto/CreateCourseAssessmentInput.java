package mn.erin.lms.base.domain.usecase.assessment.dto;

import java.util.HashSet;
import java.util.Set;

/**
 * @author Bat-Erdene Tsogoo.
 */
public class CreateCourseAssessmentInput
{
  private final String courseId;
  private Set<String> quizIds = new HashSet<>();

  public CreateCourseAssessmentInput(String courseId)
  {
    this.courseId = courseId;
  }

  public void addQuiz(String test)
  {
    this.quizIds.add(test);
  }

  public String getCourseId()
  {
    return courseId;
  }

  public Set<String> getQuizIds()
  {
    return quizIds;
  }
}
