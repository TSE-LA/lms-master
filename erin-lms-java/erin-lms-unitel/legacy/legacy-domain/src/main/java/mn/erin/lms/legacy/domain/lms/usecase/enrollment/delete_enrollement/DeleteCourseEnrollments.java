/*
 * (C)opyright, 2019, ERIN SYSTEMS LLC
 * The source code is protected by copyright laws and international copyright
 * treaties, as well as other intellectual property laws and treaties.
 * All rights reserved.
 */

package mn.erin.lms.legacy.domain.lms.usecase.enrollment.delete_enrollement;

import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import mn.erin.domain.base.usecase.UseCase;
import mn.erin.lms.legacy.domain.lms.model.course.CourseId;
import mn.erin.lms.legacy.domain.lms.repository.CourseEnrollmentRepository;

/**
 * @author Oyungerel Chuluunsukh.
 */
public class DeleteCourseEnrollments implements UseCase<DeleteCourseEnrollmentsInput, DeleteCourseEnrollmentsOutput>
{
  private static final Logger LOGGER = LoggerFactory.getLogger(DeleteCourseEnrollments.class);
  private CourseEnrollmentRepository courseEnrollmentRepository;

  public DeleteCourseEnrollments(CourseEnrollmentRepository courseEnrollmentRepository)
  {
    this.courseEnrollmentRepository = Objects.requireNonNull(courseEnrollmentRepository, "CourseEnrollmentRepository cannot be null!");
  }

  @Override
  public DeleteCourseEnrollmentsOutput execute(DeleteCourseEnrollmentsInput input)
  {
    if (input == null)
    {
      return new DeleteCourseEnrollmentsOutput(false);
    }
    boolean deleted = courseEnrollmentRepository.deleteEnrollments(new CourseId(input.getCourseId()));
    LOGGER.info("Enrollment with id [" + input.getCourseId() + "]Successfully deleted");
    return new DeleteCourseEnrollmentsOutput(deleted);
  }
}
