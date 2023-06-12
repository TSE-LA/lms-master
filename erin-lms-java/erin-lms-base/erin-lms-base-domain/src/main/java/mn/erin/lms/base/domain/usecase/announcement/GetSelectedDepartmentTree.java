package mn.erin.lms.base.domain.usecase.announcement;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.lang3.Validate;

import mn.erin.domain.aim.model.group.Group;
import mn.erin.domain.base.model.EntityId;
import mn.erin.domain.base.usecase.UseCase;
import mn.erin.domain.base.usecase.UseCaseException;
import mn.erin.lms.base.domain.model.announcement.Announcement;
import mn.erin.lms.base.domain.model.course.SelectedDepartmentTree;
import mn.erin.lms.base.domain.repository.LmsRepositoryException;
import mn.erin.lms.base.domain.repository.LmsRepositoryRegistry;
import mn.erin.lms.base.domain.service.LmsServiceRegistry;

public class GetSelectedDepartmentTree implements UseCase<String, SelectedDepartmentTree>
{
  private final LmsRepositoryRegistry lmsRepositoryRegistry;
  private final LmsServiceRegistry lmsServiceRegistry;

  public GetSelectedDepartmentTree(LmsRepositoryRegistry lmsRepositoryRegistry, LmsServiceRegistry lmsServiceRegistry)
  {
    this.lmsRepositoryRegistry = Objects.requireNonNull(lmsRepositoryRegistry);
    this.lmsServiceRegistry = Objects.requireNonNull(lmsServiceRegistry);
  }

  @Override
  public SelectedDepartmentTree execute(String announcementId) throws UseCaseException
  {
    Validate.notBlank(announcementId);
    try
    {
      Announcement announcement = lmsRepositoryRegistry.getAnnouncementRepository().getById(announcementId);
      String departmentId = lmsServiceRegistry.getDepartmentService().getCurrentDepartmentId();

      Set<String> allDepartmentIds = lmsServiceRegistry.getAccessIdentityManagement().getSubDepartments(departmentId);
      List<Group> allDepartments = lmsServiceRegistry.getAccessIdentityManagement().getDepartments(allDepartmentIds);

      return getGroupTree(departmentId, announcement, allDepartments);
    }
    catch (LmsRepositoryException e)
    {
      throw new UseCaseException(e.getMessage(), e);
    }
  }

  private SelectedDepartmentTree getGroupTree(String departmentId, Announcement announcement, List<Group> allDepartments) throws UseCaseException
  {
    Group group = getCurrentDepartment(departmentId, allDepartments);

    SelectedDepartmentTree selectedDepartmentTree = new SelectedDepartmentTree(group.getId().getId(),
        group.getParent() != null ? group.getParent().getId() : null, group.getName());

    selectedDepartmentTree.setCurrentGroupSelected(hasGroupChecked(announcement, departmentId));

    int selectedChildrenCount = 0;

    List<String> childGroupIdList = group.getChildren().stream().map(EntityId::getId).collect(Collectors.toList());

    for (String childeGroupId : childGroupIdList)
    {
      SelectedDepartmentTree childGroupTree = getGroupTree(childeGroupId, announcement, allDepartments);
      selectedDepartmentTree.addChild(childGroupTree);
      if (hasGroupChecked(announcement, childeGroupId))
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

  private Group getCurrentDepartment(String departmentId, List<Group> allDepartments) throws UseCaseException
  {
    Optional<Group> optionalGroup = allDepartments.stream().filter(department -> department.getId().getId().equals(departmentId)).findFirst();

    if (!optionalGroup.isPresent())
    {
      throw new UseCaseException("Group with ID: [" + departmentId + "] not found.");
    }

    return optionalGroup.get();
  }

  private boolean hasGroupChecked(Announcement announcement, String departmentId)
  {
    return announcement.getDepartmentIds().contains(departmentId);
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
