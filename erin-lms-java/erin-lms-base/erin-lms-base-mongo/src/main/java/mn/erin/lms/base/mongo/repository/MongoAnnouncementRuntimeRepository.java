package mn.erin.lms.base.mongo.repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import javax.validation.constraints.NotNull;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.repository.query.QueryByExampleExecutor;

import mn.erin.lms.base.mongo.document.announcement.MongoAnnouncementRuntime;
import mn.erin.lms.base.mongo.document.announcement.MongoAnnouncementRuntimeId;
import mn.erin.lms.base.mongo.document.announcement.MongoAnnouncementRuntimeStatus;

public interface MongoAnnouncementRuntimeRepository
    extends MongoRepository<MongoAnnouncementRuntime, MongoAnnouncementRuntimeId>, QueryByExampleExecutor<MongoAnnouncementRuntime>
{
  @NotNull
  Optional<MongoAnnouncementRuntime> findById(@NotNull MongoAnnouncementRuntimeId id);

  List<MongoAnnouncementRuntime> findByIdLearnerId(String learnerId);

  List<MongoAnnouncementRuntime> findByIdAnnouncementId(String announcementId);

  @Query("{'_id.learnerId': 'test', announcementRuntimeStatus: 'NEW'}")
  List<MongoAnnouncementRuntime> findByAnnouncementRuntimeStatusAndIdLearnerId(String learnerId, MongoAnnouncementRuntimeStatus status);

  void deleteByIdAnnouncementId(String id);
}