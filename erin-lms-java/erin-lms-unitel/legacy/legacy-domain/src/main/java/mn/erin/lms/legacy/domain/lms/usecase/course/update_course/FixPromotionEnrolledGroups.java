package mn.erin.lms.legacy.domain.lms.usecase.course.update_course;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import mn.erin.domain.base.model.EntityId;
import mn.erin.domain.base.usecase.UseCaseException;
import mn.erin.lms.base.aim.AccessIdentityManagement;
import mn.erin.lms.legacy.domain.lms.model.audit.CourseAudit;
import mn.erin.lms.legacy.domain.lms.model.course.Course;
import mn.erin.lms.legacy.domain.lms.model.course.CourseDetail;
import mn.erin.lms.legacy.domain.lms.model.course.CourseId;
import mn.erin.lms.legacy.domain.lms.model.course.UserGroup;
import mn.erin.lms.legacy.domain.lms.model.enrollment.CourseEnrollment;
import mn.erin.lms.legacy.domain.lms.repository.CourseAuditRepository;
import mn.erin.lms.legacy.domain.lms.repository.CourseEnrollmentRepository;
import mn.erin.lms.legacy.domain.lms.repository.CourseRepository;
import mn.erin.lms.legacy.domain.lms.repository.LMSRepositoryException;

/**
 * @author Temuulen Naranbold
 */
public class FixPromotionEnrolledGroups extends UpdateCourseUseCase<Map<String, Date>, Map<String, Object>>
{
  private static final String ENROLLED_GROUPS = "enrolledGroups";
  private static final String START_DATE = "startDate";
  private static final String END_DATE = "endDate";
  private static final Map<String, String> NAME_CHANGED_DEPARTMENTS = new HashMap<>();
  private final Set<String> brokenCourseIds = new HashSet<>();
  private final CourseAuditRepository courseAuditRepository;
  private int updatedCourseCount;
  private final CourseEnrollmentRepository courseEnrollmentRepository;
  private final AccessIdentityManagement accessIdentityManagement;

  public FixPromotionEnrolledGroups(CourseRepository courseRepository, CourseEnrollmentRepository courseEnrollmentRepository,
      AccessIdentityManagement accessIdentityManagement, CourseAuditRepository courseAuditRepository)
  {
    super(courseRepository);
    this.courseEnrollmentRepository = courseEnrollmentRepository;
    this.accessIdentityManagement = accessIdentityManagement;
    this.courseAuditRepository = courseAuditRepository;
  }

  @Override
  public Map<String, Object> execute(Map<String, Date> dateFilter) throws UseCaseException
  {
    setNameChangedDepartments();
    updatedCourseCount = 0;
    List<Course> enrolledGroupsWithName = new ArrayList<>();
    List<Course> userGroupsWithName = new ArrayList<>();
    List<Course> userGroupsWithId = new ArrayList<>();
    List<Course> emptyUserGroups = new ArrayList<>();

    //Getting the all required data
    List<Course> filteredCourses = courseRepository.getCourseList(dateFilter.get(START_DATE), dateFilter.get(END_DATE));
    List<CourseEnrollment> filteredEnrollments = courseEnrollmentRepository.getEnrollmentList(dateFilter.get(START_DATE), dateFilter.get(END_DATE));
    List<CourseAudit> promoAudit = courseAuditRepository.listAll();
    Map<String, Set<String>> enrollmentMap = getPromoEnrollmentMap(filteredEnrollments);
    Map<String, Set<String>> promoAuditMap = getCourseAuditMap(promoAudit);
    Map<String, String> departmentNamesAndId = accessIdentityManagement.getDepartmentNamesAndId();

    for (Course course : filteredCourses)
    {
      List<String> enrolledGroups = (List<String>) course.getCourseDetail().getProperties().get(ENROLLED_GROUPS);
      //Checks the course has enrolledGroups and not empty
      if (course.getCourseDetail().getProperties().get(ENROLLED_GROUPS) != null && !enrolledGroups.isEmpty())
      {
        //Checks the enrolledGroups contains group name. If it, replace the name with its id and add all enrolled learners
        if (departmentNamesAndId.keySet().stream().anyMatch(enrolledGroups::contains))
        {
          CourseDetail updatingDetail = mapToNewCourseDetail(course.getCourseDetail());
          Set<String> departmentIds = getIdsByNames(departmentNamesAndId, new HashSet<>(enrolledGroups));
          updatingDetail.addProperty(ENROLLED_GROUPS, (Serializable) departmentIds);
          updateUserGroupsAndAddLearners(course.getUserGroup(), course.getCourseId(), departmentIds, enrollmentMap, promoAuditMap);
          enrolledGroupsWithName.add(course);
          updateCourseDetail(course.getCourseId(), updatingDetail);
        }
      }
      else
      {
        Set<String> userGroups = course.getUserGroup().getGroups();
        Set<String> departmentIds = new HashSet<>();
        if (!userGroups.isEmpty())
        {
          //Checks the userGroup contains group name. If it, replace the name with its id and add the subGroups
          if (departmentNamesAndId.keySet().stream().anyMatch(userGroups::contains))
          {
            departmentIds.addAll(getIdsByNames(departmentNamesAndId, userGroups));
            departmentIds.addAll(getSubDepartmentIds(departmentIds));
            course.getCourseDetail().addProperty(ENROLLED_GROUPS, (Serializable) departmentIds);
            userGroupsWithName.add(course);
          }
          //Checks the userGroup contains group ID. If it, add all IDs to enrolledGroup property
          else if (departmentNamesAndId.values().stream().anyMatch(userGroups::contains))
          {
            departmentIds.addAll(userGroups);
            course.getCourseDetail().addProperty(ENROLLED_GROUPS, (Serializable) userGroups);
            userGroupsWithId.add(course);
          }
        }
        //If the course enrolledGroup is null and userGroup is empty add empty enrolledGroups property
        else
        {
          course.getCourseDetail().addProperty(ENROLLED_GROUPS, (Serializable) Collections.emptyList());
          emptyUserGroups.add(course);
        }
        updateUserGroupsAndAddLearners(course.getUserGroup(), course.getCourseId(), departmentIds, enrollmentMap, promoAuditMap);
        updateCourseDetail(course.getCourseId(), course.getCourseDetail());
      }
    }

    brokenCourseIds.addAll(enrolledGroupsWithName.stream().map(Course::getCourseId).map(EntityId::getId).collect(Collectors.toSet()));
    brokenCourseIds.addAll(userGroupsWithName.stream().map(Course::getCourseId).map(EntityId::getId).collect(Collectors.toSet()));
    brokenCourseIds.addAll(userGroupsWithId.stream().map(Course::getCourseId).map(EntityId::getId).collect(Collectors.toSet()));
    brokenCourseIds.addAll(emptyUserGroups.stream().map(Course::getCourseId).map(EntityId::getId).collect(Collectors.toSet()));

    Map<String, Object> result = new HashMap<>();
    result.put("Enrolled groups with name", enrolledGroupsWithName);
    result.put("Not have enrolled groups and user groups with name", userGroupsWithName);
    result.put("Not have enrolled groups and user groups with id", userGroupsWithId);
    result.put("Not have enrolled groups and user groups is empty ", emptyUserGroups);
    result.put("Total broken courses IDs", brokenCourseIds);
    result.put("Total fixed courses count", updatedCourseCount);

    return result;
  }

  private Map<String, Set<String>> getCourseAuditMap(List<CourseAudit> promoAudit)
  {
    Map<String, Set<String>> auditMap = new HashMap<>();
    for (CourseAudit audit : promoAudit)
    {

      if (!auditMap.containsKey(audit.getCourseId().getId()))
      {
        auditMap.put(audit.getCourseId().getId(), new HashSet<>());
      }
      else
      {
        Set<String> learners = auditMap.get(audit.getCourseId().getId());
        learners.add(audit.getLearnerId().getId());
        auditMap.put(audit.getCourseId().getId(), learners);
      }
    }
    return auditMap;
  }

  private Map<String, Set<String>> getPromoEnrollmentMap(List<CourseEnrollment> filteredEnrollments)
  {
    Map<String, Set<String>> enrollmentMap = new HashMap<>();
    for (CourseEnrollment enrollment : filteredEnrollments)
    {

      if (!enrollmentMap.containsKey(enrollment.getCourseId().getId()))
      {
        enrollmentMap.put(enrollment.getCourseId().getId(), new HashSet<>());
      }
      else
      {
        Set<String> learners = enrollmentMap.get(enrollment.getCourseId().getId());
        learners.add(enrollment.getLearnerId().getId());
        enrollmentMap.put(enrollment.getCourseId().getId(), learners);
      }
    }
    return enrollmentMap;
  }

  private Set<String> getIdsByNames(Map<String, String> departmentNamesAndIds, Set<String> enrolledGroupsName)
  {
    Set<String> departmentIds = new HashSet<>();
    for (String groupName : enrolledGroupsName)
    {
      Optional<String> departmentId = Optional.ofNullable(departmentNamesAndIds.get(groupName));
      if (departmentId.isPresent())
      {
        departmentIds.add(departmentId.get());
      }
      else
      {
        Optional<String> changedName = Optional.ofNullable(NAME_CHANGED_DEPARTMENTS.get(groupName));
        if (changedName.isPresent())
        {
          Optional<String> changedDepartmentId = Optional.ofNullable(departmentNamesAndIds.get(changedName.get()));
          if (changedDepartmentId.isPresent())
          {
            departmentIds.add(changedDepartmentId.get());
            LOGGER.info("Updated course enrolled group [{}] to [{}]", groupName, changedName);
          }
        }
        else
        {
          LOGGER.info("{} group name not found", groupName);
        }
      }
    }
    return departmentIds;
  }

  private CourseDetail mapToNewCourseDetail(CourseDetail oldDetail)
  {
    CourseDetail updatingDetail = new CourseDetail(oldDetail.getTitle());
    updatingDetail.setCreatedDate(oldDetail.getCreatedDate());
    updatingDetail.setModifiedDate(oldDetail.getModifiedDate());
    updatingDetail.setDescription(oldDetail.getDescription());
    updatingDetail.setNotes(oldDetail.getNotes());
    updatingDetail.changePublishStatus(oldDetail.getPublishStatus());
    for (Map.Entry<String, Object> entry : oldDetail.getProperties().entrySet())
    {
      if (!entry.getKey().equals(ENROLLED_GROUPS))
      {
        updatingDetail.addProperty(entry.getKey(), (Serializable) entry.getValue());
      }
    }
    return updatingDetail;
  }

  private void updateCourseDetail(CourseId courseId, CourseDetail courseDetail)
  {
    try
    {
      courseRepository.update(courseId, courseDetail);
      updatedCourseCount++;
    }
    catch (LMSRepositoryException e)
    {
      LOGGER.error("Error during adding enrolledGroups property with Course ID: [{}]", courseId.getId());
    }
  }

  private Set<String> getSubDepartmentIds(Set<String> ids)
  {
    Set<String> subDepartments = new HashSet<>();
    for (String id : ids)
    {
      subDepartments.addAll(accessIdentityManagement.getSubDepartments(id));
    }
    return subDepartments;
  }

  private void updateUserGroupsAndAddLearners(UserGroup userGroup, CourseId courseId, Set<String> departmentIds, Map<String, Set<String>> enrollmentMap,
      Map<String, Set<String>> auditMap)
  {
    Set<String> learners = enrollmentMap.get(courseId.getId());
    if (learners == null)
    {
      learners = new HashSet<>();
    }

    Set<String> courseAudit = auditMap.get(courseId.getId());

    if (courseAudit != null)
    {
      learners.addAll(courseAudit);
    }

    if (userGroup.getUsers() != null)
    {
      learners.addAll(userGroup.getUsers());
    }

    userGroup.setUsers(learners);
    userGroup.setGroups(departmentIds);

    try
    {
      courseRepository.update(courseId, userGroup);
    }
    catch (LMSRepositoryException e)
    {
      LOGGER.error(String.format("Error during updating the course userGroup with ID: %s", courseId.getId()));
    }
  }

  private static void setNameChangedDepartments()
  {
    NAME_CHANGED_DEPARTMENTS.put("Завхан", "Завхан Улиастай");
    NAME_CHANGED_DEPARTMENTS.put("Шарын гол Партнер", "Дархан Шарын гол");
    NAME_CHANGED_DEPARTMENTS.put("Ривер гарден", "Ривер гарден ГС");
    NAME_CHANGED_DEPARTMENTS.put("ОН СТГ", "ОН Мобайлын хэлтэс");
    NAME_CHANGED_DEPARTMENTS.put("Төв", "Төв Зуунмод");
    NAME_CHANGED_DEPARTMENTS.put("Хөвсгөл", "Хөвсгөл Мөрөн, Хөвсгөл салбар ");
    NAME_CHANGED_DEPARTMENTS.put("Rural-Mobile", "Rural-Mobile -Spe");
    NAME_CHANGED_DEPARTMENTS.put("Соломолл ГС", "Соло молл ГС");
    NAME_CHANGED_DEPARTMENTS.put("BDP Mobile", "Mobile");
    NAME_CHANGED_DEPARTMENTS.put("Номин ГС.", "Номин ГС");
    NAME_CHANGED_DEPARTMENTS.put("Баялаг ундраа", "Баялаг ундраа ГС");
    NAME_CHANGED_DEPARTMENTS.put("U-Point", "U_Point");
    NAME_CHANGED_DEPARTMENTS.put("Хэнтий", "Хэнтий Бор-Өндөр, Хэнтий Чингис");
    NAME_CHANGED_DEPARTMENTS.put("13-р хороолол", "13-р хороолол ГС");
    NAME_CHANGED_DEPARTMENTS.put("Даланзадгад партнер", "Өмнөговь Даланзадгад");
    NAME_CHANGED_DEPARTMENTS.put("Зуун айл", "Зуун айл ГС");
    NAME_CHANGED_DEPARTMENTS.put("BDP Univision", "Univision");
    NAME_CHANGED_DEPARTMENTS.put("Налайх ГС", "Улаанбаатар-Налайх ГС");
    NAME_CHANGED_DEPARTMENTS.put("Эрдэнэт", "Эрдэнэт ҮС");
    NAME_CHANGED_DEPARTMENTS.put("ББХТХ", "ББГ");
    NAME_CHANGED_DEPARTMENTS.put("Чойр партнер", "Чойр");
    NAME_CHANGED_DEPARTMENTS.put("Ховд партнер", "Ховд");
    NAME_CHANGED_DEPARTMENTS.put("ОН Партнер", "ОН IPTV хэлтэс");
    NAME_CHANGED_DEPARTMENTS.put("7 буудал ГС", "Долоон буудал ГС");
    NAME_CHANGED_DEPARTMENTS.put("ОН Захиалгаө", "ОН Захиалга.");
    NAME_CHANGED_DEPARTMENTS.put("Өвөрхангай Арвайхээр Па", "Арвайхээр");
    NAME_CHANGED_DEPARTMENTS.put("ДСХХХ IPTV", "ДСХХХ");
    NAME_CHANGED_DEPARTMENTS.put("Цэнтрал", "Сэнтрал");
    NAME_CHANGED_DEPARTMENTS.put("Цогтцэций партнер", "Өмнөговь Цогтцэций");
    NAME_CHANGED_DEPARTMENTS.put("O бүс", "W бүс");
    NAME_CHANGED_DEPARTMENTS.put("Т.Пүрэвсүрэн", "Ц.Пүрэвсүрэн");
    NAME_CHANGED_DEPARTMENTS.put("Драгон", "Драгон ГС");
    NAME_CHANGED_DEPARTMENTS.put("Ханбогд партнер", "Ханбогд");
    NAME_CHANGED_DEPARTMENTS.put("Увс партнер", "Увс");
    NAME_CHANGED_DEPARTMENTS.put("ОН Салбар", "ОН Салбарын хэлтэс");
    NAME_CHANGED_DEPARTMENTS.put("Дорнод Партнер", "Дорнод");
    NAME_CHANGED_DEPARTMENTS.put("Хэнтий-Өндөрхаан", "Хэнтий Чингис");
    NAME_CHANGED_DEPARTMENTS.put("ДСХХХ Mobile", "ДСХХХ");
  }
}
