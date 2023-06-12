package mn.erin.lms.base.domain.usecase.course.dto;

import org.apache.commons.lang3.Validate;

/**
 * @author Byambajav
 */
public class CourseCalendarInput
{
  private final String date;

  public CourseCalendarInput(String calendarDate)
  {
    this.date = Validate.notBlank(calendarDate);
  }

  public String getDate()
  {
    return date;
  }
}
