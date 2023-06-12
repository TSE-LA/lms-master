package mn.erin.lms.base.domain.usecase.course.classroom;

import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.Validate;

import mn.erin.domain.base.usecase.UseCaseException;
import mn.erin.lms.base.aim.LmsDepartmentService;
import mn.erin.lms.base.aim.organization.DepartmentId;
import mn.erin.lms.base.domain.model.course.Course;
import mn.erin.lms.base.domain.model.course.CourseId;
import mn.erin.lms.base.domain.repository.LmsRepositoryRegistry;
import mn.erin.lms.base.domain.service.LmsServiceRegistry;
import mn.erin.lms.base.domain.usecase.course.CourseUseCase;
import mn.erin.lms.base.domain.usecase.course.UpdateCourseEnrollmentCount;
import mn.erin.lms.base.domain.usecase.course.classroom.dto.CourseSuggestedUsersDto;
import mn.erin.lms.base.domain.usecase.course.classroom.dto.UpdateEnrollmentCountInput;

/**
 * @author Erdenetulga
 */
public class SaveClassroomCourseSuggestedUsers extends CourseUseCase<CourseSuggestedUsersDto, Boolean>
{
  private final UpdateCourseEnrollmentCount updateCourseEnrollmentCount;
  private final LmsDepartmentService departmentService;

  public SaveClassroomCourseSuggestedUsers(LmsRepositoryRegistry lmsRepositoryRegistry,
      LmsServiceRegistry lmsServiceRegistry)
  {
    super(lmsRepositoryRegistry, lmsServiceRegistry);
    this.updateCourseEnrollmentCount = new UpdateCourseEnrollmentCount(lmsRepositoryRegistry, lmsServiceRegistry);
    this.departmentService = lmsServiceRegistry.getDepartmentService();
  }

  public Boolean execute(CourseSuggestedUsersDto input) throws UseCaseException
  {
    Validate.notNull(input);
    Set<String> allCurrentDepartmentUsers = lmsServiceRegistry.getDepartmentService().getAllLearners(lmsServiceRegistry.getDepartmentService().getCurrentDepartmentId());
    allCurrentDepartmentUsers.removeAll(input.getUsers());
    boolean isSaved = courseSuggestedUsersRepository.saveUsers(input.getCourseId(), input.getUsers(), allCurrentDepartmentUsers);
    if (isSaved)
    {
      int enrolledCount = courseSuggestedUsersRepository.fetchAll(input.getCourseId()).get("users").size();
      Course course = getCourse(CourseId.valueOf(input.getCourseId()));
      if (course.getCourseType().getType().equals("MANAGER"))
      {
        enrolledCount += course.getCourseDepartmentRelation().getAssignedLearners().size();

        Set<DepartmentId> enrolledDepartments = course.getCourseDepartmentRelation().getAssignedDepartments();
        for (DepartmentId departmentId: enrolledDepartments)
        {
          Set<String> learners = departmentService.getLearnersByRole(departmentId.getId(), "LMS_MANAGER");
          enrolledCount += learners.size();
        }
      }
      updateCourseEnrollmentCount.execute(new UpdateEnrollmentCountInput(input.getCourseId(), Integer.toString(enrolledCount)));
    }
    return isSaved;
  }

  public Map<String, Set<String>> fetchAll(String courseId)
  {
    Validate.notNull(courseId);
    return courseSuggestedUsersRepository.fetchAll(courseId);
  }

  public Boolean saveGroups(CourseSuggestedUsersDto input)
  {
    Validate.notNull(input);
    return courseSuggestedUsersRepository.saveGroups(input.getCourseId(), input.getUsers());
  }

    public Boolean updateUsers(CourseSuggestedUsersDto input)
  {
    Validate.notNull(input);
    return courseSuggestedUsersRepository.updateUsers(input.getCourseId(), input.getUsers());
  }

  public Boolean updateGroups(CourseSuggestedUsersDto input)
  {
    Validate.notNull(input);
    return courseSuggestedUsersRepository.updateGroups(input.getCourseId(), input.getUsers());
  }
}
