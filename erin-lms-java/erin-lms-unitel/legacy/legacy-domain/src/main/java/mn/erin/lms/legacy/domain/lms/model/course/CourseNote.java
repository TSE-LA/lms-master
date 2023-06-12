package mn.erin.lms.legacy.domain.lms.model.course;

import java.io.Serializable;
import java.time.LocalDate;

/**
 * @author Bat-Erdene Tsogoo.
 */
public class CourseNote implements Serializable
{
  private static final long serialVersionUID = 1260520833229269560L;

  private final LocalDate date;
  private final String note;

  public CourseNote(LocalDate date, String note)
  {
    this.date = date;
    this.note = note;
  }

  public LocalDate getDate()
  {
    return date;
  }

  public String getNote()
  {
    return note;
  }
}
