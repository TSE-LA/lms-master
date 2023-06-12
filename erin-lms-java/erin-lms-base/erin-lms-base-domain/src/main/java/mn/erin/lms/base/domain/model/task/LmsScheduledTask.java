package mn.erin.lms.base.domain.model.task;

import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * @author mLkhagvasuren
 */
public final class LmsScheduledTask implements Runnable
{
  public final String taskIdentifier;
  public final Date date;
  public final TaskType type;
  private final Map<String, Object> properties = new HashMap<>();
  // not persisted
  private Runnable action;

  public LmsScheduledTask(String taskIdentifier, Date date, TaskType type)
  {
    this.taskIdentifier = taskIdentifier;
    this.date = date;
    this.type = type;
  }

  @Override
  public void run()
  {
    action.run();
  }

  public void setAction(Runnable action)
  {
    this.action = action;
  }

  public Object getProperty(String key)
  {
    return properties.get(key);
  }

  public void fillProperties(Map<String, Object> properties)
  {
    this.properties.putAll(properties);
  }

  public Map<String, Object> getProperties()
  {
    return Collections.unmodifiableMap(properties);
  }

  public String getStringProperty(String key)
  {
    return String.valueOf(properties.get(key));
  }

  @SuppressWarnings("unchecked")
  public Set<String> getSetProperty(String key)
  {

    return new HashSet<>(((Collection<String>) (properties.get(key) != null ? properties.get(key) : new HashSet<>())));
  }
}
