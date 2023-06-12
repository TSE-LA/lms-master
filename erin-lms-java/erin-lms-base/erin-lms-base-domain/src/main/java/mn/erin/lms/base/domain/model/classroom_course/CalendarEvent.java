package mn.erin.lms.base.domain.model.classroom_course;

import java.util.Date;

/**
 * @author Galsan Bayart.
 */
public class CalendarEvent
{
  String title;
  String state;
  String courseId;
  String startTime;
  Date date;
  String eventType;

  public CalendarEvent()
  {
  }

  public CalendarEvent(String title, String state, String courseId, String startTime, Date date, String eventType)
  {
    this.title = title;
    this.state = state;
    this.courseId = courseId;
    this.startTime = startTime;
    this.date = date;
    this.eventType = eventType;
  }

  public String getTitle()
  {
    return title;
  }

  public void setTitle(String title)
  {
    this.title = title;
  }

  public String getState()
  {
    return state;
  }

  public void setState(String state)
  {
    this.state = state;
  }

  public String getCourseId()
  {
    return courseId;
  }

  public void setCourseId(String courseId)
  {
    this.courseId = courseId;
  }

  public String getStartTime()
  {
    return startTime;
  }

  public void setStartTime(String startTime)
  {
    this.startTime = startTime;
  }

  public Date getDate()
  {
    return date;
  }

  public void setDate(Date date)
  {
    this.date = date;
  }

  public String getEventType()
  {
    return eventType;
  }

  public void setEventType(String eventType)
  {
    this.eventType = eventType;
  }
}
