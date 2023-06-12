package mn.erin.lms.base.domain.repository;

import java.util.List;

import mn.erin.lms.base.domain.model.exam.question.QuestionState;

/**
 * @author Galsan Bayart
 */
public interface QuestionStateRepository
{
  QuestionState create(QuestionState questionState);

  QuestionState update(QuestionState questionState);

  List<QuestionState> getAllByTenantId(String tenantId);

  boolean delete(String questionStateId);
}
