package mn.erin.lms.base.mongo.repository;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.repository.query.QueryByExampleExecutor;

import mn.erin.lms.base.mongo.document.exam.question.MongoQuestionGroup;

/**
 * @author Galsan Bayart
 */
public interface MongoQuestionGroupRepository extends MongoRepository<MongoQuestionGroup, String>, QueryByExampleExecutor<MongoQuestionGroup>
{
  MongoQuestionGroup findByOrganizationIdAndParentGroupId(String tenantId, String parentGroupId);

  List<MongoQuestionGroup> getAllByOrganizationId(String organizationId);

  List<MongoQuestionGroup> getAllByParentGroupIdAndOrganizationId(String parentGroupId, String organizationId);
}
