package mn.erin.lms.base.mongo.document.exam;

import java.util.Set;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * @author Galsan Bayart.
 */
@Document
public class MongoSuggestedUser
{
  @Id
  String id;

  String examId;

  Set<String> suggestedUser;

  public MongoSuggestedUser()
  {
  }

  public MongoSuggestedUser(String examId, Set<String> suggestedUser)
  {
    this.examId = examId;
    this.suggestedUser = suggestedUser;
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

  public Set<String> getSuggestedUser()
  {
    return suggestedUser;
  }

  public void setSuggestedUser(Set<String> suggestedUser)
  {
    this.suggestedUser = suggestedUser;
  }
}
