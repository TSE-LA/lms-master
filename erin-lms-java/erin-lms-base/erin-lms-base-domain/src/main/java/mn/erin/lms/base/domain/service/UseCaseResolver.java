package mn.erin.lms.base.domain.service;

import mn.erin.lms.base.domain.usecase.UseCaseDelegator;

/**
 * @author Bat-Erdene Tsogoo.
 */
public interface UseCaseResolver
{
  void registerUseCase(String useCaseName, UseCaseDelegator<?, ?> delegator);

  UseCaseDelegator<?, ?> getUseCaseDelegator(String useCaseName);
}
