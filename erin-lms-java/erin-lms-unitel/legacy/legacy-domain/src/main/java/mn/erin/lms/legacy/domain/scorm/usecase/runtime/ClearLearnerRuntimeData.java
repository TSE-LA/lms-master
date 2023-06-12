/*
 * (C)opyright, 2019, ERIN SYSTEMS LLC
 * The source code is protected by copyright laws and international copyright
 * treaties, as well as other intellectual property laws and treaties.
 * All rights reserved.
 */

package mn.erin.lms.legacy.domain.scorm.usecase.runtime;

import java.util.HashSet;
import java.util.Set;

import mn.erin.domain.base.usecase.UseCase;
import mn.erin.domain.base.usecase.UseCaseException;
import mn.erin.lms.legacy.domain.scorm.model.SCORMContentId;
import mn.erin.lms.legacy.domain.scorm.repository.RuntimeDataRepository;
import mn.erin.lms.legacy.domain.scorm.repository.SCORMRepositoryException;

/**
 * @author Oyungerel Chuluunsukh.
 */
public class ClearLearnerRuntimeData extends SCORMRuntimeUseCase implements UseCase<RuntimeDataInput, Void>
{
  public ClearLearnerRuntimeData(RuntimeDataRepository runtimeDataRepository)
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
    Set<String> learnerIds = new HashSet<>();
    learnerIds.addAll(input.getLearnerIds());

    SCORMContentId scormContentId = SCORMContentId.valueOf(input.getScormContentId());

    try
    {
      for (String learnerId : learnerIds)
        runtimeDataRepository.clearLearnersRuntimeData(scormContentId, learnerId);
    }
    catch (SCORMRepositoryException e)
    {
      throw new UseCaseException(e.getMessage(), e);
    }

    return null;
  }
}
