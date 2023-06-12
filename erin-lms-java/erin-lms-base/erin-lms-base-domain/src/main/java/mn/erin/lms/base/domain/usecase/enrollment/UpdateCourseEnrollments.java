package mn.erin.lms.base.domain.usecase.enrollment;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.lang3.Validate;

import mn.erin.domain.base.usecase.UseCaseException;
import mn.erin.lms.base.domain.model.Authorized;
import mn.erin.lms.base.domain.model.course.Course;
import mn.erin.lms.base.domain.model.course.CourseDepartmentRelation;
import mn.erin.lms.base.domain.model.course.CourseId;
import mn.erin.lms.base.domain.model.course.PublishStatus;
import mn.erin.lms.base.domain.model.enrollment.CourseEnrollment;
import mn.erin.lms.base.aim.organization.DepartmentId;
import mn.erin.lms.base.aim.user.Instructor;
import mn.erin.lms.base.aim.user.LearnerId;
import mn.erin.lms.base.domain.repository.LmsRepositoryException;
import mn.erin.lms.base.domain.repository.LmsRepositoryRegistry;
import mn.erin.lms.base.aim.LmsDepartmentService;
import mn.erin.lms.base.domain.service.LmsServiceRegistry;
import mn.erin.lms.base.domain.service.ProgressTrackingService;
import mn.erin.lms.base.domain.usecase.course.CourseUseCase;
import mn.erin.lms.base.domain.usecase.enrollment.dto.UpdateCourseEnrollmentsInput;

/**
 * @author Bat-Erdene Tsogoo.
 */
@Authorized(users = { Instructor.class })
public class UpdateCourseEnrollments extends CourseUseCase<UpdateCourseEnrollmentsInput, Void>
{
  public static final String SUPERVISOR = "SUPERVISOR";
  private final LmsDepartmentService departmentService;
  private final ProgressTrackingService progressTrackingService;

  private CourseId courseId;

  public UpdateCourseEnrollments(LmsRepositoryRegistry lmsRepositoryRegistry,
      LmsServiceRegistry lmsServiceRegistry)
  {
    super(lmsRepositoryRegistry, lmsServiceRegistry);
    this.departmentService = lmsServiceRegistry.getDepartmentService();
    this.progressTrackingService = lmsServiceRegistry.getProgressTrackingService();
  }

  @Override
  public Void execute(UpdateCourseEnrollmentsInput input) throws UseCaseException
  {
    Validate.notNull(input);
    courseId = CourseId.valueOf(input.getCourseId());
    Course course = getCourse(courseId);
    updateEnrollments(course, input);
    return null;
  }

  public void updateEnrollmentAndCourseType(UpdateCourseEnrollmentsInput input) throws UseCaseException
  {
    Validate.notNull(input);
    courseId = CourseId.valueOf(input.getCourseId());
    Course course = getCourse(courseId);
    Set<String> learners = new HashSet<>(input.getAssignedLearners());
    Set<String> roles = new HashSet<>();

    roles.add("LMS_MANAGER");

    if (!input.getCourseType().equals("MANAGER"))
    {
      roles.add("LMS_SUPERVISOR");
    }

    updateEnrollments(course, input, learners, roles);
  }

  private Set<String> getDepartmentsLearners(Set<String> departments, Set<String> roles)
  {
    Set<String> learners = new HashSet<>();

    if (roles.isEmpty())
    {
      return Collections.emptySet();
    }

    for (String departmentName : departments)
    {
      for (String role : roles)
      {
        Set<String> departmentLearners;
        String departmentId = departmentService.getDepartmentId(departmentName);
        if(departmentId != null) {
          departmentLearners = departmentService.getLearnersByRole(departmentId, role);
        } else {
          departmentLearners = departmentService.getLearnersByRole(departmentName, role);
        }
        learners.addAll(departmentLearners);
      }
    }

    return learners;
  }

  private void updateEnrollments(Course course, UpdateCourseEnrollmentsInput input) throws UseCaseException
  {
    Set<DepartmentId> assignedDepartments = course.getCourseDepartmentRelation().getAssignedDepartments();
    Set<DepartmentId> newDepartments = getDepartments(input.getAssignedDepartments());
    Set<String> addedLearners = new HashSet<>();
//    TODO: Remove this logic when version is 3.0.0
    if (input.hasAutoChildDepartmentEnroll())
    {
      newDepartments = getSubDepartments(getDepartments(input.getAssignedDepartments()));
      addedLearners.addAll(getAddedLearnersFromDepartments(assignedDepartments, newDepartments));
    }

    Set<LearnerId> assignedLearners = course.getCourseDepartmentRelation().getAssignedLearners();
    Set<LearnerId> newLearners = input.getAssignedLearners().stream().map(LearnerId::valueOf).collect(Collectors.toSet());

    addedLearners.addAll(getAddedLearners(assignedLearners, newLearners));
    boolean isPublished = course.getCourseDetail().getPublishStatus().equals(PublishStatus.PUBLISHED);
    if (isPublished)
    {
      createEnrollments(addedLearners);
      removeEnrollmentsAndProgressData(getRemovedLearners(assignedLearners, newLearners));
    }
    update(course, newDepartments, newLearners);

    if (input.isSendNotification() && isPublished)
    {
      sendNotification(course, addedLearners);
    }
  }

  private void updateEnrollments(Course course, UpdateCourseEnrollmentsInput input, Set<String> learners, Set<String> roles) throws UseCaseException
  {
    Set<DepartmentId> assignedDepartments = course.getCourseDepartmentRelation().getAssignedDepartments();
    Set<DepartmentId> newDepartments = getDepartments(input.getAssignedDepartments());
    Set<String> removedLearners = new HashSet<>();
    Set<String> addedLearners = new HashSet<>();
    //    TODO: Remove this logic when version is 3.0.0
    if (input.hasAutoChildDepartmentEnroll())
    {
      newDepartments = getSubDepartments(getDepartments(input.getAssignedDepartments()));
      removedLearners.addAll(getRemovedLearnersFromDepartments(assignedDepartments, newDepartments));
      addedLearners.addAll(getAddedLearnersFromDepartmentsByCourseType(assignedDepartments, newDepartments, roles));
    }

    Set<LearnerId> assignedLearners = course.getCourseDepartmentRelation().getAssignedLearners();
    Set<LearnerId> newLearners = learners.stream().map(LearnerId::valueOf).collect(Collectors.toSet());

    removedLearners.addAll(getRemovedLearners(assignedLearners, newLearners));
    addedLearners.addAll(getAddedLearners(assignedLearners, newLearners));

    if (course.getCourseDetail().getPublishStatus().equals(PublishStatus.PUBLISHED))
    {
      removeEnrollmentsAndProgressData(removedLearners);
      createEnrollments(addedLearners);
    }
    update(course, newDepartments, newLearners);
  }

  private void update(Course course, Set<DepartmentId> newDepartments, Set<LearnerId> newLearners) throws UseCaseException
  {
    CourseDepartmentRelation courseDepartmentRelation = course.getCourseDepartmentRelation();
    courseDepartmentRelation.setAssignedLearners(newLearners);
    courseDepartmentRelation.setAssignedDepartments(newDepartments);

    try
    {
      courseRepository.update(course.getCourseId(), courseDepartmentRelation);
    }
    catch (LmsRepositoryException e)
    {
      throw new UseCaseException(e.getMessage(), e);
    }
  }

  private Set<String> getRemovedLearnersFromDepartments(Set<DepartmentId> assignedDepartments, Set<DepartmentId> newDepartments)
  {
    Set<String> removedLearners = new HashSet<>();

    for (DepartmentId assignedDepartment : assignedDepartments)
    {
      if (!newDepartments.contains(assignedDepartment))
      {
        removedLearners.addAll(departmentService.getLearners(assignedDepartment.getId()));
      }
    }

    return removedLearners;
  }

  private Set<String> getAddedLearnersFromDepartments(Set<DepartmentId> assignedDepartments, Set<DepartmentId> newDepartments)
  {
    Set<String> addedLearners = new HashSet<>();

    for (DepartmentId newDepartment : newDepartments)
    {
      if (!assignedDepartments.contains(newDepartment))
      {
        addedLearners.addAll(departmentService.getLearners(newDepartment.getId()));
      }
    }

    return addedLearners;
  }

  private Set<String> getAddedLearnersFromDepartmentsByCourseType(Set<DepartmentId> assignedDepartments, Set<DepartmentId> newDepartments, Set<String> roles)
  {
    Set<String> addedLearners = new HashSet<>();

    for (DepartmentId newDepartment : newDepartments)
    {
      if (!assignedDepartments.contains(newDepartment))
      {
        for (String role : roles)
        {
          addedLearners.addAll(departmentService.getLearnersByRole(newDepartment.getId(), role));
        }
      }
    }

    return addedLearners;
  }

  private Set<String> getRemovedLearners(Set<LearnerId> assignedLearners, Set<LearnerId> newLearners)
  {
    Set<String> removedLearners = new HashSet<>();

    for (LearnerId assignedLearner : assignedLearners)
    {
      if (!newLearners.contains(assignedLearner))
      {
        removedLearners.add(assignedLearner.getId());
      }
    }

    return removedLearners;
  }

  private Set<String> getAddedLearners(Set<LearnerId> assignedLearners, Set<LearnerId> newLearners)
  {
    Set<String> addedLearners = new HashSet<>();

    for (LearnerId newLearner : newLearners)
    {
      if (!assignedLearners.contains(newLearner))
      {
        addedLearners.add(newLearner.getId());
      }
    }

    return addedLearners;
  }

  private Set<DepartmentId> getDepartments(Set<String> assignedDepartments)
  {
    Set<DepartmentId> result = new HashSet<>();
    for (String departmentName : assignedDepartments)
    {
      String departmentId = departmentService.getDepartmentId(departmentName);
      if(departmentId != null) {
        result.add(DepartmentId.valueOf(departmentId));
      } else {
        result.add(DepartmentId.valueOf(departmentName));
      }
    }

    return result;
  }

  private Set<DepartmentId> getSubDepartments(Set<DepartmentId> departments)
  {
    Set<DepartmentId> result = new HashSet<>();
    for (DepartmentId departmentId: departments)
    {
      result.addAll(departmentService.getSubDepartments(departmentId.getId()).stream()
          .map(DepartmentId::valueOf).collect(Collectors.toSet()));
    }
    return result;
  }

  private void createEnrollments(Set<String> learners)
  {
    for (String learner : learners)
    {
      courseEnrollmentRepository.save(new CourseEnrollment(courseId, LearnerId.valueOf(learner)));
    }
  }

  private void removeEnrollmentsAndProgressData(Set<String> learners)
  {
    for (String learner : learners)
    {
      courseEnrollmentRepository.delete(LearnerId.valueOf(learner), courseId);
      deleteProgressData(learner);
    }
  }

  private void deleteProgressData(String learner)
  {
    progressTrackingService.deleteLearnerData(learner, courseId.getId());
  }

  private void sendNotification(Course course, Set<String> addedLearners)
  {
    Map<String, Object> courseData = new HashMap<>();
    courseData.put("courseId", course.getCourseId().getId());
    courseData.put("courseName", course.getCourseDetail().getTitle());
    lmsServiceRegistry.getNotificationService().notifyNewlyEnrolledUsers(addedLearners, courseData);
  }
}
