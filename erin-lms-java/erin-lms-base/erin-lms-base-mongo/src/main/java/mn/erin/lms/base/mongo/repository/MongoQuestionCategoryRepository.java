package mn.erin.lms.base.mongo.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.repository.query.QueryByExampleExecutor;

import mn.erin.lms.base.mongo.document.category.MongoQuestionCategory;

/**
 * @author Galsan Bayart
 */
public interface MongoQuestionCategoryRepository extends MongoRepository<MongoQuestionCategory, String>, QueryByExampleExecutor<MongoQuestionCategory>
{
  List<MongoQuestionCategory> getAllByOrganizationId(String organizationId);

  List<MongoQuestionCategory> findByOrganizationIdAndParentCategoryId(String organizationId, String parentCategoryId);

  Optional<MongoQuestionCategory> findByOrganizationIdAndId(String organizationId, String id);
}
