package mn.erin.lms.base.mongo.document.exam;

import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * @author Galsan Bayart.
 */
@Document
public class MongoExamResult
{
  @Id
  private String id;
  private List<MongoExamResultEntity> mongoExamResults;

  public MongoExamResult()
  {
    //doNothing
  }

  public MongoExamResult(List<MongoExamResultEntity> mongoExamResults)
  {
    this.mongoExamResults = mongoExamResults;
  }

  public String getId()
  {
    return id;
  }

  public void setId(String id)
  {
    this.id = id;
  }

  public List<MongoExamResultEntity> getMongoExamResults()
  {
    return mongoExamResults;
  }

  public void setMongoExamResults(List<MongoExamResultEntity> mongoExamResults)
  {
    this.mongoExamResults = mongoExamResults;
  }
}
