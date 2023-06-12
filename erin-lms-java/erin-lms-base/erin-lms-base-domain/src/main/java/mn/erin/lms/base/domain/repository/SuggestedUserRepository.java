package mn.erin.lms.base.domain.repository;

import mn.erin.lms.base.domain.model.exam.SuggestedUser;

/**
 * @author Galsan Bayart.
 */
public interface SuggestedUserRepository
{
  boolean save(SuggestedUser suggestedUser);
}
