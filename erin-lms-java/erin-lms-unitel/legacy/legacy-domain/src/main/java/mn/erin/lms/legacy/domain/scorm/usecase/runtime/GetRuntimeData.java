/*
 * (C)opyright, 2019, ERIN SYSTEMS LLC
 * The source code is protected by copyright laws and international copyright
 * treaties, as well as other intellectual property laws and treaties.
 * All rights reserved.
 */

package mn.erin.lms.legacy.domain.scorm.usecase.runtime;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import mn.erin.domain.base.usecase.UseCase;
import mn.erin.domain.base.usecase.UseCaseException;
import mn.erin.lms.legacy.domain.scorm.model.RuntimeData;
import mn.erin.lms.legacy.domain.scorm.model.SCORMContentId;
import mn.erin.lms.legacy.domain.scorm.repository.RuntimeDataRepository;
import mn.erin.lms.legacy.domain.scorm.repository.SCORMRepositoryException;

/**
 * @author Bat-Erdene Tsogoo.
 */
public class GetRuntimeData extends SCORMRuntimeUseCase implements UseCase<RuntimeDataInput, Set<RuntimeDataOutput>>
{
  private static final Logger LOGGER = LoggerFactory.getLogger(GetRuntimeData.class);

  public GetRuntimeData(RuntimeDataRepository runtimeDataRepository)
  {
    super(runtimeDataRepository);
  }

  @Override
  public Set<RuntimeDataOutput> execute(RuntimeDataInput input) throws UseCaseException
  {
    if (input == null)
    {
      throw new UseCaseException("Runtime data input is required!");
    }

    SCORMContentId scormContentId = SCORMContentId.valueOf(input.getScormContentId());

    try
    {
      List<RuntimeData> data;

      // List all the learner's runtime data of a SCORM content if no SCO is specified.
      if (StringUtils.isBlank(input.getScoName()))
      {
        data = runtimeDataRepository.listRuntimeData(scormContentId);
      }
      // If SCO is specified, then return the runtime data of that particular SCO.
      else
      {
        data = Collections.singletonList(runtimeDataRepository.getRuntimeData(scormContentId, input.getScoName()));
      }

      return data.stream().map(this::toResult).collect(Collectors.toSet());
    }
    catch (SCORMRepositoryException e)
    {
      LOGGER.error(e.getMessage(), e);
      throw new UseCaseException(e.getMessage(), e);
    }
  }
}
