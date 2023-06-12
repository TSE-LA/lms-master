package mn.erin.lms.base.domain.usecase.enrollment.dto;

import java.util.Set;

/**
 * @author Bat-Erdene Tsogoo.
 */
public class CreateLearnerEnrollmentsInput
{
  private final String learnerId;
  private final Set<String> courses;
  private String enrollmentState;

  public CreateLearnerEnrollmentsInput(String learnerId, Set<String> courses)
  {
    this.learnerId = learnerId;
    this.courses = courses;
  }

  public CreateLearnerEnrollmentsInput(String learnerId, Set<String> courses, String enrollmentState)
  {
    this.learnerId = learnerId;
    this.courses = courses;
    this.enrollmentState = enrollmentState;
  }

  public String getLearnerId()
  {
    return learnerId;
  }

  public Set<String> getCourses()
  {
    return courses;
  }

  public String getEnrollmentState()
  {
    return enrollmentState;
  }
}
