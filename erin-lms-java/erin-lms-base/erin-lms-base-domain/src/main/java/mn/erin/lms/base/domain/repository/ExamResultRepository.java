package mn.erin.lms.base.domain.repository;

import mn.erin.lms.base.domain.model.exam.ExamResult;

/**
 * @author Galsan Bayart.
 */
public interface ExamResultRepository
{
  boolean save(ExamResult examResult);
}
