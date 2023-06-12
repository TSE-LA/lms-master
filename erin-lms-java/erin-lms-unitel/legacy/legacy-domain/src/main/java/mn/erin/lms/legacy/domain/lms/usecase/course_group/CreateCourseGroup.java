package mn.erin.lms.legacy.domain.lms.usecase.course_group;

import mn.erin.domain.base.usecase.UseCase;
import mn.erin.lms.base.aim.AccessIdentityManagement;
import mn.erin.lms.legacy.domain.lms.model.course.CourseGroup;
import mn.erin.lms.legacy.domain.lms.model.course.CourseId;
import mn.erin.lms.legacy.domain.lms.repository.CourseGroupRepository;

/**
 * @author Bat-Erdene Tsogoo.
 */
public class CreateCourseGroup implements UseCase<CreateCourseGroupInput, CreateCourseGroupOutput>
{
  private final AccessIdentityManagement accessIdentityManagement;
  private final CourseGroupRepository courseGroupRepository;

  public CreateCourseGroup(AccessIdentityManagement accessIdentityManagement, CourseGroupRepository courseGroupRepository)
  {
    this.accessIdentityManagement = accessIdentityManagement;
    this.courseGroupRepository = courseGroupRepository;
  }

  @Override
  public CreateCourseGroupOutput execute(CreateCourseGroupInput input)
  {
    String currentUserGroup = accessIdentityManagement.getUserDepartmentId(accessIdentityManagement.getCurrentUsername());
    CourseGroup courseGroup = courseGroupRepository.create(new CourseId(input.getCourseId()), currentUserGroup);
    return new CreateCourseGroupOutput(courseGroup.getGroupId());
  }
}
