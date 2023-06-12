package mn.erin.lms.base.domain.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Set;

import mn.erin.lms.base.domain.model.assessment.AssessmentId;
import mn.erin.lms.base.domain.model.category.CourseCategoryId;
import mn.erin.lms.base.domain.model.certificate.CertificateId;
import mn.erin.lms.base.domain.model.classroom_course.CalendarEvent;
import mn.erin.lms.base.domain.model.content.CourseContentId;
import mn.erin.lms.base.domain.model.course.Course;
import mn.erin.lms.base.domain.model.course.CourseDepartmentRelation;
import mn.erin.lms.base.domain.model.course.CourseDetail;
import mn.erin.lms.base.domain.model.course.CourseId;
import mn.erin.lms.base.domain.model.course.CourseType;
import mn.erin.lms.base.domain.model.course.PublishStatus;
import mn.erin.lms.base.aim.organization.DepartmentId;
import mn.erin.lms.base.aim.user.AuthorId;

/**
 * @author Bat-Erdene Tsogoo.
 */
public interface CourseRepository
{
  /**
   * Creates a new course
   *
   * @param courseCategoryId The unique ID of the course category
   * @param courseDetail     The detail of the course
   * @return A new course
   * @throws LmsRepositoryException If failed to create a course
   */
  Course create(CourseCategoryId courseCategoryId, CourseDetail courseDetail) throws LmsRepositoryException;

  /**
   * Creates a new course
   *
   * @param courseCategoryId The unique ID of the course category
   * @param courseDetail     The detail of the course
   * @param courseType       The course type
   * @return A new course
   * @throws LmsRepositoryException If failed to create a course
   */
  Course create(CourseCategoryId courseCategoryId, CourseDetail courseDetail, CourseType courseType) throws LmsRepositoryException;

  /**
   * Creates a new course
   *
   * @param courseCategoryId         The unique ID of the course category
   * @param courseCategoryName       The string of the course category name
   * @param courseDetail             The detail of the course
   * @param courseDepartmentRelation The department relationship info
   * @param courseType               The course type
   * @return A new course
   * @throws LmsRepositoryException if failed to create a course
   */
  Course create(CourseCategoryId courseCategoryId, String courseCategoryName, CourseDetail courseDetail, CourseDepartmentRelation courseDepartmentRelation,
      CourseType courseType, String assessmentId, String certificateId)
      throws LmsRepositoryException;

  /**
   * Gets a course by ID
   *
   * @param courseId The unique ID of the course
   * @return A course
   * @throws LmsRepositoryException if the course does not exist.
   */
  Course fetchById(CourseId courseId) throws LmsRepositoryException;

  /**
   * Gets a course by ID
   *
   * @param courseId  The unique ID of the course
   * @param startDate The startDate of the course
   * @param endDate   The endDate of the course
   * @return A course
   * @throws LmsRepositoryException if the course does not exist.
   */
  Course fetchById(CourseId courseId, LocalDate startDate, LocalDate endDate) throws LmsRepositoryException;

  /**
   * Removes a course by ID
   *
   * @param courseId The unique ID of the course
   * @return true if it is removed; otherwise, false.
   */
  boolean delete(CourseId courseId);

  /**
   * Checks whether a course exists or not
   *
   * @param courseId The unique ID of the course
   * @return true if exists; otherwise, false
   */
  boolean exists(CourseId courseId);

  /**
   * Lists courses by category and department
   *
   * @param courseCategoryId
   * @return A list of courses with specified parameter values. Returns empty list if no courses were found.
   */
  List<Course> listAll(CourseCategoryId courseCategoryId);

  /**
   * Lists courses by category and department and limit
   *
   * @param courseCategoryId
   * @param courseCount
   * @return A list of courses with specified parameter values. Returns empty list if no courses were found.
   */

  List<Course> listAll(CourseCategoryId courseCategoryId, int courseCount);

  /**
   * Lists courses by category date interval and instructors
   *
   * @param startDate   the course must be publish after that date
   * @param endDate     the course must be publish before that date
   * @param instructors that list contains the course is instructor
   * @return A list of courses for calendar
   */
  List<CalendarEvent> listAllForCalendar(LocalDate startDate, LocalDate endDate, Set<String> instructors);

  /**
   * Lists courses by category date interval and courses
   *
   * @param startDate the course must be publish after that date
   * @param endDate   the course must be publish before that date
   * @param courses   that list contains online-course and classroom-course from one user
   * @return A list of courses for calendar
   */
  List<CalendarEvent> listAllForCalendarLearner(LocalDate startDate, LocalDate endDate, Set<String> courses);

  /**
   * Lists courses by category and department
   *
   * @param courseCategoryId
   * @param departmentId
   * @return A list of courses with specified parameter values. Returns empty list if no courses were found.
   */
  List<Course> listAll(CourseCategoryId courseCategoryId, DepartmentId departmentId);

  /**
   * Lists courses by category and department and limit
   *
   * @param getCourseCount
   * @param courseCategoryId
   * @param departmentId
   * @return A list of courses with specified parameter values. Returns empty list if no courses were found.
   */
  List<Course> listAll(CourseCategoryId courseCategoryId, DepartmentId departmentId, int getCourseCount);

  /**
   * Lists courses by category, publish status and properties.
   *
   * @param courseCategoryId The unique ID of the course category
   * @param status           The publish status of the course
   * @param properties       The properties of the course
   * @return A list of courses with specified parameter values. Returns empty list if no courses were found.
   */
  List<Course> listAll(CourseCategoryId courseCategoryId, PublishStatus status, Map<String, Object> properties);

  /**
   * Lists courses by category, course type and department ID
   *
   * @param courseCategoryId The unique ID of the course category
   * @param courseType       The course type
   * @param courseDepartment The department of the course. If null then the previous parameters are applied
   * @return A list of courses
   */
  List<Course> listAll(CourseCategoryId courseCategoryId, CourseType courseType, DepartmentId courseDepartment);

  /**
   * Lists courses by category and course type
   *
   * @param courseCategoryId The unique ID of the course category
   * @param courseType       The course type
   * @return A list of courses
   */
  List<Course> listAll(CourseCategoryId courseCategoryId, CourseType courseType);

  /**
   * Lists courses by category, course type and publish status
   *
   * @param courseCategoryId The unique ID of the course category
   * @param courseType       The course type
   * @param publishStatus    The publish status of the course
   * @return A list of courses
   */
  List<Course> listAll(CourseCategoryId courseCategoryId, CourseType courseType, PublishStatus publishStatus);

  List<Course> listAll(CourseCategoryId courseCategoryId, PublishStatus publishStatus, DepartmentId departmentId);

  /**
   * Lists courses by category, publish status and course type
   *
   * @param courseCategoryId The unique ID of the course category
   * @param status           The publish status of the course
   * @param courseType       The course type
   * @param courseDepartment The department of the course. If null then the previous parameters are applied
   * @return A list of courses with specified parameter values. Returns empty list if no courses were found.
   */
  List<Course> listAll(CourseCategoryId courseCategoryId, PublishStatus status, CourseType courseType, DepartmentId courseDepartment);

  /**
   * Lists courses by publish status.
   *
   * @param courseCategoryId The unique ID of the course category
   * @param status           The publish status of the course
   * @return A list of courses with the specified publish status
   */
  List<Course> listAll(CourseCategoryId courseCategoryId, PublishStatus status);

  /**
   * Lists courses by department and created date.
   *
   * @param departmentId The ID of the department whose courses will be returned
   * @param startDate    The starting date
   * @param endDate      The ending date
   * @return Courses with the specified department ID and created between the start and end dates
   */
  List<Course> listAll(DepartmentId departmentId, LocalDate startDate, LocalDate endDate);

  /**
   * Lists courses by department, course type and created date
   *
   * @param departmentId The ID of the department whose courses will be returned
   * @param courseType   The course type
   * @param startDate    The starting date
   * @param endDate      The ending date
   * @return Courses with the specified department, course type and created between the start and end dates
   */
  List<Course> listAll(DepartmentId departmentId, CourseType courseType, LocalDate startDate, LocalDate endDate);

  /**
   * Lists courses by department, course type and created date
   *
   * @param departmentId     The ID of the department whose courses will be returned
   * @param courseCategoryId The course courseCategoryId
   * @param startDate        The starting date
   * @param endDate          The ending date
   * @return Courses with the specified department, course type and created between the start and end dates
   */
  List<Course> listAll(DepartmentId departmentId, CourseCategoryId courseCategoryId, LocalDate startDate, LocalDate endDate);

  /**
   * Lists courses by department, category, course type and created date
   *
   * @param departmentId     The ID of the department whose courses will be returned
   * @param courseCategoryId The ID of the category
   * @param courseType       The course type
   * @param startDate        The starting date
   * @param endDate          The ending date
   * @return Courses with the specified department, category, course type and created between the start and end dates
   */
  List<Course> listAll(DepartmentId departmentId, CourseCategoryId courseCategoryId, CourseType courseType, LocalDate startDate, LocalDate endDate);

  /**
   * Counts the courses by the specified parameters below
   *
   * @param belongingDepartments A set of departments from which courses will be queried. If a course belongs to a department in the set
   *                             Then that course will be counted
   * @param courseCategoryId     The category ID of the course
   * @param publishStatus        The publish status of the course
   * @param courseType           The course type
   * @param startDate            The starting date range for the created date attribute in a course
   * @param endDate              The ending date range for the created date attribute in a course
   * @return The count of the matching courses
   */
  Integer countCreatedCourses(Set<DepartmentId> belongingDepartments, CourseCategoryId courseCategoryId, PublishStatus publishStatus, CourseType courseType,
      LocalDate startDate, LocalDate endDate);

  Integer countCreatedCourses(Set<DepartmentId> belongingDepartments, CourseCategoryId courseCategoryId, PublishStatus publishStatus, CourseType courseType,
      LocalDate startDate, LocalDate endDate, AuthorId authorId);

  Integer countCreatedCourses(Set<DepartmentId> belongingDepartments, CourseCategoryId courseCategoryId, CourseType courseType, LocalDate startDate,
      LocalDate endDate);

  Integer countCreatedCourses(Set<DepartmentId> belongingDepartments, CourseCategoryId courseCategoryId, CourseType courseType, LocalDate startDate,
      LocalDate endDate, AuthorId authorId);

  /**
   * Lists courses by assigned departments
   *
   * @param assignedDepartments The assigned departments
   * @return Courses with the specified assigned departments
   */
  List<Course> listAll(Set<DepartmentId> assignedDepartments, LocalDate startDate, LocalDate endDate, Set<String> courseIds);

  /**
   * @param courseIds The course IDs
   * @param startDate Start date
   * @param endDate End date
   * @return Courses with the specified assigned values
   */
  List<Course> listAllByCourseIds(Set<String> courseIds, LocalDate startDate, LocalDate endDate);

  /**
   * Lists courses by assigned departments
   *
   * @param assignedDepartments The assigned departments
   * @param userId              The assigned userId
   * @param startDate           The starting date
   * @param endDate             The ending date
   * @return Courses with the specified assigned departments
   */
  List<Course> listAllByEnrolledDepartments(String assignedDepartments, String userId, LocalDate startDate, LocalDate endDate);

  /**
   * Lists courses by assigned departments and courseType
   *
   * @param assignedDepartments The assigned departments
   * @param userId              The assigned userId
   * @param courseType          The courseType
   * @param startDate           The starting date
   * @param endDate             The ending date
   * @return Courses with the specified assigned departments
   */
  List<Course> listAllByEnrolledDepartments(String assignedDepartments, String userId, CourseType courseType, LocalDate startDate, LocalDate endDate);

  /**
   * Lists courses by assigned departments and courseType
   *
   * @param assignedDepartments The assigned departments
   * @param userId              The assigned userId
   * @param courseCategoryId    The courseCategoryId
   * @param startDate           The starting date
   * @param endDate             The ending date
   * @return Courses with the specified assigned departments
   */
  List<Course> listAllByEnrolledDepartments(String assignedDepartments, String userId, CourseCategoryId courseCategoryId, LocalDate startDate,
      LocalDate endDate);

  /**
   * Lists courses by assigned departments and courseType
   *
   * @param assignedDepartments The assigned departments
   * @param userId              The assigned userId
   * @param courseCategoryId    The courseCategoryId
   * @param courseType          The courseType
   * @param startDate           The starting date
   * @param endDate             The ending date
   * @return Courses with the specified assigned departments
   */
  List<Course> listAllByEnrolledDepartments(String assignedDepartments, String userId, CourseCategoryId courseCategoryId, CourseType courseType,
      LocalDate startDate,
      LocalDate endDate);

  /**
   * Lists all department courses
   *
   * @param departmentId The ID of the department
   * @return Courses belonging to the specified department
   */
  List<Course> listAll(DepartmentId departmentId);

  /**
   * Lists all assessment's courses
   *
   * @param assessmentId The ID of the assessment
   * @return Courses which has specified assessment
   */
  List<Course> listAll(AssessmentId assessmentId);

  /**
   * Lists courses by properties
   *
   * @param properties The course properties
   * @return A list of courses with the specified properties
   */
  List<Course> listAll(Map<String, Object> properties);

  /**
   * Searches a course by the specified key and value pair
   *
   * @param text                parameters for the search query
   * @param searchByName        parameters for the search query
   * @param searchByDescription parameters for the search query
   * @return A list of courses
   */
  List<Course> search(String text, boolean searchByName, boolean searchByDescription);

  /**
   * Searches a course by the specified key and value pair
   *
   * @param text                parameters for the search query
   * @param searchByName        parameters for the search query
   * @param searchByDescription parameters for the search query
   * @param departmentId        The department of the course. If null then the previous parameters are applied
   * @return A list of courses
   */

  List<Course> search(String text, boolean searchByName, boolean searchByDescription, String departmentId);

  /**
   * Updates course
   *
   * @param courseId         The unique ID of the course
   * @param courseCategoryId The ID of the category
   * @param courseDetail     The detail of the course
   * @param courseType       The course type
   * @return An updated course
   * @throws LmsRepositoryException If the course does not exist or failed to update the course
   */
  Course update(CourseId courseId, CourseCategoryId courseCategoryId, CourseDetail courseDetail, CourseType courseType, String assessmentId,
      String certificateId) throws LmsRepositoryException;

  Course update(CourseId courseId, CourseDetail courseDetail, CourseType courseType, CourseCategoryId courseCategoryId) throws LmsRepositoryException;

  /**
   * Changes the course department relation of the course
   *
   * @param courseId                 The ID of the course
   * @param courseDepartmentRelation The new course department relation
   * @return Updated course
   * @throws LmsRepositoryException If failed to update
   */
  Course update(CourseId courseId, CourseDepartmentRelation courseDepartmentRelation) throws LmsRepositoryException;

  /**
   * Changes the course department relation of the course
   *
   * @param courseId                 The ID of the course
   * @param courseDepartmentRelation The new course department relation
   * @param courseDetail             The new course detail
   * @return Updated course
   * @throws LmsRepositoryException If failed to update
   */
  Course update(CourseId courseId, CourseDepartmentRelation courseDepartmentRelation, CourseDetail courseDetail) throws LmsRepositoryException;

  /**
   * Updates a course by setting a course content ID
   *
   * @param courseId        The unique ID of the course
   * @param courseContentId The unique ID of the course content
   * @return An updated course
   * @throws LmsRepositoryException If the course does not exist or failed to assign the content to the course.
   */
  Course update(CourseId courseId, CourseContentId courseContentId) throws LmsRepositoryException;

  /**
   * Updates a course by changing its publish status
   *
   * @param courseId      The unique ID of the course
   * @param publishStatus Publish status
   * @return An updated course
   * @throws LmsRepositoryException If the course does not exist or failed to assign the content to the course.
   */
  Course update(CourseId courseId, PublishStatus publishStatus) throws LmsRepositoryException;

  void update(CourseId courseId, int certifiedNumber) throws LmsRepositoryException;

  /**
   * Updates a course properties
   *
   * @param courseId   The unique ID of the course
   * @param properties Publish status
   * @return An updated course
   * @throws LmsRepositoryException If the course does not exist or failed to update properties to the course.
   */
  Course updateCourseProperties(CourseId courseId, Map<String, String> properties) throws LmsRepositoryException;

  /**
   * Updates a course by changing its publish status and Assigned Departments
   *
   * @param courseId                 The unique ID of the course
   * @param courseDepartmentRelation The new course department relation
   * @param publishStatus            Publish status
   * @return An updated course
   * @throws LmsRepositoryException If the course does not exist or failed to assign the content to the course.
   */
  Course updateByDepartment(CourseId courseId, CourseDepartmentRelation courseDepartmentRelation, PublishStatus publishStatus) throws LmsRepositoryException;

  /**
   * Updates a course by changing its publish status and Enrollment
   *
   * @param courseId                 The unique ID of the course
   * @param courseDepartmentRelation The new course department relation
   * @param publishStatus            Publish status
   * @return An updated course
   * @throws LmsRepositoryException If the course does not exist or failed to assign the content to the course.
   */
  Course updateByEnrollment(CourseId courseId, CourseDepartmentRelation courseDepartmentRelation, PublishStatus publishStatus) throws LmsRepositoryException;

  boolean existsByCertificate(CertificateId certificateId);
}

