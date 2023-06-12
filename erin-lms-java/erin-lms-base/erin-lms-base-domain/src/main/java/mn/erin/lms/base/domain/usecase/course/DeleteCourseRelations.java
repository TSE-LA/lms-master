package mn.erin.lms.base.domain.usecase.course;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import mn.erin.domain.base.usecase.UseCaseException;
import mn.erin.lms.base.aim.LmsDepartmentService;
import mn.erin.lms.base.aim.organization.DepartmentId;
import mn.erin.lms.base.aim.user.LearnerId;
import mn.erin.lms.base.domain.model.course.Course;
import mn.erin.lms.base.domain.model.course.CourseDepartmentRelation;
import mn.erin.lms.base.domain.model.enrollment.CourseEnrollment;
import mn.erin.lms.base.domain.repository.LmsRepositoryException;
import mn.erin.lms.base.domain.repository.LmsRepositoryRegistry;
import mn.erin.lms.base.domain.service.LmsServiceRegistry;

/**
 * @author Erdenetulga
 */
public class DeleteCourseRelations extends CourseUseCase<String, Void>
{
  private final LmsDepartmentService lmsDepartmentService;

  public DeleteCourseRelations(LmsRepositoryRegistry lmsRepositoryRegistry,
      LmsServiceRegistry lmsServiceRegistry)
  {
    super(lmsRepositoryRegistry, lmsServiceRegistry);
    lmsDepartmentService = lmsServiceRegistry.getDepartmentService();
  }

  @Override
  public Void execute(String groupId) throws UseCaseException
  {
    Set<String> members = new HashSet<>();
    Set<String> departments = lmsDepartmentService.getSubDepartments(groupId);
    for (String department : departments)
    {
      members.addAll(lmsDepartmentService.getLearners(department));
      members.addAll(lmsDepartmentService.getInstructors(department));
    }

    for (String member : members)
    {
      List<CourseEnrollment> courseEnrollments = courseEnrollmentRepository.listAll(LearnerId.valueOf(member));
      for (CourseEnrollment courseEnrollment : courseEnrollments)
      {
        deleteEnrollment(groupId, member, courseEnrollment);
      }
    }
    return null;
  }

  private void deleteEnrollment(String groupId, String member, CourseEnrollment courseEnrollment)
  {
    try
    {
      Course course = courseRepository.fetchById(courseEnrollment.getCourseId());
      CourseDepartmentRelation courseDepartmentRelation = course.getCourseDepartmentRelation();
      Set<String> modifiableAssignedDepartments = new HashSet<>();
      Set<String> modifiableAssignedLearners = new HashSet<>();
      Set<DepartmentId> unmodifiableAssignedDepartments = new HashSet<>();
      Set<LearnerId> unmodifiableAssignedLearners = new HashSet<>();


      for (DepartmentId assignedDepartment : courseDepartmentRelation.getAssignedDepartments())
      {
        modifiableAssignedDepartments.add(assignedDepartment.getId());
      }
      modifiableAssignedDepartments.removeIf(e -> e.equals(groupId));
      for (String modifiableAssignedDepartment : modifiableAssignedDepartments)
      {
        unmodifiableAssignedDepartments.add(DepartmentId.valueOf(modifiableAssignedDepartment));
      }

      for (LearnerId assignedLearner : courseDepartmentRelation.getAssignedLearners())
      {
        modifiableAssignedLearners.add(assignedLearner.getId());
      }
      modifiableAssignedLearners.removeIf(e -> e.equals(member));
      for (String modifiableAssignedLearner : modifiableAssignedLearners)
      {
        unmodifiableAssignedLearners.add(LearnerId.valueOf(modifiableAssignedLearner));
      }
      courseDepartmentRelation.setAssignedLearners(unmodifiableAssignedLearners);
      courseDepartmentRelation.setAssignedDepartments(unmodifiableAssignedDepartments);

      courseRepository.update(courseEnrollment.getCourseId(), courseDepartmentRelation);
      courseEnrollmentRepository.deleteAll(LearnerId.valueOf(member));
    }
    catch (LmsRepositoryException e)
    {
      e.printStackTrace();
    }
  }
}

