package mn.erin.lms.legacy.infrastructure.lms.rest.course;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ScheduledFuture;

/**
 * @author Bat-Erdene Tsogoo.
 */
final class ScheduledCourses
{
  private static Map<String, ScheduledFuture<?>> scheduledFutureMap = new HashMap<>();

  private ScheduledCourses()
  {
  }

  static void add(String courseId, ScheduledFuture<?> schedule)
  {
    checkForUnusedSchedules();
    scheduledFutureMap.put(courseId, schedule);
  }

  static void removeIfExists(String courseId)
  {
    checkForUnusedSchedules();
    ScheduledFuture<?> schedule = scheduledFutureMap.get(courseId);
    if (schedule != null && !schedule.isDone())
    {
      schedule.cancel(true);
      scheduledFutureMap.remove(courseId);
    }
  }

  private static void checkForUnusedSchedules()
  {
    for (Map.Entry<String, ScheduledFuture<?>> entry : scheduledFutureMap.entrySet())
    {
      if (entry.getValue().isDone())
      {
        scheduledFutureMap.remove(entry.getKey());
      }
    }
  }
}
