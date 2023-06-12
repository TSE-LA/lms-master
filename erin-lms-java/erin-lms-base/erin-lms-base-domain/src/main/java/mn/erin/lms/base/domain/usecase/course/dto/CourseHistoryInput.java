package mn.erin.lms.base.domain.usecase.course.dto;

/**
 * @author Temuulen Naranbold
 */
public class CourseHistoryInput
{
  private String year;
  private String username;

  public String getYear()
  {
    return year;
  }

  public void setYear(String year)
  {
    this.year = year;
  }

  public String getUsername()
  {
    return username;
  }

  public void setUsername(String username)
  {
    this.username = username;
  }
}
