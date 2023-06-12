package mn.erin.lms.base.mongo.document.task;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import mn.erin.lms.base.domain.model.task.TaskType;

/**
 * @author mLkhagvasuren
 */
@Document
public class MongoScheduledTask
{
  @Id
  private String taskIdentifier;
  private Date date;
  @Indexed
  private TaskType type;
  @Indexed
  private Completion completion;
  private Map<String, Object> properties = new HashMap<>();

  public String getTaskIdentifier()
  {
    return taskIdentifier;
  }

  public void setTaskIdentifier(String taskIdentifier)
  {
    this.taskIdentifier = taskIdentifier;
  }

  public Date getDate()
  {
    return date;
  }

  public void setDate(Date date)
  {
    this.date = date;
  }

  public Map<String, Object> getProperties()
  {
    return properties;
  }

  public void setProperties(Map<String, Object> properties)
  {
    this.properties = properties;
  }

  public TaskType getType()
  {
    return type;
  }

  public void setType(TaskType type)
  {
    this.type = type;
  }

  public Completion getCompletion()
  {
    return completion;
  }

  public void setCompletion(Completion completion)
  {
    this.completion = completion;
  }
}
