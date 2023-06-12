package mn.erin.lms.base.domain.repository;

import java.util.Collection;
import java.util.List;

import mn.erin.lms.base.aim.organization.OrganizationId;
import mn.erin.lms.base.domain.model.category.QuestionCategory;
import mn.erin.lms.base.domain.model.category.QuestionCategoryId;

/**
 * @author Galsan Bayart
 */
public interface QuestionCategoryRepository
{
  /**
   * Creates a new question category
   *
   * @param parentCategoryId The parent category ID of the creating question category
   * @param organizationId   The organization ID of the question category
   * @param index            The index of question category
   * @param name             The name of the question category
   * @param description      The description of the question category
   * @return Returns a new question category
   * @throws LmsRepositoryException If failed to create question category
   */
  QuestionCategory create(QuestionCategoryId parentCategoryId, OrganizationId organizationId, int index, String name, String description)
      throws LmsRepositoryException;

  /**
   * Updates a question category
   *
   * @param organizationId     The organization ID of the question category
   * @param questionCategoryId The unique ID of the question category
   * @param index              The index of question category
   * @param name               The name of the question category
   * @param description        The description of the question category
   * @throws LmsRepositoryException If failed to update question category
   */
  void update(OrganizationId organizationId, QuestionCategoryId questionCategoryId, int index, String name, String description) throws LmsRepositoryException;

  /**
   * Gets the question categories by organization ID
   *
   * @param organizationId The organization ID of the question category
   * @return Returns list of the question category
   */
  List<QuestionCategory> getAllByOrganizationId(OrganizationId organizationId);

  /**
   * Gets a question category name by id
   *
   * @param categoryId The unique ID of the question category
   * @throws LmsRepositoryException If failed to delete the category
   */
  String getCategoryName(String categoryId) throws LmsRepositoryException;

  /**
   * Deletes a question category
   *
   * @param categoryId The unique ID of the question category
   * @throws LmsRepositoryException If failed to delete the category
   */
  void delete(QuestionCategoryId categoryId) throws LmsRepositoryException;

  /**
   * Checks whether a question category exists or not
   *
   * @param questionCategoryId The unique ID of the question category
   * @return If question category exists returns true otherwise false
   */
  boolean exists(QuestionCategoryId questionCategoryId);

  /**
   * Lists question categories by parent category ID and the organization ID
   *
   * @param parentCategoryId The parent category ID of question category
   * @param organizationId   The organization ID of the question category
   * @return A collection of question categories with specified parameter values. Returns empty list if no categories were found.
   */
  Collection<QuestionCategory> listAll(QuestionCategoryId parentCategoryId, OrganizationId organizationId);

  /**
   * Lists question categories by organization ID
   *
   * @param organizationId The organization ID of the question category
   * @return Lists question category by organization ID. Returns empty list if no categories were found.
   */
  Collection<QuestionCategory> listAllByOrganizationId(OrganizationId organizationId);
}
