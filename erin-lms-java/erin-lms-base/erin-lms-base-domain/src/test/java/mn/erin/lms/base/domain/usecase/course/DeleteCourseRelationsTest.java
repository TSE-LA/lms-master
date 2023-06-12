package mn.erin.lms.base.domain.usecase.course;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import mn.erin.domain.base.usecase.UseCaseException;
import mn.erin.lms.base.aim.LmsDepartmentService;
import mn.erin.lms.base.aim.organization.DepartmentId;
import mn.erin.lms.base.aim.user.AuthorId;
import mn.erin.lms.base.aim.user.LearnerId;
import mn.erin.lms.base.domain.model.category.CourseCategoryId;
import mn.erin.lms.base.domain.model.course.Course;
import mn.erin.lms.base.domain.model.course.CourseDepartmentRelation;
import mn.erin.lms.base.domain.model.course.CourseDetail;
import mn.erin.lms.base.domain.model.course.CourseId;
import mn.erin.lms.base.domain.model.enrollment.CourseEnrollment;
import mn.erin.lms.base.domain.repository.CourseEnrollmentRepository;
import mn.erin.lms.base.domain.repository.CourseRepository;
import mn.erin.lms.base.domain.repository.LmsRepositoryRegistry;
import mn.erin.lms.base.domain.service.LmsServiceRegistry;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * @author Galsan Bayart.
 */
public class DeleteCourseRelationsTest
{
  public static final String COURSE_ID = "COURSE_ID";
  DeleteCourseRelations deleteCourseRelations;
  LmsDepartmentService lmsDepartmentService;

  @Before
  public void setUp() throws Exception
  {
    LmsRepositoryRegistry lmsRepositoryRegistry = mock(LmsRepositoryRegistry.class);
    LmsServiceRegistry lmsServiceRegistry = mock(LmsServiceRegistry.class);

    CourseEnrollmentRepository courseEnrollmentRepository = mock(CourseEnrollmentRepository.class);
    CourseRepository courseRepository = mock(CourseRepository.class);
    lmsDepartmentService = mock(LmsDepartmentService.class);

    when(lmsRepositoryRegistry.getCourseEnrollmentRepository()).thenReturn(courseEnrollmentRepository);
    when(lmsRepositoryRegistry.getCourseRepository()).thenReturn(courseRepository);
    when(lmsServiceRegistry.getDepartmentService()).thenReturn(lmsDepartmentService);

    deleteCourseRelations = new DeleteCourseRelations(lmsRepositoryRegistry, lmsServiceRegistry);

    Set<String> groupIds = new HashSet<>();
    groupIds.add("Group1");
    groupIds.add("Group2");

    when(lmsDepartmentService.getSubDepartments(any())).thenReturn(groupIds);

    Set<String> group1Members = new HashSet<>();
    group1Members.add("group1Member1");
    group1Members.add("group1Member2");
    group1Members.add("group1Member3");

    Set<String> group1Admin = new HashSet<>();
    group1Admin.add("admin1");

    when(lmsDepartmentService.getLearners("Group1")).thenReturn(group1Members);
    when(lmsDepartmentService.getInstructors("Group1")).thenReturn(group1Admin);

    Set<String> group2Members = new HashSet<>();
    group2Members.add("group2Member1");
    group2Members.add("group2Member2");
    group2Members.add("group2Member3");

    Set<String> group2Admin = new HashSet<>();
    group2Admin.add("admin2");

    when(lmsDepartmentService.getLearners("Group2")).thenReturn(group2Members);
    when(lmsDepartmentService.getInstructors("Group2")).thenReturn(group2Admin);

    when(courseEnrollmentRepository.listAll(any(LearnerId.class))).thenReturn(getCourseEnrollments());

    when(courseRepository.fetchById(any())).thenReturn(getCourse());


  }

  private Course getCourse()
  {
    return new Course(CourseId.valueOf("CourseID"), CourseCategoryId.valueOf("CourseCategoryID"), new CourseDetail("Title"), AuthorId.valueOf("AuthorID"), new CourseDepartmentRelation(
        DepartmentId.valueOf("RelationDepartment")));
  }

  private List<CourseEnrollment> getCourseEnrollments()
  {
    List<CourseEnrollment> courseEnrollmentList = new ArrayList<>();
    courseEnrollmentList.add(new CourseEnrollment(CourseId.valueOf("CourseId1"), LearnerId.valueOf("LearnerID1")));
    courseEnrollmentList.add(new CourseEnrollment(CourseId.valueOf("CourseId2"), LearnerId.valueOf("LearnerID2")));
    return courseEnrollmentList;
  }

  @Test
  public void whenSuccess() throws UseCaseException
  {
    Assert.assertNull(deleteCourseRelations.execute(COURSE_ID));
  }

  @Test
  public void whenSubDepartmentsNotExist() throws UseCaseException
  {
    Mockito.when(lmsDepartmentService.getSubDepartments(Mockito.any())).thenReturn(Collections.emptySet());
    Assert.assertNull(deleteCourseRelations.execute(COURSE_ID));
  }

  @Test
  public void whenDepartmentsHasNotMember() throws UseCaseException
  {
    when(lmsDepartmentService.getLearners(Mockito.any())).thenReturn(Collections.emptySet());
    when(lmsDepartmentService.getInstructors(Mockito.any())).thenReturn(Collections.emptySet());
    Assert.assertNull(deleteCourseRelations.execute(COURSE_ID));
  }





}