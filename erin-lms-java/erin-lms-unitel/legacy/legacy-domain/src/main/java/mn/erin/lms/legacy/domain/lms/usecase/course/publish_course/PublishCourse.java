/*
 * (C)opyright, 2019, ERIN SYSTEMS LLC
 * The source code is protected by copyright laws and international copyright
 * treaties, as well as other intellectual property laws and treaties.
 * All rights reserved.
 */

package mn.erin.lms.legacy.domain.lms.usecase.course.publish_course;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import mn.erin.domain.aim.exception.AimRepositoryException;
import mn.erin.domain.aim.model.group.Group;
import mn.erin.domain.aim.repository.GroupRepository;
import mn.erin.domain.base.usecase.UseCaseException;
import mn.erin.lms.base.aim.AccessIdentityManagement;
import mn.erin.lms.legacy.domain.lms.model.content.CourseContentId;
import mn.erin.lms.legacy.domain.lms.model.course.Course;
import mn.erin.lms.legacy.domain.lms.model.course.CourseId;
import mn.erin.lms.legacy.domain.lms.model.course.PublishStatus;
import mn.erin.lms.legacy.domain.lms.model.course.UserGroup;
import mn.erin.lms.legacy.domain.lms.model.enrollment.CourseEnrollment;
import mn.erin.lms.legacy.domain.lms.repository.CourseEnrollmentRepository;
import mn.erin.lms.legacy.domain.lms.repository.CourseGroupRepository;
import mn.erin.lms.legacy.domain.lms.repository.CourseRepository;
import mn.erin.lms.legacy.domain.lms.repository.LMSRepositoryException;
import mn.erin.lms.legacy.domain.lms.usecase.course.CourseUseCase;
import mn.erin.lms.legacy.domain.lms.usecase.course.get_course.GetCourseOutput;
import mn.erin.lms.legacy.domain.lms.usecase.course.update_course.UpdateCourseProperties;
import mn.erin.lms.legacy.domain.lms.usecase.course.update_course.UpdateCoursePropertiesInput;
import mn.erin.lms.legacy.domain.lms.usecase.course.update_course.UpdateUserGroup;
import mn.erin.lms.legacy.domain.lms.usecase.course.update_course.UpdateUserGroupInput;
import mn.erin.lms.legacy.domain.lms.usecase.enrollment.create_enrollment.CreateCourseEnrollments;
import mn.erin.lms.legacy.domain.lms.usecase.enrollment.create_enrollment.CreateCourseEnrollmentsInput;
import mn.erin.lms.legacy.domain.lms.usecase.enrollment.create_enrollment.CreateCourseEnrollmentsOutput;

import org.apache.commons.lang3.Validate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Bat-Erdene Tsogoo.
 */
public class PublishCourse extends CourseUseCase<PublishCourseInput, PublishCourseOutput>
{
  private static final Logger LOGGER = LoggerFactory.getLogger(PublishCourse.class);

  private CreateCourseEnrollments createCourseEnrollment;
  private UpdateUserGroup updateUserGroup;
  private AccessIdentityManagement accessIdentityManagement;
  private GroupRepository groupRepository;
  private CourseGroupRepository courseGroupRepository;

  private Set<String> enrolledGroups;

  public PublishCourse(CourseRepository courseRepository, CourseEnrollmentRepository courseEnrollmentRepository,
      AccessIdentityManagement accessIdentityManagement, CourseGroupRepository courseGroupRepository, GroupRepository groupRepository)
  {
    super(courseRepository);
    this.accessIdentityManagement = Validate.notNull(accessIdentityManagement);
    createCourseEnrollment = new CreateCourseEnrollments(courseEnrollmentRepository);
    updateUserGroup = new UpdateUserGroup(courseRepository);
    this.courseGroupRepository = Objects.requireNonNull(courseGroupRepository, "CourseGroupRepository cannot be null!");
    this.groupRepository = groupRepository;
    this.enrolledGroups = new HashSet<>();
  }

  @Override
  public PublishCourseOutput execute(PublishCourseInput input) throws UseCaseException
  {
    Validate.notNull(input, "Input is required to publish a course");

    CourseId courseId = new CourseId(input.getCourseId());
    CourseContentId courseContentId = new CourseContentId(input.getCourseContentId());
    Course updated;
    UpdateCourseProperties updateCourseProperties = new UpdateCourseProperties(courseRepository);
    try
    {
      Map<String, Object> additionalProperties = new HashMap<>();
      additionalProperties.put("publishedDate", new Date());
      updateCourseProperties.execute(new UpdateCoursePropertiesInput(courseId.getId(), additionalProperties));
      updateUserGroup.execute(new UpdateUserGroupInput(courseId.getId(), input.getUserGroups()));
      courseRepository.update(courseId, courseContentId);
      updated = courseRepository.update(courseId, PublishStatus.PUBLISHED);
    }
    catch (LMSRepositoryException | RuntimeException e)
    {
      LOGGER.error(e.getMessage(), e);
      throw new UseCaseException("Failed to update the course");
    }

    UserGroup userGroup = updated.getUserGroup();
    if (userGroup != null)
    {
      Set<String> allGroupMembers = getGroupMembers(userGroup, input.getUserGroups().hasGroupEnroll());

      CreateCourseEnrollmentsInput enrollmentInput = toEnrollmentInput(updated.getCourseId().getId(), allGroupMembers);
      try
      {
        CreateCourseEnrollmentsOutput enrollmentsOutput = createCourseEnrollment.execute(enrollmentInput);
        createGroupCourses(courseId);
        Map<String, Object> enrolledGroups = new HashMap<>();
        enrolledGroups.put("enrolledGroups", this.enrolledGroups);
        updateCourseProperties.execute(new UpdateCoursePropertiesInput(courseId.getId(), enrolledGroups));
        return toOutput(enrollmentsOutput, getCourseOutput(updated));
      }
      catch (UseCaseException e)
      {
        throw new UseCaseException("Failed to enroll learners. The cause: " + e.getMessage());
      }
    }
    return new PublishCourseOutput(getCourseOutput(updated), new ArrayList<>(), new HashSet<>());
  }

  private Set<String> getGroupMembers(UserGroup userGroup, boolean hasGroupEnrollment)
  {

    Set<String> usersInGroups = new HashSet<>();

    for (String group : userGroup.getGroups())
    {
      try
      {
        Group assignedGroup = groupRepository.findByName(group);
        //TODO: remove this hasGroupEnrollment logic later
        if (assignedGroup != null && !hasGroupEnrollment)
        {
          Set<String> assignedSubGroups = accessIdentityManagement.getSubDepartments(assignedGroup.getId().getId());
          this.enrolledGroups.addAll(assignedSubGroups);
        }else{
          this.enrolledGroups.add(group);
      }
      }
      catch (AimRepositoryException e)
      {
        LOGGER.error("Failed to get group for name [{}]", group, e);
      }
    }

    if (!hasGroupEnrollment)
    {
      for (String enrolledGroup : this.enrolledGroups)
      {
        Set<String> learners = accessIdentityManagement.getLearners(enrolledGroup);
        usersInGroups.addAll(learners);
      }
    }

    usersInGroups.addAll(userGroup.getUsers());
    return usersInGroups;
  }

  private PublishCourseOutput toOutput(CreateCourseEnrollmentsOutput input, GetCourseOutput courseOutput)
  {
    List<CourseEnrollment> courseEnrollments = input.getEnrollments();

    List<String> learnerIds = new ArrayList<>();

    for (CourseEnrollment courseEnrollment : courseEnrollments)
    {
      learnerIds.add(courseEnrollment.getLearnerId().getId());
    }

    Set<String> groupAdmins = new HashSet<>();
    for (String group : enrolledGroups)
    {
      Set<String> admins = accessIdentityManagement.getInstructors(group);
      groupAdmins.addAll(admins);
    }

    return new PublishCourseOutput(courseOutput, learnerIds, groupAdmins);
  }

  private CreateCourseEnrollmentsInput toEnrollmentInput(String courseId, Set<String> learners)
  {
    CreateCourseEnrollmentsInput enrollmentInput = new CreateCourseEnrollmentsInput(courseId);
    for (String learner : learners)
    {
      enrollmentInput.addLearnerId(learner);
    }
    return enrollmentInput;
  }

  private void createGroupCourses(CourseId courseId)
  {
    for (String group : this.enrolledGroups)
    {
      courseGroupRepository.create(courseId, group);
    }
  }
}
