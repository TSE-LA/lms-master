package mn.erin.lms.base.domain.service.exam;

import java.util.Date;

import mn.erin.lms.base.domain.model.task.LmsScheduledTask;
import mn.erin.lms.base.domain.model.task.TaskIdentifier;
import mn.erin.lms.base.domain.model.task.TaskType;
import mn.erin.lms.base.domain.repository.LmsRepositoryRegistry;

/**
 * @author Temuulen Naranbold
 */
public class ExamScheduledTaskRemoverImpl implements ExamScheduledTaskRemover
{
  private final LmsRepositoryRegistry lmsRepositoryRegistry;

  public ExamScheduledTaskRemoverImpl(LmsRepositoryRegistry lmsRepositoryRegistry)
  {
    this.lmsRepositoryRegistry = lmsRepositoryRegistry;
  }

  @Override
  public void deleteScheduledTasks(String examId)
  {
    LmsScheduledTask startTask = new LmsScheduledTask(TaskIdentifier.START + examId, new Date(), TaskType.EXAM_START);
    if (lmsRepositoryRegistry.getScheduledTaskRepository().exists(startTask.taskIdentifier))
    {
      lmsRepositoryRegistry.getScheduledTaskRepository().remove(startTask.taskIdentifier);
    }

    LmsScheduledTask endTask = new LmsScheduledTask(TaskIdentifier.PUBLISH + examId, new Date(), TaskType.EXAM_START);
    if (lmsRepositoryRegistry.getScheduledTaskRepository().exists(endTask.taskIdentifier))
    {
      lmsRepositoryRegistry.getScheduledTaskRepository().remove(endTask.taskIdentifier);
    }

    LmsScheduledTask publishTask = new LmsScheduledTask(TaskIdentifier.CLOSE + examId, new Date(), TaskType.EXAM_START);
    if (lmsRepositoryRegistry.getScheduledTaskRepository().exists(publishTask.taskIdentifier))
    {
      lmsRepositoryRegistry.getScheduledTaskRepository().remove(publishTask.taskIdentifier);
    }
  }
}
