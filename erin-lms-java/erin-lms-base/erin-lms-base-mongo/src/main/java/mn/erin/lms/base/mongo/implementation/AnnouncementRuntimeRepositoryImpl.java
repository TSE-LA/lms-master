package mn.erin.lms.base.mongo.implementation;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import mn.erin.lms.base.domain.model.announcement.AnnouncementId;
import mn.erin.lms.base.domain.model.announcement.AnnouncementRuntime;
import mn.erin.lms.base.domain.model.announcement.AnnouncementRuntimeStatus;
import mn.erin.lms.base.domain.repository.AnnouncementRuntimeRepository;
import mn.erin.lms.base.domain.repository.LmsRepositoryException;
import mn.erin.lms.base.mongo.document.announcement.MongoAnnouncementRuntime;
import mn.erin.lms.base.mongo.document.announcement.MongoAnnouncementRuntimeId;
import mn.erin.lms.base.mongo.document.announcement.MongoAnnouncementRuntimeStatus;
import mn.erin.lms.base.mongo.repository.MongoAnnouncementRuntimeRepository;

public class AnnouncementRuntimeRepositoryImpl implements AnnouncementRuntimeRepository
{
  private final MongoAnnouncementRuntimeRepository mongoAnnouncementRuntimeRepository;

  public AnnouncementRuntimeRepositoryImpl(MongoAnnouncementRuntimeRepository mongoAnnouncementRuntimeRepository)
  {
    this.mongoAnnouncementRuntimeRepository = mongoAnnouncementRuntimeRepository;
  }

  @Override
  public AnnouncementRuntime create(String announcementId, String learnerId, AnnouncementRuntimeStatus status,
      LocalDateTime viewedDate) throws LmsRepositoryException
  {
    MongoAnnouncementRuntimeId id = new MongoAnnouncementRuntimeId(announcementId, learnerId);
    MongoAnnouncementRuntime announcementRuntimeData = new MongoAnnouncementRuntime(id, MongoAnnouncementRuntimeStatus.valueOf(status.name()), viewedDate);
    this.mongoAnnouncementRuntimeRepository.save(announcementRuntimeData);
    return mapToAnnouncementRuntime(announcementRuntimeData);
  }

  @Override
  public boolean createAnnouncements(List<AnnouncementRuntime> announcementRuntimeData)
      throws LmsRepositoryException
  {
    List<MongoAnnouncementRuntime> mongoAnnouncementRuntimes = announcementRuntimeData.stream().map(this::mapToMongoAnnouncementRuntime)
        .collect(Collectors.toList());
    this.mongoAnnouncementRuntimeRepository.saveAll(mongoAnnouncementRuntimes);
    return true;
  }

  @Override
  public boolean update(String announcementId, String learnedId, AnnouncementRuntimeStatus status,
      LocalDateTime viewedDate) throws LmsRepositoryException
  {
    MongoAnnouncementRuntimeId id = new MongoAnnouncementRuntimeId(announcementId, learnedId);
    MongoAnnouncementRuntime announcementRuntime = new MongoAnnouncementRuntime(id, MongoAnnouncementRuntimeStatus.valueOf(status.name()), viewedDate);
    this.mongoAnnouncementRuntimeRepository.save(announcementRuntime);
    return true;
  }

  @Override
  public boolean delete(String announcementId, String learnerId) throws LmsRepositoryException
  {
    this.mongoAnnouncementRuntimeRepository.deleteById(new MongoAnnouncementRuntimeId(announcementId, learnerId));
    return true;
  }

  @Override
  public boolean deleteAllByAnnouncementId(String announcementId) throws LmsRepositoryException
  {
    this.mongoAnnouncementRuntimeRepository.deleteByIdAnnouncementId(announcementId);
    return true;
  }

  @Override
  public AnnouncementRuntime findById(String announcementId, String learnerId) throws LmsRepositoryException
  {
    Optional<MongoAnnouncementRuntime> mongoAnnouncementRuntime = this.mongoAnnouncementRuntimeRepository.findById(
        new MongoAnnouncementRuntimeId(announcementId, learnerId));
    if (!mongoAnnouncementRuntime.isPresent())
    {
      throw new LmsRepositoryException("The announcement runtime data with the ID: [" + announcementId + "] does not exist!");
    }

    return mapToAnnouncementRuntime(mongoAnnouncementRuntime.get());
  }

  @Override
  public List<AnnouncementRuntime> findByLearnerId(String learnerId) throws LmsRepositoryException
  {
    List<MongoAnnouncementRuntime> mongoAnnouncementRuntimes = this.mongoAnnouncementRuntimeRepository.findByIdLearnerId(learnerId);
    return mongoAnnouncementRuntimes.stream().map(this::mapToAnnouncementRuntime).collect(Collectors.toList());
  }

  @Override
  public List<AnnouncementRuntime> findByLearnerIdAndStatus(String learnerId, AnnouncementRuntimeStatus status)
      throws LmsRepositoryException
  {
    List<MongoAnnouncementRuntime> mongoAnnouncementRuntimes = this.mongoAnnouncementRuntimeRepository.findByAnnouncementRuntimeStatusAndIdLearnerId(learnerId,
        MongoAnnouncementRuntimeStatus.valueOf(status.name()));
    return mongoAnnouncementRuntimes.stream().map(this::mapToAnnouncementRuntime).collect(Collectors.toList());
  }

  @Override
  public List<AnnouncementRuntime> findByAnnouncementId(String announcementId) throws LmsRepositoryException
  {
    List<MongoAnnouncementRuntime> mongoAnnouncementRuntimes = this.mongoAnnouncementRuntimeRepository.findByIdAnnouncementId(announcementId);
    return mongoAnnouncementRuntimes.stream().map(this::mapToAnnouncementRuntime).collect(Collectors.toList());
  }

  private AnnouncementRuntime mapToAnnouncementRuntime(MongoAnnouncementRuntime mongoAnnouncementRuntime)
  {
    MongoAnnouncementRuntimeStatus status = mongoAnnouncementRuntime.getAnnouncementRuntimeStatus();
    return new AnnouncementRuntime(
        AnnouncementId.valueOf(mongoAnnouncementRuntime.getId().getAnnouncementId()),
        mongoAnnouncementRuntime.getId().getLearnerId(),
        AnnouncementRuntimeStatus.valueOf(status.name()),
        mongoAnnouncementRuntime.getViewedDate());
  }

  private MongoAnnouncementRuntime mapToMongoAnnouncementRuntime(AnnouncementRuntime announcementRuntime)
  {
    MongoAnnouncementRuntimeId id = new MongoAnnouncementRuntimeId(announcementRuntime.getAnnouncementId().getId(), announcementRuntime.getLearnerId());
    MongoAnnouncementRuntimeStatus status = MongoAnnouncementRuntimeStatus.valueOf(announcementRuntime.getState().name());
    LocalDateTime viewedDate = announcementRuntime.getViewedDate();
    return new MongoAnnouncementRuntime(id, status, viewedDate);
  }
}
