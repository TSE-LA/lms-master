package mn.erin.lms.base.domain.service.impl;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.Validate;

import mn.erin.lms.base.domain.service.UseCaseResolver;
import mn.erin.lms.base.domain.usecase.UseCaseDelegator;

/**
 * @author Bat-Erdene Tsogoo.
 */
public class UseCaseResolverImpl implements UseCaseResolver
{
  private Map<String, UseCaseDelegator<?, ?>> delegatorMap = new HashMap<>();

  @Override
  public void registerUseCase(String useCaseName, UseCaseDelegator<?, ?> delegator)
  {
    this.delegatorMap.put(useCaseName, delegator);
  }

  @Override
  public UseCaseDelegator<?, ?> getUseCaseDelegator(String useCaseName)
  {
    Validate.notBlank(useCaseName, "Use-case name must be specified!");
    return delegatorMap.get(useCaseName);
  }
}
