package mn.erin.lms.base.domain.usecase.course;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import mn.erin.domain.aim.model.group.Group;
import mn.erin.domain.base.model.EntityId;
import mn.erin.lms.base.aim.LmsDepartmentService;
import mn.erin.lms.base.aim.organization.DepartmentId;
import mn.erin.lms.base.domain.model.course.Course;
import mn.erin.lms.base.domain.model.course.CourseDepartmentRelation;
import mn.erin.lms.base.domain.model.course.CourseId;
import mn.erin.domain.base.usecase.UseCaseException;
import mn.erin.lms.base.domain.model.course.SelectedDepartmentTree;
import mn.erin.lms.base.domain.repository.LmsRepositoryException;
import mn.erin.lms.base.domain.repository.LmsRepositoryRegistry;
import mn.erin.lms.base.domain.service.LmsServiceRegistry;

/**
 * @author Temuulen Naranbold
 */
public class GetSelectedDepartmentTree extends CourseUseCase<String, SelectedDepartmentTree>
{
  private final LmsDepartmentService departmentService;

  public GetSelectedDepartmentTree(LmsRepositoryRegistry lmsRepositoryRegistry, LmsServiceRegistry lmsServiceRegistry)
  {
    super(lmsRepositoryRegistry, lmsServiceRegistry);
    this.departmentService = lmsServiceRegistry.getDepartmentService();
  }

  @Override
  public SelectedDepartmentTree execute(String courseId) throws UseCaseException
  {
    try
    {
      Course course = lmsRepositoryRegistry.getCourseRepository().fetchById(CourseId.valueOf(courseId));

      String departmentId = departmentService.getCurrentDepartmentId();

      Set<String> allDepartmentIds = lmsServiceRegistry.getAccessIdentityManagement().getSubDepartments(departmentId);

      List<Group> allDepartments = lmsServiceRegistry.getAccessIdentityManagement().getDepartments(allDepartmentIds);

      return getGroupTree(departmentId, course.getCourseDepartmentRelation(), allDepartments);
    }
    catch (LmsRepositoryException | IllegalArgumentException | NullPointerException e)
    {
      throw new UseCaseException(e.getMessage());
    }
  }

  private SelectedDepartmentTree getGroupTree(String departmentId, CourseDepartmentRelation courseDepartmentRelation, List<Group> allDepartments)
      throws UseCaseException
  {
    Optional<Group> optionalGroup = allDepartments.stream().filter(department -> department.getId().getId().equals(departmentId)).findFirst();

    if (!optionalGroup.isPresent())
    {
      throw new UseCaseException("Group with ID: [" + departmentId + "] not found.");
    }

    Group group = optionalGroup.get();

    SelectedDepartmentTree selectedDepartmentTree = new SelectedDepartmentTree(group.getId().getId(),
        group.getParent() != null ? group.getParent().getId() : null, group.getName());
    selectedDepartmentTree.setCurrentGroupSelected(hasGroupChecked(courseDepartmentRelation, departmentId));
    int selectedChildrenCount = 0;

    for (String childGroupId : group.getChildren().stream().map(EntityId::getId).collect(Collectors.toList()))
    {
      SelectedDepartmentTree childGroupTree = getGroupTree(childGroupId, courseDepartmentRelation, allDepartments);
      selectedDepartmentTree.addChild(childGroupTree);
      if (hasGroupChecked(courseDepartmentRelation, childGroupId))
      {
        selectedDepartmentTree.setSomeChildrenSelected(true);
        selectedChildrenCount++;
      }
      else
      {
        selectedDepartmentTree.setSomeChildrenSelected(childGroupTree.isSomeChildrenSelected() || childGroupTree.isAllChildrenSelected());
      }
    }

    if (allChildrenSelected(selectedDepartmentTree, selectedChildrenCount))
    {
      selectedDepartmentTree.setAllChildrenSelected(true);
      selectedDepartmentTree.setSomeChildrenSelected(false);
    }
    else if (selectedChildrenCount > 0 && !selectedDepartmentTree.getChildren().isEmpty())
    {
      selectedDepartmentTree.setSomeChildrenSelected(true);
    }

    return selectedDepartmentTree;
  }

  private boolean hasGroupChecked(CourseDepartmentRelation courseDepartmentRelation, String groupId)
  {
    return courseDepartmentRelation.getAssignedDepartments().contains(DepartmentId.valueOf(groupId));
  }

  private boolean allChildrenSelected(SelectedDepartmentTree selectedDepartmentTree, int selectedChildrenCount)
  {
    int childrenSize = selectedDepartmentTree.getChildren().size();
    int allChildrenCount = 0;
    for (SelectedDepartmentTree childTree : selectedDepartmentTree.getChildren())
    {
      if (childTree.isAllChildrenSelected() || childTree.getChildren().isEmpty())
      {
        allChildrenCount++;
      }
    }
    return childrenSize == selectedChildrenCount && !selectedDepartmentTree.getChildren().isEmpty() && allChildrenCount == childrenSize;
  }
}
