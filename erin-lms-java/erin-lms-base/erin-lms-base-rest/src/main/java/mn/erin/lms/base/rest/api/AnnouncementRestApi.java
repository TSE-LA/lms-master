package mn.erin.lms.base.rest.api;

import java.util.Collections;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

import mn.erin.domain.base.usecase.UseCaseException;
import mn.erin.infrastucture.rest.common.response.RestEntity;
import mn.erin.infrastucture.rest.common.response.RestResponse;
import mn.erin.infrastucture.rest.common.response.RestResult;
import mn.erin.lms.base.domain.repository.LmsRepositoryRegistry;
import mn.erin.lms.base.domain.service.LmsServiceRegistry;
import mn.erin.lms.base.domain.usecase.announcement.CreateAnnouncement;
import mn.erin.lms.base.domain.usecase.announcement.DeleteAnnouncement;
import mn.erin.lms.base.domain.usecase.announcement.GetAllAnnouncements;
import mn.erin.lms.base.domain.usecase.announcement.GetAnnouncementById;
import mn.erin.lms.base.domain.usecase.announcement.GetAnnouncementsByLearner;
import mn.erin.lms.base.domain.usecase.announcement.GetNewAnnouncementsByLearner;
import mn.erin.lms.base.domain.usecase.announcement.GetSelectedDepartmentTree;
import mn.erin.lms.base.domain.usecase.announcement.PublishAnnouncement;
import mn.erin.lms.base.domain.usecase.announcement.UpdateAnnouncement;
import mn.erin.lms.base.domain.usecase.announcement.ViewAnnouncement;
import mn.erin.lms.base.domain.usecase.announcement.dto.AnnouncementDto;
import mn.erin.lms.base.domain.usecase.announcement.dto.CreateAnnouncementInput;
import mn.erin.lms.base.domain.usecase.announcement.dto.CreateAnnouncementOutput;
import mn.erin.lms.base.domain.usecase.announcement.dto.GetAllAnnouncementsInput;
import mn.erin.lms.base.domain.usecase.announcement.dto.GetAnnouncementsByLearnerOutput;
import mn.erin.lms.base.domain.usecase.announcement.dto.GetNewAnnouncementsByLearnerOutput;
import mn.erin.lms.base.domain.usecase.announcement.dto.UpdateAnnouncementInput;
import mn.erin.lms.base.rest.model.RestAnnouncement;

@Api("Announcement REST API")
@RequestMapping(value = "/lms/announcements", name = "Provides base LMS announcement features")
@RestController
public class AnnouncementRestApi extends BaseLmsRestApi
{
  private static final Logger LOGGER = LoggerFactory.getLogger(AnnouncementRestApi.class);

  AnnouncementRestApi(LmsRepositoryRegistry lmsRepositoryRegistry, LmsServiceRegistry lmsServiceRegistry)
  {
    super(lmsRepositoryRegistry, lmsServiceRegistry);
  }

  @ApiOperation("creates a new announcement")
  @PostMapping()
  public ResponseEntity<RestResult> create(@RequestBody RestAnnouncement body)
  {
    CreateAnnouncement createAnnouncement = new CreateAnnouncement(lmsRepositoryRegistry.getAnnouncementRepository());
    CreateAnnouncementInput input = new CreateAnnouncementInput(body.getAuthor(), body.getTitle(), body.getContent(), body.getDepartmentIds());

    try
    {
      CreateAnnouncementOutput output = createAnnouncement.execute(input);
      return RestResponse.success(output.getAnnouncementId());
    }
    catch (UseCaseException e)
    {
      LOGGER.error(e.getMessage(), e);
      return RestResponse.internalError(e.getMessage());
    }
  }

  @ApiOperation("publish announcement")
  @PostMapping("/{announcementId}/publish")
  public ResponseEntity<RestResult> publish(@PathVariable String announcementId)
  {
    try
    {
      PublishAnnouncement publishAnnouncement = new PublishAnnouncement(lmsRepositoryRegistry, lmsServiceRegistry);
      boolean isPublished = publishAnnouncement.execute(announcementId);
      return RestResponse.success(isPublished);
    }
    catch (UseCaseException e)
    {
      LOGGER.error(e.getMessage(), e);
      return RestResponse.internalError(e.getMessage());
    }
  }

  @ApiOperation("view announcement")
  @PostMapping("/{announcementId}/view")
  public ResponseEntity<RestResult> view(@PathVariable String announcementId)
  {
    try
    {
      ViewAnnouncement viewAnnouncement = new ViewAnnouncement(lmsRepositoryRegistry.getAnnouncementRuntimeRepository(),
          lmsServiceRegistry.getAccessIdentityManagement());
      return RestResponse.success(viewAnnouncement.execute(announcementId));
    }
    catch (UseCaseException e)
    {
      LOGGER.error(e.getMessage(), e);
      return RestResponse.internalError(e.getMessage());
    }
  }

  @ApiOperation("updates announcement by id")
  @PutMapping()
  public ResponseEntity<RestResult> update(@RequestBody RestAnnouncement body)
  {
    try
    {
      UpdateAnnouncement updateAnnouncement = new UpdateAnnouncement(lmsRepositoryRegistry.getAnnouncementRepository());
      UpdateAnnouncementInput updateAnnouncementInput = new UpdateAnnouncementInput(
          body.getAnnouncementId(),
          body.getModifiedUser(),
          body.getTitle(),
          body.getContent(),
          body.getDepartmentIds()
      );

      boolean isUpdated = updateAnnouncement.execute(updateAnnouncementInput);
      return RestResponse.success(isUpdated);
    }
    catch (UseCaseException e)
    {
      LOGGER.error(e.getMessage(), e);
      return RestResponse.internalError(e.getMessage());
    }
  }

  @ApiOperation("deletes announcement by id")
  @DeleteMapping("/{announcementId}")
  public ResponseEntity<RestResult> delete(@PathVariable String announcementId)
  {
    try
    {
      DeleteAnnouncement deleteAnnouncement = new DeleteAnnouncement(lmsRepositoryRegistry.getAnnouncementRepository(),
          lmsRepositoryRegistry.getAnnouncementRuntimeRepository());
      boolean isDeleted = deleteAnnouncement.execute(announcementId);
      return RestResponse.success(isDeleted);
    }
    catch (UseCaseException e)
    {
      LOGGER.error(e.getMessage(), e);
      return RestResponse.internalError(e.getMessage());
    }
  }

  @ApiOperation("fetches all announcements")
  @GetMapping()
  public ResponseEntity<RestResult> getAll(@RequestParam String startDate, @RequestParam String endDate)
  {
    try
    {
      GetAllAnnouncements getAllAnnouncements = new GetAllAnnouncements(lmsRepositoryRegistry.getAnnouncementRepository());
      GetAllAnnouncementsInput input = new GetAllAnnouncementsInput(startDate, endDate);
      List<AnnouncementDto> announcements = getAllAnnouncements.execute(input).getAnnouncements();

      return RestResponse.success(announcements);
    }
    catch (UseCaseException e)
    {
      LOGGER.error(e.getMessage(), e);
      return RestResponse.internalError(e.getMessage());
    }
  }

  @ApiOperation("fetches all announcements of learner")
  @GetMapping("/learner")
  public ResponseEntity<RestResult> getAllByLearnerId(@RequestParam String startDate, @RequestParam String endDate)
  {
    try
    {
      GetAnnouncementsByLearner getAnnouncementsByLearner = new GetAnnouncementsByLearner(lmsRepositoryRegistry, lmsServiceRegistry);
      GetAnnouncementsByLearnerOutput output = getAnnouncementsByLearner.execute(new GetAllAnnouncementsInput(startDate, endDate));

      return RestResponse.success(output == null ? Collections.emptyList() : output.getAnnouncements());
    }
    catch (UseCaseException e)
    {
      LOGGER.error(e.getMessage(), e);
      return RestResponse.internalError(e.getMessage());
    }
  }

  @ApiOperation("fetches all announcements of learner")
  @GetMapping("/notification")
  public ResponseEntity<RestResult> getNotificationByLearner()
  {
    GetNewAnnouncementsByLearner getNewAnnouncementsByLearner = new GetNewAnnouncementsByLearner(lmsRepositoryRegistry, lmsServiceRegistry);
    try
    {
      GetNewAnnouncementsByLearnerOutput output = getNewAnnouncementsByLearner.execute(null);
      return RestResponse.success(output.getAnnouncementRuntimeList().size());
    }
    catch (UseCaseException e)
    {
      LOGGER.error(e.getMessage(), e);
      return RestResponse.internalError(e.getMessage());
    }
  }

  @ApiOperation("fetches announcement by id")
  @GetMapping("/{announcementId}")
  public ResponseEntity<RestResult> getById(@PathVariable String announcementId)
  {
    try
    {
      GetAnnouncementById getAnnouncementById = new GetAnnouncementById(lmsRepositoryRegistry.getAnnouncementRepository());
      return RestResponse.success(getAnnouncementById.execute(announcementId).getAnnouncement());
    }
    catch (UseCaseException e)
    {
      LOGGER.error(e.getMessage(), e);
      return RestResponse.internalError(e.getMessage());
    }
  }

  @ApiOperation("Gets selected departments")
  @GetMapping("/group-announcement/{announcementId}")
  public ResponseEntity<RestResult> getGroupAnnouncement(@PathVariable String announcementId)
  {
    GetSelectedDepartmentTree getSelectedDepartmentTree = new GetSelectedDepartmentTree(lmsRepositoryRegistry, lmsServiceRegistry);
    try
    {
      return RestResponse.success(getSelectedDepartmentTree.execute(announcementId));
    }
    catch (UseCaseException e)
    {
      LOGGER.error(e.getMessage(), e);
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(RestEntity.of(e.getMessage()));
    }
  }
}
