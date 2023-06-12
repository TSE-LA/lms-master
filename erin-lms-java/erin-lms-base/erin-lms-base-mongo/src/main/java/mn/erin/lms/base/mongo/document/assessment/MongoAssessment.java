package mn.erin.lms.base.mongo.document.assessment;

import java.time.LocalDateTime;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * @author Bat-Erdene Tsogoo.
 */
@Document
public class MongoAssessment
{
  @Id
  private String id;

  @Indexed
  private String name;

  @Indexed
  private MongoAssessmentStatus status;

  private String quizId;

  private String authorId;

  private String description;

  private LocalDateTime createdDate;

  private LocalDateTime modifiedDate;

  public MongoAssessment()
  {
  }

  public MongoAssessment(String id, String name, String quizId, String authorId, LocalDateTime createdDate,
      LocalDateTime modifiedDate)
  {
    this.id = id;
    this.name = name;
    this.quizId = quizId;
    this.authorId = authorId;
    this.createdDate = createdDate;
    this.modifiedDate = modifiedDate;
  }

  public void setQuizId(String quizId)
  {
    this.quizId = quizId;
  }

  public void setDescription(String description)
  {
    this.description = description;
  }

  public void setStatus(MongoAssessmentStatus status)
  {
    this.status = status;
  }

  public MongoAssessmentStatus getStatus()
  {
    return status;
  }

  public String getId()
  {
    return id;
  }

  public String getName()
  {
    return name;
  }

  public String getQuizId()
  {
    return quizId;
  }

  public String getAuthorId()
  {
    return authorId;
  }

  public LocalDateTime getCreatedDate()
  {
    return createdDate;
  }

  public LocalDateTime getModifiedDate()
  {
    return modifiedDate;
  }

  public String getDescription()
  {
    return description;
  }
}
