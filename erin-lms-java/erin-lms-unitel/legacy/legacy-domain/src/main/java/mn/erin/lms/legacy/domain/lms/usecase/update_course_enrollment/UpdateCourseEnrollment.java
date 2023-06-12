/*
 * (C)opyright, 2019, ERIN SYSTEMS LLC
 * The source code is protected by copyright laws and international copyright
 * treaties, as well as other intellectual property laws and treaties.
 * All rights reserved.
 */

package mn.erin.lms.legacy.domain.lms.usecase.update_course_enrollment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import mn.erin.domain.aim.exception.AimRepositoryException;
import mn.erin.domain.aim.model.group.Group;
import mn.erin.domain.aim.model.group.GroupId;
import mn.erin.domain.aim.repository.GroupRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import mn.erin.domain.base.usecase.UseCase;
import mn.erin.domain.base.usecase.UseCaseException;
import mn.erin.lms.base.aim.AccessIdentityManagement;
import mn.erin.lms.legacy.domain.lms.model.course.Course;
import mn.erin.lms.legacy.domain.lms.model.course.CourseId;
import mn.erin.lms.legacy.domain.lms.model.course.PublishStatus;
import mn.erin.lms.legacy.domain.lms.model.enrollment.CourseEnrollment;
import mn.erin.lms.legacy.domain.lms.model.enrollment.LearnerId;
import mn.erin.lms.legacy.domain.lms.repository.CourseAuditRepository;
import mn.erin.lms.legacy.domain.lms.repository.CourseEnrollmentRepository;
import mn.erin.lms.legacy.domain.lms.repository.CourseGroupRepository;
import mn.erin.lms.legacy.domain.lms.repository.CourseRepository;
import mn.erin.lms.legacy.domain.lms.repository.LMSRepositoryException;
import mn.erin.lms.legacy.domain.lms.usecase.course.get_course.GetCourseOutput;
import mn.erin.lms.legacy.domain.lms.usecase.course.update_course.UpdateCourseProperties;
import mn.erin.lms.legacy.domain.lms.usecase.course.update_course.UpdateCoursePropertiesInput;
import mn.erin.lms.legacy.domain.lms.usecase.course.update_course.UpdateUserGroup;
import mn.erin.lms.legacy.domain.lms.usecase.course.update_course.UpdateUserGroupInput;
import mn.erin.lms.legacy.domain.lms.usecase.enrollment.create_enrollment.CreateCourseEnrollments;
import mn.erin.lms.legacy.domain.lms.usecase.enrollment.create_enrollment.CreateCourseEnrollmentsInput;
import mn.erin.lms.legacy.domain.lms.usecase.update_course_enrollment.dto.UpdateCourseEnrollmentInput;

/**
 * @author Oyungerel Chuluunsukh.
 */
public class UpdateCourseEnrollment implements UseCase<UpdateCourseEnrollmentInput, UpdateCourseEnrollmentOutput>
{
  private static final Logger LOGGER = LoggerFactory.getLogger(UpdateCourseEnrollment.class);
  private final CourseRepository courseRepository;
  private final CourseEnrollmentRepository courseEnrollmentRepository;
  private final AccessIdentityManagement accessIdentityManagement;
  private final CourseAuditRepository courseAuditRepository;
  private final GroupRepository groupRepository;
  private final CourseGroupRepository courseGroupRepository;

  private Set<String> enrolledGroups;

  public UpdateCourseEnrollment(CourseRepository courseRepository, CourseEnrollmentRepository courseEnrollmentRepository,
      AccessIdentityManagement accessIdentityManagement, CourseAuditRepository courseAuditRepository, GroupRepository groupRepository,
      CourseGroupRepository courseGroupRepository)
  {
    this.courseRepository = Objects.requireNonNull(courseRepository, "Course repository cannot be null!");
    this.courseEnrollmentRepository = Objects.requireNonNull(courseEnrollmentRepository, "Course enrollment repository cannot be null!");
    this.accessIdentityManagement = Objects.requireNonNull(accessIdentityManagement);
    this.courseAuditRepository = Objects.requireNonNull(courseAuditRepository, "CourseAuditRepository cannot be null!");
    this.groupRepository = Objects.requireNonNull(groupRepository);
    this.courseGroupRepository = Objects.requireNonNull(courseGroupRepository);
    this.enrolledGroups = new HashSet<>();
  }

  //TODO: refactor me so that userGroups property is no longer used
  @Override
  public UpdateCourseEnrollmentOutput execute(UpdateCourseEnrollmentInput input) throws UseCaseException
  {
    if (input == null)
    {
      throw new UseCaseException("Update Course Enrollment cannot be null!");
    }
    CourseId courseId = new CourseId(input.getCourseId());

    Course course;
    try
    {
      //Get original course
      course = courseRepository.getCourse(courseId);
    }
    catch (LMSRepositoryException e)
    {
      LOGGER.error("Could not get course with id [{}]", courseId.getId());
      throw new UseCaseException(e.getMessage(), e);
    }

    UpdateUserGroupInput updateUserGroupInput = new UpdateUserGroupInput(input.getCourseId());
    Set<String> courseUsers = new HashSet<>();
    if (!input.getUserGroups().hasGroupEnroll())
    {
      courseUsers.addAll(course.getUserGroup().getUsers());
    }
    courseUsers.addAll(input.getUserGroups().getUsers());
    updateUserGroupInput.getUserGroups().setUsers(courseUsers);
    updateUserGroupInput.getUserGroups().setGroupEnroll(input.getUserGroups().hasGroupEnroll());

    try
    {
      //Get previously assigned groups and combine with new
      Set<String> courseGroups = new HashSet<>();
      if (!input.getUserGroups().hasGroupEnroll())
      {
        courseGroups.addAll(course.getUserGroup().getGroups());
        input.getUserGroups().setGroups(getAllGroupsName(getSubGroups(input.getUserGroups().getGroups())));
      }
      courseGroups.addAll(input.getUserGroups().getGroups());
      updateUserGroupInput.getUserGroups().setGroups(courseGroups);
    }
    catch (AimRepositoryException e)
    {
      throw new UseCaseException(e.getMessage(), e);
    }

    UpdateUserGroup updateUserGroup = new UpdateUserGroup(courseRepository);
    GetCourseOutput getCourseOutput = updateUserGroup.execute(updateUserGroupInput);

    UpdateCourseEnrollmentOutput output = new UpdateCourseEnrollmentOutput();

    if (course.getCourseDetail().getPublishStatus() == PublishStatus.PUBLISHED)
    {
      Set<String> allNewLearners = new HashSet<>();
      if (!input.getUserGroups().hasGroupEnroll())
      {
        allNewLearners = getGroupMembers(input.getUserGroups().getGroups());
      }
      allNewLearners.addAll(input.getUserGroups().getUsers());

      output.setContentId(getCourseOutput.getCourseContentId());

      List<CourseEnrollment> existingEnrollments = courseEnrollmentRepository.getEnrollmentList(courseId);
      Map<String, String> enrollmentMap = new HashMap<>();

      for (CourseEnrollment existingEnrollment : existingEnrollments)
      {
        enrollmentMap.put(existingEnrollment.getLearnerId().getId(), existingEnrollment.getId().getId());
      }
      List<String> enrolledLearnerIds = new ArrayList<>(enrollmentMap.keySet());

      //Filter out users with no enrollment and readership on promotion
      Set<String> newNotEnrolledLearners = allNewLearners.stream().filter(learner -> {
        return !enrolledLearnerIds.contains(learner) && !courseAuditRepository.isExist(courseId, new LearnerId(learner));
      }).collect(Collectors.toSet());
      //Create enrollments
      if (!newNotEnrolledLearners.isEmpty())
      {
        createEnrollments(newNotEnrolledLearners, courseId);
      }

      deleteEnrollments(input, enrolledLearnerIds, enrollmentMap);

      for (String groupId : updateUserGroupInput.getUserGroups().getGroups())
      {
        //Create course group relation for report
        courseGroupRepository.create(courseId, groupId);
      }

      //Delete extra readerships that was replaced by enrollments
      deleteCourseAudits(newNotEnrolledLearners, courseId);
    }

    //Update course enrolled groups properties
    Map<String, Object> groupEnrollment = new HashMap<>();
    groupEnrollment.put("enrolledGroups", !input.getUserGroups().hasGroupEnroll() ? this.enrolledGroups : input.getUserGroups().getGroups());
    UpdateCourseProperties updateCourseProperties = new UpdateCourseProperties(courseRepository);
    updateCourseProperties.execute(new UpdateCoursePropertiesInput(courseId.getId(), groupEnrollment));

    return output;
  }

  private Set<String> getSubGroups(Set<String> groupNames) throws AimRepositoryException
  {
    Set<String> result = new HashSet<>();
    for (String groupName: groupNames)
    {
      Group group = groupRepository.findByName(groupName);
      result.addAll(accessIdentityManagement.getSubDepartments(group.getId().getId()));
    }
    return result;
  }
  private Set<String> getAllGroupsName(Set<String> groupIds)
  {
    Set<String> result = new HashSet<>();
    for (String groupId: groupIds)
    {
      Group group = groupRepository.findById(GroupId.valueOf(groupId));
      result.add(group.getName());
    }
    return result;
  }

  private void createEnrollments(Set<String> newNotEnrolledLearners, CourseId courseId) throws UseCaseException
  {
    CreateCourseEnrollmentsInput input = new CreateCourseEnrollmentsInput(courseId.getId());
    for (String learnerId : newNotEnrolledLearners)
    {
      input.addLearnerId(learnerId);
    }

    CreateCourseEnrollments createCourseEnrollments = new CreateCourseEnrollments(courseEnrollmentRepository);
    try
    {
      createCourseEnrollments.execute(input);
    }
    catch (UseCaseException e)
    {
      LOGGER.error(e.getMessage(), e);
      throw new UseCaseException("Failed to create enrollment");
    }
  }

  private Set<String> getGroupMembers(Set<String> newGroups)
  {
    Set<String> usersInGroups = new HashSet<>();

    for (String group : newGroups)
    {
      try
      {
        Group assignedGroup = groupRepository.findByName(group);
        if (assignedGroup != null)
        {
          Set<String> learners = accessIdentityManagement.getLearners(assignedGroup.getId().getId());
          usersInGroups.addAll(learners);

          Set<String> subGroups = accessIdentityManagement.getSubDepartments(assignedGroup.getId().getId());
          this.enrolledGroups.addAll(subGroups);
        }
      }
      catch (AimRepositoryException e)
      {
        LOGGER.error("Failed to get group for name [{}]", group, e);
      }
    }
    return usersInGroups;
  }

  private void deleteCourseAudits(Set<String> learners, CourseId courseId)
  {
    for (String learner : learners)
    {
      courseAuditRepository.delete(courseId, new LearnerId(learner));
    }
  }

  private void deleteEnrollments(UpdateCourseEnrollmentInput input, List<String> enrolledLearnerIds, Map<String, String> enrollmentMap) throws UseCaseException
  {
    if (input.getUserGroups().hasGroupEnroll())
    {
      Set<String> removingEnrollmentIds = enrolledLearnerIds.stream()
          .filter(removingLearner -> !input.getUserGroups().getUsers().contains(removingLearner))
          .map(enrollmentMap::get)
          .collect(Collectors.toSet());
      try
      {
        if (!removingEnrollmentIds.isEmpty())
        {
          courseEnrollmentRepository.deleteEnrollments(removingEnrollmentIds);
        }
      }
      catch (LMSRepositoryException e)
      {
        throw new UseCaseException(e.getMessage());
      }
    }
  }
}
