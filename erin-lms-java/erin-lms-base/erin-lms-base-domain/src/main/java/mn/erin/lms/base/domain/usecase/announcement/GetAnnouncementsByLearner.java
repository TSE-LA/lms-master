package mn.erin.lms.base.domain.usecase.announcement;

import java.sql.Timestamp;
import java.text.ParseException;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.lang3.Validate;

import mn.erin.common.datetime.DateTimeUtils;
import mn.erin.domain.base.usecase.UseCase;
import mn.erin.domain.base.usecase.UseCaseException;
import mn.erin.lms.base.domain.model.announcement.Announcement;
import mn.erin.lms.base.domain.model.announcement.AnnouncementRuntime;
import mn.erin.lms.base.domain.repository.LmsRepositoryException;
import mn.erin.lms.base.domain.repository.LmsRepositoryRegistry;
import mn.erin.lms.base.domain.service.LmsServiceRegistry;
import mn.erin.lms.base.domain.usecase.announcement.dto.GetAllAnnouncementsInput;
import mn.erin.lms.base.domain.usecase.announcement.dto.GetAnnouncementsByLearnerOutput;
import mn.erin.lms.base.domain.util.DateUtil;

public class GetAnnouncementsByLearner implements UseCase<GetAllAnnouncementsInput, GetAnnouncementsByLearnerOutput>
{
  private final LmsRepositoryRegistry lmsRepositoryRegistry;
  private final LmsServiceRegistry lmsServiceRegistry;

  public GetAnnouncementsByLearner(LmsRepositoryRegistry lmsRepositoryRegistry, LmsServiceRegistry lmsServiceRegistry)
  {
    this.lmsRepositoryRegistry = Objects.requireNonNull(lmsRepositoryRegistry);
    this.lmsServiceRegistry = Objects.requireNonNull(lmsServiceRegistry);
  }

  @Override
  public GetAnnouncementsByLearnerOutput execute(GetAllAnnouncementsInput input) throws UseCaseException
  {
    Validate.notNull(input);

    try
    {
      String userName = this.lmsServiceRegistry.getAccessIdentityManagement().getCurrentUsername();
      LocalDateTime startDate = DateUtil.stringToLocalDateTime(input.getStartDate(), LocalTime.MIN);
      LocalDateTime endDate = DateUtil.stringToLocalDateTime(input.getEndDate(), LocalTime.MAX);
      List<AnnouncementRuntime> announcementRuntimeList = this.lmsRepositoryRegistry.getAnnouncementRuntimeRepository().findByLearnerId(userName);
      Set<String> announcementIds = announcementRuntimeList.stream().map(announcement -> announcement.getAnnouncementId().getId())
          .collect(Collectors.toSet());

      List<Announcement> announcements = this.lmsRepositoryRegistry.getAnnouncementRepository().getAllById(announcementIds, startDate, endDate);

      List<Map<String, Object>> announcementList = new ArrayList<>();

      for (Announcement announcement : announcements)
      {
        String announcementId = announcement.getAnnouncementId().getId();
        Optional<AnnouncementRuntime> announcementRuntime = announcementRuntimeList.stream().filter(ar -> ar.getAnnouncementId().getId().equals(announcementId))
            .findFirst();
        if (!announcementRuntime.isPresent())
        {
          continue;
        }

        Map<String, Object> announcementMap = new HashMap<>();
        announcementMap.put("announcementId", announcementId);
        announcementMap.put("title", announcement.getTitle());
        announcementMap.put("createdDate", DateTimeUtils.toShortIsoString(toDate(announcement.getCreatedDate())));
        announcementMap.put("modifiedDate", DateTimeUtils.toShortIsoString(toDate(announcement.getModifiedDate())));
        announcementMap.put("author", announcement.getAuthor());
        announcementMap.put("modifiedUser", announcement.getModifiedUser());
        announcementMap.put("viewStatus", announcementRuntime.get().getState());
        announcementMap.put("publishStatus", announcement.getPublishStatus());

        announcementList.add(announcementMap);
      }

      return new GetAnnouncementsByLearnerOutput(announcementList);
    }
    catch (LmsRepositoryException | ParseException e)
    {
      throw new UseCaseException(e.getMessage(), e);
    }
  }

  private Date toDate(LocalDateTime localDateTime)
  {
    return Timestamp.valueOf(localDateTime);
  }
}
