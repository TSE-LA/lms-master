/*
 * (C)opyright, 2018, ERIN SYSTEMS LLC
 * The source code is protected by copyright laws and international copyright
 * treaties, as well as other intellectual property laws and treaties.
 * All rights reserved.
 */

package mn.erin.lms.legacy.domain.lms.usecase.course.unpublish_course;

import java.util.Objects;

import mn.erin.domain.base.usecase.UseCaseException;
import mn.erin.lms.legacy.domain.lms.model.content.CourseContentId;
import mn.erin.lms.legacy.domain.lms.model.course.Course;
import mn.erin.lms.legacy.domain.lms.model.course.CourseId;
import mn.erin.lms.legacy.domain.lms.model.course.PublishStatus;
import mn.erin.lms.legacy.domain.lms.repository.CourseAuditRepository;
import mn.erin.lms.legacy.domain.lms.repository.CourseEnrollmentRepository;
import mn.erin.lms.legacy.domain.lms.repository.CourseRepository;
import mn.erin.lms.legacy.domain.lms.repository.LMSRepositoryException;
import mn.erin.lms.legacy.domain.lms.usecase.course.CourseUseCase;
import mn.erin.lms.legacy.domain.lms.usecase.course.get_course.GetCourseInput;

/**
 * author Tamir Batmagnai.
 */
public class HideCourse extends CourseUseCase<GetCourseInput, HideCourseOutput>
{
  private static final String INPUT_IS_NULL = "Null input while unpublish course!";
  private static final String COURSE_HAS_UNPUBLISHED_STATUS = "This course has unpublished status, must be published status!";

  private static final String NULL_COURSE_RETURNS = "Null course returned after update course status!";
  private static final String COURSE_DOES_NOT_EXIST = "Course does not exist with ID[%s]";

  private CourseEnrollmentRepository enrollmentRepository;
  private CourseAuditRepository courseAuditRepository;

  public HideCourse(CourseRepository courseRepository, CourseEnrollmentRepository courseEnrollmentRepository,
      CourseAuditRepository courseAuditRepository)
  {
    super(courseRepository);
    this.enrollmentRepository = Objects.requireNonNull(courseEnrollmentRepository, "CourseEnrollmentRepository cannot be null!");
    this.courseAuditRepository = Objects.requireNonNull(courseAuditRepository, "CourseAuditRepositoyr cannot be null!");
  }

  @Override
  public HideCourseOutput execute(GetCourseInput input) throws UseCaseException
  {
    if (null == input)
    {
      throw new UseCaseException(INPUT_IS_NULL);
    }

    CourseId courseId = new CourseId(input.getCourseId());

    try
    {
      Course course = courseRepository.getCourse(courseId);

      if (null == course)
      {
        throw new UseCaseException(NULL_COURSE_RETURNS);
      }
      if (!isPublished(course))
      {
        throw new UseCaseException(COURSE_HAS_UNPUBLISHED_STATUS);
      }

      CourseContentId courseContentId = course.getCourseContentId();

      if (null == courseContentId)
      {
        throw new UseCaseException("Updated course's content id is null!");
      }

      boolean isDeleted = deleteEnrollments(courseId.getId());
      if (!isDeleted)
      {
        throw new UseCaseException(String.format("Failed to delete enrollments by course id [%s]!", courseId.getId()));
      }

      courseRepository.update(courseId, PublishStatus.UNPUBLISHED);
      courseAuditRepository.delete(courseId);

      return new HideCourseOutput(true, courseContentId.getId());
    }
    catch (LMSRepositoryException e)
    {
      throw new UseCaseException("Failed to hide course : " + e.getMessage(), e);
    }
  }

  private boolean isPublished(Course course) throws UseCaseException
  {

    if (null == course)
    {
      throw new UseCaseException(String.format(COURSE_DOES_NOT_EXIST, course.getCourseId().getId()));
    }

    PublishStatus status = course.getCourseDetail().getPublishStatus();

    return status.equals(PublishStatus.PUBLISHED);
  }

  private boolean deleteEnrollments(String courseId)
  {
    return enrollmentRepository.deleteEnrollments(new CourseId(courseId));
  }
}
