package mn.erin.lms.base.domain.service.exam;

import java.util.Optional;

import mn.erin.lms.base.domain.model.exam.Exam;

/**
 * @author mLkhagvasuren
 */
public interface ExamInteractionService
{
  Optional<Exam> getCurrentUserOngoingExam();
}
