package mn.erin.lms.base.domain.usecase.course.dto;

import java.util.Set;

/**
 * @author Byambajav
 */
public class CourseCalendarDto
{
  private final Set<CalendarDayPropertiesDto> calendarDays;

  public CourseCalendarDto(Set<CalendarDayPropertiesDto> calendarDays)
  {
    this.calendarDays = calendarDays;
  }

  public Set<CalendarDayPropertiesDto> getCalendarDays()
  {
    return calendarDays;
  }
}
