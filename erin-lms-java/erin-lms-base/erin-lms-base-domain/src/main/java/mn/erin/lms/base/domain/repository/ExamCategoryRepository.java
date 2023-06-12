package mn.erin.lms.base.domain.repository;

import java.util.Collection;
import java.util.List;

import mn.erin.lms.base.aim.organization.OrganizationId;
import mn.erin.lms.base.domain.model.category.ExamCategory;
import mn.erin.lms.base.domain.model.category.ExamCategoryId;

/**
 * @author Temuulen Naranbold
 */
public interface ExamCategoryRepository
{
  ExamCategory create(ExamCategoryId parentCategoryId, OrganizationId organizationId, int index, String name, String description) throws LmsRepositoryException;

  void update(ExamCategoryId id, OrganizationId organizationId, int index, String name, String description) throws LmsRepositoryException;

  Collection<ExamCategory> listAll(ExamCategoryId parentCategoryId, OrganizationId organizationId);

  Collection<ExamCategory> listAllByOrganizationId(OrganizationId organizationId);

  List<ExamCategory> listAllByParentCategoryId(ExamCategoryId categoryId);

  boolean exists(ExamCategoryId examCategoryId);

  void delete(ExamCategoryId id) throws LmsRepositoryException;
}
