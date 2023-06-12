package mn.erin.lms.base.mongo.implementation;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;

import mn.erin.lms.base.domain.model.category.CourseCategory;
import mn.erin.lms.base.domain.model.category.CourseCategoryId;
import mn.erin.lms.base.aim.organization.OrganizationId;
import mn.erin.lms.base.domain.repository.CourseCategoryRepository;
import mn.erin.lms.base.domain.repository.LmsRepositoryException;
import mn.erin.lms.base.mongo.document.category.MongoCourseCategory;
import mn.erin.lms.base.mongo.repository.MongoCourseCategoryRepository;
import mn.erin.lms.base.mongo.util.IdGenerator;

/**
 * @author Bat-Erdene Tsogoo.
 */
public class CourseCategoryRepositoryImpl implements CourseCategoryRepository
{
  private final MongoCourseCategoryRepository mongoCourseCategoryRepository;
  private static final String CATEGORY_NOT_FOUND = "Category ID with: [%s] not found!";

  public CourseCategoryRepositoryImpl(MongoCourseCategoryRepository mongoCourseCategoryRepository)
  {
    this.mongoCourseCategoryRepository = mongoCourseCategoryRepository;
  }

  @Override
  public CourseCategory create(OrganizationId organizationId, CourseCategoryId parentCategoryId, String categoryName, String description, boolean autoEnroll)
  {
    String id = IdGenerator.generateId();
    MongoCourseCategory mongoCourseCategory;

    if (parentCategoryId == null)
    {
      mongoCourseCategory = new MongoCourseCategory(id, organizationId.getId(), categoryName);
    }
    else
    {
      mongoCourseCategory = new MongoCourseCategory(id, organizationId.getId(), parentCategoryId.getId(), categoryName, autoEnroll);
    }

    if (!StringUtils.isBlank(description))
    {
      mongoCourseCategory.setDescription(description);
    }

    mongoCourseCategoryRepository.save(mongoCourseCategory);

    return new CourseCategory(organizationId, CourseCategoryId.valueOf(id), parentCategoryId, categoryName, autoEnroll);
  }

  @Override
  public Collection<CourseCategory> listAll(OrganizationId organizationId)
  {
    return mongoCourseCategoryRepository.findByOrganizationId(organizationId.getId()).stream()
        .map(this::map).collect(Collectors.toList());
  }

  @Override
  public List<CourseCategory> listAll(CourseCategoryId parentCategoryId)
  {
    return mongoCourseCategoryRepository.findByParentCategoryId(parentCategoryId.getId())
        .stream().map(this::map).collect(Collectors.toList());
  }

  @Override
  public Collection<CourseCategory> listAll(OrganizationId organizationId, CourseCategoryId parentCategoryId)
  {
    return mongoCourseCategoryRepository.findByOrganizationIdAndParentCategoryId(organizationId.getId(), parentCategoryId.getId())
        .stream().map(this::map).collect(Collectors.toList());
  }

  private CourseCategory map(MongoCourseCategory mongoCourseCategory)
  {
    CourseCategory courseCategory = new CourseCategory(OrganizationId.valueOf(mongoCourseCategory.getOrganizationId()),
        CourseCategoryId.valueOf(mongoCourseCategory.getId()),
        mongoCourseCategory.getParentCategoryId() != null ? CourseCategoryId.valueOf(mongoCourseCategory.getParentCategoryId()) : null,
        mongoCourseCategory.getName(), mongoCourseCategory.isAutoEnroll());
    courseCategory.setDescription(mongoCourseCategory.getDescription());
    return courseCategory;
  }

  @Override
  public boolean exists(CourseCategoryId courseCategoryId)
  {
    return mongoCourseCategoryRepository.existsById(courseCategoryId.getId());
  }

  @Override
  public List<CourseCategory> listAllByAutoEnrollment(OrganizationId organizationId)
  {
    return mongoCourseCategoryRepository.findAllByOrganizationIdAndAutoEnrollTrue(organizationId.getId()).stream()
        .map(this::map).collect(Collectors.toList());
  }

  @Override
  public List<CourseCategory> listAllByAutoEnrollment(OrganizationId organizationId, CourseCategoryId parentCategoryId)
  {
    return mongoCourseCategoryRepository.findAllByOrganizationIdAndParentCategoryIdAndAutoEnrollTrue(organizationId.getId(), parentCategoryId.getId()).stream()
        .map(this::map).collect(Collectors.toList());
  }

  @Override
  public void update(OrganizationId organizationId, CourseCategoryId categoryId, String name, String description, boolean autoEnroll) throws LmsRepositoryException
  {
    Optional<MongoCourseCategory> optional = mongoCourseCategoryRepository.findByOrganizationIdAndId(organizationId.getId(), categoryId.getId());
    MongoCourseCategory mongoCategory = optional.orElseThrow(() -> new LmsRepositoryException(String.format(CATEGORY_NOT_FOUND, categoryId)));

    mongoCategory.setName(name);
    mongoCategory.setDescription(description);
    mongoCategory.setAutoEnroll(autoEnroll);

    mongoCourseCategoryRepository.save(mongoCategory);
  }

  @Override
  public void updateAutoEnrollment(CourseCategoryId categoryId, boolean autoEnroll) throws LmsRepositoryException
  {
    Optional<MongoCourseCategory> optional = mongoCourseCategoryRepository.findById(categoryId.getId());
    MongoCourseCategory mongoCategory = optional.orElseThrow(() -> new LmsRepositoryException(String.format(CATEGORY_NOT_FOUND, categoryId)));
    mongoCategory.setAutoEnroll(autoEnroll);

    mongoCourseCategoryRepository.save(mongoCategory);
  }

  @Override
  public void updateAutoEnrollments(Set<CourseCategoryId> categoryIds, boolean autoEnroll)
  {
    List<MongoCourseCategory> mongoCategories = mongoCourseCategoryRepository.findAllByIdIn(categoryIds.stream().map(CourseCategoryId::getId)
        .collect(Collectors.toSet()));

    for (MongoCourseCategory category: mongoCategories)
    {
      category.setAutoEnroll(autoEnroll);
    }

    mongoCourseCategoryRepository.saveAll(mongoCategories);
  }

  @Override
  public CourseCategory getById(CourseCategoryId courseCategoryId) throws LmsRepositoryException
  {
    Optional<MongoCourseCategory> mongoCourseCategory = mongoCourseCategoryRepository.findById(courseCategoryId.getId());
    if (mongoCourseCategory.isPresent())
    {
      return map(mongoCourseCategory.get());
    }
    throw new LmsRepositoryException("Could not find course category with ID: " + courseCategoryId.getId());
  }

  @Override
  public CourseCategory addSubCategory(OrganizationId organizationId, CourseCategoryId categoryId, CourseCategoryId subCategoryId)
  {
    throw new UnsupportedOperationException();
  }

  @Override
  public CourseCategory getCourseCategory(OrganizationId organizationId, CourseCategoryId parentCategoryId, String categoryName)
  {
    throw new UnsupportedOperationException();
  }

  @Override
  public String getCourseCategoryNameById(CourseCategoryId courseCategoryId) throws LmsRepositoryException
  {
    Optional<MongoCourseCategory> optional = mongoCourseCategoryRepository.findById(courseCategoryId.getId());

    if (!optional.isPresent())
    {
      throw new LmsRepositoryException(String.format(CATEGORY_NOT_FOUND, courseCategoryId.getId()));
    }

    return optional.get().getName();
  }

  @Override
  public void delete(CourseCategoryId courseCategoryId)
  {
    mongoCourseCategoryRepository.deleteById(courseCategoryId.getId());
  }

  @Override
  public void delete(OrganizationId organizationId, CourseCategoryId parentCategoryId, String categoryName)
  {
    throw new UnsupportedOperationException();
  }
}
