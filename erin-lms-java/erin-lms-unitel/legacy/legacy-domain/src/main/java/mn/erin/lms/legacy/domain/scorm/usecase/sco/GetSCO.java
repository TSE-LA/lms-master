/*
 * (C)opyright, 2019, ERIN SYSTEMS LLC
 * The source code is protected by copyright laws and international copyright
 * treaties, as well as other intellectual property laws and treaties.
 * All rights reserved.
 */

package mn.erin.lms.legacy.domain.scorm.usecase.sco;

import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import mn.erin.domain.base.usecase.UseCase;
import mn.erin.domain.base.usecase.UseCaseException;
import mn.erin.lms.legacy.domain.scorm.model.SCO;
import mn.erin.lms.legacy.domain.scorm.model.SCORMContentId;
import mn.erin.lms.legacy.domain.scorm.repository.SCORMContentRepository;
import mn.erin.lms.legacy.domain.scorm.repository.SCORMRepositoryException;

/**
 * @author Bat-Erdene Tsogoo.
 */
public class GetSCO implements UseCase<String, Set<GetSCOResult>>
{
  private static final Logger LOGGER = LoggerFactory.getLogger(GetSCO.class);

  private final SCORMContentRepository scormContentRepository;

  public GetSCO(SCORMContentRepository scormContentRepository)
  {
    this.scormContentRepository = Objects.requireNonNull(scormContentRepository, "SCORMContentRepository cannot be null!");
  }

  @Override
  public Set<GetSCOResult> execute(String scormContentId) throws UseCaseException
  {
    if (StringUtils.isBlank(scormContentId))
    {
      throw new UseCaseException("SCORM content ID is missing!");
    }

    try
    {
      Set<SCO> scos = scormContentRepository.getShareableContentObjects(SCORMContentId.valueOf(scormContentId));

      Set<GetSCOResult> results = new LinkedHashSet<>();

      for (SCO sco : scos)
      {
        GetSCOResult result = new GetSCOResult(sco.getPath(), sco.getName());
        results.add(result);
      }

      return results;
    }
    catch (SCORMRepositoryException e)
    {
      LOGGER.error(e.getMessage(), e);
      throw new UseCaseException("Failed to get the SCOs of the SCORM content with the ID [" + scormContentId + "]", e);
    }
  }
}
