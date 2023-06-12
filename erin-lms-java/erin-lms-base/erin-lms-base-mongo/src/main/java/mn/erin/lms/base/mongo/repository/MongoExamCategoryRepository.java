package mn.erin.lms.base.mongo.repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.repository.query.QueryByExampleExecutor;

import mn.erin.lms.base.mongo.document.category.MongoExamCategory;

/**
 * @author Temuulen Naranbold
 */
public interface MongoExamCategoryRepository extends MongoRepository<MongoExamCategory, String>, QueryByExampleExecutor<MongoExamCategory>
{
  List<MongoExamCategory> findByOrganizationId(String organizationId);

  List<MongoExamCategory> findAllByOrganizationId(String organizationId);

  List<MongoExamCategory> findByOrganizationIdAndParentCategoryId(String organizationId, String parentCategoryId);

  List<MongoExamCategory> findAllByParentCategoryId(String parentCategoryId);

  List<MongoExamCategory> findAllByIdIn(Collection<String> id);

  Optional<MongoExamCategory> findByOrganizationIdAndId(String organizationId, String id);
}
