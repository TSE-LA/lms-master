/*
 * (C)opyright, 2019, ERIN SYSTEMS LLC
 * The source code is protected by copyright laws and international copyright
 * treaties, as well as other intellectual property laws and treaties.
 * All rights reserved.
 */

package mn.erin.lms.legacy.domain.lms.usecase.course.get_enrolled_course_list;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import mn.erin.domain.aim.repository.GroupRepository;
import mn.erin.domain.aim.repository.MembershipRepository;
import mn.erin.domain.base.usecase.UseCase;
import mn.erin.domain.base.usecase.UseCaseException;
import mn.erin.lms.base.aim.AccessIdentityManagement;
import mn.erin.lms.legacy.domain.lms.model.audit.CourseAudit;
import mn.erin.lms.legacy.domain.lms.model.course.Course;
import mn.erin.lms.legacy.domain.lms.model.course.CourseCategory;
import mn.erin.lms.legacy.domain.lms.model.course.CourseCategoryId;
import mn.erin.lms.legacy.domain.lms.model.course.CourseId;
import mn.erin.lms.legacy.domain.lms.model.enrollment.CourseEnrollment;
import mn.erin.lms.legacy.domain.lms.model.enrollment.CourseEnrollmentState;
import mn.erin.lms.legacy.domain.lms.model.enrollment.LearnerId;
import mn.erin.lms.legacy.domain.lms.repository.CourseAssessmentRepository;
import mn.erin.lms.legacy.domain.lms.repository.CourseAuditRepository;
import mn.erin.lms.legacy.domain.lms.repository.CourseCategoryRepository;
import mn.erin.lms.legacy.domain.lms.repository.CourseContentRepository;
import mn.erin.lms.legacy.domain.lms.repository.CourseEnrollmentRepository;
import mn.erin.lms.legacy.domain.lms.repository.CourseGroupRepository;
import mn.erin.lms.legacy.domain.lms.repository.CourseRepository;
import mn.erin.lms.legacy.domain.lms.repository.LMSRepositoryException;
import mn.erin.lms.legacy.domain.lms.service.EnrollmentStateService;
import mn.erin.lms.legacy.domain.lms.usecase.course.get_course.GetCourseOutput;
import mn.erin.lms.legacy.domain.lms.usecase.course.get_course_list.GetCourseList;
import mn.erin.lms.legacy.domain.lms.usecase.course.get_course_list.GetCourseListInput;
import mn.erin.lms.legacy.domain.lms.usecase.course_content.get_course_content.GetCourseContentInput;
import mn.erin.lms.legacy.domain.lms.usecase.course_content.get_course_publish_status.GetCoursePublishStatus;
import mn.erin.lms.legacy.domain.lms.usecase.course_content.get_course_publish_status.GetCoursePublishStatusOutput;
import mn.erin.lms.legacy.domain.lms.usecase.course_group.GetGroupCourseInput;
import mn.erin.lms.legacy.domain.lms.usecase.course_group.GetGroupCourses;

/**
 * @author Oyungerel Chuluunsukh.
 */
public class GetEnrolledCourseList implements UseCase<GetEnrolledListInput, List<GetCourseOutput>>
{
  private static final Logger LOGGER = LoggerFactory.getLogger(GetEnrolledCourseList.class);

  private CourseEnrollmentRepository courseEnrollmentRepository;
  private AccessIdentityManagement accessIdentityManagement;
  private CourseCategoryRepository courseCategoryRepository;
  private EnrollmentStateService enrollmentStateService;
  private CourseRepository courseRepository;
  private CourseAuditRepository courseAuditRepository;
  private CourseContentRepository courseContentRepository;
  private CourseAssessmentRepository courseAssessmentRepository;

  private final List<String> groupCourses;

  public GetEnrolledCourseList(CourseRepository courseRepository, CourseEnrollmentRepository courseEnrollmentRepository,
      AccessIdentityManagement accessIdentityManagement, CourseCategoryRepository courseCategoryRepository,
      EnrollmentStateService enrollmentStateService, CourseGroupRepository courseGroupRepository,
      GroupRepository groupRepository, MembershipRepository membershipRepository, CourseAuditRepository courseAuditRepository,
      CourseContentRepository courseContentRepository, CourseAssessmentRepository courseAssessmentRepository)
  {
    this.courseEnrollmentRepository = Objects.requireNonNull(courseEnrollmentRepository, "CourseEnrollmentRepository cannot be null!");
    this.courseCategoryRepository = Objects.requireNonNull(courseCategoryRepository, "CourseCategoryRepository cannot be null!");
    this.accessIdentityManagement = Objects.requireNonNull(accessIdentityManagement);
    this.enrollmentStateService = Objects.requireNonNull(enrollmentStateService, "EnrollmentStateService cannot be null!");
    this.courseRepository = Objects.requireNonNull(courseRepository, "CourseRepository cannot be null!");
    this.courseAuditRepository = Objects.requireNonNull(courseAuditRepository, "CourseAuditRepository cannot be null!");
    this.courseContentRepository = Objects.requireNonNull(courseContentRepository, "CourseContentRepository cannot be null!");
    this.courseAssessmentRepository = Objects.requireNonNull(courseAssessmentRepository, "CourseAssessmentRepository cannot be null!");

    this.groupCourses = new GetGroupCourses(accessIdentityManagement, courseGroupRepository, groupRepository, membershipRepository)
        .execute(new GetGroupCourseInput(null));
  }

  @Override
  public List<GetCourseOutput> execute(GetEnrolledListInput input) throws UseCaseException
  {
    if (input == null)
    {
      throw new UseCaseException("GetEnrolledListInput cannot be null!");
    }
    String parentCategory = input.getParentCategoryId();
    List<GetCourseOutput> courseListOutputs;
    GetCourseList getCourseList = new GetCourseList(courseRepository);
    if (parentCategory == null)
    {
      GetCourseListInput listInput = new GetCourseListInput(input.getCategoryId());
      listInput.setProperties(input.getProperties());
      listInput.setPublishStatus(input.getPublishStatus());

      courseListOutputs = getCourseList.execute(listInput);
    }
    else
    {
      courseListOutputs = getAllCourses(parentCategory, getCourseList);
    }

    GetCoursePublishStatus getCoursePublishStatus = new GetCoursePublishStatus(courseContentRepository, courseRepository, courseAssessmentRepository);
    for(GetCourseOutput course: courseListOutputs)
    {
      GetCoursePublishStatusOutput status = getCoursePublishStatus.execute(new GetCourseContentInput(course.getId()));
      course.setIsReadyToPublish(status != null ? status.getStatus() : "notReady");
    }

    if ("LMS_ADMIN".equalsIgnoreCase(accessIdentityManagement.getRole(accessIdentityManagement.getCurrentUsername())))
    {
      return courseListOutputs.stream().filter(courseOutput -> this.groupCourses.contains(courseOutput.getId())).collect(Collectors.toList());
    }
    else
    {
      List<CourseEnrollment> courseEnrollments = getCourseEnrollments(input);
      List<GetCourseOutput> enrolledCourses = new ArrayList<>();

      for (CourseEnrollment courseEnrollment : courseEnrollments)
      {
        for (GetCourseOutput course : courseListOutputs)
        {
          if (course.getId().equals(courseEnrollment.getCourseId().getId()))
          {
            CourseEnrollmentState courseEnrollmentState = enrollmentStateService.getCourseEnrollmentState(new CourseId(course.getId()));
            course.setEnrollmentState(courseEnrollmentState == null ? CourseEnrollmentState.ABANDONED.name() : courseEnrollmentState.name());
            enrolledCourses.add(course);
          }
        }
      }

      List<CourseAudit> courseAudits = courseAuditRepository.listAll(new LearnerId(accessIdentityManagement.getCurrentUsername()));

      List<GetCourseOutput> auditCourses = new ArrayList<>();

      List<String> mappedEnrollment = courseEnrollments.stream().map(enrollment -> enrollment.getCourseId().getId()).collect(Collectors.toList());

      for (CourseAudit courseAudit : courseAudits)
      {
        if(mappedEnrollment.contains(courseAudit.getCourseId().getId()))
        {
          continue;
        }
        try
        {
          Course courseToAudit = courseRepository.getCourse(courseAudit.getCourseId());
          for (GetCourseOutput course : courseListOutputs)
          {
            if (course.getId().equals(courseToAudit.getCourseId().getId()))
            {
              course.setEnrollmentState(CourseEnrollmentState.AUDIT.name());
              auditCourses.add(course);
            }
          }
        }
        catch (LMSRepositoryException e)
        {
          LOGGER.error(e.getMessage(), e);
        }
      }

      List<GetCourseOutput> result = new ArrayList<>();
      result.addAll(enrolledCourses);
      result.addAll(auditCourses);
      return result;
    }
  }

  private List<CourseEnrollment> getCourseEnrollments(GetEnrolledListInput input)
  {
    String currentUser = accessIdentityManagement.getCurrentUsername();
    LearnerId learnerId = new LearnerId(currentUser);
    List<CourseEnrollment> courseEnrollments;
    String enrollmentStateString = input.getEnrollmentState();

    if (enrollmentStateString == null)
    {
      courseEnrollments = courseEnrollmentRepository.getEnrollmentList(learnerId);
    }
    else
    {
      CourseEnrollmentState enrollmentState = CourseEnrollmentState.valueOf(enrollmentStateString);
      courseEnrollments = courseEnrollmentRepository.getEnrollmentList(learnerId, enrollmentState);
    }
    return courseEnrollments;
  }

  private List<GetCourseOutput> getAllCourses(String parentCategory, GetCourseList getCourseList)
  {
    List<GetCourseOutput> courseListOutputs = new ArrayList<>();
    Collection<CourseCategory> categories = courseCategoryRepository.listAll(new CourseCategoryId(parentCategory));
    if (!categories.isEmpty())
    {
      for (CourseCategory category : categories)
      {
        GetCourseListInput getCourseListInput = new GetCourseListInput(category.getCategoryId().getId());
        List<GetCourseOutput> courseListOutput = getCourseList.execute(getCourseListInput);
        if (courseListOutput != null && !courseListOutput.isEmpty())
        {
          courseListOutputs.addAll(courseListOutput);
        }
      }
    }
    return courseListOutputs;
  }
}
