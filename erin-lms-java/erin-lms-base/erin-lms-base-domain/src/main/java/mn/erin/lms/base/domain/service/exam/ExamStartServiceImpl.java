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
public class ExamStartServiceImpl implements ExamStartService
{
  private static final Logger LOGGER = LoggerFactory.getLogger(ExamStartServiceImpl.class);

  private final LmsRepositoryRegistry lmsRepositoryRegistry;
  private final LmsTaskScheduler lmsTaskScheduler;

  public ExamStartServiceImpl(LmsRepositoryRegistry lmsRepositoryRegistry, LmsTaskScheduler lmsTaskScheduler)
  {
    this.lmsRepositoryRegistry = lmsRepositoryRegistry;
    this.lmsTaskScheduler = lmsTaskScheduler;
  }

  @Override
  public void startExamOn(String examId, Date date)
  {
    Date now = new Date();
    LmsScheduledTask task = new LmsScheduledTask(TaskIdentifier.START + examId, date, TaskType.EXAM_START);

    if (date.before(now))
    {
      try
      {
        //if start date is passed start exam right away
        lmsRepositoryRegistry.getExamRepository().updateExamStatus(examId, ExamStatus.STARTED);
        if (lmsRepositoryRegistry.getScheduledTaskRepository().exists(task.taskIdentifier))
        {
          lmsRepositoryRegistry.getScheduledTaskRepository().remove(task.taskIdentifier);
        }
      }
      catch (LmsRepositoryException e)
      {
        LOGGER.error("Failed to start exam [{}] on [{}]", examId, now, e);
      }
    }
    else
    {
      //if start date is yet to come reschedule
      task.setAction(() -> {
        try
        {
          lmsRepositoryRegistry.getExamRepository().updateExamStatus(examId, ExamStatus.STARTED);
        }
        catch (LmsRepositoryException e)
        {
          LOGGER.error("Failed to start exam [{}] on [{}]", examId, date, e);
        }
      });
      Map<String, Object> map = new HashMap<>();
      map.put("examId", examId);
      task.fillProperties(map);
      lmsTaskScheduler.reschedule(task);
    }
  }
}
