package mn.erin.lms.base.domain.repository.task;

import java.util.Collection;

import mn.erin.lms.base.domain.model.task.LmsScheduledTask;

/**
 * @author mLkhagvasuren
 */
public interface ScheduledTaskRepository
{
  Collection<LmsScheduledTask> getAllPendingTasks();

  boolean exists(String taskIdentifier);

  void remove(String taskIdentifier);

  void save(LmsScheduledTask task);
}
