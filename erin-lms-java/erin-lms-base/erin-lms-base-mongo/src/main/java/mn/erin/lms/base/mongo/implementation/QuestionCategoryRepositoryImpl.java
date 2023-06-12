package mn.erin.lms.base.mongo.implementation;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import mn.erin.lms.base.aim.organization.OrganizationId;
import mn.erin.lms.base.domain.model.category.QuestionCategory;
import mn.erin.lms.base.domain.model.category.QuestionCategoryId;
import mn.erin.lms.base.domain.repository.LmsRepositoryException;
import mn.erin.lms.base.domain.repository.QuestionCategoryRepository;
import mn.erin.lms.base.mongo.document.category.MongoQuestionCategory;
import mn.erin.lms.base.mongo.repository.MongoQuestionCategoryRepository;
import mn.erin.lms.base.mongo.util.IdGenerator;

/**
 * @author Galsan Bayart
 */
public class QuestionCategoryRepositoryImpl implements QuestionCategoryRepository
{
  MongoQuestionCategoryRepository mongoQuestionCategoryRepository;
  private static final String CATEGORY_NOT_FOUND = "Question category ID with: [%s] not found!";

  public QuestionCategoryRepositoryImpl(MongoQuestionCategoryRepository mongoQuestionCategoryRepository)
  {
    this.mongoQuestionCategoryRepository = mongoQuestionCategoryRepository;
  }

  @Override
  public QuestionCategory create(QuestionCategoryId parentCategoryId, OrganizationId organizationId, int index, String name, String description)
  {
    String id = IdGenerator.generateId();
    MongoQuestionCategory mongoQuestionCategory = new MongoQuestionCategory(id, parentCategoryId.getId(), organizationId.getId(), index, name, description);

    mongoQuestionCategoryRepository.save(mongoQuestionCategory);
    return new QuestionCategory(QuestionCategoryId.valueOf(mongoQuestionCategory.getId()), parentCategoryId, organizationId, index, name, description);
  }

  @Override
  public void update(OrganizationId organizationId, QuestionCategoryId questionCategoryId, int index, String name, String description) throws LmsRepositoryException
  {
    Optional<MongoQuestionCategory> optional = mongoQuestionCategoryRepository.findByOrganizationIdAndId(organizationId.getId(), questionCategoryId.getId());
    MongoQuestionCategory mongoQuestionCategory = optional.orElseThrow(() -> new LmsRepositoryException(String.format(CATEGORY_NOT_FOUND, questionCategoryId)));

    mongoQuestionCategory.setIndex(index);
    mongoQuestionCategory.setName(name);
    mongoQuestionCategory.setDescription(description);

    mongoQuestionCategoryRepository.save(mongoQuestionCategory);
  }

  @Override
  public List<QuestionCategory> getAllByOrganizationId(OrganizationId organizationId)
  {
    return mongoQuestionCategoryRepository.getAllByOrganizationId(organizationId.getId()).stream().map(this::mapToQuestionCategory).collect(Collectors.toList());
  }

  @Override
  public String getCategoryName(String categoryId) throws LmsRepositoryException
  {
    Optional<MongoQuestionCategory> questionCategory = mongoQuestionCategoryRepository.findById(categoryId);
    if(questionCategory.isPresent()){
      return questionCategory.get().getName();
    }
    else {
      throw new LmsRepositoryException("Question category not found");
    }
  }

  @Override
  public void delete(QuestionCategoryId categoryId)
  {
    mongoQuestionCategoryRepository.deleteById(categoryId.getId());
  }

  @Override
  public boolean exists(QuestionCategoryId questionCategoryId)
  {
    return mongoQuestionCategoryRepository.existsById(questionCategoryId.getId());
  }

  @Override
  public Collection<QuestionCategory> listAll(QuestionCategoryId parentCategoryId, OrganizationId organizationId)
  {
    return mongoQuestionCategoryRepository.findByOrganizationIdAndParentCategoryId(organizationId.getId(), parentCategoryId.getId())
        .stream().map(this::mapToQuestionCategory).collect(Collectors.toList());
  }

  @Override
  public Collection<QuestionCategory> listAllByOrganizationId(OrganizationId organizationId)
  {
    return mongoQuestionCategoryRepository.getAllByOrganizationId(organizationId.getId())
        .stream().map(this::mapToQuestionCategory).collect(Collectors.toList());
  }

  private QuestionCategory mapToQuestionCategory(MongoQuestionCategory mongoQuestionCategory)
  {
    return new QuestionCategory(QuestionCategoryId.valueOf(mongoQuestionCategory.getId()),
        QuestionCategoryId.valueOf(mongoQuestionCategory.getParentCategoryId()),
        OrganizationId.valueOf(mongoQuestionCategory.getOrganizationId()),
        mongoQuestionCategory.getIndex(),
        mongoQuestionCategory.getName(),
        mongoQuestionCategory.getDescription());
  }
}
