package mn.erin.lms.base.domain.service.exam;

import mn.erin.lms.base.domain.model.exam.ExamId;
import mn.erin.lms.base.domain.model.task.LmsScheduledTask;
import mn.erin.lms.base.domain.model.task.TaskType;
import mn.erin.lms.base.domain.repository.task.ScheduledTaskRepository;
import mn.erin.lms.base.domain.service.LmsServiceRegistry;

/**
 * @author mLkhagvasuren
 */
public class ScheduledTaskInitializerImpl implements ScheduledTaskInitializer
{
  private final ScheduledTaskRepository taskRepository;
  private final LmsServiceRegistry lmsServiceRegistry;

  public ScheduledTaskInitializerImpl(ScheduledTaskRepository taskRepository, LmsServiceRegistry lmsServiceRegistry)
  {
    this.taskRepository = taskRepository;
    this.lmsServiceRegistry = lmsServiceRegistry;
  }

  @Override
  public void startPendingTasks()
  {
    for (LmsScheduledTask task : taskRepository.getAllPendingTasks())
    {
      String examId = getExamId(task);
      if (task.type == TaskType.EXAM_START)
      {
        lmsServiceRegistry.getExamStartService().startExamOn(examId, task.date);
      }
      else if (task.type == TaskType.EXAM_EXPIRATION)
      {
        lmsServiceRegistry.getExamExpirationService().expireExamOn(examId, task.date);
      }
      else if (task.type == TaskType.EXAM_PUBLICATION)
      {
        ExamPublishTaskInfo examPublishTaskInfo = new ExamPublishTaskInfo();
        examPublishTaskInfo.setExamId(ExamId.valueOf(examId));
        examPublishTaskInfo.setEnrolledLearners(task.getSetProperty("examEnrolledLearners"));
        lmsServiceRegistry.getExamPublicationService().publishExamOn(examPublishTaskInfo, task.date);
      }
      else if (task.type == TaskType.COURSE_PUBLICATION)
      {
       //TODO implement this
      }
    }
  }

  private String getExamId(LmsScheduledTask task)
  {
    return task.getStringProperty("examId");
  }
}
