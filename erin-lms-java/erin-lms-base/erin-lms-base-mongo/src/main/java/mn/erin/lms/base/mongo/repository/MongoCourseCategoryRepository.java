package mn.erin.lms.base.mongo.repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.repository.query.QueryByExampleExecutor;

import mn.erin.lms.base.mongo.document.category.MongoCourseCategory;

/**
 * @author Bat-Erdene Tsogoo.
 */
public interface MongoCourseCategoryRepository extends MongoRepository<MongoCourseCategory, String>,
    QueryByExampleExecutor<MongoCourseCategory>
{
  List<MongoCourseCategory> findByOrganizationId(String organizationId);

  List<MongoCourseCategory> findByParentCategoryId(String parentCategoryId);

  List<MongoCourseCategory> findByOrganizationIdAndParentCategoryId(String organizationId, String parentCategoryId);

  List<MongoCourseCategory> findAllByIdIn(Set<String> ids);

  Optional<MongoCourseCategory> findByOrganizationIdAndId(String organizationId, String id);

  List<MongoCourseCategory> findAllByOrganizationIdAndAutoEnrollTrue(String organizationId);

  List<MongoCourseCategory> findAllByOrganizationIdAndParentCategoryIdAndAutoEnrollTrue(String organizationId, String parentCategoryId);
}
