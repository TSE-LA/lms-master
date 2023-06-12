package mn.erin.lms.base.mongo.document.exam.question;

import java.util.Date;
import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * @author Galsan Bayart
 */
@Document
public class MongoQuestion
{
  @Id
  private String id;
  private String value;
  private String categoryId;
  private String groupId;
  private String author;
  private Date createdDate;
  private MongoQuestionDetail detail;
  private MongoQuestionType type;
  private List<MongoAnswerEntity> answers;
  private MongoQuestionStatus status;
  private String modifiedUser;
  private Date modifiedDate;
  private int score;

  public MongoQuestion()
  {
  }

  public String getId()
  {
    return id;
  }

  public void setId(String id)
  {
    this.id = id;
  }

  public String getValue()
  {
    return value;
  }

  public void setValue(String value)
  {
    this.value = value;
  }

  public String getAuthor()
  {
    return author;
  }

  public void setAuthor(String author)
  {
    this.author = author;
  }

  public Date getCreatedDate()
  {
    return createdDate;
  }

  public void setCreatedDate(Date createdDate)
  {
    this.createdDate = createdDate;
  }

  public MongoQuestionDetail getDetail()
  {
    return detail;
  }

  public void setDetail(MongoQuestionDetail detail)
  {
    this.detail = detail;
  }

  public MongoQuestionType getType()
  {
    return type;
  }

  public void setType(MongoQuestionType type)
  {
    this.type = type;
  }

  public List<MongoAnswerEntity> getAnswers()
  {
    return answers;
  }

  public void setAnswers(List<MongoAnswerEntity> answers)
  {
    this.answers = answers;
  }

  public MongoQuestionStatus getStatus()
  {
    return status;
  }

  public void setStatus(MongoQuestionStatus status)
  {
    this.status = status;
  }

  public String getModifiedUser()
  {
    return modifiedUser;
  }

  public void setModifiedUser(String modifiedUser)
  {
    this.modifiedUser = modifiedUser;
  }

  public Date getModifiedDate()
  {
    return modifiedDate;
  }

  public void setModifiedDate(Date modifiedDate)
  {
    this.modifiedDate = modifiedDate;
  }

  public int getScore()
  {
    return score;
  }

  public void setScore(int score)
  {
    this.score = score;
  }

  public String getCategoryId()
  {
    return categoryId;
  }

  public void setCategoryId(String categoryId)
  {
    this.categoryId = categoryId;
  }

  public String getGroupId()
  {
    return groupId;
  }

  public void setGroupId(String groupId)
  {
    this.groupId = groupId;
  }
}
