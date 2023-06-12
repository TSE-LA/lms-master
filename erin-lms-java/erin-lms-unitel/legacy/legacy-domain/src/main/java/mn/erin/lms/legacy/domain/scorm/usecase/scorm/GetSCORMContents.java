/*
 * (C)opyright, 2019, ERIN SYSTEMS LLC
 * The source code is protected by copyright laws and international copyright
 * treaties, as well as other intellectual property laws and treaties.
 * All rights reserved.
 */

package mn.erin.lms.legacy.domain.scorm.usecase.scorm;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

import mn.erin.domain.base.usecase.UseCase;
import mn.erin.domain.base.usecase.UseCaseException;
import mn.erin.lms.legacy.domain.scorm.model.SCORMContent;
import mn.erin.lms.legacy.domain.scorm.repository.SCORMContentRepository;

/**
 * @author Bat-Erdene Tsogoo.
 */
public class GetSCORMContents implements UseCase<Void, List<SCORMContentOutput>>
{
  private final SCORMContentRepository scormContentRepository;

  public GetSCORMContents(SCORMContentRepository scormContentRepository)
  {
    this.scormContentRepository = Objects.requireNonNull(scormContentRepository, "SCORMContentRepository cannot be null!");
  }

  @Override
  public List<SCORMContentOutput> execute(Void nil) throws UseCaseException
  {
    Collection<SCORMContent> scormContents = scormContentRepository.listAll();

    if (scormContents == null || scormContents.isEmpty())
    {
      throw new UseCaseException("No SCORM content was found!");
    }

    List<SCORMContentOutput> outputs = new ArrayList<>();

    for (SCORMContent scormContent : scormContents)
    {
      SCORMContentOutput output = new SCORMContentOutput(scormContent.getScormContentId().getId(), scormContent.getName());
      outputs.add(output);
    }

    return outputs;
  }
}
