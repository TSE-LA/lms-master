package mn.erin.lms.base.rest.scheduler;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Future;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

import mn.erin.lms.base.domain.model.task.LmsScheduledTask;
import mn.erin.lms.base.domain.repository.task.ScheduledTaskRepository;
import mn.erin.lms.base.domain.service.LmsTaskScheduler;
import mn.erin.lms.base.domain.service.TaskCancellationResult;

/**
 * @author Bat-Erdene Tsogoo.
 */
public class SpringThreadPoolTaskScheduler implements LmsTaskScheduler,  DisposableBean
{
  private static final Logger LOG = LoggerFactory.getLogger(SpringThreadPoolTaskScheduler.class);

  private final Map<String, Future<?>> scheduledProcessHolder = new ConcurrentHashMap<>();

  private final ScheduledTaskRepository taskRepository;
  private final ThreadPoolTaskScheduler taskScheduler;

  public SpringThreadPoolTaskScheduler(ScheduledTaskRepository taskRepository, ThreadPoolTaskScheduler taskScheduler)
  {
    this.taskRepository = taskRepository;
    this.taskScheduler = taskScheduler;
    taskScheduler.setPoolSize(10);
    // remove from thread pool when task is cancelled
    taskScheduler.setRemoveOnCancelPolicy(true);
    taskScheduler.initialize();
  }

  @Override
  public void schedule(LmsScheduledTask task)
  {
    CompositeRunnable runnable = new CompositeRunnable(task, () -> {
      scheduledProcessHolder.remove(task.taskIdentifier);
      taskRepository.remove(task.taskIdentifier);
    });
    scheduledProcessHolder.put(task.taskIdentifier, taskScheduler.schedule(runnable, task.date));
    taskRepository.save(task);
  }

  @Override
  public TaskCancellationResult cancel(String taskIdentifier)
  {
    TaskCancellationResult result = new TaskCancellationResult();
    Future<?> process = scheduledProcessHolder.get(taskIdentifier);
    if (process != null)
    {
      result.complete(process.isDone());
      result.cancelled(process.cancel(false));
      scheduledProcessHolder.remove(taskIdentifier);
    }
    else
    {
      LOG.warn("Task [{}] doesn't exist for cancellation", taskIdentifier);
    }
    taskRepository.remove(taskIdentifier);
    return result;
  }

  @Override
  public void destroy()
  {
    taskScheduler.shutdown();
  }

  @Override
  public void reschedule(LmsScheduledTask task)
  {
    // cancel and re-schedule if task exists, create new schedule otherwise
    cancel(task.taskIdentifier);
    schedule(task);
  }

  private static final class CompositeRunnable implements Runnable
  {
    private final Runnable taskOne;
    private final Runnable taskTwo;

    CompositeRunnable(Runnable taskOne, Runnable taskTwo)
    {
      this.taskOne = taskOne;
      this.taskTwo = taskTwo;
    }

    @Override
    public void run()
    {
      try
      {
        taskOne.run();
      }
      finally
      {
        // we want to release the task even if it failed in order to prevent memory leak
        taskTwo.run();
      }
    }
  }
}
