package mn.erin.lms.base.mongo.document.course;

import java.time.LocalDateTime;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * @author Temuulen Naranbold
 */
@Document
public class MongoLearnerCourseHistory
{
  @Id
  private ObjectId id;
  @Indexed
  private String courseId;
  @Indexed
  private String userId;
  @Indexed
  private String name;
  @Indexed
  private String type;
  @Indexed
  private double credit;
  @Indexed
  private LocalDateTime completionDate;
  @Indexed
  private Float percentage;

  public MongoLearnerCourseHistory()
  {
  }

  public MongoLearnerCourseHistory(String courseId, String userId, String name, String type, double credit, LocalDateTime completionDate, Float percentage)
  {
    this.courseId = courseId;
    this.userId = userId;
    this.name = name;
    this.type = type;
    this.credit = credit;
    this.completionDate = completionDate;
    this.percentage = percentage;
  }

  public ObjectId getId()
  {
    return id;
  }

  public void setId(ObjectId id)
  {
    this.id = id;
  }

  public String getCourseId()
  {
    return courseId;
  }

  public void setCourseId(String courseId)
  {
    this.courseId = courseId;
  }

  public String getName()
  {
    return name;
  }

  public void setName(String name)
  {
    this.name = name;
  }

  public String getType()
  {
    return type;
  }

  public void setType(String type)
  {
    this.type = type;
  }

  public double getCredit()
  {
    return credit;
  }

  public void setCredit(double credit)
  {
    this.credit = credit;
  }

  public String getUserId()
  {
    return userId;
  }

  public void setUserId(String userId)
  {
    this.userId = userId;
  }

  public LocalDateTime getCompletionDate()
  {
    return completionDate;
  }

  public void setCompletionDate(LocalDateTime completionDate)
  {
    this.completionDate = completionDate;
  }

  public Float getPercentage()
  {
    return percentage;
  }

  public void setPercentage(Float percentage)
  {
    this.percentage = percentage;
  }
}
