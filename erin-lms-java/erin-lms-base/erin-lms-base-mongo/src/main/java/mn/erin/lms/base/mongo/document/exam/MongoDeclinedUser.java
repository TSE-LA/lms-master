package mn.erin.lms.base.mongo.document.exam;

import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * @author Galsan Bayart
 */
@Document
public class MongoDeclinedUser
{
  @Id
  private String id;
  private String examId;
  private List<MongoDeclinedUserInfo> declinedUserInfos;


  public MongoDeclinedUser()
  {
  }

  public MongoDeclinedUser(String examId, List<MongoDeclinedUserInfo> declinedUserInfos)
  {
    this.examId = examId;
    this.declinedUserInfos = declinedUserInfos;
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

  public List<MongoDeclinedUserInfo> getDeclinedUserInfos()
  {
    return declinedUserInfos;
  }

  public void setDeclinedUserInfos(List<MongoDeclinedUserInfo> declinedUserInfos)
  {
    this.declinedUserInfos = declinedUserInfos;
  }
}
