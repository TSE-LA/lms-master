package mn.erin.lms.base.domain.usecase.classroom;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import mn.erin.domain.aim.model.tenant.TenantId;
import mn.erin.domain.aim.model.user.User;
import mn.erin.domain.aim.model.user.UserAggregate;
import mn.erin.domain.aim.model.user.UserContact;
import mn.erin.domain.aim.model.user.UserId;
import mn.erin.domain.aim.model.user.UserIdentity;
import mn.erin.domain.aim.model.user.UserProfile;
import mn.erin.domain.aim.model.user.UserStateChangeSource;
import mn.erin.domain.aim.model.user.UserStatus;
import mn.erin.domain.base.usecase.UseCaseException;
import mn.erin.lms.base.aim.AccessIdentityManagement;
import mn.erin.lms.base.aim.user.LearnerId;
import mn.erin.lms.base.domain.model.classroom_course.Attendance;
import mn.erin.lms.base.domain.model.classroom_course.ClassroomCourseAttendance;
import mn.erin.lms.base.domain.model.course.CourseId;
import mn.erin.lms.base.domain.repository.ClassroomCourseAttendanceRepository;
import mn.erin.lms.base.domain.repository.LmsRepositoryException;
import mn.erin.lms.base.domain.repository.LmsRepositoryRegistry;
import mn.erin.lms.base.domain.service.LmsServiceRegistry;
import mn.erin.lms.base.domain.usecase.course.classroom.GetClassroomCourseAttendance;

import static org.mockito.ArgumentMatchers.anySet;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * @author Temuulen Naranbold
 */
public class GetClassroomCourseAttendanceTest
{
  private static final String COURSE_ID = "courseId";
  private AccessIdentityManagement accessIdentityManagement;
  private ClassroomCourseAttendanceRepository classroomCourseAttendanceRepository;
  private GetClassroomCourseAttendance getClassroomCourseAttendance;

  @Before
  public void setUp() throws LmsRepositoryException
  {
    LmsRepositoryRegistry lmsRepositoryRegistry = mock(LmsRepositoryRegistry.class);
    LmsServiceRegistry lmsServiceRegistry = mock(LmsServiceRegistry.class);
    classroomCourseAttendanceRepository = mock(ClassroomCourseAttendanceRepository.class);
    accessIdentityManagement = mock(AccessIdentityManagement.class);

    when(classroomCourseAttendanceRepository.findByCourseId(CourseId.valueOf(COURSE_ID))).thenReturn(generateClassroomCourseAttendance());
    when(lmsServiceRegistry.getAccessIdentityManagement()).thenReturn(accessIdentityManagement);
    when(accessIdentityManagement.getUserAggregatesByUsername(anySet())).thenReturn(generateUserAggregates());

    getClassroomCourseAttendance = new GetClassroomCourseAttendance(lmsRepositoryRegistry, lmsServiceRegistry, classroomCourseAttendanceRepository);
  }

  @Test(expected = UseCaseException.class)
  public void whenCourseId_isNull() throws UseCaseException
  {
    getClassroomCourseAttendance.execute(null);
  }

  @Test(expected = UseCaseException.class)
  public void whenCourseId_isBlank() throws UseCaseException
  {
    getClassroomCourseAttendance.execute("");
  }

  @Test
  public void whenHasInactiveUsers() throws UseCaseException
  {
    List<UserAggregate> aggregates = generateUserAggregates();
    UserAggregate aggregate = new UserAggregate(
        new User(UserId.valueOf(aggregates.get(1).getUserId()), TenantId.valueOf(aggregates.get(1).getTenantId()),
            UserStatus.ARCHIVED, UserStateChangeSource.SYSTEM, LocalDateTime.MIN),
        aggregates.get(1).getIdentity(),
        aggregates.get(1).getProfile()
    );
    aggregates.remove(1);
    aggregates.add(aggregate);
    when(accessIdentityManagement.getUserAggregatesByUsername(anySet())).thenReturn(aggregates);
    Assert.assertEquals(4, getClassroomCourseAttendance.execute(COURSE_ID).getAttendances().size());
  }

  @Test
  public void whenAllUsers_statusIsActive() throws UseCaseException
  {
    Assert.assertEquals(5, getClassroomCourseAttendance.execute(COURSE_ID).getAttendances().size());
  }

  @Test(expected = UseCaseException.class)
  public void when_findByCourseId_throwsException() throws LmsRepositoryException, UseCaseException
  {
    when(classroomCourseAttendanceRepository.findByCourseId(CourseId.valueOf(COURSE_ID))).thenThrow(LmsRepositoryException.class);
    getClassroomCourseAttendance.execute(COURSE_ID);
  }

  private ClassroomCourseAttendance generateClassroomCourseAttendance()
  {
    List<Attendance> attendances = new ArrayList<>();
    for (int i = 0; i < 5; i++)
    {
      attendances.add(new Attendance(LearnerId.valueOf("learner" + i), false, Collections.emptyList()));
    }
    return new ClassroomCourseAttendance(CourseId.valueOf("courseId"), attendances);
  }

  private List<UserAggregate> generateUserAggregates()
  {
    List<UserAggregate> aggregates = new ArrayList<>();
    for (int i = 0; i < 5; i++)
    {
      User user = new User(UserId.valueOf("userId"+ i), TenantId.valueOf("tenant"));
      aggregates.add(new UserAggregate(
          user,
          new UserIdentity(user.getUserId(), "learner" + i, "secret"),
          new UserProfile(user.getUserId(), new UserContact())
      ));
    }
    return aggregates;
  }
}
