package mn.erin.lms.base.mongo.repository;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.repository.query.QueryByExampleExecutor;

import mn.erin.lms.base.mongo.document.exam.MongoExamGroup;

/**
 * @author Galsan Bayart.
 */
public interface MongoExamGroupRepository extends MongoRepository<MongoExamGroup, String>, QueryByExampleExecutor<MongoExamGroup>
{
  List<MongoExamGroup> findAllByParentIdAndOrganizationId(String parentId, String organizationId);

  List<MongoExamGroup> findByOrganizationId(String organizationId);

  MongoExamGroup findByNameAndOrganizationId(String name, String organizationId);

  MongoExamGroup findByIdAndOrganizationId(String id, String organizationId);
}
