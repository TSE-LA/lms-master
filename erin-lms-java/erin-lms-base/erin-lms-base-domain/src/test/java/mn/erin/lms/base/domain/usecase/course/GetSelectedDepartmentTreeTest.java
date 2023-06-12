package mn.erin.lms.base.domain.usecase.course;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import mn.erin.domain.aim.model.group.Group;
import mn.erin.domain.aim.model.group.GroupId;
import mn.erin.domain.aim.model.tenant.TenantId;
import mn.erin.domain.base.usecase.UseCaseException;
import mn.erin.lms.base.aim.AccessIdentityManagement;
import mn.erin.lms.base.aim.LmsDepartmentService;
import mn.erin.lms.base.aim.organization.DepartmentId;
import mn.erin.lms.base.domain.model.course.Course;
import mn.erin.lms.base.domain.model.course.SelectedDepartmentTree;
import mn.erin.lms.base.domain.repository.CourseRepository;
import mn.erin.lms.base.domain.repository.LmsRepositoryException;
import mn.erin.lms.base.domain.repository.LmsRepositoryRegistry;
import mn.erin.lms.base.domain.service.LmsServiceRegistry;

import static mn.erin.lms.base.domain.usecase.course.CourseTestUtils.generateCourse;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
/**
 * @author Temuulen Naranbold
 */
public class GetSelectedDepartmentTreeTest
{
  private static final String ROOT_DEPARTMENT = "rootDepartment";
  private CourseRepository courseRepository;
  private Course course;

  GetSelectedDepartmentTree getSelectedDepartmentTree;

  @Before
  public void setUp() throws LmsRepositoryException
  {
    LmsRepositoryRegistry lmsRepositoryRegistry = mock(LmsRepositoryRegistry.class);
    LmsServiceRegistry lmsServiceRegistry = mock(LmsServiceRegistry.class);
    AccessIdentityManagement accessIdentityManagement = mock(AccessIdentityManagement.class);
    courseRepository = mock(CourseRepository.class);
    LmsDepartmentService lmsDepartmentService = mock(LmsDepartmentService.class);

    when(lmsRepositoryRegistry.getCourseRepository()).thenReturn(courseRepository);
    when(lmsServiceRegistry.getAccessIdentityManagement()).thenReturn(accessIdentityManagement);
    when(lmsServiceRegistry.getDepartmentService()).thenReturn(lmsDepartmentService);
    when(lmsDepartmentService.getCurrentDepartmentId()).thenReturn(ROOT_DEPARTMENT);

    course = generateCourse();
    Set<String> departmentIds = departmentIds(5);
    departmentIds.add(ROOT_DEPARTMENT);
    List<Group> groups = dummyGroups(5);
    course.getCourseDepartmentRelation().setAssignedDepartments(departmentIds.stream().map(DepartmentId::valueOf).collect(Collectors.toSet()));

    when(courseRepository.fetchById(course.getCourseId())).thenReturn(course);
    when(accessIdentityManagement.getSubDepartments(course.getCourseDepartmentRelation().getCourseDepartment().getId()))
        .thenReturn(departmentIds);
    when(accessIdentityManagement.getDepartments(departmentIds)).thenReturn(groups);

    getSelectedDepartmentTree = new GetSelectedDepartmentTree(lmsRepositoryRegistry, lmsServiceRegistry);
  }

  @Test(expected = UseCaseException.class)
  public void whenCourseId_isNull() throws UseCaseException
  {
    getSelectedDepartmentTree.execute(null);
  }

  @Test(expected = UseCaseException.class)
  public void whenCourseId_isBlank() throws UseCaseException
  {
    getSelectedDepartmentTree.execute("");
  }

  @Test(expected = UseCaseException.class)
  public void whenCourse_notFound() throws LmsRepositoryException, UseCaseException
  {
    when(courseRepository.fetchById(course.getCourseId())).thenThrow(LmsRepositoryException.class);
    getSelectedDepartmentTree.execute(course.getCourseId().getId());
  }

  @Test(expected = UseCaseException.class)
  public void whenCourse_isNull() throws LmsRepositoryException, UseCaseException
  {
    when(courseRepository.fetchById(course.getCourseId())).thenReturn(null);
    getSelectedDepartmentTree.execute(course.getCourseId().getId());
  }

  @Test
  public void whenAllChildrenSelected() throws UseCaseException
  {
    SelectedDepartmentTree selectedDepartmentTree = getSelectedDepartmentTree.execute(course.getCourseId().getId());
    Assert.assertTrue(selectedDepartmentTree.isAllChildrenSelected());
    Assert.assertFalse(selectedDepartmentTree.isSomeChildrenSelected());
  }

  @Test
  public void whenSomeChildrenSelected() throws UseCaseException
  {
    Set<String> ids = departmentIds(3);
    ids.add(ROOT_DEPARTMENT);
    course.getCourseDepartmentRelation().setAssignedDepartments(ids.stream().map(DepartmentId::valueOf).collect(Collectors.toSet()));
    SelectedDepartmentTree selectedDepartmentTree = getSelectedDepartmentTree.execute(course.getCourseId().getId());
    Assert.assertFalse(selectedDepartmentTree.isAllChildrenSelected());
    Assert.assertTrue(selectedDepartmentTree.isSomeChildrenSelected());
  }

  @Test
  public void whenCurrentGroupSelected() throws UseCaseException
  {
    Set<String> ids = departmentIds(1);
    ids.add(ROOT_DEPARTMENT);
    course.getCourseDepartmentRelation().setAssignedDepartments(ids.stream().map(DepartmentId::valueOf).collect(Collectors.toSet()));
    SelectedDepartmentTree selectedDepartmentTree = getSelectedDepartmentTree.execute(course.getCourseId().getId());
    Assert.assertEquals("departmentId-0", findTreeById(selectedDepartmentTree, "departmentId-0").getId());
  }

  private SelectedDepartmentTree findTreeById(SelectedDepartmentTree selectedDepartmentTree, String id)
  {
    if (selectedDepartmentTree.getId().equals(id))
    {
      return selectedDepartmentTree;
    }
    else
    {
      return findTreeById(selectedDepartmentTree.getChildren().iterator().next(), id);
    }
  }

  private Set<String> departmentIds(int count)
  {
    Set<String> ids = new HashSet<>();
    for (int counter = 0; counter < count; counter++)
    {
      ids.add("departmentId-" + counter);
    }
    return ids;
  }

  private List<Group> dummyGroups(int count)
  {
    List<Group> groups = new ArrayList<>();
    TenantId tenantId = TenantId.valueOf("lms");
    Group root = new Group(GroupId.valueOf("rootDepartment"), null, tenantId, "rootGroup");
    root.addChild(GroupId.valueOf("departmentId-0"));
    groups.add(root);

    for (int counter = 0; counter < count; counter++)
    {
      Group group = new Group(
          GroupId.valueOf("departmentId-" + counter),
          counter == 0 ? root.getId() : GroupId.valueOf("departmentId-" + counter),
          tenantId,
          "group-" + counter
      );
      if (counter + 1 < count)
      {
        group.addChild(GroupId.valueOf("departmentId-" + (counter + 1)));
      }
      groups.add(group);
    }

    return groups;
  }
}