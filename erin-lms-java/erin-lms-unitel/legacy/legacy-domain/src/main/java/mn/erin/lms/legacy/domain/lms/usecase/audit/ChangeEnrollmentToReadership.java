package mn.erin.lms.legacy.domain.lms.usecase.audit;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.Validate;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import mn.erin.domain.base.usecase.UseCase;
import mn.erin.domain.base.usecase.UseCaseException;
import mn.erin.lms.legacy.domain.lms.model.course.Course;
import mn.erin.lms.legacy.domain.lms.model.course.PublishStatus;
import mn.erin.lms.legacy.domain.lms.model.enrollment.CourseEnrollment;
import mn.erin.lms.legacy.domain.lms.repository.CourseAuditRepository;
import mn.erin.lms.legacy.domain.lms.repository.CourseEnrollmentRepository;
import mn.erin.lms.legacy.domain.lms.repository.CourseRepository;
import mn.erin.lms.legacy.domain.lms.usecase.audit.dto.EnrollmentToReaderShipInput;

/**
 * @author Galsan Bayart
 */

public class ChangeEnrollmentToReadership implements UseCase<EnrollmentToReaderShipInput, Boolean>
{
  private static final Logger LOGGER = LoggerFactory.getLogger(ChangeEnrollmentToReadership.class);

  private final CourseEnrollmentRepository courseEnrollmentRepository;
  private final CourseAuditRepository courseAuditRepository;
  private final CourseRepository courseRepository;

  public ChangeEnrollmentToReadership(
      CourseEnrollmentRepository courseEnrollmentRepository, CourseAuditRepository courseAuditRepository,
      CourseRepository courseRepository)
  {
    this.courseEnrollmentRepository = courseEnrollmentRepository;
    this.courseAuditRepository = courseAuditRepository;
    this.courseRepository = courseRepository;
  }

  @Override
  public Boolean execute(@NotNull EnrollmentToReaderShipInput input) throws UseCaseException
  {
    Validate.notNull(input.getGroupId());
    Validate.notNull(input.getStartDate());
    Validate.notNull(input.getEndDate());
    Validate.notNull(input.getState());

    String newState = input.getState();
    if (input.getEndDate().before(input.getStartDate()))
    {
      Date tmp = input.getStartDate();
      input.setStartDate(input.getEndDate());
      input.setEndDate(tmp);
    }

    Date startDate = Date.from(convertToLocalDate(input.getStartDate()).atStartOfDay(ZoneId.systemDefault()).toInstant());
    Date endDate = Date.from(convertToLocalDate(input.getEndDate()).atTime(LocalTime.MAX).atZone(ZoneId.systemDefault()).toInstant());

    List<Course> courses;
    if (!newState.equals("all"))
    {
      courses = courseRepository.getCourseList(PublishStatus.PUBLISHED, startDate, endDate, newState);
    }
    else
    {
      courses = courseRepository.getCourseList(startDate, endDate);
    }
    if (courses.isEmpty())
    {
      LOGGER.info("Any course created in given date interval");
      return false;
    }

    List<CourseEnrollment> enrollments = courseEnrollmentRepository
        .getEnrollments(courses.stream().map(Course::getCourseId).collect(
            Collectors.toSet()));

    if (enrollments.isEmpty())
    {
      LOGGER.info("[{}] courses created in given date interval. But any enrollment created", courses.size());
      return false;
    }

    int errorCounter = 0;
    for (CourseEnrollment enrollment : enrollments)
    {
      boolean isDeleted = courseEnrollmentRepository.deleteEnrollment(enrollment.getCourseId(), enrollment.getLearnerId());
      if (isDeleted)
      {
        courseAuditRepository.create(enrollment.getCourseId(), enrollment.getLearnerId());
      }
      else
      {
        errorCounter++;
        LOGGER.error("Couldn't delete enrollment with CourseID = [{}] & LearnerId = [{}]", enrollment.getCourseId(), enrollment.getLearnerId());
      }
    }
    if (errorCounter == 0)
    {
      LOGGER.info("[{}] course's [{}] enrollments changed into readership. All enrollments successfully changed", courses.size(), enrollments.size());
    }
    else
    {
      LOGGER.info("[{}] enrollments couldn't changed into readership. You can find it from log", errorCounter);
    }
    return true;
  }

  public LocalDate convertToLocalDate(Date dateToConvert)
  {
    return dateToConvert.toInstant()
        .atZone(ZoneId.systemDefault())
        .toLocalDate();
  }
}
