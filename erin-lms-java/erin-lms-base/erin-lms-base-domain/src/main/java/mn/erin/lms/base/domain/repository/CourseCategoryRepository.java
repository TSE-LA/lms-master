package mn.erin.lms.base.domain.repository;

import java.util.Collection;
import java.util.List;
import java.util.Set;

import mn.erin.lms.base.domain.model.category.CourseCategory;
import mn.erin.lms.base.domain.model.category.CourseCategoryId;
import mn.erin.lms.base.aim.organization.OrganizationId;

/**
 * @author Bat-Erdene Tsogoo.
 */
public interface CourseCategoryRepository
{
  /**
   * Creates a new course category.
   *
   * @param organizationId   The unique ID of the organization where the category will be created.
   * @param parentCategoryId The unique ID of the parent category. If null, then the category becomes a root category
   * @param categoryName     The name of the course category
   * @param description      The short description of the category
   * @param autoEnroll       The boolean parameter which indicates new users should be enrolled automatically
   * @return A new course category
   * @throws LmsRepositoryException If failed to create a new course category
   */
  CourseCategory create(OrganizationId organizationId, CourseCategoryId parentCategoryId, String categoryName, String description, boolean autoEnroll)
      throws LmsRepositoryException;

  /**
   * Adds a sub category to a category
   *
   * @param organizationId The unique ID of the organization
   * @param categoryId     The unique ID of the category where the subcategory will be added
   * @param subCategoryId  The unique ID of the subcategory
   * @return An updated category (the parent category where the subcategory is added)
   * @throws LmsRepositoryException if either organization ID or course category ID does not exist.
   */
  CourseCategory addSubCategory(OrganizationId organizationId, CourseCategoryId categoryId, CourseCategoryId subCategoryId)
      throws LmsRepositoryException;

  /**
   * Lists all course categories of a given organization
   *
   * @param organizationId The unique ID of the organization
   * @return A collection of course categories. Returns empty collection if no course categories were found.
   */
  Collection<CourseCategory> listAll(OrganizationId organizationId);

  List<CourseCategory> listAll(CourseCategoryId parentCategoryId);

  /**
   * Lists all course categories of a given organization and parent category
   *
   * @param organizationId   The unique ID of the organization
   * @param parentCategoryId The unique ID of the parent category
   * @return A collection of course categories. Returns empty collection if no course categories were found.
   */
  Collection<CourseCategory> listAll(OrganizationId organizationId, CourseCategoryId parentCategoryId);

  /**
   * Gets a course category by organization ID, parent category ID and category name
   *
   * @param organizationId   The unique ID of the organization
   * @param parentCategoryId The unique ID of the parent category
   * @param categoryName     The name of the course category
   * @return A course category
   * @throws LmsRepositoryException if the course category does not exist.
   */
  CourseCategory getCourseCategory(OrganizationId organizationId, CourseCategoryId parentCategoryId, String categoryName)
      throws LmsRepositoryException;

  /**
   * Gets a course category by ID
   *
   * @param courseCategoryId The unique ID of the course category
   * @return A course category name by Id
   * @throws LmsRepositoryException If the course category does not exist
   */
  String getCourseCategoryNameById(CourseCategoryId courseCategoryId) throws LmsRepositoryException;

  /**
   * Deletes a course category by ID
   *
   * @param courseCategoryId The unique ID of the course category
   * @throws LmsRepositoryException If the course category does not exist or failed to delete the category
   */
  void delete(CourseCategoryId courseCategoryId) throws LmsRepositoryException;

  /**
   * Deletes a course category by its organization ID, parent category ID and category name.
   *
   * @param organizationId   The unique ID of the organization
   * @param parentCategoryId The unique ID of the parent category
   * @param categoryName     The name of the course category
   * @throws LmsRepositoryException If failed to delete the course category
   */
  void delete(OrganizationId organizationId, CourseCategoryId parentCategoryId, String categoryName) throws LmsRepositoryException;

  /**
   * Checks if a course category exists
   *
   * @param courseCategoryId The ID of the course category
   * @return true if exists; otherwise, false
   */
  boolean exists(CourseCategoryId courseCategoryId);

  List<CourseCategory> listAllByAutoEnrollment(OrganizationId organizationId);

  List<CourseCategory> listAllByAutoEnrollment(OrganizationId organizationId, CourseCategoryId parentCategoryId);

  void update(OrganizationId organizationId, CourseCategoryId categoryId, String name, String description, boolean autoEnroll) throws LmsRepositoryException;

  void updateAutoEnrollment(CourseCategoryId categoryId, boolean autoEnroll) throws LmsRepositoryException;

  void updateAutoEnrollments(Set<CourseCategoryId> categoryIds, boolean autoEnroll);

  CourseCategory getById(CourseCategoryId courseCategoryId) throws LmsRepositoryException;
}
