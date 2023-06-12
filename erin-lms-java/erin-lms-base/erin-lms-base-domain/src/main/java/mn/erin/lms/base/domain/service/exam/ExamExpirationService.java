package mn.erin.lms.base.domain.service.exam;

import java.util.Date;

/**
 * @author mLkhagvasuren
 */
public interface ExamExpirationService
{
  void expireExamOn(String examId, Date date);
}
