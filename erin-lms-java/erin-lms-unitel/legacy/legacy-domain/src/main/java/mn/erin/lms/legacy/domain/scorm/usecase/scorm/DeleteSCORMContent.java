/*
 * (C)opyright, 2019, ERIN SYSTEMS LLC
 * The source code is protected by copyright laws and international copyright
 * treaties, as well as other intellectual property laws and treaties.
 * All rights reserved.
 */

package mn.erin.lms.legacy.domain.scorm.usecase.scorm;

import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import mn.erin.domain.base.usecase.UseCase;
import mn.erin.domain.base.usecase.UseCaseException;
import mn.erin.lms.legacy.domain.scorm.model.SCORMContentId;
import mn.erin.lms.legacy.domain.scorm.repository.RuntimeDataRepository;
import mn.erin.lms.legacy.domain.scorm.repository.SCORMContentRepository;
import mn.erin.lms.legacy.domain.scorm.repository.SCORMRepositoryException;

/**
 * @author Bat-Erdene Tsogoo.
 */
public class DeleteSCORMContent implements UseCase<String, Void>
{
  private static final Logger LOGGER = LoggerFactory.getLogger(DeleteSCORMContent.class);

  private final SCORMContentRepository scormContentRepository;
  private final RuntimeDataRepository runtimeDataRepository;

  public DeleteSCORMContent(SCORMContentRepository scormContentRepository, RuntimeDataRepository runtimeDataRepository)
  {
    this.scormContentRepository = Objects.requireNonNull(scormContentRepository, "SCORMContentRepository cannot be null!");
    this.runtimeDataRepository = Objects.requireNonNull(runtimeDataRepository, "RuntimeDataRepository cannot be null!");
  }

  @Override
  public Void execute(String scormContentId) throws UseCaseException
  {
    SCORMContentId id = SCORMContentId.valueOf(scormContentId);

    try
    {
      scormContentRepository.delete(id);
    }
    catch (SCORMRepositoryException e)
    {
      LOGGER.error(e.getMessage(), e);
      throw new UseCaseException(e.getMessage(), e);
    }

    try
    {
      runtimeDataRepository.delete(id);
    }
    catch (SCORMRepositoryException e)
    {
      LOGGER.error(e.getMessage(), e);
      throw new UseCaseException(e.getMessage(), e);
    }

    return null;
  }
}
