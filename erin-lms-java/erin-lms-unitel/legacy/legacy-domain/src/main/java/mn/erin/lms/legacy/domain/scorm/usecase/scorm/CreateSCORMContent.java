/*
 * (C)opyright, 2019, ERIN SYSTEMS LLC
 * The source code is protected by copyright laws and international copyright
 * treaties, as well as other intellectual property laws and treaties.
 * All rights reserved.
 */

package mn.erin.lms.legacy.domain.scorm.usecase.scorm;

import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import mn.erin.domain.base.usecase.UseCase;
import mn.erin.domain.base.usecase.UseCaseException;
import mn.erin.lms.legacy.domain.scorm.model.AssetId;
import mn.erin.lms.legacy.domain.scorm.model.ContentAggregation;
import mn.erin.lms.legacy.domain.scorm.model.SCORMContent;
import mn.erin.lms.legacy.domain.scorm.model.SCORMWrapper;
import mn.erin.lms.legacy.domain.scorm.repository.SCORMContentRepository;
import mn.erin.lms.legacy.domain.scorm.repository.SCORMRepositoryException;
import mn.erin.lms.legacy.domain.scorm.repository.SCORMWrapperRepository;

/**
 * @author Bat-Erdene Tsogoo.
 */
public class CreateSCORMContent implements UseCase<CreateSCORMContentInput, SCORMContentOutput>
{
  private static final Logger LOGGER = LoggerFactory.getLogger(CreateSCORMContent.class);

  private final SCORMContentRepository scormContentRepository;
  private final SCORMWrapperRepository scormWrapperRepository;

  public CreateSCORMContent(SCORMContentRepository scormContentRepository, SCORMWrapperRepository scormWrapperRepository)
  {
    this.scormContentRepository = Objects.requireNonNull(scormContentRepository, "SCORMContentRepository cannot be null!");
    this.scormWrapperRepository = Objects.requireNonNull(scormWrapperRepository, "SCORMWrapperRepository cannot be null!");
  }

  @Override
  public SCORMContentOutput execute(CreateSCORMContentInput input) throws UseCaseException
  {
    if (input == null)
    {
      throw new UseCaseException("SCORM content input cannot be null!");
    }

    try
    {
      ContentAggregation contentAggregation = getContentAggregation(input.getScormContentName(), input.getOrganizations());
      Optional<String> wrapperType = Optional.ofNullable(input.getWrapperType());

      SCORMWrapper scormWrapper;
      if (wrapperType.isPresent())
      {
        scormWrapper = scormWrapperRepository.getWrapper(wrapperType.get());
      }
      else
      {
        // If wrapper type is not specified, then the default one is used
        scormWrapper = scormWrapperRepository.getDefaultWrapper();
      }

      SCORMContent scormContent = scormContentRepository.create(scormWrapper, contentAggregation);

      return new SCORMContentOutput(scormContent.getScormContentId().getId(), scormContent.getName());
    }
    catch (SCORMRepositoryException e)
    {
      LOGGER.error(e.getMessage(), e);
      throw new UseCaseException(e.getMessage(), e);
    }
  }

  private ContentAggregation getContentAggregation(String scormContentName, Set<OrganizationInfo> organizationInfos)
      throws SCORMRepositoryException
  {
    Set<ContentAggregation.Organization> organizations = new LinkedHashSet<>();

    int counter = 0;
    for (OrganizationInfo organizationInfo : organizationInfos)
    {
      Set<ContentAggregation.Resource> resources = new LinkedHashSet<>();

      for (ResourceInfo resourceInfo : organizationInfo.getResources())
      {
        AssetId assetId = AssetId.valueOf(resourceInfo.getAssetId());
        ContentAggregation.Resource resource = scormContentRepository.getResource(assetId);

        resources.add(resource);
      }

      ContentAggregation.Organization organization = new ContentAggregation.Organization(organizationInfo.getTitle());
      // add the resources into the organization
      resources.forEach(organization::addResource);
      organization.setShortID("sco" + counter);
      organizations.add(organization);
      counter++;
    }

    ContentAggregation contentAggregation = new ContentAggregation(scormContentName);
    // add the organizations to the content aggregation
    organizations.forEach(contentAggregation::addOrganization);
    return contentAggregation;
  }
}
