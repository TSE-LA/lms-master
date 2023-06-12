package mn.erin.lms.base.domain.usecase.course.dto;

import java.util.Set;

/**
 * @author Byambajav
 */
public class CalendarDayPropertiesDto
{
  private final String month;
  private final int index;
  private final Set<CalendarEventDto> events;

  public CalendarDayPropertiesDto(String month, int index, Set<CalendarEventDto> events)
  {
    this.month = month;
    this.index = index;
    this.events = events;
  }

  public String getMonth()
  {
    return month;
  }

  public int getIndex()
  {
    return index;
  }

  public Set<CalendarEventDto> getEvents()
  {
    return events;
  }

  public void addEvent(CalendarEventDto calendarEventDto){
    events.add(calendarEventDto);
  }
}
