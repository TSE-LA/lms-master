package mn.erin.lms.base.mongo.document.certificate;

import java.time.LocalDateTime;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;

/**
 * @author Bat-Erdene Tsogoo.
 */
public class MongoLearnerCertificate
{
  @Id
  private ObjectId id;

  @Indexed
  private String learnerId;

  @Indexed
  private String courseId;

  private String certificateId;

  private LocalDateTime receivedDate;

  public MongoLearnerCertificate()
  {
  }

  public MongoLearnerCertificate(String id, String learnerId, String courseId, String certificateId, LocalDateTime receivedDate)
  {
    this.id = new ObjectId(id);
    this.learnerId = learnerId;
    this.courseId = courseId;
    this.certificateId = certificateId;
    this.receivedDate = receivedDate;
  }

  public ObjectId getId()
  {
    return id;
  }

  public void setId(String id)
  {
    this.id = new ObjectId(id);
  }

  public String getLearnerId()
  {
    return learnerId;
  }

  public String getCourseId()
  {
    return courseId;
  }

  public String getCertificateId()
  {
    return certificateId;
  }

  public LocalDateTime getReceivedDate()
  {
    return receivedDate;
  }
}
