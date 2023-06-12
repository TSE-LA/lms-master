package mn.erin.lms.base.domain.usecase.announcement;

import java.util.Objects;

import org.apache.commons.lang3.Validate;

import mn.erin.domain.base.usecase.UseCase;
import mn.erin.domain.base.usecase.UseCaseException;
import mn.erin.lms.base.aim.AccessIdentityManagement;
import mn.erin.lms.base.domain.model.announcement.AnnouncementRuntimeStatus;
import mn.erin.lms.base.domain.repository.AnnouncementRuntimeRepository;
import mn.erin.lms.base.domain.repository.LmsRepositoryException;
import mn.erin.lms.base.domain.util.DateUtil;

public class ViewAnnouncement implements UseCase<String, Boolean>
{
  private final AnnouncementRuntimeRepository announcementRuntimeRepository;
  private final AccessIdentityManagement accessIdentityManagement;

  public ViewAnnouncement(AnnouncementRuntimeRepository announcementRuntimeRepository,
      AccessIdentityManagement accessIdentityManagement)
  {
    this.announcementRuntimeRepository = Objects.requireNonNull(announcementRuntimeRepository);
    this.accessIdentityManagement = Objects.requireNonNull(accessIdentityManagement);
  }

  @Override
  public Boolean execute(String input) throws UseCaseException
  {
    Validate.notBlank(input);
    try
    {
      String userName = this.accessIdentityManagement.getCurrentUsername();
      return this.announcementRuntimeRepository.update(input, userName, AnnouncementRuntimeStatus.VIEWED, DateUtil.now());
    }
    catch (LmsRepositoryException e)
    {
      throw new UseCaseException(e.getMessage(), e);
    }
  }
}
