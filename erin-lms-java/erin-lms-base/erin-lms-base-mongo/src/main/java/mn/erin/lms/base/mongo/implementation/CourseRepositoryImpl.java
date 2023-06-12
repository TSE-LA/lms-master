package mn.erin.lms.base.mongo.implementation;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import javax.inject.Inject;

import com.mongodb.client.MongoCollection;
import org.apache.commons.lang3.Validate;
import org.bson.Document;

import mn.erin.domain.base.model.EntityId;
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
import mn.erin.lms.base.domain.repository.CourseRepository;
import mn.erin.lms.base.domain.repository.LmsRepositoryException;
import mn.erin.lms.base.aim.AuthorIdProvider;
import mn.erin.lms.base.domain.service.CourseTypeResolver;
import mn.erin.lms.base.mongo.document.course.MongoCourse;
import mn.erin.lms.base.mongo.document.course.MongoPublishStatus;
import mn.erin.lms.base.mongo.repository.MongoCourseRepository;
import mn.erin.lms.base.mongo.util.ClassroomCourseAggregations;
import mn.erin.lms.base.mongo.util.IdGenerator;
import mn.erin.lms.base.mongo.util.ObjectMapper;

import static mn.erin.lms.base.mongo.util.ClassroomCourseAggregations.DATE_FORMATTER;
import static mn.erin.lms.base.mongo.util.ClassroomCourseAggregations.STAGE_MATCH_FOR_INSTRUCTOR;
import static mn.erin.lms.base.mongo.util.ClassroomCourseAggregations.STAGE_MATCH_FOR_LEARNER;
import static mn.erin.lms.base.mongo.util.ClassroomCourseAggregations.STAGE_PROJECT_FOR_INSTRUCTOR;
import static mn.erin.lms.base.mongo.util.ClassroomCourseAggregations.STAGE_PROJECT_FOR_LEARNER;
import static mn.erin.lms.base.mongo.util.ClassroomCourseAggregations.toArrayString;

/**
 * @author Bat-Erdene Tsogoo.
 */
public class CourseRepositoryImpl implements CourseRepository
{
  private static final String ERROR_MSG_EMPTY = "search query can not be null";

  private final MongoCourseRepository mongoCourseRepository;

  private AuthorIdProvider authorIdProvider;
  private CourseTypeResolver courseTypeResolver;

  private final MongoCollection<Document> courseCollection;

  public CourseRepositoryImpl(MongoCollection<Document> courseCollection, MongoCourseRepository mongoCourseRepository)
  {
    this.courseCollection = courseCollection;
    this.mongoCourseRepository = mongoCourseRepository;
  }

  @Inject
  public void setAuthorIdProvider(AuthorIdProvider authorIdProvider)
  {
    this.authorIdProvider = authorIdProvider;
  }

  @Inject
  public void setCourseTypeResolver(CourseTypeResolver courseTypeResolver)
  {
    this.courseTypeResolver = courseTypeResolver;
  }

  @Override
  public Course create(CourseCategoryId courseCategoryId, String courseCategoryName, CourseDetail courseDetail,
      CourseDepartmentRelation courseDepartmentRelation, CourseType courseType, String assessmentId, String certificateId)
  {
    String id = IdGenerator.generateId();
    MongoCourse mongoCourse = new MongoCourse(id, courseDetail.getTitle(), courseCategoryId.getId(), courseType.getType(),
        MongoPublishStatus.valueOf(courseDetail.getPublishStatus().name()), authorIdProvider.getAuthorId());
    mongoCourse.setCategoryName(courseCategoryName);
    mongoCourse.setAssignedDepartments(courseDepartmentRelation.getAssignedDepartments().stream().map(EntityId::getId).collect(Collectors.toSet()));
    mongoCourse.setAssignedLearners(courseDepartmentRelation.getAssignedLearners().stream().map(EntityId::getId).collect(Collectors.toSet()));
    mongoCourse.setCourseDepartment(courseDepartmentRelation.getCourseDepartment().getId());
    mongoCourse.setDescription(courseDetail.getDescription());
    mongoCourse.setProperties(courseDetail.getProperties());
    mongoCourse.setAssessmentId(assessmentId);
    mongoCourse.setHasCertificate(certificateId != null);
    mongoCourse.setHasAssessment(assessmentId != null);
    mongoCourse.setCertificateId(certificateId);
    mongoCourse.setCreatedDate(courseDetail.getDateInfo().getCreatedDate());
    mongoCourse.setModifiedDate(courseDetail.getDateInfo().getModifiedDate());
    mongoCourse.setThumbnailUrl(courseDetail.getThumbnailUrl());
    mongoCourse.setCredit(courseDetail.getCredit());

    mongoCourseRepository.save(mongoCourse);

    return ObjectMapper.mapToCourse(mongoCourse, courseTypeResolver);
  }

  @Override
  public Course fetchById(CourseId courseId) throws LmsRepositoryException
  {
    return getCourse(courseId.getId());
  }

  @Override
  public Course fetchById(CourseId courseId, LocalDate startDate, LocalDate endDate) throws LmsRepositoryException
  {
    return getCourse(courseId.getId(), startDate, endDate);
  }

  @Override
  public boolean delete(CourseId courseId)
  {
    mongoCourseRepository.deleteById(courseId.getId());
    return true;
  }

  @Override
  public boolean exists(CourseId courseId)
  {
    return mongoCourseRepository.existsById(courseId.getId());
  }

  @Override
  public List<Course> listAll(CourseCategoryId courseCategoryId, int courseCount)
  {
    List<MongoCourse> mongoCourses = mongoCourseRepository.findByCategoryId(courseCategoryId.getId());
    return getCourses(mongoCourses);
  }

  @Override
  public List<CalendarEvent> listAllForCalendar(LocalDate startDate, LocalDate endDate, Set<String> instructors)
  {
    return courseCollection.aggregate(Arrays.asList(
        Document.parse(STAGE_PROJECT_FOR_INSTRUCTOR),
        Document.parse(String.format(
            STAGE_MATCH_FOR_INSTRUCTOR,
            toArrayString(instructors),
            startDate.atStartOfDay().format(DATE_FORMATTER),
            endDate.atTime(LocalTime.MAX).format(DATE_FORMATTER)
        ))
    )).map(ClassroomCourseAggregations::mapToCourseCalendar).into(new ArrayList<>());
  }

  @Override
  public List<CalendarEvent> listAllForCalendarLearner(LocalDate startDate, LocalDate endDate, Set<String> courses)
  {
    return courseCollection.aggregate(Arrays.asList(
        Document.parse(STAGE_PROJECT_FOR_LEARNER),
        Document.parse(String.format(
            STAGE_MATCH_FOR_LEARNER,
            toArrayString(courses),
            startDate.atStartOfDay().format(DATE_FORMATTER),
            endDate.atTime(LocalTime.MAX).format(DATE_FORMATTER)
        ))
    )).map(ClassroomCourseAggregations::mapToCourseCalendar).into(new ArrayList<>());
  }

  @Override
  public List<Course> listAll(CourseCategoryId courseCategoryId)
  {
    List<MongoCourse> mongoCourses = mongoCourseRepository.findByCategoryId(courseCategoryId.getId());
    return getCourses(mongoCourses);
  }

  @Override
  public List<Course> listAll(CourseCategoryId courseCategoryId, DepartmentId departmentId)
  {
    List<MongoCourse> mongoCourses = mongoCourseRepository
        .findByCategoryIdAndCourseDepartment(courseCategoryId.getId(), departmentId.getId());
    mongoCourses.addAll(mongoCourseRepository.findByCategoryIdAndAssignedDepartmentsIsContaining(courseCategoryId.getId(), departmentId.getId()));
    return getCourses(mongoCourses);
  }

  @Override
  public List<Course> listAll(CourseCategoryId courseCategoryId, DepartmentId departmentId, int getCourseCount)
  {
    List<MongoCourse> mongoCourses = mongoCourseRepository
        .findByCategoryIdAndCourseDepartmentAndPublishStatus(courseCategoryId.getId(), departmentId.getId(), MongoPublishStatus.valueOf("PUBLISHED"));
    mongoCourses.addAll(mongoCourseRepository.findByCategoryIdAndAssignedDepartmentsIsContainingAndPublishStatus(courseCategoryId.getId(), departmentId.getId(),
        MongoPublishStatus.valueOf("PUBLISHED")));
    return getCourses(mongoCourses);
  }

  @Override
  public List<Course> listAll(CourseCategoryId courseCategoryId, CourseType courseType, DepartmentId courseDepartment)
  {
    List<MongoCourse> mongoCourses = mongoCourseRepository
        .findByCategoryIdAndCourseTypeAndCourseDepartment(courseCategoryId.getId(), courseType.getType(), courseDepartment.getId());
    mongoCourses.addAll(mongoCourseRepository
        .findByCategoryIdAndCourseTypeAndAssignedDepartmentsIsContaining(courseCategoryId.getId(), courseType.getType(), courseDepartment.getId()));
    return getCourses(mongoCourses);
  }

  @Override
  public List<Course> listAll(CourseCategoryId courseCategoryId, CourseType courseType)
  {
    List<MongoCourse> mongoCourses = mongoCourseRepository.findByCategoryIdAndCourseType(courseCategoryId.getId(), courseType.getType());
    return getCourses(mongoCourses);
  }

  @Override
  public List<Course> listAll(CourseCategoryId courseCategoryId, CourseType courseType, PublishStatus publishStatus)
  {
    List<MongoCourse> mongoCourses = mongoCourseRepository
        .findByCategoryIdAndPublishStatusAndCourseType(courseCategoryId.getId(), MongoPublishStatus.valueOf(publishStatus.name()), courseType.getType());
    return getCourses(mongoCourses);
  }

  @Override
  public List<Course> listAll(CourseCategoryId courseCategoryId, PublishStatus publishStatus, DepartmentId departmentId)
  {
    List<MongoCourse> mongoCourses =
        mongoCourseRepository.findByCategoryIdAndPublishStatusAndCourseDepartment(courseCategoryId.getId(), MongoPublishStatus.valueOf(publishStatus.name()),
            departmentId.getId());
    mongoCourses.addAll(mongoCourseRepository
        .findByCategoryIdAndPublishStatusAndAssignedDepartmentsIsContaining(courseCategoryId.getId(), MongoPublishStatus.valueOf(publishStatus.name()),
            departmentId.getId()));
    return getCourses(mongoCourses);
  }

  @Override
  public List<Course> listAll(CourseCategoryId courseCategoryId, PublishStatus status, CourseType courseType, DepartmentId courseDepartment)
  {
    List<MongoCourse> mongoCourses = mongoCourseRepository
        .findByCategoryIdAndPublishStatusAndCourseTypeAndCourseDepartment(courseCategoryId.getId(), MongoPublishStatus.valueOf(status.name()),
            courseType.getType(), courseDepartment.getId());
    mongoCourses.addAll(mongoCourseRepository
        .findByCategoryIdAndPublishStatusAndCourseTypeAndAssignedDepartmentsIsContaining(courseCategoryId.getId(), MongoPublishStatus.valueOf(status.name()),
            courseType.getType(), courseDepartment.getId()));
    return getCourses(mongoCourses);
  }

  @Override
  public List<Course> listAllByEnrolledDepartments(String assignedDepartments, String assignedUser, LocalDate startDate, LocalDate endDate)
  {
    List<MongoCourse> mongoCourses = mongoCourseRepository.findByAssignedDepartmentsIsContainingAndCreatedDateBetween(
        assignedDepartments, startDate.atStartOfDay(), LocalDateTime.of(endDate, LocalTime.MAX));
    mongoCourses.addAll(mongoCourseRepository.findByAssignedLearnersIsContainingAndCreatedDateBetween(
        assignedUser, startDate.atStartOfDay(), LocalDateTime.of(endDate, LocalTime.MAX)));
    return getCourses(mongoCourses);
  }

  @Override
  public List<Course> listAllByEnrolledDepartments(String departmentId, String assignedUser, CourseType courseType, LocalDate startDate, LocalDate endDate)
  {
    List<MongoCourse> mongoCourses = mongoCourseRepository.findByAssignedDepartmentsIsContainingAndCourseTypeAndCreatedDateBetween(
        departmentId, courseType.getType(), startDate.atStartOfDay(), LocalDateTime.of(endDate, LocalTime.MAX));
    mongoCourses.addAll(mongoCourseRepository.findByAssignedLearnersIsContainingAndCourseTypeAndCreatedDateBetween(
        assignedUser, courseType.getType(), startDate.atStartOfDay(), LocalDateTime.of(endDate, LocalTime.MAX)));
    return getCourses(mongoCourses);
  }

  @Override
  public List<Course> listAllByEnrolledDepartments(String departmentId, String assignedUser, CourseCategoryId courseCategoryId, LocalDate startDate,
      LocalDate endDate)
  {
    List<MongoCourse> mongoCourses = mongoCourseRepository.findByAssignedDepartmentsIsContainingAndCategoryIdAndCreatedDateBetween(
        departmentId, courseCategoryId.getId(), startDate.atStartOfDay(), LocalDateTime.of(endDate, LocalTime.MAX));
    mongoCourses.addAll(mongoCourseRepository.findByAssignedLearnersIsContainingAndCategoryIdAndCreatedDateBetween(
        assignedUser, courseCategoryId.getId(), startDate.atStartOfDay(), LocalDateTime.of(endDate, LocalTime.MAX)));
    return getCourses(mongoCourses);
  }

  @Override
  public List<Course> listAllByEnrolledDepartments(String departmentId, String assignedUser, CourseCategoryId courseCategoryId, CourseType courseType,
      LocalDate startDate,
      LocalDate endDate)
  {
    List<MongoCourse> mongoCourses = mongoCourseRepository.findByAssignedDepartmentsIsContainingAndCategoryIdAndCourseTypeAndCreatedDateBetween(
        departmentId, courseCategoryId.getId(), courseType.getType(), startDate.atStartOfDay(), LocalDateTime.of(endDate, LocalTime.MAX));
    mongoCourses.addAll(mongoCourseRepository.findByAssignedLearnersIsContainingAndCategoryIdAndCourseTypeAndCreatedDateBetween(
        assignedUser, courseCategoryId.getId(), courseType.getType(), startDate.atStartOfDay(), LocalDateTime.of(endDate, LocalTime.MAX)));
    return getCourses(mongoCourses);
  }

  @Override
  public List<Course> listAll(DepartmentId departmentId, LocalDate startDate, LocalDate endDate)
  {
    List<MongoCourse> mongoCourses = mongoCourseRepository
        .findByCourseDepartmentAndCreatedDateBetween(departmentId.getId(),
            startDate.atStartOfDay(), LocalDateTime.of(endDate, LocalTime.MAX));
    mongoCourses.addAll(mongoCourseRepository
        .findByAssignedDepartmentsIsContainingAndCreatedDateBetween(departmentId.getId(),
            startDate.atStartOfDay(), LocalDateTime.of(endDate, LocalTime.MAX)));
    return getCourses(mongoCourses);
  }

  @Override
  public List<Course> listAll(DepartmentId departmentId, CourseType courseType, LocalDate startDate, LocalDate endDate)
  {
    List<MongoCourse> mongoCourses = mongoCourseRepository
        .findByCourseDepartmentAndCourseTypeAndCreatedDateBetween(departmentId.getId(), courseType.getType(), startDate.atStartOfDay(),
            LocalDateTime.of(endDate, LocalTime.MAX));
    mongoCourses.addAll(mongoCourseRepository
        .findByAssignedDepartmentsIsContainingAndCourseTypeAndCreatedDateBetween(departmentId.getId(), courseType.getType(), startDate.atStartOfDay(),
            LocalDateTime.of(endDate, LocalTime.MAX)));
    return getCourses(mongoCourses);
  }

  @Override
  public List<Course> listAll(DepartmentId departmentId, CourseCategoryId courseCategoryId, LocalDate startDate,
      LocalDate endDate)
  {
    List<MongoCourse> mongoCourses = mongoCourseRepository
        .findByCourseDepartmentAndCategoryIdAndCreatedDateBetween(departmentId.getId(),
            courseCategoryId.getId(), startDate.atStartOfDay(), LocalDateTime.of(endDate, LocalTime.MAX));
    mongoCourses.addAll(mongoCourseRepository
        .findByAssignedDepartmentsIsContainingAndCategoryIdAndCreatedDateBetween(departmentId.getId(),
            courseCategoryId.getId(), startDate.atStartOfDay(), LocalDateTime.of(endDate, LocalTime.MAX)));
    return getCourses(mongoCourses);
  }

  @Override
  public List<Course> listAll(DepartmentId departmentId, CourseCategoryId courseCategoryId, CourseType courseType,
      LocalDate startDate, LocalDate endDate)
  {
    List<MongoCourse> mongoCourses = mongoCourseRepository
        .findByCourseDepartmentAndCategoryIdAndCourseTypeAndCreatedDateBetween(departmentId.getId(),
            courseCategoryId.getId(), courseType.getType(), startDate.atStartOfDay(), LocalDateTime.of(endDate, LocalTime.MAX));
    mongoCourses.addAll(mongoCourseRepository
        .findByAssignedDepartmentsIsContainingAndCategoryIdAndCourseTypeAndCreatedDateBetween(departmentId.getId(),
            courseCategoryId.getId(), courseType.getType(), startDate.atStartOfDay(), LocalDateTime.of(endDate, LocalTime.MAX)));
    return getCourses(mongoCourses);
  }

  @Override
  public List<Course> listAll(Set<DepartmentId> assignedDepartments, LocalDate startDate, LocalDate endDate, Set<String> courseIds)
  {
    List<MongoCourse> mongoCourses = mongoCourseRepository.findByCourseDepartmentInAndCreatedDateBetweenAndIdNotIn(
        assignedDepartments.stream().map(DepartmentId::getId).collect(Collectors.toSet()),
        startDate.atStartOfDay(),
        LocalDateTime.of(endDate, LocalTime.MAX),
        courseIds);
    return getCourses(mongoCourses);
  }

  @Override
  public List<Course> listAllByCourseIds(Set<String> courseIds, LocalDate startDate, LocalDate endDate)
  {
    List<MongoCourse> mongoCourses = mongoCourseRepository.findByIdInAndPublishDateBetween(courseIds, startDate.atStartOfDay(), LocalDateTime.of(endDate, LocalTime.MAX));
    return getCourses(mongoCourses);
  }

  @Override
  public List<Course> listAll(DepartmentId departmentId)
  {
    List<MongoCourse> mongoCourses = mongoCourseRepository.findByCourseDepartment(departmentId.getId());
    return getCourses(mongoCourses);
  }

  @Override
  public List<Course> listAll(AssessmentId assessmentId)
  {
    List<MongoCourse> mongoCourses = mongoCourseRepository.findByAssessmentId(assessmentId.getId());
    return getCourses(mongoCourses);
  }

  @Override
  public Integer countCreatedCourses(Set<DepartmentId> belongingDepartments, CourseCategoryId courseCategoryId, PublishStatus publishStatus,
      CourseType courseType, LocalDate startDate, LocalDate endDate)
  {
    return mongoCourseRepository.countByCourseDepartmentInAndCategoryIdAndPublishStatusAndCourseTypeAndCreatedDateBetween(belongingDepartments
            .stream().map(DepartmentId::getId).collect(Collectors.toSet()), courseCategoryId.getId(), MongoPublishStatus.valueOf(publishStatus.name()),
        courseType.getType(), startDate.atStartOfDay(),
        LocalDateTime.of(endDate, LocalTime.MAX));
  }

  @Override
  public Integer countCreatedCourses(Set<DepartmentId> belongingDepartments, CourseCategoryId courseCategoryId, PublishStatus publishStatus,
      CourseType courseType, LocalDate startDate, LocalDate endDate, AuthorId authorId)
  {
    return mongoCourseRepository.countByCourseDepartmentInAndCategoryIdAndPublishStatusAndCourseTypeAndCreatedDateBetweenAndAuthorId(belongingDepartments
            .stream().map(DepartmentId::getId).collect(Collectors.toSet()), courseCategoryId.getId(), MongoPublishStatus.valueOf(publishStatus.name()),
        courseType.getType(), startDate.atStartOfDay(),
        LocalDateTime.of(endDate, LocalTime.MAX), authorId.getId());
  }

  @Override
  public Integer countCreatedCourses(Set<DepartmentId> belongingDepartments, CourseCategoryId courseCategoryId, CourseType courseType, LocalDate startDate,
      LocalDate endDate)
  {
    return mongoCourseRepository.countByCourseDepartmentInAndCategoryIdAndCourseTypeAndCreatedDateBetween(belongingDepartments
            .stream().map(DepartmentId::getId).collect(Collectors.toSet()), courseCategoryId.getId(), courseType.getType(), startDate.atStartOfDay(),
        LocalDateTime.of(endDate, LocalTime.MAX));
  }

  @Override
  public Integer countCreatedCourses(Set<DepartmentId> belongingDepartments, CourseCategoryId courseCategoryId, CourseType courseType, LocalDate startDate,
      LocalDate endDate, AuthorId authorId)
  {
    return mongoCourseRepository.countByCourseDepartmentInAndCategoryIdAndCourseTypeAndCreatedDateBetweenAndAuthorId(belongingDepartments
            .stream().map(DepartmentId::getId).collect(Collectors.toSet()), courseCategoryId.getId(), courseType.getType(), startDate.atStartOfDay(),
        LocalDateTime.of(endDate, LocalTime.MAX), authorId.getId());
  }

  @Override
  public Course update(CourseId courseId, CourseCategoryId courseCategoryId, CourseDetail courseDetail, CourseType courseType, String assessmentId,
      String certificateId) throws LmsRepositoryException
  {
    MongoCourse mongoCourse = getMongoCourse(courseId.getId());
    mongoCourse.setTitle(courseDetail.getTitle());
    mongoCourse.setPublishStatus(MongoPublishStatus.valueOf(courseDetail.getPublishStatus().name()));
    mongoCourse.setCourseType(courseType.getType());
    mongoCourse.setModifiedDate(LocalDateTime.now());
    mongoCourse.setPublishDate(courseDetail.getDateInfo().getPublishDate());
    mongoCourse.setHasFeedbackOption(courseDetail.hasFeedbackOption());
    mongoCourse.setHasAssessment(courseDetail.hasAssessment());
    mongoCourse.setHasCertificate(courseDetail.hasCertificate());
    mongoCourse.setHasQuiz(courseDetail.hasQuiz());
    mongoCourse.setProperties(courseDetail.getProperties());
    mongoCourse.setDescription(courseDetail.getDescription());
    mongoCourse.setCategoryId(courseCategoryId.getId());
    mongoCourse.setThumbnailUrl(courseDetail.getThumbnailUrl());
    mongoCourse.setAssessmentId(assessmentId);
    mongoCourse.setCertificateId(certificateId);
    mongoCourse.setCredit(courseDetail.getCredit());

    mongoCourseRepository.save(mongoCourse);

    return ObjectMapper.mapToCourse(mongoCourse, courseTypeResolver);
  }

  @Override
  public Course update(CourseId courseId, CourseDetail courseDetail, CourseType courseType, CourseCategoryId courseCategoryId) throws LmsRepositoryException
  {
    MongoCourse mongoCourse = getMongoCourse(courseId.getId());
    mongoCourse.setTitle(courseDetail.getTitle());
    mongoCourse.setDescription(courseDetail.getDescription());
    mongoCourse.setCourseType(courseType.getType());
    mongoCourse.setCategoryId(courseCategoryId.getId());
    mongoCourse.getProperties().putAll(courseDetail.getProperties());

    mongoCourseRepository.save(mongoCourse);

    return ObjectMapper.mapToCourse(mongoCourse, courseTypeResolver);
  }

  @Override
  public Course update(CourseId courseId, CourseDepartmentRelation courseDepartmentRelation) throws LmsRepositoryException
  {
    MongoCourse mongoCourse = getMongoCourse(courseId.getId());
    mongoCourse.setAssignedDepartments(courseDepartmentRelation.getAssignedDepartments().stream().map(EntityId::getId).collect(Collectors.toSet()));
    mongoCourse.setAssignedLearners(courseDepartmentRelation.getAssignedLearners().stream().map(EntityId::getId).collect(Collectors.toSet()));
    mongoCourse.setCourseDepartment(courseDepartmentRelation.getCourseDepartment().getId());

    mongoCourseRepository.save(mongoCourse);

    return ObjectMapper.mapToCourse(mongoCourse, courseTypeResolver);
  }

  @Override
  public Course updateByDepartment(CourseId courseId, CourseDepartmentRelation courseDepartmentRelation, PublishStatus publishStatus)
      throws LmsRepositoryException
  {
    MongoCourse mongoCourse = getMongoCourse(courseId.getId());
    mongoCourse.setAssignedDepartments(courseDepartmentRelation.getAssignedDepartments().stream().map(EntityId::getId).collect(Collectors.toSet()));
    mongoCourse.setCourseDepartment(courseDepartmentRelation.getCourseDepartment().getId());
    mongoCourse.setAssignedLearners(courseDepartmentRelation.getAssignedLearners().stream().map(EntityId::getId).collect(Collectors.toSet()));
    mongoCourse.setPublishStatus(MongoPublishStatus.valueOf(publishStatus.name()));
    mongoCourseRepository.save(mongoCourse);

    return ObjectMapper.mapToCourse(mongoCourse, courseTypeResolver);
  }

  @Override
  public Course updateByEnrollment(CourseId courseId, CourseDepartmentRelation courseDepartmentRelation, PublishStatus publishStatus)
      throws LmsRepositoryException
  {
    MongoCourse mongoCourse = getMongoCourse(courseId.getId());
    mongoCourse.setAssignedLearners(courseDepartmentRelation.getAssignedLearners().stream().map(EntityId::getId).collect(Collectors.toSet()));
    mongoCourse.setCourseDepartment(courseDepartmentRelation.getCourseDepartment().getId());
    mongoCourseRepository.save(mongoCourse);

    return ObjectMapper.mapToCourse(mongoCourse, courseTypeResolver);
  }

  @Override
  public Course update(CourseId courseId, CourseDepartmentRelation courseDepartmentRelation, CourseDetail courseDetail) throws LmsRepositoryException
  {
    MongoCourse mongoCourse = getMongoCourse(courseId.getId());
    mongoCourse.setProperties(courseDetail.getProperties());
    mongoCourse.setAssignedDepartments(courseDepartmentRelation.getAssignedDepartments().stream().map(EntityId::getId).collect(Collectors.toSet()));
    mongoCourse.setAssignedLearners(courseDepartmentRelation.getAssignedLearners().stream().map(EntityId::getId).collect(Collectors.toSet()));
    mongoCourse.setCourseDepartment(courseDepartmentRelation.getCourseDepartment().getId());

    mongoCourseRepository.save(mongoCourse);

    return ObjectMapper.mapToCourse(mongoCourse, courseTypeResolver);
  }

  @Override
  public Course update(CourseId courseId, CourseContentId courseContentId) throws LmsRepositoryException
  {
    MongoCourse mongoCourse = getMongoCourse(courseId.getId());
    mongoCourse.setCourseContentId(courseContentId.getId());

    mongoCourseRepository.save(mongoCourse);

    return ObjectMapper.mapToCourse(mongoCourse, courseTypeResolver);
  }

  @Override
  public Course update(CourseId courseId, PublishStatus publishStatus) throws LmsRepositoryException
  {
    MongoCourse mongoCourse = getMongoCourse(courseId.getId());
    mongoCourse.setPublishStatus(MongoPublishStatus.valueOf(publishStatus.name()));

    mongoCourseRepository.save(mongoCourse);

    return ObjectMapper.mapToCourse(mongoCourse, courseTypeResolver);
  }

  @Override
  public void update(CourseId courseId, int certifiedNumber) throws LmsRepositoryException
  {
    MongoCourse mongoCourse = getMongoCourse(courseId.getId());
    mongoCourse.setCertifiedNumber(certifiedNumber);
    mongoCourseRepository.save(mongoCourse);
  }

  @Override
  public Course updateCourseProperties(CourseId courseId, Map<String, String> properties) throws LmsRepositoryException
  {
    MongoCourse mongoCourse = getMongoCourse(courseId.getId());
    mongoCourse.getProperties().putAll(properties);

    mongoCourseRepository.save(mongoCourse);

    return ObjectMapper.mapToCourse(mongoCourse, courseTypeResolver);
  }

  @Override
  public boolean existsByCertificate(CertificateId certificateId)
  {
    return mongoCourseRepository.existsByCertificateId(certificateId.getId());
  }

  private List<Course> getCourses(List<MongoCourse> mongoCourses)
  {
    Set<Course> courses = new HashSet<>();

    for (MongoCourse mongoCourse : mongoCourses)
    {
      courses.add(ObjectMapper.mapToCourse(mongoCourse, courseTypeResolver));
    }
    return new ArrayList<>(courses);
  }

  private Course getCourse(String courseId) throws LmsRepositoryException
  {
    MongoCourse mongoCourse = getMongoCourse(courseId);
    return ObjectMapper.mapToCourse(mongoCourse, courseTypeResolver);
  }

  private Course getCourse(String courseId, LocalDate startDate, LocalDate endDate) throws LmsRepositoryException
  {
    MongoCourse mongoCourse = getMongoCourse(courseId, startDate, endDate);
    return ObjectMapper.mapToCourse(mongoCourse, courseTypeResolver);
  }

  private MongoCourse getMongoCourse(String courseId) throws LmsRepositoryException
  {
    Optional<MongoCourse> mongoCourse = mongoCourseRepository.findById(courseId);

    if (!mongoCourse.isPresent())
    {
      throw new LmsRepositoryException("The course with the ID: [" + courseId + "] does not exist!");
    }

    return mongoCourse.get();
  }

  private MongoCourse getMongoCourse(String courseId, LocalDate startDate, LocalDate endDate) throws LmsRepositoryException
  {
    Optional<MongoCourse> mongoCourse = mongoCourseRepository
        .findByIdAndCreatedDateBetween(courseId, startDate.atStartOfDay(), LocalDateTime.of(endDate, LocalTime.MAX));

    if (!mongoCourse.isPresent())
    {
      throw new LmsRepositoryException("The course with the ID: [" + courseId + "] does not exist!");
    }

    return mongoCourse.get();
  }

  @Override
  public Course create(CourseCategoryId courseCategoryId, CourseDetail courseDetail)
  {
    throw new UnsupportedOperationException();
  }

  @Override
  public Course create(CourseCategoryId courseCategoryId, CourseDetail courseDetail, CourseType courseType)
  {
    throw new UnsupportedOperationException();
  }

  @Override
  public List<Course> listAll(CourseCategoryId courseCategoryId, PublishStatus status, Map<String, Object> properties)
  {
    throw new UnsupportedOperationException();
  }

  @Override
  public List<Course> listAll(CourseCategoryId courseCategoryId, PublishStatus status)
  {
    List<MongoCourse> mongoCourses = mongoCourseRepository
        .findByCategoryIdAndPublishStatus(courseCategoryId.getId(), MongoPublishStatus.valueOf(status.name()));
    return getCourses(mongoCourses);
  }

  @Override
  public List<Course> search(String text, boolean searchByName, boolean searchByDescription)
  {
    Validate.notEmpty(text, ERROR_MSG_EMPTY);
    List<MongoCourse> mongoCourseList;

    if (searchByDescription && !searchByName)
    {
      mongoCourseList = mongoCourseRepository.findByDescriptionRegex(text);
    }
    else if (!searchByDescription && searchByName)
    {
      mongoCourseList = mongoCourseRepository.findByTitleRegex(text);
    }
    else
    {
      mongoCourseList = mongoCourseRepository.findByTitleRegexOrDescriptionRegex(text, text);
    }
    return getCourses(mongoCourseList);
  }

  @Override
  public List<Course> search(String text, boolean searchByName, boolean searchByDescription, String departmentId)
  {
    Validate.notEmpty(text, ERROR_MSG_EMPTY);
    Validate.notNull(departmentId);
    List<MongoCourse> mongoCourseList;

    if (searchByDescription && !searchByName)
    {
      List<MongoCourse> collect = new ArrayList<>();
      mongoCourseList = mongoCourseRepository.findByDescriptionRegexAndCourseDepartment(text, departmentId);
      for (MongoCourse mongoCourse : mongoCourseList)
      {
        if (mongoCourse.getAssignedDepartments().contains(departmentId) || mongoCourse.getCourseDepartment().equals(departmentId))
        {
          collect.add(mongoCourse);
        }
      }
      mongoCourseList = collect;
    }
    else if (!searchByDescription && searchByName)
    {
      List<MongoCourse> collect = new ArrayList<>();
      mongoCourseList = mongoCourseRepository.findByTitleRegexAndCourseDepartment(text, departmentId);
      for (MongoCourse mongoCourse : mongoCourseList)
      {
        if (mongoCourse.getAssignedDepartments().contains(departmentId) || mongoCourse.getCourseDepartment().equals(departmentId))
        {
          collect.add(mongoCourse);
        }
      }
      mongoCourseList = collect;
    }
    else
    {
      List<MongoCourse> collect = new ArrayList<>();
      mongoCourseList = mongoCourseRepository.findByTitleRegexOrDescriptionRegexAndCourseDepartment(text, text, departmentId);
      for (MongoCourse mongoCourse : mongoCourseList)
      {
        if (mongoCourse.getAssignedDepartments().contains(departmentId) || mongoCourse.getCourseDepartment().equals(departmentId))
        {
          collect.add(mongoCourse);
        }
      }
      mongoCourseList.addAll(collect);
    }
    return getCourses(mongoCourseList);
  }

  @Override
  public List<Course> listAll(Map<String, Object> properties)
  {
    throw new UnsupportedOperationException();
  }
}
