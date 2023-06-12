package mn.erin.lms.jarvis.mongo.repository;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;

import mn.erin.lms.jarvis.mongo.document.report.MongoField;

/**
 * @author Temuulen Naranbold
 */
public interface MongoFieldRepository extends MongoRepository<MongoField, String>
{
  List<MongoField> findAllByOrganizationId(String organizationId);
  List<MongoField> findAllByOrganizationIdAndName(String organizationId, String name);
}
