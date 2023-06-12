package mn.erin.lms.base.domain.model.course;

import java.util.Collections;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import mn.erin.lms.base.aim.organization.DepartmentId;
import mn.erin.lms.base.aim.user.LearnerId;

/**
 * @author Bat-Erdene Tsogoo.
 */
public class CourseDepartmentRelation
{
  private final DepartmentId courseDepartment;
  private Set<DepartmentId> assignedDepartments = new HashSet<>();
  private Set<LearnerId> assignedLearners = new HashSet<>();

  public CourseDepartmentRelation(DepartmentId courseDepartment)
  {
    this.courseDepartment = Objects.requireNonNull(courseDepartment);
  }

  public void setAssignedDepartments(Set<DepartmentId> assignedDepartments)
  {
    this.assignedDepartments = assignedDepartments;
  }

  public void setAssignedLearners(Set<LearnerId> assignedLearners)
  {
    this.assignedLearners = assignedLearners;
  }

  public DepartmentId getCourseDepartment()
  {
    return courseDepartment;
  }

  public Set<DepartmentId> getAssignedDepartments()
  {
    return Collections.unmodifiableSet(assignedDepartments);
  }

  public Set<LearnerId> getAssignedLearners()
  {
    return Collections.unmodifiableSet(assignedLearners);
  }
}
