/*
 * (C)opyright, 2019, ERIN SYSTEMS LLC
 * The source code is protected by copyright laws and international copyright
 * treaties, as well as other intellectual property laws and treaties.
 * All rights reserved.
 */

package mn.erin.lms.legacy.domain.scorm.usecase.runtime;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import mn.erin.domain.base.usecase.UseCase;
import mn.erin.domain.base.usecase.UseCaseException;
import mn.erin.lms.legacy.domain.scorm.model.SCORMContentId;
import mn.erin.lms.legacy.domain.scorm.repository.RuntimeDataRepository;
import mn.erin.lms.legacy.domain.scorm.repository.SCORMRepositoryException;

/**
 * @author Bat-Erdene Tsogoo.
 */
public class ClearRuntimeData extends SCORMRuntimeUseCase implements UseCase<RuntimeDataInput, Void>
{
  private static final Logger LOGGER = LoggerFactory.getLogger(ClearRuntimeData.class);

  public ClearRuntimeData(RuntimeDataRepository runtimeDataRepository)
  {
    super(runtimeDataRepository);
  }

  @Override
  public Void execute(RuntimeDataInput input) throws UseCaseException
  {
    if (input == null)
    {
      throw new UseCaseException("Runtime data input is required!");
    }

    SCORMContentId scormContentId = SCORMContentId.valueOf(input.getScormContentId());

    try
    {
      if (StringUtils.isBlank(input.getScoName()))
      {
        runtimeDataRepository.clear(scormContentId);
      }
      else
      {
        runtimeDataRepository.clear(scormContentId, input.getScoName());
      }
    }
    catch (SCORMRepositoryException e)
    {
      LOGGER.error(e.getMessage(), e);
      throw new UseCaseException(e.getMessage(), e);
    }

    return null;
  }
}
