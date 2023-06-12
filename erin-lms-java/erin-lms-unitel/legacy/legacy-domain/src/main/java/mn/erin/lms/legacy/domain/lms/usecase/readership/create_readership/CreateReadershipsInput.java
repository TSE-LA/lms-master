package mn.erin.lms.legacy.domain.lms.usecase.readership.create_readership;

import java.util.Set;

/**
 * @author Munkh
 */
public class CreateReadershipsInput
{
  private Set<String> courses;
  private String learnerId;

  public CreateReadershipsInput(Set<String> courses, String learnerId)
  {
    this.courses = courses;
    this.learnerId = learnerId;
  }

  public Set<String> getCourses()
  {
    return courses;
  }

  public void setCourses(Set<String> courses)
  {
    this.courses = courses;
  }

  public String getLearnerId()
  {
    return learnerId;
  }

  public void setLearnerId(String learnerId)
  {
    this.learnerId = learnerId;
  }
}
