package mn.erin.lms.base.mongo.document.assessment;

import java.util.Set;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * @author Bat-Erdene Tsogoo.
 */
@Document
public class MongoCourseAssessment
{
  @Id
  private String id;

  private Set<String> quizIdList;

  public MongoCourseAssessment()
  {
  }

  public MongoCourseAssessment(String id, Set<String> quizIdList)
  {
    this.id = id;
    this.quizIdList = quizIdList;
  }

  public String getId()
  {
    return id;
  }

  public Set<String> getQuizIdList()
  {
    return quizIdList;
  }
}
