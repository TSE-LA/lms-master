package mn.erin.lms.base.mongo.implementation;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import mn.erin.lms.base.aim.organization.OrganizationId;
import mn.erin.lms.base.domain.model.exam.question.QuestionGroup;
import mn.erin.lms.base.domain.model.exam.question.QuestionGroupId;
import mn.erin.lms.base.domain.repository.LmsRepositoryException;
import mn.erin.lms.base.domain.repository.QuestionGroupRepository;
import mn.erin.lms.base.mongo.document.exam.question.MongoQuestionGroup;
import mn.erin.lms.base.mongo.repository.MongoQuestionGroupRepository;
import mn.erin.lms.base.mongo.util.IdGenerator;

/**
 * @author Galsan Bayart
 */
public class QuestionGroupRepositoryImpl implements QuestionGroupRepository
{
  private static final String QUESTION_GROUP_NOT_FOUND = "Question group ID with: [%s] not found!";

  private final MongoQuestionGroupRepository mongoQuestionGroupRepository;

  public QuestionGroupRepositoryImpl(MongoQuestionGroupRepository mongoQuestionGroupRepository)
  {
    this.mongoQuestionGroupRepository = mongoQuestionGroupRepository;
  }

  @Override
  public QuestionGroup create(String parentId, String name, OrganizationId organizationId, String description)
  {
    String id = IdGenerator.generateId();
    MongoQuestionGroup mongoQuestionGroup = new MongoQuestionGroup(id, parentId, name, organizationId.getId());
    mongoQuestionGroup.setDescription(description);
    mongoQuestionGroupRepository.save(mongoQuestionGroup);
    return mapToQuestionGroup(mongoQuestionGroup);
  }

  @Override
  public List<QuestionGroup> getAll(String parentId, String organizationId)
  {
    return mongoQuestionGroupRepository.getAllByParentGroupIdAndOrganizationId(parentId, organizationId).stream().map(this::mapToQuestionGroup).collect(Collectors.toList());
  }

  @Override
  public List<QuestionGroup> getAllByOrganizationId(OrganizationId organizationId)
  {
    return mongoQuestionGroupRepository.getAllByOrganizationId(organizationId.getId()).stream().map(this::mapToQuestionGroup).collect(Collectors.toList());
  }

  @Override
  public String update(QuestionGroup questionGroup) throws LmsRepositoryException
  {
    Optional<MongoQuestionGroup> optional = mongoQuestionGroupRepository.findById(questionGroup.getId().getId());
    MongoQuestionGroup mongoQuestionGroup = optional.orElseThrow(() -> new LmsRepositoryException(String.format(QUESTION_GROUP_NOT_FOUND, questionGroup.getId())));

    mongoQuestionGroup.setParentGroupId(questionGroup.getParentGroupId() != null ? questionGroup.getParentGroupId().getId() : null);
    mongoQuestionGroup.setName(questionGroup.getName());
    mongoQuestionGroup.setDescription(questionGroup.getDescription());

    return mongoQuestionGroupRepository.save(mongoQuestionGroup).getId();
  }

  @Override
  public QuestionGroup findById(QuestionGroupId id) throws LmsRepositoryException
  {
    Optional<MongoQuestionGroup> mongoQuestionGroup = mongoQuestionGroupRepository.findById(id.getId());

    if (mongoQuestionGroup.isPresent())
    {
      return mapToQuestionGroup(mongoQuestionGroup.get());
    }
    else
    {
      throw new LmsRepositoryException("Question group not found with id [" + id.getId() + "]");
    }
  }

  @Override
  public boolean delete(String questionGroupId)
  {
    mongoQuestionGroupRepository.deleteById(questionGroupId);
    return true;
  }

  private QuestionGroup mapToQuestionGroup(MongoQuestionGroup mongoQuestionGroup)
  {
    return new QuestionGroup(QuestionGroupId.valueOf(mongoQuestionGroup.getId()), mongoQuestionGroup.getParentGroupId() != null ? QuestionGroupId.valueOf(mongoQuestionGroup.getParentGroupId()) : null,
        mongoQuestionGroup.getName(), OrganizationId.valueOf(mongoQuestionGroup.getOrganizationId()), mongoQuestionGroup.getDescription());
  }
}
