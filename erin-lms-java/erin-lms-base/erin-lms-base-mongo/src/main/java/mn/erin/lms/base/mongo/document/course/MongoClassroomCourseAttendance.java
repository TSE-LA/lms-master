package mn.erin.lms.base.mongo.document.course;

import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * @author Bat-Erdene Tsogoo.
 */
@Document
public class MongoClassroomCourseAttendance
{
  @Id
  private String id;
  private List<MongoAttendance> attendances;

  public MongoClassroomCourseAttendance()
  {
  }

  public MongoClassroomCourseAttendance(String id, List<MongoAttendance> attendances)
  {
    this.id = id;
    this.attendances = attendances;
  }

  public String getId()
  {
    return id;
  }

  public void setId(String id)
  {
    this.id = id;
  }

  public List<MongoAttendance> getAttendances()
  {
    return attendances;
  }

  public void setAttendances(List<MongoAttendance> attendances)
  {
    this.attendances = attendances;
  }
}
