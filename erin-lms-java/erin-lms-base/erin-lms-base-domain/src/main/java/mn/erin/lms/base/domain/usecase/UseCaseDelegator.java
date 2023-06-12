package mn.erin.lms.base.domain.usecase;

import mn.erin.domain.base.usecase.UseCase;
import mn.erin.domain.base.usecase.UseCaseException;
import mn.erin.lms.base.aim.user.LmsUser;

/**
 * @author Bat-Erdene Tsogoo.
 */
public interface UseCaseDelegator<I, O>
{
  <T extends LmsUser> void register(Class<T> user, UseCase<I, O> useCase);

  O execute(LmsUser user, I input) throws UseCaseException;
}
