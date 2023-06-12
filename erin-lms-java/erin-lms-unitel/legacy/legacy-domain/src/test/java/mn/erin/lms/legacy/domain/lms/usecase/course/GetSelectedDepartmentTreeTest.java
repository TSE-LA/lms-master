package mn.erin.lms.legacy.domain.lms.usecase.course;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import mn.erin.domain.aim.model.group.Group;
import mn.erin.domain.aim.model.group.GroupId;
import mn.erin.domain.aim.model.tenant.TenantId;
import mn.erin.domain.aim.repository.GroupRepository;
import mn.erin.domain.base.usecase.UseCaseException;
import mn.erin.lms.base.aim.LmsDepartmentService;
import mn.erin.lms.legacy.domain.lms.model.course.AuthorId;
import mn.erin.lms.legacy.domain.lms.model.course.Course;
import mn.erin.lms.legacy.domain.lms.model.course.CourseCategoryId;
import mn.erin.lms.legacy.domain.lms.model.course.CourseDetail;
import mn.erin.lms.legacy.domain.lms.model.course.CourseId;
import mn.erin.lms.legacy.domain.lms.repository.CourseRepository;
import mn.erin.lms.legacy.domain.lms.repository.LMSRepositoryException;
import mn.erin.lms.legacy.domain.lms.usecase.course.get_course_enrollment_tree.GetSelectedDepartmentTree;
import mn.erin.lms.legacy.domain.lms.usecase.course.get_course_enrollment_tree.SelectedDepartmentTree;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * @author Temuulen Naranbold
 */
public class GetSelectedDepartmentTreeTest
{
  private CourseRepository courseRepository;
  private GroupRepository groupRepository;
  private Course course;
  private Group rootGroup;
  private Group group;

  private GetSelectedDepartmentTree getSelectedDepartmentTree;

  @Before
  public void setUp() throws LMSRepositoryException
  {
    courseRepository = mock(CourseRepository.class);
    groupRepository = mock(GroupRepository.class);
    LmsDepartmentService lmsDepartmentService = mock(LmsDepartmentService.class);

    course = new Course(new CourseId("id"), new CourseCategoryId("category"), new AuthorId("author"), new CourseDetail("title"));
    rootGroup = new Group(GroupId.valueOf("id"), null, TenantId.valueOf("tenant"), "rootGroup");
    group = new Group(GroupId.valueOf("id-1"), rootGroup.getId(), rootGroup.getTenantId(), "group");

    rootGroup.addChild(group.getId());

    when(courseRepository.getCourse(course.getCourseId())).thenReturn(course);
    when(groupRepository.findById(rootGroup.getId())).thenReturn(rootGroup);
    when(groupRepository.findById(group.getId())).thenReturn(group);
    when(lmsDepartmentService.getCurrentDepartmentId()).thenReturn(rootGroup.getId().getId());


    getSelectedDepartmentTree = new GetSelectedDepartmentTree(courseRepository, groupRepository, lmsDepartmentService);
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
  public void whenCourse_notFound() throws LMSRepositoryException, UseCaseException
  {
    when(courseRepository.getCourse(course.getCourseId())).thenThrow(LMSRepositoryException.class);
    getSelectedDepartmentTree.execute(course.getCourseId().getId());
  }

  @Test
  public void whenCurrentGroupSelected() throws UseCaseException
  {
    List<String> groups = new ArrayList<>();
    groups.add(rootGroup.getId().getId());
    groups.add(group.getId().getId());
    course.getCourseDetail().addProperty("enrolledGroups", (Serializable) groups);
    SelectedDepartmentTree selectedDepartmentTree = getSelectedDepartmentTree.execute(course.getCourseId().getId());
    Assert.assertTrue(findTreeById(selectedDepartmentTree, group.getId().getId()).isCurrentGroupSelected());
    Assert.assertTrue(findTreeById(selectedDepartmentTree, rootGroup.getId().getId()).isCurrentGroupSelected());
  }

  @Test
  public void whenAllChildrenSelected() throws UseCaseException
  {
    List<String> groups = new ArrayList<>();
    groups.add(rootGroup.getId().getId());
    groups.add(group.getId().getId());
    course.getCourseDetail().addProperty("enrolledGroups", (Serializable) groups);
    SelectedDepartmentTree selectedDepartmentTree = getSelectedDepartmentTree.execute(course.getCourseId().getId());
    Assert.assertTrue(selectedDepartmentTree.isAllChildrenSelected());
    Assert.assertFalse(selectedDepartmentTree.isSomeChildrenSelected());
  }

  @Test
  public void whenSomeChildrenSelected() throws UseCaseException
  {
    Group group1 = new Group(GroupId.valueOf("id-2"), rootGroup.getId(), rootGroup.getTenantId(), "group2");
    rootGroup.addChild(group1.getId());
    List<String> groups = new ArrayList<>();
    groups.add(rootGroup.getId().getId());
    groups.add(group1.getId().getId());
    course.getCourseDetail().addProperty("enrolledGroups", (Serializable) groups);
    when(groupRepository.findById(group1.getId())).thenReturn(group1);

    SelectedDepartmentTree selectedDepartmentTree = getSelectedDepartmentTree.execute(rootGroup.getId().getId());
    Assert.assertFalse(selectedDepartmentTree.isAllChildrenSelected());
    Assert.assertTrue(selectedDepartmentTree.isSomeChildrenSelected());
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
}
