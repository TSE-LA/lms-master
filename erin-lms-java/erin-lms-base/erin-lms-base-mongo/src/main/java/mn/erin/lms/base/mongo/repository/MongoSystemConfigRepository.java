package mn.erin.lms.base.mongo.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;

import mn.erin.lms.base.mongo.document.content.MongoSystemConfig;

/**
 * @author Temuulen Naranbold
 */
public interface MongoSystemConfigRepository extends MongoRepository<MongoSystemConfig, String>
{
  Optional<MongoSystemConfig> findByOrganizationId(String organizationId);

  List<MongoSystemConfig> findAllByOrganizationId(String organizationId);
}
