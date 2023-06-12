package mn.erin.lms.base.domain.usecase.announcement;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.lang3.Validate;

import mn.erin.domain.base.usecase.UseCase;
import mn.erin.domain.base.usecase.UseCaseException;
import mn.erin.lms.base.aim.user.Author;
import mn.erin.lms.base.aim.user.Instructor;
import mn.erin.lms.base.domain.model.Authorized;
import mn.erin.lms.base.domain.model.announcement.Announcement;
import mn.erin.lms.base.domain.model.announcement.AnnouncementId;
import mn.erin.lms.base.domain.model.announcement.AnnouncementRuntime;
import mn.erin.lms.base.domain.model.announcement.AnnouncementRuntimeStatus;
import mn.erin.lms.base.domain.model.course.PublishStatus;
import mn.erin.lms.base.domain.repository.LmsRepositoryException;
import mn.erin.lms.base.domain.repository.LmsRepositoryRegistry;
import mn.erin.lms.base.domain.service.LmsServiceRegistry;

@Authorized(users = { Author.class, Instructor.class })
public class PublishAnnouncement implements UseCase<String, Boolean>
{

  private final LmsRepositoryRegistry lmsRepositoryRegistry;
  private final LmsServiceRegistry lmsServiceRegistry;

  public PublishAnnouncement(LmsRepositoryRegistry lmsRepositoryRegistry, LmsServiceRegistry LmsServiceRegistry)
  {
    this.lmsRepositoryRegistry = Objects.requireNonNull(lmsRepositoryRegistry);
    this.lmsServiceRegistry = Objects.requireNonNull(LmsServiceRegistry);
  }

  @Override
  public Boolean execute(String input) throws UseCaseException
  {
    Validate.notBlank(input);
    try
    {
      Announcement announcement = this.lmsRepositoryRegistry.getAnnouncementRepository().getById(input);
      update(announcement);

      Set<String> departmentIds = announcement.getDepartmentIds();
      Set<String> learnerIds = departmentIds.stream()
          .map(id -> this.lmsServiceRegistry.getAccessIdentityManagement().getLearners(id))
          .flatMap(Collection::stream)
          .collect(Collectors.toSet());

      List<AnnouncementRuntime> announcementRuntimeData = learnerIds.stream()
          .map(learnerId -> new AnnouncementRuntime(AnnouncementId.valueOf(input), learnerId, AnnouncementRuntimeStatus.NEW, null))
          .collect(Collectors.toList());

      this.notifyPublished(announcement, learnerIds);

      return this.lmsRepositoryRegistry.getAnnouncementRuntimeRepository().createAnnouncements(announcementRuntimeData);
    }
    catch (Exception e)
    {
      throw new UseCaseException(e.getMessage(), e);
    }
  }

  private void update(Announcement announcement) throws UseCaseException
  {
    String userName = this.lmsServiceRegistry.getAuthenticationService().getCurrentUsername();
    announcement.setModifiedUser(userName);
    announcement.setPublishStatus(PublishStatus.PUBLISHED);
    announcement.setModifiedDate(LocalDateTime.now(ZoneId.of("Asia/Ulaanbaatar")));

    try
    {
      lmsRepositoryRegistry.getAnnouncementRepository().update(announcement);
    }
    catch (LmsRepositoryException e)
    {
      throw new UseCaseException(e.getMessage(), e);
    }
  }

  private void notifyPublished(Announcement announcement, Set<String> learners)
  {
    Map<String, Object> publishedCourseData = new HashMap<>();
    publishedCourseData.put("id", announcement.getAnnouncementId().getId());
    publishedCourseData.put("title", announcement.getTitle());
    lmsServiceRegistry.getNotificationService().notifyAnnouncementPublished(learners, publishedCourseData);
  }
}
