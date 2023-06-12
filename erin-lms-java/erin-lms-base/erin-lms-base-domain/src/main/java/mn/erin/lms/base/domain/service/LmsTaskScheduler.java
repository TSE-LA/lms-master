package mn.erin.lms.base.domain.service;

import mn.erin.lms.base.domain.model.task.LmsScheduledTask;

/**
 * @author Bat-Erdene Tsogoo.
 */
public interface LmsTaskScheduler
{
  void schedule(LmsScheduledTask task);

  TaskCancellationResult cancel(String taskIdentifier);

  void reschedule(LmsScheduledTask task);
}
