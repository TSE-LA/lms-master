package mn.erin.lms.base.domain.usecase.course.dto;

/**
 * @author Byambajav
 */
public class CalendarEventDto
{
  private final String title;
  private final String state;
  private final String courseId;
  private final String startTime;
  private final String eventType;

  public CalendarEventDto(String title, String state, String courseId, String startTime, String eventType)
  {
    this.title = title;
    this.state = state;
    this.courseId = courseId;
    this.startTime = startTime;
    this.eventType = eventType;
  }

  public String getTitle()
  {
    return title;
  }

  public String getState()
  {
    return state;
  }

  public String getCourseId()
  {
    return courseId;
  }

  public String getStartTime()
  {
    return startTime;
  }

  public String getEventType()
  {
    return eventType;
  }
}
