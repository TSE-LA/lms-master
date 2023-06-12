/*
 * (C)opyright, 2019, ERIN SYSTEMS LLC
 * The source code is protected by copyright laws and international copyright
 * treaties, as well as other intellectual property laws and treaties.
 * All rights reserved.
 */

package mn.erin.lms.legacy.infrastructure.scorm.rest;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import mn.erin.domain.base.usecase.UseCaseException;
import mn.erin.infrastucture.rest.common.response.RestResponse;
import mn.erin.infrastucture.rest.common.response.RestResult;
import mn.erin.lms.legacy.domain.scorm.repository.RuntimeDataRepository;
import mn.erin.lms.legacy.domain.scorm.repository.SCORMContentRepository;
import mn.erin.lms.legacy.domain.scorm.repository.SCORMWrapperRepository;
import mn.erin.lms.legacy.domain.scorm.usecase.runtime.ClearRuntimeData;
import mn.erin.lms.legacy.domain.scorm.usecase.runtime.CreateRuntimeData;
import mn.erin.lms.legacy.domain.scorm.usecase.runtime.GetRuntimeData;
import mn.erin.lms.legacy.domain.scorm.usecase.runtime.RuntimeDataInfo;
import mn.erin.lms.legacy.domain.scorm.usecase.runtime.RuntimeDataInput;
import mn.erin.lms.legacy.domain.scorm.usecase.runtime.RuntimeDataOutput;
import mn.erin.lms.legacy.domain.scorm.usecase.runtime.SaveRuntimeData;
import mn.erin.lms.legacy.domain.scorm.usecase.runtime.SaveRuntimeDataInput;
import mn.erin.lms.legacy.domain.scorm.usecase.sco.GetSCO;
import mn.erin.lms.legacy.domain.scorm.usecase.sco.GetSCOResult;
import mn.erin.lms.legacy.domain.scorm.usecase.scorm.CreateSCORMContent;
import mn.erin.lms.legacy.domain.scorm.usecase.scorm.CreateSCORMContentInput;
import mn.erin.lms.legacy.domain.scorm.usecase.scorm.DeleteSCORMContent;
import mn.erin.lms.legacy.domain.scorm.usecase.scorm.GetSCORMContents;
import mn.erin.lms.legacy.domain.scorm.usecase.scorm.OrganizationInfo;
import mn.erin.lms.legacy.domain.scorm.usecase.scorm.ResourceInfo;
import mn.erin.lms.legacy.domain.scorm.usecase.scorm.SCORMContentOutput;

/**
 * @author Bat-Erdene Tsogoo.
 */
@Api("SCORM Content REST Api")
@RequestMapping(value = "/legacy/scorm-contents", name = "Provides access to SCORM content features")
public class SCORMContentRestApi
{
  private static final String ERROR_MSG_CAUSE = "The cause: %s";

  private final SCORMContentRepository scormContentRepository;
  private final SCORMWrapperRepository scormWrapperRepository;
  private final RuntimeDataRepository runtimeDataRepository;

  public SCORMContentRestApi(SCORMContentRepository scormContentRepository, SCORMWrapperRepository scormWrapperRepository,
      RuntimeDataRepository runtimeDataRepository)
  {
    this.scormContentRepository = Objects.requireNonNull(scormContentRepository, "SCORMContentRepository cannot be null!");
    this.scormWrapperRepository = Objects.requireNonNull(scormWrapperRepository, "SCORMWrapperRepository cannot be null!");
    this.runtimeDataRepository = Objects.requireNonNull(runtimeDataRepository, "RuntimeDataRepository cannot be null!");
  }

  @ApiOperation(value = "Gets the list of all SCORM contents",
      notes = "The result contains the id and name of a SCORM content")
  @GetMapping
  @ResponseBody
  public ResponseEntity readAll()
  {
    GetSCORMContents getScormContents = new GetSCORMContents(scormContentRepository);
    try
    {
      List<SCORMContentOutput> output = getScormContents.execute(null);

      return RestResponse.success(output);
    }
    catch (UseCaseException e)
    {
      return RestResponse.internalError(String.format("Failed to get SCORM contents\n"
          + ERROR_MSG_CAUSE, e.getMessage()));
    }
  }

  @ApiOperation(value = "Gets the list of all SCOs of a SCORM content",
      notes = "The result contains the URLs and names of SCOs")
  @GetMapping(value = "/{scormContentId}")
  @ResponseBody
  public ResponseEntity<RestResult> readById(@PathVariable String scormContentId)
  {
    GetSCO getShareableContentObjects = new GetSCO(scormContentRepository);

    RuntimeDataInput input = new RuntimeDataInput(scormContentId);
    GetRuntimeData getRuntimeData = new GetRuntimeData(runtimeDataRepository);

    try
    {
      Set<GetSCOResult> scos = getShareableContentObjects.execute(scormContentId);
      Collection<RuntimeDataOutput> runtimeData = getRuntimeData.execute(input);

      if (runtimeData.isEmpty())
      {
        CreateRuntimeData createRuntimeData = new CreateRuntimeData(runtimeDataRepository, scormContentRepository);
        runtimeData = createRuntimeData.execute(scormContentId);
      }

      Set<RestSCOModel> response = new LinkedHashSet<>();
      for (GetSCOResult sco : scos)
      {
        String scoName = sco.getName();

        for (RuntimeDataOutput datum : runtimeData)
        {
          if (scoName.equals(datum.getScoName()))
          {
            response.add(new RestSCOModel(scoName, sco.getPath(), datum.getRuntimeData()));
          }
        }
      }

      return RestResponse.success(response);
    }
    catch (UseCaseException e)
    {
      return RestResponse.internalError(String.format("Failed to read data of the SCORM content with the ID: [%s]\n"
          + ERROR_MSG_CAUSE, scormContentId, e.getMessage()));
    }
  }

  @ApiOperation(value = "Creates a new SCORM content",
      notes = "If no wrapper type is defined, the default wrapper will be used")
  @PostMapping
  @ResponseBody
  public ResponseEntity create(@RequestBody RestContentAggregation request)
  {
    CreateSCORMContent createScormContent = new CreateSCORMContent(scormContentRepository, scormWrapperRepository);
    CreateSCORMContentInput input = toInput(request);

    Optional<String> wrapperType = Optional.ofNullable(request.getWrapperType());
    wrapperType.ifPresent(input::setWrapperType);

    try
    {
      SCORMContentOutput output = createScormContent.execute(input);
      return RestResponse.success(output);
    }
    catch (UseCaseException e)
    {
      return RestResponse.internalError(String.format("Failed to create SCORM content\n"
          + ERROR_MSG_CAUSE, e.getMessage()));
    }
  }

  @ApiOperation(value = "Deletes a SCORM content",
      notes = "Deleting a SCORM content also deletes every recorded learner's runtime data associated to the content")
  @DeleteMapping(value ="/{scormContentId}")
  @ResponseBody
  public ResponseEntity delete(@PathVariable String scormContentId)
  {
    DeleteSCORMContent deleteScormContent = new DeleteSCORMContent(scormContentRepository, runtimeDataRepository);

    try
    {
      deleteScormContent.execute(scormContentId);
      return RestResponse.success();
    }
    catch (UseCaseException e)
    {
      return RestResponse.internalError(String.format("Failed to delete SCORM content with the ID: [%s]\n"
          + ERROR_MSG_CAUSE, scormContentId, e.getMessage()));
    }
  }

  @ApiOperation(value = "Gets the current learner's runtime data of a SCORM content")
  @GetMapping(value = "/{scormContentId}/runtime-data")
  @ResponseBody
  public ResponseEntity read(@PathVariable String scormContentId,
      @RequestParam(required = false) String scoName)
  {
    Optional<String> sco = Optional.ofNullable(scoName);

    RuntimeDataInput input = new RuntimeDataInput(scormContentId);
    sco.ifPresent(input::setScoName);

    GetRuntimeData getRuntimeData = new GetRuntimeData(runtimeDataRepository);
    try
    {
      Set<RuntimeDataOutput> result = getRuntimeData.execute(input);
      return RestResponse.success(result);
    }
    catch (UseCaseException e)
    {
      return RestResponse.internalError(String.format("Failed to get the run-time data of the SCORM content with the ID: [%s]\n"
          + ERROR_MSG_CAUSE, scormContentId, e.getMessage()));
    }
  }

  @ApiOperation(value = "Creates a new runtime data on the SCO of the SCORM content")
  @PostMapping(value = "/{scormContentId}/runtime-data")
  @ResponseBody
  public ResponseEntity create(@PathVariable String scormContentId)
  {
    CreateRuntimeData createRuntimeData = new CreateRuntimeData(runtimeDataRepository, scormContentRepository);
    try
    {
      List<RuntimeDataOutput> output = createRuntimeData.execute(scormContentId);
      return RestResponse.success(output);
    }
    catch (UseCaseException e)
    {
      return RestResponse.internalError(String.format("Failed to create run-time data on the SCORM content with the ID: [%s]\n"
          + ERROR_MSG_CAUSE, scormContentId, e.getMessage()));
    }
  }

  @ApiOperation(value = "Save the current learner's runtime data of a SCO")
  @PutMapping(value = "/{scormContentId}/runtime-data")
  @ResponseBody
  public ResponseEntity update(@PathVariable String scormContentId,
      @RequestBody RestRuntimeData request)
  {
    SaveRuntimeDataInput input = new SaveRuntimeDataInput(scormContentId, request.getScoName(), request.getData());

    SaveRuntimeData saveRuntimeData = new SaveRuntimeData(runtimeDataRepository);
    try
    {
      Map<String, RuntimeDataInfo> updatedCmiData = saveRuntimeData.execute(input);

      return RestResponse.success(updatedCmiData);
    }
    catch (UseCaseException e)
    {
      return RestResponse.internalError(String.format("Failed to update run-time data on the SCORM content with the ID: [%s]\n"
          + ERROR_MSG_CAUSE, scormContentId, e.getMessage()));
    }
  }

  @ApiOperation(value = "Clears the learner's runtime data of a SCORM content")
  @DeleteMapping(value = "/{scormContentId}/runtime-data")
  @ResponseBody
  public ResponseEntity delete(@PathVariable String scormContentId,
      @RequestParam(required = false) String scoName)
  {
    Optional<String> sco = Optional.ofNullable(scoName);

    RuntimeDataInput input = new RuntimeDataInput(scormContentId);
    sco.ifPresent(input::setScoName);

    ClearRuntimeData clearRuntimeData = new ClearRuntimeData(runtimeDataRepository);
    try
    {
      clearRuntimeData.execute(input);
      return RestResponse.success();
    }
    catch (UseCaseException e)
    {
      return RestResponse.internalError(String.format("Failed to clear run-time data of the SCORM content with the ID: [%s]\n"
          + ERROR_MSG_CAUSE, scormContentId, e.getMessage()));
    }
  }

  private CreateSCORMContentInput toInput(RestContentAggregation request)
  {
    CreateSCORMContentInput createScormContentInput = new CreateSCORMContentInput(request.getScormContentName());

    for (OrganizationEntry organizationEntry : request.getOrganization())
    {
      OrganizationInfo organizationInfo = new OrganizationInfo(organizationEntry.getTitle());

      organizationEntry.getResources().forEach(assetEntry -> {
        ResourceInfo resourceInfo = new ResourceInfo(assetEntry.getAssetId());

        if (assetEntry.getDependencies() != null && !assetEntry.getDependencies().isEmpty())
        {
          assetEntry.getDependencies().forEach(resourceInfo::addDependency);
        }

        organizationInfo.addResourceInfo(resourceInfo);
      });

      createScormContentInput.addOrganizationInfo(organizationInfo);
    }

    return createScormContentInput;
  }
}
