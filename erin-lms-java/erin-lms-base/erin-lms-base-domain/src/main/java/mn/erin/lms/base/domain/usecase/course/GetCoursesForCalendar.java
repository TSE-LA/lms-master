package mn.erin.lms.base.domain.usecase.course;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.DayOfWeek;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import mn.erin.domain.base.model.EntityId;
import mn.erin.domain.base.usecase.UseCase;
import mn.erin.domain.base.usecase.UseCaseException;
import mn.erin.lms.base.aim.AccessIdentityManagement;
import mn.erin.lms.base.aim.LmsDepartmentService;
import mn.erin.lms.base.aim.organization.DepartmentId;
import mn.erin.lms.base.aim.role.LmsRole;
import mn.erin.lms.base.aim.user.LearnerId;
import mn.erin.lms.base.domain.model.classroom_course.CalendarEvent;
import mn.erin.lms.base.domain.model.course.Course;
import mn.erin.lms.base.domain.model.course.CourseId;
import mn.erin.lms.base.domain.model.course.PublishStatus;
import mn.erin.lms.base.domain.model.enrollment.CourseEnrollment;
import mn.erin.lms.base.domain.repository.CourseEnrollmentRepository;
import mn.erin.lms.base.domain.repository.CourseRepository;
import mn.erin.lms.base.domain.repository.LmsRepositoryRegistry;
import mn.erin.lms.base.domain.service.LmsServiceRegistry;
import mn.erin.lms.base.domain.usecase.course.dto.CalendarDayPropertiesDto;
import mn.erin.lms.base.domain.usecase.course.dto.CalendarEventDto;
import mn.erin.lms.base.domain.usecase.course.dto.CourseCalendarDto;
import mn.erin.lms.base.domain.usecase.course.dto.CourseCalendarInput;

/**
 * @author Byambajav
 */
public class GetCoursesForCalendar implements UseCase<CourseCalendarInput, CourseCalendarDto>
{

  private final CourseRepository courseRepository;
  private final LmsDepartmentService departmentService;
  private final CourseEnrollmentRepository courseEnrollmentRepository;
  private final AccessIdentityManagement accessIdentityManagement;

  public GetCoursesForCalendar(LmsRepositoryRegistry lmsRepositoryRegistry, LmsServiceRegistry lmsServiceRegistry)
  {
    this.courseRepository = lmsRepositoryRegistry.getCourseRepository();
    this.departmentService = lmsServiceRegistry.getDepartmentService();
    this.courseEnrollmentRepository = lmsRepositoryRegistry.getCourseEnrollmentRepository();
    this.accessIdentityManagement = lmsServiceRegistry.getAccessIdentityManagement();
  }

  @Override
  public CourseCalendarDto execute(CourseCalendarInput input) throws UseCaseException
  {
    LocalDate date = null;
    try
    {
      date = (new SimpleDateFormat("yyyy-MM-dd").parse(input.getDate())).toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
    }
    catch (ParseException e)
    {
      e.printStackTrace();
    }

    assert date != null;

    LocalDate startDate;
    if (date.getDayOfWeek().equals(DayOfWeek.MONDAY))
    {
      startDate = date;
    }
    else
    {
      startDate = date.with(TemporalAdjusters.previous(DayOfWeek.MONDAY));
    }

    LocalDate endDate;
    if (date.with(TemporalAdjusters.lastDayOfMonth()).getDayOfWeek().equals(DayOfWeek.SUNDAY))
    {
      endDate = date.with(TemporalAdjusters.lastDayOfMonth());
    }
    else
    {
      endDate = date.with(TemporalAdjusters.lastDayOfMonth()).with(TemporalAdjusters.next(DayOfWeek.SUNDAY));
    }

    String username = accessIdentityManagement.getCurrentUsername();
    String role = accessIdentityManagement.getRole(username);
    String currentDepartmentId = departmentService.getCurrentDepartmentId();
    Set<String> subDepartments = departmentService.getSubDepartments(currentDepartmentId);
    subDepartments.add(currentDepartmentId);
    List<CourseEnrollment> enrollments = courseEnrollmentRepository.listAll(LearnerId.valueOf(username));

    List<Course> courses = new ArrayList<>();
    List<CalendarEvent> calendarEvents = new ArrayList<>();

    if (role.equalsIgnoreCase(LmsRole.LMS_ADMIN.name()))
    {
      Set<String> allInstructors = new HashSet<>();
      for (String subDepartment : subDepartments)
      {
        allInstructors.addAll(departmentService.getInstructors(subDepartment));
      }
      List<CalendarEvent> events = courseRepository.listAllForCalendar(startDate, endDate, allInstructors);
      for (CalendarEvent event : events)
      {
        if (!event.getState().equalsIgnoreCase("new"))
        {
          calendarEvents.add(event);
        }
      }
      Set<String> courseIds = calendarEvents.stream().map(CalendarEvent::getCourseId).collect(Collectors.toSet());
      courses.addAll(courseRepository.listAll(subDepartments.stream().map(DepartmentId::valueOf).collect(Collectors.toSet()), startDate, endDate, courseIds));
    }
    else
    {
      calendarEvents.addAll(courseRepository.listAllForCalendarLearner(startDate, endDate, enrollments.stream()
          .map(CourseEnrollment::getCourseId).map(CourseId::getId).collect(Collectors.toSet())));
      Set<String> classroomCourseIds = calendarEvents.stream().map(CalendarEvent::getCourseId).collect(Collectors.toSet());
      Set<String> enrollmentIds = enrollments.stream().map(CourseEnrollment::getCourseId).map(EntityId::getId).collect(Collectors.toSet());
      Set<String> duplicates = new HashSet<>(classroomCourseIds);
      duplicates.retainAll(enrollmentIds);
      enrollmentIds.removeAll(duplicates);
      courses.addAll(courseRepository.listAllByCourseIds(enrollmentIds, startDate, endDate));
    }

    mapToCalendarEvent(courses, calendarEvents);
    return toCourseCalendarDto(calendarEvents);
  }

  private void mapToCalendarEvent(List<Course> courses, List<CalendarEvent> calendarEvents)
  {
    for (Course course : courses)
    {
      if (course.getCourseDetail().getPublishStatus().equals(PublishStatus.PUBLISHED)
      && course.getCourseDetail().getDateInfo().getPublishDate() != null)
      {
        LocalDateTime publishDate = course.getCourseDetail().getDateInfo().getPublishDate();
        String hour = String.valueOf(publishDate.getHour());
        String minute = String.valueOf(publishDate.getMinute());
        Instant instant = publishDate.atZone(ZoneId.of("Asia/Ulaanbaatar")).toInstant();
        calendarEvents.add(
            new CalendarEvent(
                course.getCourseDetail().getTitle(),
                course.getCourseDetail().getPublishStatus().name(),
                course.getCourseId().getId(),
                hour + ":" + minute,
                Date.from(instant),
                "online-course"
            )
        );
      }
    }
  }

  private CourseCalendarDto toCourseCalendarDto(List<CalendarEvent> calendarEvents)
  {
    Map<Date, List<CalendarEvent>> eventsPerDay = new HashMap<>();
    for (CalendarEvent event : calendarEvents)
    {
      if (eventsPerDay.get(event.getDate()) == null)
      {
        eventsPerDay.put(event.getDate(), Collections.singletonList(event));
      }
      else
      {
        List<CalendarEvent> events = new ArrayList<>(eventsPerDay.get(event.getDate()));
        events.add(event);
        eventsPerDay.put(event.getDate(), events);
      }
    }

    Set<CalendarDayPropertiesDto> calendarDays = new HashSet<>();

    for (Map.Entry<Date, List<CalendarEvent>> maps : eventsPerDay.entrySet())
    {
      Set<CalendarEventDto> calendarEventDtos = new HashSet<>();
      for (CalendarEvent calendarEvent : maps.getValue())
      {
        calendarEventDtos.add(
            new CalendarEventDto(calendarEvent.getTitle(), calendarEvent.getState(),
                calendarEvent.getCourseId(), calendarEvent.getStartTime(), calendarEvent.getEventType()));
      }

      LocalDate localDate = maps.getKey().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
      CalendarDayPropertiesDto calendarDayPropertiesDto = new CalendarDayPropertiesDto(localDate.getMonth().name().toLowerCase(), localDate.getDayOfMonth(),
          calendarEventDtos);
      calendarDays.add(calendarDayPropertiesDto);
    }
    return new CourseCalendarDto(calendarDays);
  }
}
