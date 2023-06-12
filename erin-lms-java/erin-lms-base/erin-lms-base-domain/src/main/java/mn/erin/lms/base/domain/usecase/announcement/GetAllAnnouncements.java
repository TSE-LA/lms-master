package mn.erin.lms.base.domain.usecase.announcement;

import java.text.ParseException;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.apache.commons.lang3.Validate;

import mn.erin.domain.base.usecase.UseCase;
import mn.erin.domain.base.usecase.UseCaseException;
import mn.erin.lms.base.aim.user.Author;
import mn.erin.lms.base.aim.user.Instructor;
import mn.erin.lms.base.domain.model.Authorized;
import mn.erin.lms.base.domain.model.announcement.Announcement;
import mn.erin.lms.base.domain.repository.AnnouncementRepository;
import mn.erin.lms.base.domain.repository.LmsRepositoryException;
import mn.erin.lms.base.domain.usecase.announcement.dto.GetAllAnnouncementsInput;
import mn.erin.lms.base.domain.usecase.announcement.dto.GetAllAnnouncementsOutput;
import mn.erin.lms.base.domain.util.DateUtil;

@Authorized(users = { Author.class, Instructor.class })
public class GetAllAnnouncements extends AnnouncementUseCase implements UseCase<GetAllAnnouncementsInput, GetAllAnnouncementsOutput>
{
  private final AnnouncementRepository announcementRepository;

  public GetAllAnnouncements(AnnouncementRepository announcementRepository)
  {
    this.announcementRepository = Objects.requireNonNull(announcementRepository);
  }

  @Override
  public GetAllAnnouncementsOutput execute(GetAllAnnouncementsInput input) throws UseCaseException
  {
    Validate.notNull(input);
    try
    {
      LocalDateTime startDate = DateUtil.stringToLocalDateTime(input.getStartDate(), LocalTime.MIN).toLocalDate().atTime(LocalTime.MIN);
      LocalDateTime endDate = DateUtil.stringToLocalDateTime(input.getEndDate(), LocalTime.MAX).toLocalDate().atTime(LocalTime.MAX);

      List<Announcement> announcements = this.announcementRepository.listAll(startDate, endDate);
      return new GetAllAnnouncementsOutput(
          announcements.stream().map(this::toAnnouncementDto).collect(Collectors.toList())
      );
    }
    catch (LmsRepositoryException e)
    {
      throw new UseCaseException(e.getMessage(), e);
    }
    catch (ParseException | DateTimeParseException e)
    {
      throw new UseCaseException("Could not parse date!", e);
    }
  }
}
