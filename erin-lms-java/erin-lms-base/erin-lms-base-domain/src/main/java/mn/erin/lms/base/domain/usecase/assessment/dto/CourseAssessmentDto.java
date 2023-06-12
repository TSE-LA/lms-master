package mn.erin.lms.base.domain.usecase.assessment.dto;

import java.util.Set;

/**
 * @author Bat-Erdene Tsogoo.
 */
public class CourseAssessmentDto
{
  private final String courseId;
  private final Set<String> quizIdList;

  public CourseAssessmentDto(String courseId, Set<String> quizIdList)
  {
    this.courseId = courseId;
    this.quizIdList = quizIdList;
  }

  public String getCourseId()
  {
    return courseId;
  }

  public Set<String> getQuizIdList()
  {
    return quizIdList;
  }
}
