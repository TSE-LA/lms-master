package mn.erin.lms.base.mongo.implementation;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import mn.erin.lms.base.aim.organization.OrganizationId;
import mn.erin.lms.base.domain.model.category.ExamCategory;
import mn.erin.lms.base.domain.model.category.ExamCategoryId;
import mn.erin.lms.base.domain.repository.ExamCategoryRepository;
import mn.erin.lms.base.domain.repository.LmsRepositoryException;
import mn.erin.lms.base.mongo.document.category.MongoExamCategory;
import mn.erin.lms.base.mongo.repository.MongoExamCategoryRepository;
import mn.erin.lms.base.mongo.util.IdGenerator;

/**
 * @author Temuulen Naranbold
 */
public class ExamCategoryRepositoryImpl implements ExamCategoryRepository
{
  private static final String CATEGORY_NOT_FOUND = "Exam category ID with: [%s] not found!";

  private final MongoExamCategoryRepository mongoExamCategoryRepository;

  public ExamCategoryRepositoryImpl(MongoExamCategoryRepository mongoExamCategoryRepository)
  {
    this.mongoExamCategoryRepository = mongoExamCategoryRepository;
  }

  @Override
  public ExamCategory create(ExamCategoryId parentCategoryId, OrganizationId organizationId, int index, String name, String description)
  {
    String id = IdGenerator.generateId();
    MongoExamCategory mongoExamCategory = new MongoExamCategory(id, index, parentCategoryId.getId(), organizationId.getId(), name, description);

    mongoExamCategoryRepository.save(mongoExamCategory);
    return mapToExamCategory(mongoExamCategory);
  }

  @Override
  public void update(ExamCategoryId id, OrganizationId organizationId, int index, String name, String description) throws LmsRepositoryException
  {
    Optional<MongoExamCategory> optional = mongoExamCategoryRepository.findByOrganizationIdAndId(organizationId.getId(), id.getId());
    MongoExamCategory mongoExamCategory = optional.orElseThrow(() -> new LmsRepositoryException(String.format(CATEGORY_NOT_FOUND, id)));

    mongoExamCategory.setIndex(index);
    mongoExamCategory.setName(name);
    mongoExamCategory.setDescription(description);

    mongoExamCategoryRepository.save(mongoExamCategory);
  }

  @Override
  public Collection<ExamCategory> listAll(ExamCategoryId parentCategoryId, OrganizationId organizationId)
  {
    return mongoExamCategoryRepository.findByOrganizationIdAndParentCategoryId(organizationId.getId(), parentCategoryId.getId())
        .stream().map(this::mapToExamCategory).collect(Collectors.toList());
  }

  @Override
  public Collection<ExamCategory> listAllByOrganizationId(OrganizationId organizationId)
  {
    return mongoExamCategoryRepository.findAllByOrganizationId(organizationId.getId()).stream().map(this::mapToExamCategory).collect(Collectors.toList());
  }

  @Override
  public List<ExamCategory> listAllByParentCategoryId(ExamCategoryId categoryId)
  {
    return mongoExamCategoryRepository.findAllByParentCategoryId(categoryId.getId()).stream().map(this::mapToExamCategory).collect(Collectors.toList());
  }

  @Override
  public boolean exists(ExamCategoryId examCategoryId)
  {
    return mongoExamCategoryRepository.existsById(examCategoryId.getId());
  }

  @Override
  public void delete(ExamCategoryId id) throws LmsRepositoryException
  {
    mongoExamCategoryRepository.deleteById(id.getId());
  }

  private ExamCategory mapToExamCategory(MongoExamCategory mongoExamCategory)
  {
    return new ExamCategory(ExamCategoryId.valueOf(mongoExamCategory.getId()),
        mongoExamCategory.getIndex(),
        ExamCategoryId.valueOf(mongoExamCategory.getParentCategoryId()),
        OrganizationId.valueOf(mongoExamCategory.getOrganizationId()),
        mongoExamCategory.getName(), mongoExamCategory.getDescription());
  }
}
