package mn.erin.lms.base.domain.service.exam;

import java.util.Date;

/**
 * @author mLkhagvasuren
 */
public interface ExamPublicationService
{
  void publishExamOn(ExamPublishTaskInfo taskInfo, Date date);
}
