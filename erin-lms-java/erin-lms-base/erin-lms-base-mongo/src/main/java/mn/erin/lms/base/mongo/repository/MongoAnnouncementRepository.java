package mn.erin.lms.base.mongo.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import javax.validation.constraints.NotNull;

import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.repository.query.QueryByExampleExecutor;

import mn.erin.lms.base.mongo.document.announcement.MongoAnnouncement;

public interface MongoAnnouncementRepository extends MongoRepository<MongoAnnouncement, String>, QueryByExampleExecutor<MongoAnnouncement>
{
  @NotNull
  Optional<MongoAnnouncement> findById(@NotNull String id);

  List<MongoAnnouncement> findByModifiedDateBetween(LocalDateTime from, LocalDateTime to, Sort sort);

  List<MongoAnnouncement> findAllByIdInAndModifiedDateBetween(@NotNull Set<String> ids, LocalDateTime from, LocalDateTime to, Sort sort);
}