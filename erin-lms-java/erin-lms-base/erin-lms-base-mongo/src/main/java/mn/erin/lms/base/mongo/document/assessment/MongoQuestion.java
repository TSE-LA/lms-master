package mn.erin.lms.base.mongo.document.assessment;

import java.util.List;

/**
 * @author Bat-Erdene Tsogoo.
 */
public class MongoQuestion
{
  private MongoQuestionType type;
  private String title;
  private boolean required;
  private List<MongoAnswer> answers;

  public MongoQuestion()
  {
  }

  public MongoQuestion(MongoQuestionType type, String title, List<MongoAnswer> answers, boolean required)
  {
    this.type = type;
    this.title = title;
    this.answers = answers;
    this.required = required;
  }

  public MongoQuestionType getType()
  {
    return type;
  }

  public String getTitle()
  {
    return title;
  }

  public List<MongoAnswer> getAnswers()
  {
    return answers;
  }

  public boolean isRequired() {
    return required;
  }

  public void setRequired(boolean required) {
    this.required = required;
  }
}
