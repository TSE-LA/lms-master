package mn.erin.lms.base.domain.repository;

import mn.erin.lms.base.domain.model.exam.DeclinedUser;

/**
 * @author Galsan Bayart.
 */
public interface DeclinedUserRepository
{
  boolean save(DeclinedUser declinedUser);
}
