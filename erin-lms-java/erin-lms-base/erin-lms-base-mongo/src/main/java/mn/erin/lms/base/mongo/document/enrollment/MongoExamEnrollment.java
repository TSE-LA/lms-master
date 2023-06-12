package mn.erin.lms.base.mongo.document.enrollment;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * @author Galsan Bayart
 */
@Document
public class MongoExamEnrollment
{
  @Id
  private String id;

  private String examId;
  private String learnerId;
  private String permission;

  public MongoExamEnrollment()
  {
  }

  public MongoExamEnrollment(String id, String examId, String learnerId, String permission)
  {
    this.id = id;
    this.examId = examId;
    this.learnerId = learnerId;
    this.permission = permission;
  }

  public String getId()
  {
    return id;
  }

  public void setId(String id)
  {
    this.id = id;
  }

  public String getExamId()
  {
    return examId;
  }

  public void setExamId(String examId)
  {
    this.examId = examId;
  }

  public String getLearnerId()
  {
    return learnerId;
  }

  public void setLearnerId(String learnerId)
  {
    this.learnerId = learnerId;
  }

  public String getPermission()
  {
    return permission;
  }

  public void setPermission(String permission)
  {
    this.permission = permission;
  }
}
