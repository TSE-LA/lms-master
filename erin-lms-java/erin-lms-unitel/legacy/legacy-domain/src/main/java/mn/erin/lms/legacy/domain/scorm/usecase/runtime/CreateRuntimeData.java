/*
 * (C)opyright, 2019, ERIN SYSTEMS LLC
 * The source code is protected by copyright laws and international copyright
 * treaties, as well as other intellectual property laws and treaties.
 * All rights reserved.
 */

package mn.erin.lms.legacy.domain.scorm.usecase.runtime;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import mn.erin.domain.base.usecase.UseCase;
import mn.erin.domain.base.usecase.UseCaseException;
import mn.erin.lms.legacy.domain.scorm.model.RuntimeData;
import mn.erin.lms.legacy.domain.scorm.model.RuntimeDataId;
import mn.erin.lms.legacy.domain.scorm.model.SCO;
import mn.erin.lms.legacy.domain.scorm.model.SCORMContentId;
import mn.erin.lms.legacy.domain.scorm.repository.RuntimeDataRepository;
import mn.erin.lms.legacy.domain.scorm.repository.SCORMContentRepository;
import mn.erin.lms.legacy.domain.scorm.repository.SCORMRepositoryException;

/**
 * @author Bat-Erdene Tsogoo.
 */
public class CreateRuntimeData extends SCORMRuntimeUseCase implements UseCase<String, List<RuntimeDataOutput>>
{
  private static final Logger LOGGER = LoggerFactory.getLogger(CreateRuntimeData.class);

  private final SCORMContentRepository scormContentRepository;

  public CreateRuntimeData(RuntimeDataRepository runtimeDataRepository, SCORMContentRepository scormContentRepository)
  {
    super(runtimeDataRepository);
    this.scormContentRepository = Objects.requireNonNull(scormContentRepository, "SCORMContentRepository cannot be null!");
  }

  @Override
  public List<RuntimeDataOutput> execute(String scormContentId) throws UseCaseException
  {
    List<RuntimeDataOutput> results = new ArrayList<>();

    Set<SCO> scos;

    try
    {
      scos = scormContentRepository.getShareableContentObjects(SCORMContentId.valueOf(scormContentId));
    }
    catch (SCORMRepositoryException e)
    {
      LOGGER.error(e.getMessage(), e);
      throw new UseCaseException(e.getMessage(), e);
    }

    for (SCO sco : scos)
    {
      RuntimeData runtimeData = runtimeDataRepository.create(sco);

      if (runtimeData == null)
      {
        LOGGER.warn("Failed to create runtime data on SCO: {}. Rolling back...", sco.getName());
        deleteRuntimeData(results);
        throw new UseCaseException("Failed to create runtime data on SCO: [" + sco.getName() + "]");
      }

      results.add(toResult(runtimeData));
    }

    return results;
  }

  private void deleteRuntimeData(List<RuntimeDataOutput> runtimeData)
  {
    try
    {
      for (RuntimeDataOutput datum : runtimeData)
      {
        runtimeDataRepository.delete(RuntimeDataId.valueOf(datum.getRuntimeDataId()));
      }
    }
    catch (SCORMRepositoryException e)
    {
      LOGGER.error("An error occurred while deleting a Run-Time data.\nThe cause: {}", e.getMessage(), e);
    }
  }
}
