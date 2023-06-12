package mn.erin.lms.base.mongo.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.jetbrains.annotations.NotNull;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.repository.query.QueryByExampleExecutor;

import mn.erin.lms.base.mongo.document.course.MongoCourse;
import mn.erin.lms.base.mongo.document.course.MongoPublishStatus;

/**
 * @author Bat-Erdene Tsogoo.
 */
public interface MongoCourseRepository extends MongoRepository<MongoCourse, String>, QueryByExampleExecutor<MongoCourse>
{
  @NotNull
  Optional<MongoCourse> findById(@NotNull String id);

  Optional<MongoCourse> findByIdAndCreatedDateBetween(String id, LocalDateTime from, LocalDateTime to);

  List<MongoCourse> findByCategoryId(String categoryId);

  List<MongoCourse> findByCategoryIdAndPublishStatus(String categoryId, MongoPublishStatus publishStatus);

  List<MongoCourse> findByCategoryIdAndCourseDepartment(String categoryId, String courseDepartment);

  List<MongoCourse> findByCategoryIdAndCourseDepartmentAndPublishStatus(String categoryId, String courseDepartment, MongoPublishStatus publishStatus);

  List<MongoCourse> findByCategoryIdAndAssignedDepartmentsIsContaining(String categoryId, String courseDepartment);

  List<MongoCourse> findByCategoryIdAndAssignedDepartmentsIsContainingAndPublishStatus(String categoryId, String courseDepartment,
      MongoPublishStatus publishStatus);

  List<MongoCourse> findByCategoryIdAndPublishStatusAndCourseType(String categoryId, MongoPublishStatus publishStatus, String courseType);

  List<MongoCourse> findByCategoryIdAndCourseTypeAndCourseDepartment(String categoryId, String courseType, String courseDepartment);

  List<MongoCourse> findByCategoryIdAndCourseTypeAndAssignedDepartmentsIsContaining(String categoryId, String courseType, String courseDepartment);

  List<MongoCourse> findByCategoryIdAndCourseType(String categoryId, String courseType);

  List<MongoCourse> findByCategoryIdAndPublishStatusAndCourseTypeAndCourseDepartment(String categoryId, MongoPublishStatus publishStatus, String courseType,
      String courseDepartment);

  List<MongoCourse> findByCategoryIdAndPublishStatusAndCourseTypeAndAssignedDepartmentsIsContaining(String categoryId, MongoPublishStatus publishStatus,
      String courseType,
      String courseDepartment);

  List<MongoCourse> findByCourseDepartment(String courseDepartment);

  List<MongoCourse> findByAssessmentId(String assessmentId);

  List<MongoCourse> findByCourseDepartmentAndCreatedDateBetween(String courseDepartment, LocalDateTime from, LocalDateTime to);

  List<MongoCourse> findByAssignedDepartmentsIsContainingAndCreatedDateBetween(String assignedDepartment, LocalDateTime from, LocalDateTime to);

  List<MongoCourse> findByAssignedLearnersIsContainingAndCreatedDateBetween(String assignedLearner, LocalDateTime from, LocalDateTime to);

  List<MongoCourse> findByAssignedLearnersIsContainingAndCourseTypeAndCreatedDateBetween(String assignedLearner, String courseType, LocalDateTime from,
      LocalDateTime to);

  List<MongoCourse> findByCourseDepartmentAndCourseTypeAndCreatedDateBetween(String courseDepartment, String courseType,
      LocalDateTime from, LocalDateTime to);

  List<MongoCourse> findByAssignedDepartmentsIsContainingAndCourseTypeAndCreatedDateBetween(String assignedDepartment, String courseType,
      LocalDateTime from, LocalDateTime to);

  List<MongoCourse> findByCourseDepartmentAndCategoryIdAndCreatedDateBetween(String courseDepartment, String categoryId, LocalDateTime from, LocalDateTime to);

  List<MongoCourse> findByAssignedDepartmentsIsContainingAndCategoryIdAndCreatedDateBetween(String assignedDepartment, String categoryId, LocalDateTime from,
      LocalDateTime to);

  List<MongoCourse> findByAssignedLearnersIsContainingAndCategoryIdAndCreatedDateBetween(String assignedUser, String categoryId, LocalDateTime from,
      LocalDateTime to);

  List<MongoCourse> findByCourseDepartmentAndCategoryIdAndCourseTypeAndCreatedDateBetween(String courseDepartment, String categoryId, String courseType,
      LocalDateTime from, LocalDateTime to);

  List<MongoCourse> findByAssignedDepartmentsIsContainingAndCategoryIdAndCourseTypeAndCreatedDateBetween(String assignedDepartment, String categoryId,
      String courseType, LocalDateTime from, LocalDateTime to);

  List<MongoCourse> findByAssignedLearnersIsContainingAndCategoryIdAndCourseTypeAndCreatedDateBetween(String assignedUser, String categoryId,
      String courseType, LocalDateTime from, LocalDateTime to);

  List<MongoCourse> findByCourseDepartmentInAndCreatedDateBetweenAndIdNotIn(Set<String> assignedDepartments, LocalDateTime from, LocalDateTime to, Set<String> ids);

  List<MongoCourse> findByIdInAndPublishDateBetween(Set<String> courseIds, LocalDateTime from, LocalDateTime to);

  List<MongoCourse> findByCategoryIdAndPublishStatusAndCourseDepartment(String categoryId, MongoPublishStatus publishStatus, String courseDepartment);

  List<MongoCourse> findByCategoryIdAndPublishStatusAndAssignedDepartmentsIsContaining(String categoryId, MongoPublishStatus publishStatus, String courseDepartment);

  @Query(value = "{ $or: [ { 'title' : {$regex:?0,$options:'i'} }, { 'description' : {$regex:?0,$options:'i'} } ] }")
  List<MongoCourse> findByTitleRegexOrDescriptionRegex(String title, String description);

  @Query(value = "{'title': {$regex : ?0, $options: 'i'}}")
  List<MongoCourse> findByTitleRegex(String title);

  @Query(value = "{'description': {$regex : ?0, $options: 'i'}}")
  List<MongoCourse> findByDescriptionRegex(String description);

  @Query(value = "{ $or: [ { 'title' : {$regex:?0,$options:'i'} }, { 'description' : {$regex:?0,$options:'i'} } ] }")
  List<MongoCourse> findByTitleRegexOrDescriptionRegexAndCourseDepartment(String title, String description, String department);

  @Query(value = "{'title': {$regex : ?0, $options: 'i'}}")
  List<MongoCourse> findByTitleRegexAndCourseDepartment(String title, String department);

  @Query(value = "{'description': {$regex : ?0, $options: 'i'}}")
  List<MongoCourse> findByDescriptionRegexAndCourseDepartment(String description, String department);

  Integer countByCourseDepartmentInAndCategoryIdAndPublishStatusAndCourseTypeAndCreatedDateBetweenAndAuthorId(Set<String> courseDepartments,
      String categoryId, MongoPublishStatus publishStatus, String courseType, LocalDateTime from, LocalDateTime to, String authorId);

  Integer countByCourseDepartmentInAndCategoryIdAndPublishStatusAndCourseTypeAndCreatedDateBetween(Set<String> courseDepartments,
      String categoryId, MongoPublishStatus publishStatus, String courseType, LocalDateTime from, LocalDateTime to);

  Integer countByCourseDepartmentInAndCategoryIdAndCourseTypeAndCreatedDateBetween(Set<String> courseDepartments,
      String categoryId, String courseType, LocalDateTime from, LocalDateTime to);

  Integer countByCourseDepartmentInAndCategoryIdAndCourseTypeAndCreatedDateBetweenAndAuthorId(Set<String> courseDepartments,
      String categoryId, String courseType, LocalDateTime from, LocalDateTime to, String authorId);

  boolean existsByCertificateId(String certificateId);
}
