package mn.erin.lms.base.domain.service.exam;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import mn.erin.lms.base.domain.model.exam.ExamStatus;
import mn.erin.lms.base.domain.model.task.LmsScheduledTask;
import mn.erin.lms.base.domain.model.task.TaskIdentifier;
import mn.erin.lms.base.domain.model.task.TaskType;
import mn.erin.lms.base.domain.repository.LmsRepositoryException;
import mn.erin.lms.base.domain.repository.LmsRepositoryRegistry;
import mn.erin.lms.base.domain.service.LmsTaskScheduler;

/**
 * @author mLkhagvasuren
 */
public class ExamExpirationServiceImpl implements ExamExpirationService
{
  private static final Logger LOGGER = LoggerFactory.getLogger(ExamExpirationServiceImpl.class);

  private final LmsRepositoryRegistry lmsRepositoryRegistry;
  private final LmsTaskScheduler lmsTaskScheduler;

  public ExamExpirationServiceImpl(LmsRepositoryRegistry lmsRepositoryRegistry, LmsTaskScheduler lmsTaskScheduler)
  {
    this.lmsRepositoryRegistry = lmsRepositoryRegistry;
    this.lmsTaskScheduler = lmsTaskScheduler;
  }

  @Override
  public void expireExamOn(String examId, Date date)
  {
    // immediately close the exam for past dates
    Date now = new Date();
    LmsScheduledTask task = new LmsScheduledTask(TaskIdentifier.CLOSE + examId, date, TaskType.EXAM_EXPIRATION);

    if (date.before(now))
    {
      try
      {
        lmsRepositoryRegistry.getExamRepository().updateExamStatus(examId, ExamStatus.FINISHED);
        if (lmsRepositoryRegistry.getScheduledTaskRepository().exists(task.taskIdentifier))
        {
          lmsRepositoryRegistry.getScheduledTaskRepository().remove(task.taskIdentifier);
        }
      }
      catch (LmsRepositoryException e)
      {
        LOGGER.error("Failed to close exam [{}] on [{}]", examId, now, e);
      }
    }
    else
    {
      // otherwise, schedule
      task.setAction(() -> {
        try
        {
          lmsRepositoryRegistry.getExamRepository().updateExamStatus(examId, ExamStatus.FINISHED);
        }
        catch (LmsRepositoryException e)
        {
          LOGGER.error("Failed to close exam [{}] on [{}]", examId, date, e);
        }
      });
      Map<String, Object> map = new HashMap<>();
      map.put("examId", examId);
      task.fillProperties(map);
      lmsTaskScheduler.reschedule(task);
    }
  }
}
