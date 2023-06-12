package mn.erin.lms.base.mongo.implementation;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.commons.lang3.Validate;

import mn.erin.lms.base.aim.organization.OrganizationId;
import mn.erin.lms.base.domain.model.exam.ExamGroup;
import mn.erin.lms.base.domain.model.exam.ExamGroupId;
import mn.erin.lms.base.domain.repository.ExamGroupRepository;
import mn.erin.lms.base.domain.repository.LmsRepositoryException;
import mn.erin.lms.base.mongo.document.exam.MongoExamGroup;
import mn.erin.lms.base.mongo.repository.MongoExamGroupRepository;
import mn.erin.lms.base.mongo.util.IdGenerator;

/**
 * @author Galsan Bayart.
 */
public class ExamGroupRepositoryImpl implements ExamGroupRepository
{
  public static final String ERR_MSG_EXAM_GROUP_NOT_FOUND = "Exam group with id=[%s] does not exist";

  private final MongoExamGroupRepository mongoExamGroupRepository;

  public ExamGroupRepositoryImpl(MongoExamGroupRepository mongoExamGroupRepository)
  {
    this.mongoExamGroupRepository = mongoExamGroupRepository;
  }

  @Override
  public boolean exists(ExamGroupId id)
  {
    return mongoExamGroupRepository.findById(id.getId()).isPresent();
  }

  @Override
  public ExamGroup create(String parentId, String name, OrganizationId organizationId, String description)
  {
    String id = IdGenerator.generateId();
    MongoExamGroup mongoExamGroup = new MongoExamGroup(id, parentId, name, organizationId.getId(), description);

    return mapToExamGroup(mongoExamGroupRepository.save(mongoExamGroup));
  }

  @Override
  public String update(ExamGroup examGroup) throws LmsRepositoryException
  {
    Validate.notNull(examGroup);

    Optional<MongoExamGroup> optional = mongoExamGroupRepository.findById(examGroup.getId().getId());
    MongoExamGroup mongoExamGroup = optional.orElseThrow(() -> new LmsRepositoryException(String.format(ERR_MSG_EXAM_GROUP_NOT_FOUND, examGroup.getId().getId())));

    mongoExamGroup.setParentId(examGroup.getParentId());
    mongoExamGroup.setName(examGroup.getName());
    mongoExamGroup.setOrganizationId(examGroup.getOrganizationId().getId());
    mongoExamGroup.setDescription(examGroup.getDescription());

    return mongoExamGroupRepository.save(mongoExamGroup).getId();
  }

  @Override
  public ExamGroup findById(ExamGroupId id) throws LmsRepositoryException
  {
    Optional<MongoExamGroup> optionalMongoExamGroup = mongoExamGroupRepository.findById(id.getId());
    if (optionalMongoExamGroup.isPresent())
    {
      return mapToExamGroup(optionalMongoExamGroup.get());
    }
    else
    {
      throw new LmsRepositoryException(String.format(ERR_MSG_EXAM_GROUP_NOT_FOUND, id));
    }
  }

  @Override
  public boolean delete(ExamGroupId id)
  {
    mongoExamGroupRepository.deleteById(id.getId());
    return true;
  }

  @Override
  public List<ExamGroup> findByParentIdAndOrganizationId(ExamGroupId id, OrganizationId organizationId)
  {
    return mongoExamGroupRepository.findAllByParentIdAndOrganizationId(id.getId(), organizationId.getId()).stream().map(this::mapToExamGroup).collect(Collectors.toList());
  }

  @Override
  public void updateAllParentId(List<ExamGroup> examGroups)
  {
    mongoExamGroupRepository.saveAll(examGroups.stream().map(this::mapToMongoExamGroup).collect(Collectors.toList()));
  }

  @Override
  public List<ExamGroup> findByOrganizationId(String organizationId)
  {
    return mongoExamGroupRepository.findByOrganizationId(organizationId).stream().map(this::mapToExamGroup).collect(Collectors.toList());
  }

  @Override
  public ExamGroup findByNameAndOrganizationId(String name, String organizationId)
  {
    return mapToExamGroup(mongoExamGroupRepository.findByNameAndOrganizationId(name, organizationId));
  }

  @Override
  public ExamGroup findByIdAndOrganizationId(ExamGroupId examGroupId, String organizationId)
  {
    return mapToExamGroup(mongoExamGroupRepository.findByIdAndOrganizationId(examGroupId.getId(), organizationId));
  }

  private ExamGroup mapToExamGroup(MongoExamGroup mongoExamGroup)
  {
    return new ExamGroup(
        ExamGroupId.valueOf(mongoExamGroup.getId()),
        mongoExamGroup.getParentId(),
        mongoExamGroup.getName(),
        OrganizationId.valueOf(mongoExamGroup.getOrganizationId()),
        mongoExamGroup.getDescription()
    );
  }

  private MongoExamGroup mapToMongoExamGroup(ExamGroup examGroup)
  {
    return new MongoExamGroup(examGroup.getId().getId(), examGroup.getParentId(), examGroup.getName(), examGroup.getDescription(),
        examGroup.getOrganizationId().getId());
  }
}
