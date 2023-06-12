package mn.erin.lms.base.mongo.implementation;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.data.domain.Sort;

import mn.erin.lms.base.domain.model.announcement.Announcement;
import mn.erin.lms.base.domain.model.announcement.AnnouncementId;
import mn.erin.lms.base.domain.model.course.PublishStatus;
import mn.erin.lms.base.domain.repository.AnnouncementRepository;
import mn.erin.lms.base.domain.repository.LmsRepositoryException;
import mn.erin.lms.base.mongo.document.announcement.MongoAnnouncement;
import mn.erin.lms.base.mongo.document.course.MongoPublishStatus;
import mn.erin.lms.base.mongo.repository.MongoAnnouncementRepository;
import mn.erin.lms.base.mongo.util.IdGenerator;

public class AnnouncementRepositoryImpl implements AnnouncementRepository
{
  private final MongoAnnouncementRepository mongoAnnouncementRepository;

  public AnnouncementRepositoryImpl(MongoAnnouncementRepository mongoAnnouncementRepository)
  {
    this.mongoAnnouncementRepository = mongoAnnouncementRepository;
  }

  @Override
  public Announcement create(String author, String title, String content, Set<String> groupIds,
      PublishStatus publishStatus) throws LmsRepositoryException
  {
    String id = IdGenerator.generateId();
    LocalDateTime createdDate = LocalDateTime.now(ZoneId.of("Asia/Ulaanbaatar"));

    MongoAnnouncement mongoAnnouncement = new MongoAnnouncement(
        id, author, createdDate, createdDate, title, content, groupIds, MongoPublishStatus.valueOf(publishStatus.name())
    );

    this.mongoAnnouncementRepository.save(mongoAnnouncement);
    return mapToAnnouncement(mongoAnnouncement);
  }

  @Override
  public boolean update(Announcement announcement)
  {
    MongoAnnouncement mongoAnnouncement = new MongoAnnouncement(
        announcement.getAnnouncementId().getId(),
        announcement.getAuthor(),
        announcement.getCreatedDate(),
        announcement.getModifiedDate(),
        announcement.getTitle(),
        announcement.getContent(),
        announcement.getDepartmentIds(),
        MongoPublishStatus.valueOf(announcement.getPublishStatus().name())
    );
    mongoAnnouncement.setModifiedDate(announcement.getModifiedDate());
    this.mongoAnnouncementRepository.save(mongoAnnouncement);
    return true;
  }

  @Override
  public boolean delete(String announcementId)
  {
    this.mongoAnnouncementRepository.deleteById(announcementId);
    return true;
  }

  @Override
  public List<Announcement> listAll(LocalDateTime startDate, LocalDateTime endDate)
  {
    List<MongoAnnouncement> mongoAnnouncements = this.mongoAnnouncementRepository.findByModifiedDateBetween(startDate, endDate,
        Sort.by(Sort.Direction.DESC, "modifiedDate"));
    return mongoAnnouncements.stream().map(this::mapToAnnouncement).collect(Collectors.toList());
  }

  @Override
  public Announcement getById(String announcementId) throws LmsRepositoryException
  {
    Optional<MongoAnnouncement> mongoAnnouncement = this.mongoAnnouncementRepository.findById(announcementId);
    if (!mongoAnnouncement.isPresent())
    {
      throw new LmsRepositoryException("The announcement with the ID: [" + announcementId + "] does not exist!");
    }

    return mapToAnnouncement(mongoAnnouncement.get());
  }

  @Override
  public List<Announcement> getAllById(Set<String> ids, LocalDateTime startDate, LocalDateTime endDate)
      throws LmsRepositoryException
  {
    List<MongoAnnouncement> mongoAnnouncements = this.mongoAnnouncementRepository.findAllByIdInAndModifiedDateBetween(ids, startDate, endDate,
        Sort.by(Sort.Direction.DESC, "modifiedDate"));
    return mongoAnnouncements.stream().map(this::mapToAnnouncement).collect(Collectors.toList());
  }

  private Announcement mapToAnnouncement(MongoAnnouncement mongoAnnouncement)
  {
    return new Announcement(
        AnnouncementId.valueOf(mongoAnnouncement.getId()),
        mongoAnnouncement.getAuthor(),
        mongoAnnouncement.getModifiedUser(),
        mongoAnnouncement.getCreatedDate(),
        mongoAnnouncement.getModifiedDate(),
        mongoAnnouncement.getTitle(),
        mongoAnnouncement.getContent(),
        mongoAnnouncement.getDepartmentIds(),
        PublishStatus.valueOf(mongoAnnouncement.getPublishStatus().name())
    );
  }
}
