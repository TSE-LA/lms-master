package mn.erin.lms.legacy.domain.lms.usecase.course.get_course_enrollment_tree;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import mn.erin.domain.aim.model.group.Group;
import mn.erin.domain.aim.model.group.GroupId;
import mn.erin.domain.aim.repository.GroupRepository;
import mn.erin.domain.base.model.EntityId;
import mn.erin.domain.base.usecase.UseCaseException;
import mn.erin.lms.base.aim.LmsDepartmentService;
import mn.erin.lms.legacy.domain.lms.model.course.Course;
import mn.erin.lms.legacy.domain.lms.model.course.CourseId;
import mn.erin.lms.legacy.domain.lms.repository.CourseRepository;
import mn.erin.lms.legacy.domain.lms.repository.LMSRepositoryException;
import mn.erin.lms.legacy.domain.lms.usecase.course.CourseUseCase;

/**
 * @author Temuulen Naranbold
 */
public class GetSelectedDepartmentTree extends CourseUseCase<String, SelectedDepartmentTree>
{
  private final GroupRepository groupRepository;
  private final LmsDepartmentService departmentService;

  public GetSelectedDepartmentTree(CourseRepository courseRepository, GroupRepository groupRepository, LmsDepartmentService departmentService)
  {
    super(courseRepository);
    this.groupRepository = groupRepository;
    this.departmentService = departmentService;
  }

  @Override
  public SelectedDepartmentTree execute(String courseId) throws UseCaseException
  {
    try
    {
      Course course = courseRepository.getCourse(new CourseId(courseId));
      String department = departmentService.getCurrentDepartmentId();
      List<String> enrolledGroups = (List<String>) course.getCourseDetail().getProperties().get("enrolledGroups");
      if (enrolledGroups == null)
      {
        enrolledGroups = new ArrayList<>(course.getUserGroup().getGroups());
      }
      return getGroupTree(department, enrolledGroups);
    }
    catch (LMSRepositoryException | IllegalArgumentException | NullPointerException e)
    {
      throw new UseCaseException(e.getMessage());
    }
  }

  private SelectedDepartmentTree getGroupTree(String rootGroupId, List<String> groups)
  {
    Group group = groupRepository.findById(GroupId.valueOf(rootGroupId));

    SelectedDepartmentTree selectedDepartmentTree = new SelectedDepartmentTree(group.getId().getId(),
        group.getParent() != null ? group.getParent().getId() : null, group.getName());
    selectedDepartmentTree.setCurrentGroupSelected(hasGroupChecked(groups, group.getId().getId()));

    int selectedChildrenCount = 0;

    for (String childGroupId : group.getChildren().stream().map(EntityId::getId).collect(Collectors.toList()))
    {
      SelectedDepartmentTree childGroupTree = getGroupTree(childGroupId, groups);
      selectedDepartmentTree.addChild(childGroupTree);
      if (hasGroupChecked(groups, childGroupTree.getId()))
      {
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

  private boolean hasGroupChecked(List<String> groups, String groupId)
  {
    return groups.contains(groupId);
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
