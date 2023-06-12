package mn.erin.lms.base.domain.repository;

import java.util.List;

import mn.erin.lms.base.aim.organization.OrganizationId;
import mn.erin.lms.base.domain.model.exam.ExamGroup;
import mn.erin.lms.base.domain.model.exam.ExamGroupId;

/**
 * @author Galsan Bayart.
 */
public interface ExamGroupRepository
{
  /**
   * @param id exam group id
   * @return existing or not existing
   */
  boolean exists(ExamGroupId id);

  /**
   * @param parentId parent exam group id
   * @param name exam group name
   * @param organizationId organization id of exam group
   * @param description exam group description
   * @return created exam group
   */
  ExamGroup create(String parentId, String name, OrganizationId organizationId, String description);

  /**
   * @param examGroup The updating exam group
   * @throws LmsRepositoryException when given id does not exist returns that exception
   */
  String update(ExamGroup examGroup) throws LmsRepositoryException;

  /**
   *
   * @param id exam group id
   * @return exam group
   * @throws LmsRepositoryException when given id does not exist returns that exception
   */
  ExamGroup findById(ExamGroupId id) throws LmsRepositoryException;

  /**
   * @param id exam group id
   */
  boolean delete(ExamGroupId id);

  /**
   * @param id parent exam group id
   * @return all child groups
   */
  List<ExamGroup> findByParentIdAndOrganizationId(ExamGroupId id, OrganizationId organizationId);

  /**
   * @param examGroups exam group list
   */
  void updateAllParentId(List<ExamGroup> examGroups);

  /**
   * @param organizationId organization id of exam group
   * @return all exam groups
   */
  List<ExamGroup> findByOrganizationId(String organizationId);

  ExamGroup findByNameAndOrganizationId(String name, String organizationId);

  ExamGroup findByIdAndOrganizationId(ExamGroupId examGroupId, String organizationId);
}
