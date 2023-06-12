package mn.erin.lms.unitel.domain.usecase.dto;

import java.time.LocalDate;
import java.util.Set;

/**
 * @author Bat-Erdene Tsogoo.
 */
public class GetCourseCountInput
{
  private final LocalDate startDate;
  private final LocalDate endDate;
  private final String parentCategoryId;

  private String authorId;
  private Set<String> courseTypes;

  public GetCourseCountInput(LocalDate startDate, LocalDate endDate, String parentCategoryId)
  {
    this.startDate = startDate;
    this.endDate = endDate;
    this.parentCategoryId = parentCategoryId;
    validateDates(startDate, endDate);
  }

  public void setCourseTypes(Set<String> courseTypes)
  {
    this.courseTypes = courseTypes;
  }

  public void setAuthorId(String authorId)
  {
    this.authorId = authorId;
  }

  public String getParentCategoryId()
  {
    return parentCategoryId;
  }

  public LocalDate getStartDate()
  {
    return startDate;
  }

  public LocalDate getEndDate()
  {
    return endDate;
  }

  public String getAuthorId()
  {
    return authorId;
  }

  public Set<String> getCourseTypes()
  {
    return courseTypes;
  }

  private void validateDates(LocalDate startDate, LocalDate endDate)
  {
    if (startDate.isAfter(endDate))
    {
      throw new IllegalArgumentException("Start date cannot be greater than end date");
    }
  }
}
