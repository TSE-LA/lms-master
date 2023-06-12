package mn.erin.lms.base.domain.service.exam;

import java.util.Date;

/**
 * @author mLkhagvasuren
 */
public interface ExamStartService
{
  void startExamOn(String examId, Date date);
}
