/*
 * (C)opyright, 2019, ERIN SYSTEMS LLC
 * The source code is protected by copyright laws and international copyright
 * treaties, as well as other intellectual property laws and treaties.
 * All rights reserved.
 */

package mn.erin.lms.legacy.infrastructure.lms.rest.course;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Optional;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ScheduledFuture;
import java.util.stream.Collectors;
import javax.inject.Inject;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import mn.erin.common.datetime.DateTimeUtils;
import mn.erin.common.mail.EmailService;
import mn.erin.common.mail.EmailUtil;
import mn.erin.common.sms.SmsMessageFactory;
import mn.erin.common.sms.SmsSender;
import mn.erin.domain.aim.repository.GroupRepository;
import mn.erin.domain.aim.repository.MembershipRepository;
import mn.erin.domain.base.model.person.ContactInfo;
import mn.erin.domain.base.usecase.UseCaseException;
import mn.erin.domain.dms.repository.DMSRepositoryException;
import mn.erin.domain.dms.usecase.folder.copy_folder.CopyFolderInput;
import mn.erin.domain.dms.usecase.folder.delete_folder.DeleteFolderInput;
import mn.erin.domain.dms.usecase.folder.get_folder.GetFolderInput;
import mn.erin.domain.dms.usecase.folder.get_folder.GetFolderOuput;
import mn.erin.domain.dms.usecase.folder.update_folder.UpdateFolderInput;
import mn.erin.infrastucture.rest.common.response.RestEntity;
import mn.erin.infrastucture.rest.common.response.RestResponse;
import mn.erin.infrastucture.rest.common.response.RestResult;
import mn.erin.lms.base.aim.AccessIdentityManagement;
import mn.erin.lms.base.aim.LmsDepartmentService;
import mn.erin.lms.base.domain.service.LmsFileSystemService;
import mn.erin.lms.legacy.domain.lms.model.course.Course;
import mn.erin.lms.legacy.domain.lms.model.course.CourseCategory;
import mn.erin.lms.legacy.domain.lms.model.course.CourseCategoryId;
import mn.erin.lms.legacy.domain.lms.model.course.CourseId;
import mn.erin.lms.legacy.domain.lms.model.course.PublishStatus;
import mn.erin.lms.legacy.domain.lms.repository.CourseAssessmentRepository;
import mn.erin.lms.legacy.domain.lms.repository.CourseAuditRepository;
import mn.erin.lms.legacy.domain.lms.repository.CourseCategoryRepository;
import mn.erin.lms.legacy.domain.lms.repository.CourseContentRepository;
import mn.erin.lms.legacy.domain.lms.repository.CourseEnrollmentRepository;
import mn.erin.lms.legacy.domain.lms.repository.CourseGroupRepository;
import mn.erin.lms.legacy.domain.lms.repository.CourseQuestionnaireRepository;
import mn.erin.lms.legacy.domain.lms.repository.CourseRepository;
import mn.erin.lms.legacy.domain.lms.repository.CourseTestRepository;
import mn.erin.lms.legacy.domain.lms.repository.LMSRepositoryException;
import mn.erin.lms.legacy.domain.lms.service.CourseService;
import mn.erin.lms.legacy.domain.lms.service.EnrollmentStateService;
import mn.erin.lms.legacy.domain.lms.usecase.course.clone_course.CloneCourse;
import mn.erin.lms.legacy.domain.lms.usecase.course.clone_course.CloneCourseInput;
import mn.erin.lms.legacy.domain.lms.usecase.course.create_course.CreateCourse;
import mn.erin.lms.legacy.domain.lms.usecase.course.create_course.CreateCourseInput;
import mn.erin.lms.legacy.domain.lms.usecase.course.create_course.CreateCourseOutput;
import mn.erin.lms.legacy.domain.lms.usecase.course.delete_course.DeleteCourse;
import mn.erin.lms.legacy.domain.lms.usecase.course.delete_course.DeleteCourseInput;
import mn.erin.lms.legacy.domain.lms.usecase.course.delete_course.DeleteCourseOutput;
import mn.erin.lms.legacy.domain.lms.usecase.course.get_course.GetCourse;
import mn.erin.lms.legacy.domain.lms.usecase.course.get_course.GetCourseInput;
import mn.erin.lms.legacy.domain.lms.usecase.course.get_course.GetCourseOutput;
import mn.erin.lms.legacy.domain.lms.usecase.course.get_course_enrollment_tree.GetSelectedDepartmentTree;
import mn.erin.lms.legacy.domain.lms.usecase.course.get_enrolled_course_list.GetEnrolledCourseList;
import mn.erin.lms.legacy.domain.lms.usecase.course.get_enrolled_course_list.GetEnrolledListInput;
import mn.erin.lms.legacy.domain.lms.usecase.course.publish_course.PublishCourse;
import mn.erin.lms.legacy.domain.lms.usecase.course.publish_course.PublishCourseInput;
import mn.erin.lms.legacy.domain.lms.usecase.course.publish_course.PublishCourseOutput;
import mn.erin.lms.legacy.domain.lms.usecase.course.search_course_list.SearchCourseList;
import mn.erin.lms.legacy.domain.lms.usecase.course.search_course_list.SearchCourseListInput;
import mn.erin.lms.legacy.domain.lms.usecase.course.unpublish_course.HideCourse;
import mn.erin.lms.legacy.domain.lms.usecase.course.unpublish_course.HideCourseOutput;
import mn.erin.lms.legacy.domain.lms.usecase.course.update_course.FixPromotionEnrolledGroups;
import mn.erin.lms.legacy.domain.lms.usecase.course.update_course.UpdateCourseDetail;
import mn.erin.lms.legacy.domain.lms.usecase.course.update_course.UpdateCourseDetailInput;
import mn.erin.lms.legacy.domain.lms.usecase.course.update_course.UpdateCourseFully;
import mn.erin.lms.legacy.domain.lms.usecase.course.update_course.UpdateCourseInput;
import mn.erin.lms.legacy.domain.lms.usecase.course.update_course.UpdateCourseProperties;
import mn.erin.lms.legacy.domain.lms.usecase.course.update_course.UpdateCoursePropertiesInput;
import mn.erin.lms.legacy.domain.lms.usecase.course.update_course.UpdateUserGroup;
import mn.erin.lms.legacy.domain.lms.usecase.course.update_course.UpdateUserGroupInput;
import mn.erin.lms.legacy.domain.lms.usecase.course.update_course.UserGroupsInput;
import mn.erin.lms.legacy.domain.lms.usecase.course_assessment.get_assessment.GetAssessment;
import mn.erin.lms.legacy.domain.lms.usecase.course_assessment.get_assessment.GetAssessmentInput;
import mn.erin.lms.legacy.domain.lms.usecase.course_assessment.get_assessment.GetAssessmentOutput;
import mn.erin.lms.legacy.domain.lms.usecase.course_content.ContentModuleInfo;
import mn.erin.lms.legacy.domain.lms.usecase.course_content.get_course_content.GetCourseContent;
import mn.erin.lms.legacy.domain.lms.usecase.course_content.get_course_content.GetCourseContentInput;
import mn.erin.lms.legacy.domain.lms.usecase.course_content.get_course_content.GetCourseContentOutput;
import mn.erin.lms.legacy.domain.lms.usecase.course_group.CreateCourseGroup;
import mn.erin.lms.legacy.domain.lms.usecase.course_group.GetGroupCourses;
import mn.erin.lms.legacy.domain.lms.usecase.course_questionnaire.get_questionnaire.GetQuestionnaire;
import mn.erin.lms.legacy.domain.lms.usecase.course_questionnaire.get_questionnaire.GetQuestionnaireInput;
import mn.erin.lms.legacy.domain.lms.usecase.course_questionnaire.get_questionnaire.GetQuestionnaireOutput;
import mn.erin.lms.legacy.domain.lms.usecase.course_test.QuestionInfo;
import mn.erin.lms.legacy.domain.lms.usecase.course_test.get_course_test.GetCourseTest;
import mn.erin.lms.legacy.domain.lms.usecase.course_test.get_course_test.GetCourseTestInput;
import mn.erin.lms.legacy.domain.lms.usecase.course_test.get_course_test.GetCourseTestOutput;
import mn.erin.lms.legacy.domain.lms.usecase.update_course_enrollment.UpdateCourseEnrollment;
import mn.erin.lms.legacy.domain.lms.usecase.update_course_enrollment.UpdateCourseEnrollmentOutput;
import mn.erin.lms.legacy.domain.lms.usecase.update_course_enrollment.dto.UpdateCourseEnrollmentInput;
import mn.erin.lms.legacy.domain.scorm.usecase.runtime.ClearLearnerRuntimeData;
import mn.erin.lms.legacy.domain.scorm.usecase.runtime.RuntimeDataInput;
import mn.erin.lms.legacy.domain.scorm.usecase.scorm.CreateSCORMContent;
import mn.erin.lms.legacy.domain.scorm.usecase.scorm.CreateSCORMContentInput;
import mn.erin.lms.legacy.domain.scorm.usecase.scorm.DeleteSCORMContent;
import mn.erin.lms.legacy.domain.scorm.usecase.scorm.OrganizationInfo;
import mn.erin.lms.legacy.domain.scorm.usecase.scorm.ResourceInfo;
import mn.erin.lms.legacy.domain.scorm.usecase.scorm.SCORMContentOutput;
import mn.erin.lms.legacy.domain.scorm.usecase.scorm_questionnaire.CreateSCORMQuestionnaire;
import mn.erin.lms.legacy.domain.scorm.usecase.scorm_questionnaire.CreateSCORMQuestionnaireInput;
import mn.erin.lms.legacy.domain.scorm.usecase.scorm_test.AnswerInfo;
import mn.erin.lms.legacy.domain.scorm.usecase.scorm_test.CreateSCORMTest;
import mn.erin.lms.legacy.domain.scorm.usecase.scorm_test.CreateSCORMTestInput;
import mn.erin.lms.legacy.domain.scorm.usecase.scorm_test.SCORMQuestionInfo;
import mn.erin.lms.legacy.infrastructure.lms.repository.dms.CoursePathResolver;
import mn.erin.lms.legacy.infrastructure.lms.rest.BaseCourseRestApi;
import mn.erin.lms.legacy.infrastructure.scorm.base.service.SCORMPackagingService;
import mn.erin.lms.legacy.infrastructure.scorm.rest.SCORMContentRestApi;

/**
 * @author Bat-Erdene Tsogoo.
 */
@Api("Course")
@RequestMapping(value = "/legacy/courses", name = "Provides 'ERIN' LMS course features")
public class CourseRestApi extends BaseCourseRestApi
{
  private static final Logger LOGGER = LoggerFactory.getLogger(CourseRestApi.class);

  private static final String ERR_MSG_COURSE_NOT_FOUND = "The course with the ID: [%s] does not exist!";
  private static final String ERR_MSG_BY_SEARCH = "Failed to search the course with the searchkey: [%s]";
  private static final String ERR_MSG_UPDATE = "Failed to update the course with the ID: [%s]";
  private static final String ERR_MSG_DELETE = "Failed to delete the course with the ID: [%s]";
  private static final String ERR_MSG_COURSE_ID_BLANK = "Course id cannot be blank!";
  private static final String ERR_MSG_COURSE_REQUEST_BODY = "Course request body cannot be null!";

  private static final String QUERY_PARAMETER_CATEGORY_ID = "courseCategoryId";
  private static final String QUERY_PARAMETER_PUBLISH_STATUS = "publishStatus";
  private static final String QUERY_PARAMETER_ENROLLMENT_STATE = "enrollmentState";
  private static final String QUERY_PARAMETER_PARENT_CATEGORY_ID = "parentCourseCategoryID";
  private static final String SCORM_CONTENT_FOLDER_NAME = "SCORM";

  private static final String HAS_TEST = "hasTest";
  private static final String HAS_FEED_BACK = "hasFeedBack";

  private static final String DOMAIN_NAME = "domainName";
  public static final String COURSE_ID = "courseId";
  private String domainName = "noDomain";

  private final CourseCategoryRepository courseCategoryRepository;
  private final CourseEnrollmentRepository courseEnrollmentRepository;
  private final CourseAssessmentRepository courseAssessmentRepository;
  private final CourseQuestionnaireRepository courseQuestionnaireRepository;
  private final CourseTestRepository courseTestRepository;
  private final CourseGroupRepository courseGroupRepository;

  private CoursePathResolver coursePathResolver;

  private SCORMPackagingService scormPackagingService;

  @Inject
  private GroupRepository groupRepository;

  @Inject
  private MembershipRepository membershipRepository;

  @Inject
  private CourseAuditRepository courseAuditRepository;

  @Inject
  private EnrollmentStateService enrollmentStateService;

  @Inject
  private CreateSCORMContent createSCORMContent;

  @Inject
  private DeleteSCORMContent deleteSCORMContent;

  @Inject
  private ClearLearnerRuntimeData clearLearnerRuntimeData;

  @Inject
  private CreateSCORMTest createSCORMTest;

  @Inject
  private CreateSCORMQuestionnaire createSCORMQuestionnaire;

  @Inject
  private SCORMContentRestApi scormContentRestApi;

  @Inject
  private AccessIdentityManagement accessIdentityManagement;

  @Inject
  private EmailService legacyEmailService;

  @Inject
  private LmsFileSystemService lmsFileSystemService;

  @Inject
  private SmsSender legacySmsSender;

  @Inject
  private SmsMessageFactory legacySmsMessageFactory;

  @Inject
  private LmsDepartmentService lmsDepartmentService;

  private final ThreadPoolTaskScheduler taskScheduler = new ThreadPoolTaskScheduler();
  private long startTime;
  private long endTime;

  public CourseRestApi(CourseRepository courseRepository, CourseCategoryRepository courseCategoryRepository,
      CourseContentRepository courseContentRepository, CourseEnrollmentRepository courseEnrollmentRepository,
      CourseAssessmentRepository courseAssessmentRepository, CourseTestRepository courseTestRepository,
      CourseQuestionnaireRepository courseQuestionnaireRepository, CourseService courseService,
      CourseGroupRepository courseGroupRepository)
  {
    super(courseRepository, courseContentRepository, courseService);
    this.courseCategoryRepository = Objects.requireNonNull(courseCategoryRepository, "CourseCategoryRepository cannot be null!");
    this.courseEnrollmentRepository = Objects.requireNonNull(courseEnrollmentRepository, "CourseEnrollmentRepository cannot be null!");
    this.courseAssessmentRepository = Objects.requireNonNull(courseAssessmentRepository, "CourseAssessmentRepository cannot be null!");
    this.courseTestRepository = Objects.requireNonNull(courseTestRepository, "CourseTestRepository cannot be null!");
    this.courseQuestionnaireRepository = Objects.requireNonNull(courseQuestionnaireRepository, "CourseQuestionnaireRepository cannot be null!");
    this.courseGroupRepository = Objects.requireNonNull(courseGroupRepository, "CourseGroupRepository cannot be null!");
    this.setDomainName();
  }

  @Inject
  public void setCoursePathResolver(CoursePathResolver coursePathResolver)
  {
    this.coursePathResolver = coursePathResolver;
  }

  @Inject
  public void setScormPackagingService(SCORMPackagingService scormPackagingService)
  {
    this.scormPackagingService = scormPackagingService;
  }

  @ApiOperation("Create a new course")
  @PostMapping
  public ResponseEntity<RestResult> create(@RequestBody RestCourse request)
  {
    RestCourseDetail courseDetail = request.getCourseDetail();

    CreateCourseInput input = new CreateCourseInput(courseDetail.getTitle(), request.getCategoryId(), courseDetail.getCourseProperties());
    input.setDescription(request.getCourseDetail().getDescription());

    CreateCourseGroup createCourseGroup = new CreateCourseGroup(accessIdentityManagement, courseGroupRepository);

    CreateCourse createCourse = new CreateCourse(courseRepository, courseCategoryRepository, createCourseGroup);
    try
    {
      CreateCourseOutput output = createCourse.execute(input);
      createCourseFolder(output.getId(), courseDetail.getTitle());
      return RestResponse.success(output);
    }
    catch (UseCaseException e)
    {
      LOGGER.error(e.getMessage(), e);
      return RestResponse.internalError("Failed to create a course [" + courseDetail.getTitle() + "]");
    }
    catch (NoSuchElementException e)
    {
      return RestResponse.notFound("The course category with the ID: [" + request.getCategoryId() + "] does not exist!");
    }
  }

  @ApiOperation(value = "Publishes a course and creates a SCORM content")
  @PostMapping("/{courseId}/publish")
  public ResponseEntity<RestResult> publish(@PathVariable String courseId, @RequestBody PublishRequestModel request)
  {
    return publishCourseAndCreateSCORMContent(courseId, request);
  }

  @ApiOperation(value = "Clones a course")
  @PostMapping("/{courseId}/clone")
  public ResponseEntity<RestResult> clone(@PathVariable String courseId, @RequestBody RestCourse request)
  {
    RestCourseDetail courseDetail = request.getCourseDetail();

    CreateCourseInput createCourseInput = new CreateCourseInput(courseDetail.getTitle(), request.getCategoryId(), courseDetail.getCourseProperties());
    createCourseInput.setDescription(request.getCourseDetail().getDescription());

    if (courseDetail.getUserGroup() != null)
    {
      createCourseInput.setUsers(courseDetail.getUserGroup().getUsers());
      createCourseInput.setGroups(courseDetail.getUserGroup().getGroups());
    }

    CloneCourse cloneCourse = new CloneCourse(courseRepository, courseCategoryRepository, courseAssessmentRepository,
        courseContentRepository, new CreateCourseGroup(accessIdentityManagement, courseGroupRepository));
    CloneCourseInput input = new CloneCourseInput(courseId, createCourseInput);

    try
    {
      String existingCourseFolderId = getCourseFolderId(courseId);
      String contentFolderId = getContentFolderId(existingCourseFolderId);
      GetCourseOutput courseOutput = cloneCourse.execute(input);
      String folderId = createCourseFolder(courseOutput.getId(), courseOutput.getTitle());

      CopyFolderInput copyFolderInput = new CopyFolderInput(folderId, contentFolderId);
      copyFolder.execute(copyFolderInput);

      return RestResponse.success(courseOutput);
    }
    catch (UseCaseException e)
    {
      LOGGER.error(e.getMessage(), e);
      return RestResponse.internalError(e.getMessage());
    }
  }

  @ApiOperation(value = "Get courses by category with optional publish status value and query parameters",
      notes = "The query parameter 'courseCategoryId' is required to make this call")
  @GetMapping
  public ResponseEntity<RestResult> read(@RequestParam Map<String, String> filter)
  {
    GetEnrolledListInput input = new GetEnrolledListInput(filter.get(QUERY_PARAMETER_CATEGORY_ID));
    filter.remove(QUERY_PARAMETER_CATEGORY_ID);

    Optional<String> parentCategoryId = Optional.ofNullable(filter.get(QUERY_PARAMETER_PARENT_CATEGORY_ID));
    filter.remove(QUERY_PARAMETER_PARENT_CATEGORY_ID);
    parentCategoryId.ifPresent(input::setParentCategoryId);

    Optional<String> publishStatusValue = Optional.ofNullable(filter.get(QUERY_PARAMETER_PUBLISH_STATUS));
    filter.remove(QUERY_PARAMETER_PUBLISH_STATUS);
    publishStatusValue.ifPresent(input::setPublishStatus);

    Optional<String> enrollmentStatusValue = Optional.ofNullable(filter.get(QUERY_PARAMETER_ENROLLMENT_STATE));
    filter.remove(QUERY_PARAMETER_ENROLLMENT_STATE);
    enrollmentStatusValue.ifPresent(input::setEnrollmentState);

    Map<String, Object> courseProperties = filter.entrySet().stream()
        .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

    input.setProperties(courseProperties);

    GetEnrolledCourseList getEnrolledCourseList = new GetEnrolledCourseList(courseRepository, courseEnrollmentRepository, accessIdentityManagement,
        courseCategoryRepository, enrollmentStateService, courseGroupRepository, groupRepository, membershipRepository, courseAuditRepository,
        courseContentRepository, courseAssessmentRepository);
    List<GetCourseOutput> result;
    try
    {
      result = getEnrolledCourseList.execute(input);
    }
    catch (UseCaseException e)
    {
      LOGGER.error(e.getMessage(), e);
      return RestResponse.internalError("Failed to get course cause: " + e.getMessage());
    }

    return RestResponse.success(result);
  }

  @ApiOperation(value = "search text")
  @GetMapping("/search")
  public ResponseEntity searchingCourses(@RequestParam String query,
      @RequestParam(required = false) boolean name,
      @RequestParam(required = false) boolean description)
  {
    SearchCourseListInput input = new SearchCourseListInput(query);
    input.searchByName(name);
    input.searchByDescription(description);

    if (StringUtils.isBlank(query))
    {
      return RestResponse.badRequest("The search value is null or blank");
    }
    GetGroupCourses getGroupCourses = new GetGroupCourses(accessIdentityManagement, courseGroupRepository, groupRepository, membershipRepository);
    SearchCourseList searchCourseList =
        new SearchCourseList(enrollmentStateService, courseService, courseEnrollmentRepository, accessIdentityManagement, courseAuditRepository,
            getGroupCourses);
    try
    {
      List<GetCourseOutput> output = searchCourseList.execute(input);

      for (GetCourseOutput course : output)
      {
        String categoryName;
        try
        {
          CourseCategory courseCategory =
              courseCategoryRepository.getCourseCategory(new CourseCategoryId(course.getCourseCategory()));
          categoryName = courseCategory.getName();
        }
        catch (LMSRepositoryException e)
        {
          categoryName = "";
        }
        course.setCourseCategory(categoryName);
      }

      return RestResponse.success(output);
    }
    catch (UseCaseException e)
    {
      LOGGER.error(e.getMessage(), e);
      return RestResponse.notFound(String.format(ERR_MSG_BY_SEARCH, query));
    }
  }

  @ApiOperation("Get a course by ID")
  @GetMapping("/{courseId}")
  public ResponseEntity<RestResult> readById(@PathVariable String courseId)
  {
    try
    {
      GetCourseOutput output = getCourse(courseId);
      return RestResponse.success(output);
    }
    catch (UseCaseException e)
    {
      LOGGER.error(e.getMessage(), e);
      return RestResponse.notFound(String.format(ERR_MSG_COURSE_NOT_FOUND, courseId));
    }
  }

  @ApiOperation(value = "Updates course whole info",
      notes = "This API updates/replaces the whole course info including category, title, description and properties.")
  @PutMapping("/fully/{courseId}")
  public ResponseEntity<RestResult> updateCourse(@PathVariable String courseId, @RequestBody RestCourse restCourse)
  {
    String previousCourseTitle;

    if (StringUtils.isBlank(courseId))
    {
      return RestResponse.badRequest(ERR_MSG_COURSE_ID_BLANK);
    }

    if (null == restCourse)
    {
      return RestResponse.badRequest(ERR_MSG_COURSE_REQUEST_BODY);
    }

    try
    {
      GetCourseOutput existCourse = getCourse(courseId);
      previousCourseTitle = existCourse.getTitle();
    }
    catch (UseCaseException e)
    {
      LOGGER.error(e.getMessage(), e);
      return RestResponse.internalError(ERR_MSG_COURSE_NOT_FOUND);
    }

    RestCourseDetail courseDetail = restCourse.getCourseDetail();
    String categoryId = restCourse.getCategoryId();

    if (StringUtils.isBlank(categoryId) || null == courseDetail)
    {
      return RestResponse.badRequest("Category id or course detail cannot be null!");
    }

    UpdateCourseInput input = new UpdateCourseInput(courseId, categoryId, courseDetail.getTitle(), courseDetail.getCourseProperties());
    input.setDescription(restCourse.getCourseDetail().getDescription());
    input.setNote(restCourse.getCourseDetail().getNote());

    UpdateCourseFully updateCourse = new UpdateCourseFully(courseRepository);
    GetCourseOutput output;
    try
    {
      output = updateCourse.execute(input);
    }
    catch (UseCaseException e)
    {
      LOGGER.error(e.getMessage(), e);
      return RestResponse.internalError(String.format(ERR_MSG_UPDATE, courseId));
    }

    if (previousCourseTitle.trim().equals(output.getTitle().trim()))
    {
      return RestResponse.success(output);
    }

    return updateCourseFolder(output, courseId);
  }

  @ApiOperation(value = "Update course detail",
      notes = "This API updates/replaces the whole course detail including title, description and properties.")
  @PutMapping("/{courseId}")
  public ResponseEntity<RestResult> update(@PathVariable String courseId, @RequestBody RestCourseDetail request)
  {
    String previousCourseTitle;

    if (StringUtils.isBlank(courseId))
    {
      return RestResponse.badRequest(ERR_MSG_COURSE_ID_BLANK);
    }

    if (null == request)
    {
      return RestResponse.badRequest(ERR_MSG_COURSE_REQUEST_BODY);
    }

    try
    {
      GetCourseOutput existCourse = getCourse(courseId);
      previousCourseTitle = existCourse.getTitle();
    }
    catch (UseCaseException e)
    {
      LOGGER.error(e.getMessage(), e);
      return RestResponse.internalError(ERR_MSG_COURSE_NOT_FOUND);
    }

    UpdateCourseDetailInput input = new UpdateCourseDetailInput(courseId, request.getTitle(), request.getCourseProperties());
    input.setDescription(request.getDescription());
    input.setUserGroupsInput(mapToUpdateCourseInput(request.getUserGroup()));

    UpdateCourseDetail updateCourseDetail = new UpdateCourseDetail(courseRepository);
    GetCourseOutput output;
    try
    {
      output = updateCourseDetail.execute(input);
    }
    catch (UseCaseException e)
    {
      LOGGER.error(e.getMessage(), e);
      return RestResponse.internalError(String.format(ERR_MSG_UPDATE, courseId));
    }

    if (previousCourseTitle.trim().equals(output.getTitle().trim()))
    {
      return RestResponse.success(output);
    }

    return updateCourseFolder(output, courseId);
  }

  @ApiOperation(value = "Update courses enrolled groups")
  @PutMapping("/updateCoursesEnrolledGroups")
  public ResponseEntity<RestResult> updateCourseEnrolledGroups(
      @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
      @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate)
  {
    Map<String, Date> dateFilter = new HashMap<>();
    Date startDateFilter = Date.from(startDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
    Date endDateFilter = Date.from(endDate.atTime(23,59,59).atZone(ZoneId.systemDefault()).toInstant());

    dateFilter.put("startDate", startDateFilter);
    dateFilter.put("endDate", endDateFilter);
    FixPromotionEnrolledGroups fixPromotionEnrolledGroups = new FixPromotionEnrolledGroups(
        courseRepository, courseEnrollmentRepository, accessIdentityManagement, courseAuditRepository);

    try
    {
      return RestResponse.success(fixPromotionEnrolledGroups.execute(dateFilter));
    }
    catch (UseCaseException e)
    {
      return RestResponse.internalError(e.getMessage());
    }
  }

  private UserGroupsInput mapToUpdateCourseInput(RestUserGroup userGroup)
  {
    UserGroupsInput input = new UserGroupsInput();
    input.setGroupEnroll(userGroup.hasGroupEnroll());
    for (String entry : userGroup.getUsers())
    {
      input.addUser(entry);
    }
    for (String entry : userGroup.getGroups())
    {
      input.addGroup(entry);
    }
    return input;
  }

  @ApiOperation(value = "Update course properties",
      notes = "If a field does not exist, then it'll add that new field")
  @PatchMapping("/{courseId}/properties")
  public ResponseEntity<RestResult> update(@PathVariable String courseId, @RequestBody Map<String, Object> courseProperties)
  {
    UpdateCoursePropertiesInput input = new UpdateCoursePropertiesInput(courseId, courseProperties);
    UpdateCourseProperties updateCourseProperties = new UpdateCourseProperties(courseRepository);

    try
    {
      GetCourseOutput output = updateCourseProperties.execute(input);
      return RestResponse.success(output);
    }
    catch (UseCaseException e)
    {
      return RestResponse.internalError(String.format(ERR_MSG_UPDATE, courseId));
    }
    catch (NoSuchElementException e)
    {
      return RestResponse.notFound(String.format(ERR_MSG_COURSE_NOT_FOUND, courseId));
    }
  }

  @ApiOperation(value = "Update course enrollment",
      notes = "updates course userGroup and creates, deletes enrollments")
  @PatchMapping("/{courseId}/enrollment")
  public ResponseEntity<RestResult> update(@PathVariable String courseId, @RequestBody RestEnrollmentUpdate enrollmentUpdate)
  {
    if (enrollmentUpdate == null)
    {
      return RestResponse.badRequest("RestEnrollmentUpdate cannot be null!");
    }

    UpdateCourseEnrollment updateCourseEnrollment = new UpdateCourseEnrollment(courseRepository, courseEnrollmentRepository, accessIdentityManagement,
        courseAuditRepository, groupRepository, courseGroupRepository);
    UpdateCourseEnrollmentOutput output = new UpdateCourseEnrollmentOutput();
    RestUserGroup restUserGroup = enrollmentUpdate.getUserGroup();
    if (restUserGroup == null)
    {
      return RestResponse.badRequest("RestUserGroup cannot be null!");
    }
    else
    {
      try
      {
        output = updateCourseEnrollment.execute(new UpdateCourseEnrollmentInput(courseId, mapToUpdateCourseInput(restUserGroup)));
      }
      catch (UseCaseException e)
      {
        LOGGER.error(e.getMessage(), e);
        RestResponse.internalError("Error occurred while updating enrollments");
      }
    }
    String contentId = output.getContentId();
    if (enrollmentUpdate.isDeleteProgress() && !contentId.isEmpty())
    {
      RuntimeDataInput runtimeDataInput = new RuntimeDataInput(contentId);
      for (String learnerId : output.getLearnerIds())
      {
        runtimeDataInput.addLearnerId(learnerId);
      }

      try
      {
        clearLearnerRuntimeData.execute(runtimeDataInput);
      }
      catch (UseCaseException e)
      {
        LOGGER.error(e.getMessage(), e);
        RestResponse.internalError("Error occurred while deleting runtime data!");
      }
    }
    return RestResponse.success();
  }

  @ApiOperation("Delete a course")
  @DeleteMapping("/{courseId}")
  public ResponseEntity<RestResult> delete(@PathVariable String courseId)
  {
    GetCourseOutput courseOutput;
    try
    {
      courseOutput = getCourse(courseId);
    }
    catch (UseCaseException e)
    {
      return RestResponse.notFound(String.format(ERR_MSG_COURSE_NOT_FOUND, courseId));
    }

    DeleteCourseInput input = new DeleteCourseInput(courseId);

    DeleteCourse deleteCourse = new DeleteCourse(courseRepository, courseEnrollmentRepository,
        courseContentRepository, courseAssessmentRepository, courseTestRepository, courseGroupRepository, courseAuditRepository);
    DeleteCourseOutput output;
    try
    {
      output = deleteCourse.execute(input);
    }
    catch (UseCaseException e)
    {
      LOGGER.error(e.getMessage(), e);
      return RestResponse.internalError(e.getMessage());
    }

    if (output.isDeleted())
    {
      try
      {
        if (courseOutput.getCourseContentId() != null)
        {
          deleteSCORMContent.execute(courseOutput.getCourseContentId());
        }
      }
      catch (UseCaseException e)
      {
        LOGGER.error(e.getMessage(), e);
      }

      boolean isDeleted = deleteCourseFolder(courseId);

      if (!isDeleted)
      {
        LOGGER.warn("The course with the ID: [{}] was not deleted!", courseId);
      }

      return RestResponse.success();
    }
    else
    {
      return RestResponse.notFound(String.format(ERR_MSG_DELETE, courseId));
    }
  }

  @ApiOperation("Gets the SCORM content of a course")
  @GetMapping("/{courseId}/scorm-content")
  public ResponseEntity<RestResult> read(@PathVariable String courseId)
  {
    GetCourseOutput output;
    try
    {
      output = getCourse(courseId);
    }
    catch (UseCaseException e)
    {
      return RestResponse.internalError(String.format(ERR_MSG_COURSE_NOT_FOUND, courseId));
    }

    coursePathResolver.setCourseFolderPath(courseId);
    return scormContentRestApi.readById(output.getCourseContentId());
  }

  private void updateFolderProperties(String folderId, String folderName, Map<String, Object> properties) throws UseCaseException
  {
    UpdateFolderInput updateInput = new UpdateFolderInput(folderId, folderName, properties);

    updateFolder.execute(updateInput);
  }

  private ResponseEntity<RestResult> publishCourseAndCreateSCORMContent(String courseId, PublishRequestModel request)
  {
    ScheduledCourses.removeIfExists(courseId);

    Date publishDate = request.getPublishDate();
    RestUserGroup userGroups = request.getUserGroups();
    NotificationRequestModel notificationRequestModel = request.getNotificationRequestModel();

    return publishInTheFuture(publishDate, userGroups, courseId, notificationRequestModel);
  }

  @ApiOperation(value = "Hides published course.",
      notes = "This API hides course, changes status to unpublished and deletes all SCORM related data ")
  @PutMapping("/hide/{courseId}")
  public ResponseEntity<RestResult> hideCourse(@PathVariable String courseId)
  {
    if (StringUtils.isBlank(courseId))
    {
      return RestResponse.badRequest("Course id cannot be null!");
    }

    GetCourseInput input = new GetCourseInput(courseId);
    HideCourse hideCourse = new HideCourse(courseRepository, courseEnrollmentRepository, courseAuditRepository);

    try
    {
      HideCourseOutput output = hideCourse.execute(input);
      String contentId = output.getContentId();

      if (null != contentId && output.isHidden())
      {
        deleteSCORMContent.execute(contentId);

        String courseFolderId = getCourseFolderId(courseId);
        String scormfolderid = getFolderId(courseFolderId, SCORM_CONTENT_FOLDER_NAME);

        DeleteFolderInput folderInput = new DeleteFolderInput(scormfolderid);
        deleteFolder.execute(folderInput);

        return RestResponse.success(output);
      }

      return RestResponse.internalError(String.format("Failed to hide course with id [%s]", courseId));
    }
    catch (UseCaseException e)
    {
      LOGGER.error(e.getMessage(), e);
      return RestResponse.internalError(e.getMessage());
    }
  }

  @ApiOperation("Gets selected departments")
  @GetMapping("/group-enrollment/{courseId}")
  public ResponseEntity<RestResult> getGroupEnrollment(@PathVariable String courseId)
  {
    try
    {
      GetSelectedDepartmentTree getSelectedDepartmentTree = new GetSelectedDepartmentTree(courseRepository, groupRepository, lmsDepartmentService);
      return RestResponse.success(getSelectedDepartmentTree.execute(courseId));
    }
    catch (UseCaseException e)
    {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(RestEntity.of(e.getMessage()));
    }
  }

  private ResponseEntity<RestResult> publishInTheFuture(Date publishDate, RestUserGroup userGroups, String courseId,
      NotificationRequestModel notificationRequestModel)
  {
    Map<String, Object> publishDateProperty = new HashMap<>();
    publishDateProperty.put("publishDate", DateTimeUtils.toShortIsoString(publishDate));
    UpdateUserGroup updateUserGroup = new UpdateUserGroup(courseRepository);
    try
    {
      updateUserGroup.execute(new UpdateUserGroupInput(courseId, mapToUpdateCourseInput(userGroups)));
    }
    catch (UseCaseException e)
    {
      LOGGER.error("Could not update Users and Groups!");
    }
    this.update(courseId, publishDateProperty);
    try
    {
      courseRepository.update(new CourseId(courseId), PublishStatus.PENDING);
    }
    catch (LMSRepositoryException ex)
    {
      LOGGER.error("Could not update publish status!");
    }
    startTime = System.currentTimeMillis();
    deleteSCORMFolder(courseId);
    SCORMContentOutput scormContent = createSCORMContent(courseId);
    endTime = System.currentTimeMillis();
    LOGGER.warn("PROMOTION ############## => Create scorm took [{}] millis", (endTime - startTime));

    if (scormContent == null)
    {
      return RestResponse.internalError("Failed to create the SCORM content.");
    }

    if (publishDate.after(new Date()))
    {
      taskScheduler.initialize();

      ScheduledFuture<?> schedule = taskScheduler
          .schedule(() -> publishCourse(courseId, userGroups, notificationRequestModel, scormContent.getScormContentId()), publishDate);
      ScheduledCourses.add(courseId, schedule);
    }
    else
    {
      CompletableFuture.supplyAsync(() -> publishCourse(courseId, userGroups, notificationRequestModel, scormContent.getScormContentId()));
    }

    return RestResponse.success();
  }

  private SCORMContentOutput createSCORMContent(String courseId)
  {
    String courseName = null;
    boolean hasTest = false;
    boolean hasFeedBack = false;
    try
    {
      GetCourseOutput getCourseOutput = getCourse(courseId);
      courseName = getCourseOutput.getTitle();
      hasTest = getCourseOutput.getProperties().containsKey(HAS_TEST) && (boolean) getCourseOutput.getProperties().get(HAS_TEST);
      hasFeedBack = getCourseOutput.getProperties().containsKey(HAS_FEED_BACK) && (boolean) getCourseOutput.getProperties().get(HAS_FEED_BACK);
    }
    catch (UseCaseException e)
    {
      LOGGER.error(String.format(ERR_MSG_COURSE_NOT_FOUND, courseId));
    }

    GetCourseContentOutput courseContent = getCourseContent(courseId);
    if (courseContent == null)
    {
      LOGGER.error("Course content was not found!");
      return null;
    }

    SCORMContentOutput scormContentOutput = createSCORMContent(courseId, courseName, courseContent.getCourseModules(), hasTest, hasFeedBack);
    if (scormContentOutput == null)
    {
      LOGGER.error("Failed to create the SCORM content.");
    }
    return scormContentOutput;
  }

  private ResponseEntity<RestResult> updateCourseFolder(GetCourseOutput output, String courseId)
  {
    String courseTitle = output.getTitle();
    String courseFolderId;

    try
    {
      courseFolderId = getCourseFolderId(courseId);
    }
    catch (UseCaseException e)
    {
      LOGGER.error(e.getMessage(), e);
      return RestResponse.internalError("Course folder does not exist!");
    }

    try
    {
      updateFolderProperties(courseFolderId, courseId, getDescriptionProperty(courseTitle));
    }
    catch (UseCaseException e)
    {
      LOGGER.error(e.getMessage(), e);
      return RestResponse.internalError("Failed to update course folder name!");
    }

    return RestResponse.success(output);
  }

  private Map<String, Object> getDescriptionProperty(String courseTitle)
  {
    Map<String, Object> properties = new HashMap<>();
    properties.put(DESCRIPTION, courseTitle);

    return properties;
  }

  private boolean publishCourse(String courseId, RestUserGroup userGroups, NotificationRequestModel notificationRequestModel, String scormContentId)
  {
    PublishCourseInput input = new PublishCourseInput(courseId, scormContentId);

    if (userGroups != null)
    {
      input.setUserGroups(mapToUpdateCourseInput(userGroups));
    }

    PublishCourse publishCourse = new PublishCourse(courseRepository, courseEnrollmentRepository, accessIdentityManagement, courseGroupRepository,
        groupRepository);
    Course course;
    Map<String, Object> templateData;
    try
    {
      course = courseRepository.getCourse(new CourseId(courseId));
      templateData = new HashMap<>(course.getCourseDetail().getProperties());
      templateData.put(COURSE_ID, courseId);
      templateData.put("courseName", course.getCourseDetail().getTitle());
      templateData.put("memo", notificationRequestModel.getMemo());
      templateData.put("authorId", course.getAuthorId().getId());
      templateData.putIfAbsent("endDate", "");
      templateData.put(DOMAIN_NAME, domainName);
    }
    catch (LMSRepositoryException e)
    {
      LOGGER.error("Failed get course [{}] cause: [{}]", courseId, e.getMessage());
      return false;
    }

    ContactInfo currentUserContact = accessIdentityManagement.getContactInfo(course.getAuthorId().getId());

    try
    {
      startTime = System.currentTimeMillis();
      PublishCourseOutput output = publishCourse.execute(input);
      endTime = System.currentTimeMillis();
      LOGGER.warn("PROMOTION ############## => Publish course took [{}] millis", (endTime - startTime));
      List<String> enrolledLearnerIds = output.getEnrolledLearnerIds();
      Set<String> groupAdmins = output.getGroupAdmins();

      GetCourseOutput courseOutput = output.getCourseOutput();

      Set<String> users = new HashSet<>();
      users.addAll(groupAdmins);
      users.addAll(enrolledLearnerIds);

      sendPublishedNotifications(notificationRequestModel, templateData, users);

      this.sendNoticeToPublisher(templateData, currentUserContact, courseOutput.getTitle(), true);

      return true;
    }
    catch (UseCaseException e)
    {
      //rollback all
      deleteSCORMFolder(courseId);
      this.sendNoticeToPublisher(templateData, currentUserContact, course.getCourseDetail().getTitle(),
          false);
      LOGGER.error("Failed to publish the course. The cause: [{}]", e.getMessage());
      return false;
    }
  }

  private void sendPublishedNotifications(NotificationRequestModel notificationRequestModel, Map<String, Object> templateData, Set<String> users)
  {
    try
    {
      if (notificationRequestModel.isSendEmail())
      {
        startTime = System.currentTimeMillis();
        sendEmail(templateData, users);
        endTime = System.currentTimeMillis();
        LOGGER.warn("PROMOTION ############## => Send mail took [{}] millis", (endTime - startTime));
      }
    }
    catch (RuntimeException e)
    {
      LOGGER.error(e.getMessage(), e);
    }

    if (notificationRequestModel.isSendSms())
    {
      startTime = System.currentTimeMillis();
      sendSms(templateData, users);
      endTime = System.currentTimeMillis();
      LOGGER.warn("PROMOTION ############## => Send sms took [{}] millis", (endTime - startTime));
    }
  }

  private void sendNoticeToPublisher(Map<String, Object> templateData, ContactInfo currentUserContact, String courseName, boolean success)
  {
    if (currentUserContact == null)
    {
      return;
    }

    String message;
    String link;
    if (success)
    {
      message = " амжилттай нийтлэгдлээ.";
      link = domainName + "/#/promotion/launch/" + templateData.get(COURSE_ID).toString() + "/true";
    }
    else
    {
      message = " нийтлэхэд алдаа гарсан байна, дахин нийтэлнэ үү.";
      link = domainName + "/#/promotion/create/" + templateData.get(COURSE_ID).toString() + "/structure";
    }

    templateData.put("message", message);
    templateData.put("link", link);

    if (currentUserContact.getEmail() != null)
    {
      EmailUtil.bulkSendEmailAsync(legacyEmailService,
          Collections.singletonList(currentUserContact.getEmail()),
          "[" + courseName + "] урамшуулал нийтлэгдсэн тухай", "promotion-publish.ftl",
          templateData);
    }
    if (currentUserContact.getPhone() != null)
    {
      String smsMessage = "Энэ өдрийн мэнд хүргэе. Таны '" + courseName + "' нэртэй урамшуулал " + message + " " + link;
      legacySmsSender.sendSms(currentUserContact.getPhone(), smsMessage);
    }
  }

  private void sendEmail(Map<String, Object> emailTemplateData, Set<String> enrolledLearnerIds)
  {
    emailTemplateData.put(DOMAIN_NAME, domainName);
    for (String learnerId : enrolledLearnerIds)
    {
      ContactInfo contactInfo = accessIdentityManagement.getContactInfo(learnerId);
      if (contactInfo != null && contactInfo.getEmail() != null)
      {
        EmailUtil.sendEmailAsync(legacyEmailService, contactInfo.getEmail(), emailTemplateData);
      }
    }
  }

  private void sendSms(Map<String, Object> smsTemplateData, Set<String> enrolledLearnerIds)
  {
    String message = legacySmsMessageFactory.newMessage(smsTemplateData);

    LOGGER.info("Sending sms to [{}] enrolled users.", enrolledLearnerIds.size());
    for (String learnerId : enrolledLearnerIds)
    {
      ContactInfo contactInfo = accessIdentityManagement.getContactInfo(learnerId);
      if (contactInfo != null && contactInfo.getPhone() != null)
      {
        CompletableFuture<Boolean> result = CompletableFuture.supplyAsync(() -> legacySmsSender.sendSms(contactInfo.getPhone(), message));
        result.handle((isMessageSent, exception) -> {
          if (!isMessageSent || exception != null)
          {
            LOGGER.error("Failed to send an SMS to user: [{}]", learnerId, exception);
          }
          else
          {
            LOGGER.info("Successfully sent an SMS to user: [{}]", learnerId);
          }
          return null;
        });
      }
    }
  }

  private SCORMContentOutput createSCORMContent(String courseId, String courseName, List<ContentModuleInfo> courseModules, boolean hasTest, boolean hasFeedBack)
  {
    String scormFolderId = null;
    try
    {
      startTime = System.currentTimeMillis();
      String courseFolderId = getCourseFolderId(courseId);
      endTime = System.currentTimeMillis();
      LOGGER.warn("PROMOTION ############## => Get course folder [{}] millis", (endTime - startTime));

      checkExistingSCORMAndDelete(courseId, courseFolderId);

      scormFolderId = createFolderInside(courseFolderId, SCORM_CONTENT_FOLDER_NAME);
      scormPackagingService.setPackageId(scormFolderId);

      CreateSCORMContentInput scormContentInput = toInput(courseModules, courseName);

      if (hasTest)
      {
        createSCORMTest(courseId, scormContentInput);
      }
      if (hasFeedBack)
      {
        createSCORMFeedBack(courseId, scormContentInput);
      }
      // TODO set wrapper type with the specified value from a request
      scormContentInput.setWrapperType("Carousel");
      return createSCORMContent.execute(scormContentInput);
    }
    catch (UseCaseException e)
    {
      deleteFolder(scormFolderId);
      return null;
    }
  }

  private void checkExistingSCORMAndDelete(String courseId, String courseFolderId)
  {
    try
    {
      String existingFolder = lmsFileSystemService.getSCORMFolderId(courseFolderId);
      boolean deleted = lmsFileSystemService.deleteFolder(existingFolder);
      if (!deleted)
      {
        throw new DMSRepositoryException("PROMOTION: Could not delete already existing SCORM folder! [" + courseId + "]");
      }
    }
    catch (DMSRepositoryException e)
    {
      LOGGER.warn(e.getMessage());
    }
  }

  private void deleteSCORMFolder(String courseId)
  {
    try
    {
      String courseFolderId = getCourseFolderId(courseId);
      String scormFolderId = getFolderId(courseFolderId, SCORM_CONTENT_FOLDER_NAME);
      deleteFolder(scormFolderId);
    }
    catch (UseCaseException e)
    {
      LOGGER.warn(e.getMessage());
    }
  }

  private void deleteFolder(String folderId)
  {
    if (folderId != null)
    {
      DeleteFolderInput input = new DeleteFolderInput(folderId);
      deleteFolder.execute(input);
    }
  }

  private String getFolderId(String parentFolderId, String folderName) throws UseCaseException
  {
    GetFolderInput input = new GetFolderInput(parentFolderId, folderName);

    GetFolderOuput output = getFolder.execute(input);

    return output.getId();
  }

  private GetCourseContentOutput getCourseContent(String courseId)
  {
    try
    {
      GetCourseContentInput getCourseContentInput = new GetCourseContentInput(courseId);
      GetCourseContent getCourseContent = new GetCourseContent(courseContentRepository);
      return getCourseContent.execute(getCourseContentInput);
    }
    catch (UseCaseException e)
    {
      return null;
    }
  }

  private CreateSCORMContentInput toInput(List<ContentModuleInfo> courseModules, String scormContentName)
  {
    CreateSCORMContentInput createSCORMContentInput = new CreateSCORMContentInput(scormContentName);

    for (ContentModuleInfo moduleInfo : courseModules)
    {
      OrganizationInfo organizationInfo = new OrganizationInfo(moduleInfo.getName());

      moduleInfo.getSectionInfos().forEach(sectionInfo -> {
        ResourceInfo resourceInfo = new ResourceInfo(sectionInfo.getFileId());
        organizationInfo.addResourceInfo(resourceInfo);
      });

      createSCORMContentInput.addOrganizationInfo(organizationInfo);
    }

    return createSCORMContentInput;
  }

  private GetCourseOutput getCourse(String courseId) throws UseCaseException
  {
    GetCourseInput getCourseInput = new GetCourseInput(courseId);
    GetCourse getCourse = new GetCourse(courseRepository);
    return getCourse.execute(getCourseInput);
  }

  private void createSCORMTest(String courseId, CreateSCORMContentInput scormContentInput) throws UseCaseException
  {
    GetAssessment getAssessment = new GetAssessment(courseAssessmentRepository, courseRepository);
    GetAssessmentInput getAssessmentInput = new GetAssessmentInput(courseId);
    GetAssessmentOutput getAssessmentOutput = getAssessment.execute(getAssessmentInput);
    List<String> testIds = getAssessmentOutput.getCourseTestIds();

    GetCourseTest getCourseTest = new GetCourseTest(courseTestRepository);
    for (String testId : testIds)
    {
      GetCourseTestInput getCourseTestInput = new GetCourseTestInput(testId);
      GetCourseTestOutput test = getCourseTest.execute(getCourseTestInput);
      CreateSCORMTestInput input = new CreateSCORMTestInput(test.getName(), toSCORMQuestionInfo(test.getQuestions()));
      input.setMaxAttemps(test.getMaxAttempts());
      input.setThresholdScore(test.getThresholdScore());
      OrganizationInfo testInfo = createSCORMTest.execute(input);
      scormContentInput.addOrganizationInfo(testInfo);
    }
  }

  private void createSCORMFeedBack(String courseId, CreateSCORMContentInput scormContentInput) throws UseCaseException
  {
    GetQuestionnaire getQuestionnaire = new GetQuestionnaire(courseQuestionnaireRepository);
    GetQuestionnaireInput getQuestionnaireInput = new GetQuestionnaireInput(courseId);
    GetQuestionnaireOutput getQuestionnaireOutput = getQuestionnaire.execute(getQuestionnaireInput);
    CreateSCORMQuestionnaireInput input = new CreateSCORMQuestionnaireInput(toSCORMQuestionInfo(getQuestionnaireOutput.getQuestions()),
        getQuestionnaireOutput.getName());

    OrganizationInfo questionnaireInfo = createSCORMQuestionnaire.execute(input);
    scormContentInput.addOrganizationInfo(questionnaireInfo);
  }

  private List<SCORMQuestionInfo> toSCORMQuestionInfo(List<QuestionInfo> questionInfos)
  {
    return questionInfos.stream()
        .map(question -> {
          List<AnswerInfo> answers = new ArrayList<>();
          question.getAnswers()
              .forEach(answerInfo -> answers.add(new AnswerInfo(answerInfo.getValue(), answerInfo.isCorrect(), answerInfo.getScore())));
          return new SCORMQuestionInfo(question.getTitle(), answers, question.getType());
        })
        .collect(Collectors.toList());
  }

  private void setDomainName()
  {
    Properties properties = System.getProperties();
    domainName = properties.getProperty(DOMAIN_NAME);
  }
}
