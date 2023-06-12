package mn.erin.lms.base.domain.service.exam;

/**
 * @author Temuulen Naranbold
 */
public interface ExamScheduledTaskRemover
{
  void deleteScheduledTasks(String examId);
}
