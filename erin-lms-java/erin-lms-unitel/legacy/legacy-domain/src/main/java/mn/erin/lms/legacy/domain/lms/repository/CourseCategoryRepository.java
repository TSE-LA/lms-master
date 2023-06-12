/*
 * (C)opyright, 2019, ERIN SYSTEMS LLC
 * The source code is protected by copyright laws and international copyright
 * treaties, as well as other intellectual property laws and treaties.
 * All rights reserved.
 */

package mn.erin.lms.legacy.domain.lms.repository;

import java.util.Collection;

import mn.erin.lms.legacy.domain.lms.model.course.CompanyId;
import mn.erin.lms.legacy.domain.lms.model.course.CourseCategory;
import mn.erin.lms.legacy.domain.lms.model.course.CourseCategoryId;

/**
 * @author Bat-Erdene Tsogoo.
 */
public interface CourseCategoryRepository
{
  /**
   * Creates a new course category.
   *
   * @param companyId        The unique ID of the company where the category will be created.
   * @param parentCategoryId The unique ID of the parent category.
   * @param categoryName     The name of the course category
   * @param description      The short description of the category
   * @return A new course category
   * @throws LMSRepositoryException If failed to create a new course category
   */
  CourseCategory create(CompanyId companyId, CourseCategoryId parentCategoryId, String categoryName, String description)
      throws LMSRepositoryException;

  /**
   * Adds a sub category to a category
   *
   * @param companyId     The unique ID of the company
   * @param categoryId    The unique ID of the category where the subcategory will be added
   * @param subCategoryId The unique ID of the subcategory
   * @return An updated category (the parent category where the subcategory is added)
   * @throws LMSRepositoryException if either company ID or course category ID does not exist.
   */
  CourseCategory addSubCategory(CompanyId companyId, CourseCategoryId categoryId, CourseCategoryId subCategoryId)
    throws LMSRepositoryException;

  /**
   * Lists all course categories of a given company
   *
   * @param companyId The unique ID of the company
   * @return A collection of course categories. Returns empty collection if no course categories were found.
   */
  Collection<CourseCategory> listAll(CompanyId companyId);

  /**
   * Lists all course categories of a given company
   *
   * @param parentCategoryId The unique ID of the company
   * @return A collection of course categories. Returns empty collection if no course categories were found.
   */
  Collection<CourseCategory> listAll(CourseCategoryId parentCategoryId);

  /**
   * Lists all course categories of a given company and parent category
   *
   * @param companyId        The unique ID of the company
   * @param parentCategoryId The unique ID of the parent category
   * @return A collection of course categories. Returns empty collection if no course categories were found.
   */
  Collection<CourseCategory> listAll(CompanyId companyId, CourseCategoryId parentCategoryId);

  /**
   * Gets a course category by company ID, parent category ID and category name
   *
   * @param companyId        The unique ID of the company
   * @param parentCategoryId The unique ID of the parent category
   * @param categoryName     The name of the course category
   * @return A course category
   * @throws LMSRepositoryException if the course category does not exist.
   */
  CourseCategory getCourseCategory(CompanyId companyId, CourseCategoryId parentCategoryId, String categoryName)
      throws LMSRepositoryException;

  /**
   * Gets a course category by ID
   *
   * @param courseCategoryId The unique ID of the course category
   * @return A course category
   * @throws LMSRepositoryException If the course category does not exist
   */
  CourseCategory getCourseCategory(CourseCategoryId courseCategoryId) throws LMSRepositoryException;

  /**
   * Deletes a course category by ID
   *
   * @param courseCategoryId The unique ID of the course category
   * @throws LMSRepositoryException If the course category does not exist or failed to delete the category
   */
  void delete(CourseCategoryId courseCategoryId) throws LMSRepositoryException;

  /**
   * Deletes a course category by its company ID, parent category ID and category name.
   *
   * @param companyId        The unique ID of the company
   * @param parentCategoryId The unique ID of the parent category
   * @param categoryName     The name of the course category
   * @throws LMSRepositoryException If failed to delete the course category
   */
  void delete(CompanyId companyId, CourseCategoryId parentCategoryId, String categoryName) throws LMSRepositoryException;
}
