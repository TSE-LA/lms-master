package mn.erin.lms.base.domain.service.exam;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import mn.erin.lms.base.aim.user.LearnerId;
import mn.erin.lms.base.domain.model.enrollment.ExamEnrollment;
import mn.erin.lms.base.domain.model.exam.ExamId;
import mn.erin.lms.base.domain.model.exam.ExamPublishStatus;
import mn.erin.lms.base.domain.model.task.LmsScheduledTask;
import mn.erin.lms.base.domain.model.task.TaskIdentifier;
import mn.erin.lms.base.domain.model.task.TaskType;
import mn.erin.lms.base.domain.repository.LmsRepositoryException;
import mn.erin.lms.base.domain.repository.LmsRepositoryRegistry;
import mn.erin.lms.base.domain.service.LmsTaskScheduler;

/**
 * @author mLkhagvasuren
 */
public class ExamPublicationServiceImpl implements ExamPublicationService
{
  private static final Logger LOG = LoggerFactory.getLogger(ExamPublicationServiceImpl.class);

  private final LmsTaskScheduler taskScheduler;
  private final LmsRepositoryRegistry lmsRepositoryRegistry;

  public ExamPublicationServiceImpl(LmsRepositoryRegistry lmsRepositoryRegistry, LmsTaskScheduler taskScheduler)
  {
    this.taskScheduler = taskScheduler;
    this.lmsRepositoryRegistry = lmsRepositoryRegistry;
  }

  @Override
  public void publishExamOn(ExamPublishTaskInfo taskInfo, Date date)
  {
    Date now = new Date();
    LmsScheduledTask task = new LmsScheduledTask(TaskIdentifier.PUBLISH + taskInfo.getExamId().getId(), date, TaskType.EXAM_PUBLICATION);
    if (date.before(now))
    {
      try
      {
        createEnrollment(taskInfo.getEnrolledLearners(), taskInfo.getExamId().getId());
        lmsRepositoryRegistry.getExamRepository().updateExamPublishStatus(taskInfo.getExamId().getId(), ExamPublishStatus.PUBLISHED);
        if (lmsRepositoryRegistry.getScheduledTaskRepository().exists(task.taskIdentifier))
        {
          lmsRepositoryRegistry.getScheduledTaskRepository().remove(task.taskIdentifier);
        }
      }
      catch (LmsRepositoryException e)
      {
        LOG.error("Failed to publish exam [{}], on [{}]", taskInfo.getExamId().getId(), now, e);
      }
    }
    else
    {
      try
      {
        lmsRepositoryRegistry.getExamRepository().updateExamPublishStatus(taskInfo.getExamId().getId(), ExamPublishStatus.PENDING);
      }
      catch (LmsRepositoryException e)
      {
        LOG.error("Failed to update exam pending status [{}], on [{}]", taskInfo.getExamId().getId(), now, e);
      }
      // cancel and reschedule due to change of publication date during update
      task.setAction(() -> {
        try
        {
          createEnrollment(taskInfo.getEnrolledLearners(), taskInfo.getExamId().getId());
          lmsRepositoryRegistry.getExamRepository().updateExamPublishStatus(taskInfo.getExamId().getId(), ExamPublishStatus.PUBLISHED);
        }
        catch (LmsRepositoryException e)
        {
          LOG.error("Failed to publish exam [{}], on [{}]", taskInfo.getExamId().getId(), date, e);
        }
      });
      Map<String, Object> enrollmentMap = new HashMap<>();
      enrollmentMap.put("examEnrolledLearners", taskInfo.getEnrolledLearners());
      enrollmentMap.put("examId", taskInfo.getExamId().getId());
      task.fillProperties(enrollmentMap);
      taskScheduler.reschedule(task);
    }
  }

  private void createEnrollment(Set<String> enrollingLearners, String examId)
  {
    Set<String> existingExamEnrollmentLearners = lmsRepositoryRegistry.getExamEnrollmentRepository().getAllReadLearnerByExamId(examId);
    // delete existing enrollment
    for (String existingEnrollmentLearner : existingExamEnrollmentLearners)
    {
      if (!enrollingLearners.contains(existingEnrollmentLearner))
      {
        lmsRepositoryRegistry.getExamEnrollmentRepository().deleteByExamIdAndLearnerId(ExamId.valueOf(examId), LearnerId.valueOf(existingEnrollmentLearner));
        LOG.info("Successfully deleted enrolled learner [{}] for exam [{}]!", existingEnrollmentLearner, examId);
      }
    }

    // create exam enrollment for enrolling learners (new)
    for (String learnerId : enrollingLearners)
    {
      ExamEnrollment existingExamEnrollment = lmsRepositoryRegistry.getExamEnrollmentRepository().getByExamIdAndLearnerId(examId, learnerId);
      if (null == existingExamEnrollment)
      {
        ExamEnrollment examEnrollment = lmsRepositoryRegistry.getExamEnrollmentRepository().createEnrollment(examId, learnerId, "r");
        LOG.info("Successfully enrolled learner [{}] for exam [{}]: \"{}\"", learnerId, examId, (null != examEnrollment));
      }
    }
  }
}
