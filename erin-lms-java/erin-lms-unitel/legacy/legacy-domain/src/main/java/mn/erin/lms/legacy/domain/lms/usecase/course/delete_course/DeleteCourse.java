/*
 * (C)opyright, 2019, ERIN SYSTEMS LLC
 * The source code is protected by copyright laws and international copyright
 * treaties, as well as other intellectual property laws and treaties.
 * All rights reserved.
 */

package mn.erin.lms.legacy.domain.lms.usecase.course.delete_course;

import java.util.Map;
import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import mn.erin.domain.base.usecase.UseCase;
import mn.erin.domain.base.usecase.UseCaseException;
import mn.erin.lms.legacy.domain.lms.model.course.Course;
import mn.erin.lms.legacy.domain.lms.model.course.CourseId;
import mn.erin.lms.legacy.domain.lms.model.course.PublishStatus;
import mn.erin.lms.legacy.domain.lms.repository.CourseAssessmentRepository;
import mn.erin.lms.legacy.domain.lms.repository.CourseAuditRepository;
import mn.erin.lms.legacy.domain.lms.repository.CourseContentRepository;
import mn.erin.lms.legacy.domain.lms.repository.CourseEnrollmentRepository;
import mn.erin.lms.legacy.domain.lms.repository.CourseGroupRepository;
import mn.erin.lms.legacy.domain.lms.repository.CourseRepository;
import mn.erin.lms.legacy.domain.lms.repository.CourseTestRepository;
import mn.erin.lms.legacy.domain.lms.repository.LMSRepositoryException;
import mn.erin.lms.legacy.domain.lms.usecase.course_assessment.delete_assessment.DeleteAssessment;
import mn.erin.lms.legacy.domain.lms.usecase.course_assessment.delete_assessment.DeleteAssessmentInput;
import mn.erin.lms.legacy.domain.lms.usecase.course_content.delete_course_content.DeleteCourseContent;
import mn.erin.lms.legacy.domain.lms.usecase.course_content.delete_course_content.DeleteCourseContentInput;
import mn.erin.lms.legacy.domain.lms.usecase.course_content.delete_course_content.DeleteCourseContentOutput;
import mn.erin.lms.legacy.domain.lms.usecase.course_content.get_course_content.GetCourseContent;
import mn.erin.lms.legacy.domain.lms.usecase.course_content.get_course_content.GetCourseContentInput;
import mn.erin.lms.legacy.domain.lms.usecase.course_content.get_course_content.GetCourseContentOutput;
import mn.erin.lms.legacy.domain.lms.usecase.enrollment.delete_enrollement.DeleteCourseEnrollments;
import mn.erin.lms.legacy.domain.lms.usecase.enrollment.delete_enrollement.DeleteCourseEnrollmentsInput;
import mn.erin.lms.legacy.domain.lms.usecase.enrollment.delete_enrollement.DeleteCourseEnrollmentsOutput;

/**
 * @author Oyungerel Chuluunsukh.
 */
public class DeleteCourse implements UseCase<DeleteCourseInput, DeleteCourseOutput>
{
  private static final Logger LOGGER = LoggerFactory.getLogger(DeleteCourse.class);

  private final CourseRepository courseRepository;
  private CourseGroupRepository courseGroupRepository;
  private CourseAuditRepository courseAuditRepository;
  private GetCourseContent getCourseContent;
  private DeleteCourseContent deleteCourseContent;
  private DeleteCourseEnrollments deleteCourseEnrollments;
  private DeleteAssessment deleteAssessment;

  public DeleteCourse(CourseRepository courseRepository, CourseEnrollmentRepository enrollmentRepository,
      CourseContentRepository contentRepository, CourseAssessmentRepository courseAssessmentRepository,
      CourseTestRepository courseTestRepository, CourseGroupRepository courseGroupRepository, CourseAuditRepository courseAuditRepository)
  {
    this.courseRepository = Objects.requireNonNull(courseRepository, "CourseRepository cannot be null!");
    getCourseContent = new GetCourseContent(contentRepository);
    deleteCourseContent = new DeleteCourseContent(contentRepository);
    deleteCourseEnrollments = new DeleteCourseEnrollments(enrollmentRepository);
    deleteAssessment = new DeleteAssessment(courseAssessmentRepository, courseTestRepository);
    this.courseGroupRepository = courseGroupRepository;
    this.courseAuditRepository = courseAuditRepository;
  }

  @Override
  public DeleteCourseOutput execute(DeleteCourseInput input) throws UseCaseException
  {
    if (input == null)
    {
      throw new UseCaseException("Input is required to delete a course!");
    }
    CourseId courseId = new CourseId(input.getCourseId());
    Course course = getCourse(courseId);
    boolean isPublished = isCoursePublished(course);
    boolean hasTest = hasTest(course);

    if (hasTest)
    {
      boolean isDeleted = deleteAssessment.execute(new DeleteAssessmentInput(courseId.getId())).isDeleted();
      if (!isDeleted)
      {
        LOGGER.warn("COURSE ASSESSMENT OF THE COURSE WITH THE ID: {} WAS NOT DELETED", courseId.getId());
      }
    }

    if (isPublished)
    {
      DeleteCourseEnrollmentsOutput enrollmentOutput = deleteCourseEnrollments.execute(new DeleteCourseEnrollmentsInput(courseId.getId()));
      courseGroupRepository.delete(courseId);
      courseAuditRepository.delete(courseId);
      return enrollmentOutput.isDeleted() ? deleteCourse(courseId) : new DeleteCourseOutput(false);
    }

    courseGroupRepository.delete(courseId);
    courseAuditRepository.delete(courseId);
    return deleteCourse(courseId);
  }

  private DeleteCourseOutput deleteCourse(CourseId courseId) throws UseCaseException
  {
    try
    {
      GetCourseContentOutput courseContent = getCourseContent.execute(new GetCourseContentInput(courseId.getId()));

      if (courseContent == null)
      {
        return new DeleteCourseOutput(courseRepository.removeCourse(courseId));
      }
    }
    catch (UseCaseException e)
    {
      return new DeleteCourseOutput(courseRepository.removeCourse(courseId));
    }

    boolean contentDeleted;
    DeleteCourseContentOutput contentOutput = deleteCourseContent.execute(new DeleteCourseContentInput(courseId.getId()));
    contentDeleted = contentOutput.isDeleted();

    if (contentDeleted)
    {
      return new DeleteCourseOutput(courseRepository.removeCourse(courseId));
    }
    else
    {
      throw new UseCaseException("Could not delete course content!");
    }
  }

  private boolean isCoursePublished(Course course)
  {
    PublishStatus publishStatus = course.getCourseDetail().getPublishStatus();
    return publishStatus == PublishStatus.PUBLISHED;
  }

  private boolean hasTest(Course course)
  {
    Map<String, Object> courseProperties = course.getCourseDetail().getProperties();
    String hasTestField = "hasTest";
    return courseProperties.containsKey(hasTestField) && (boolean) courseProperties.get(hasTestField);
  }

  private Course getCourse(CourseId courseId) throws UseCaseException
  {
    try
    {
      return courseRepository.getCourse(courseId);
    }
    catch (LMSRepositoryException e)
    {
      throw new UseCaseException("The course with the with the ID: [" + courseId.getId() + "] does not exist!");
    }
  }

  public void setInnerUsecases(DeleteCourseContent deleteCourseContent, DeleteCourseEnrollments deleteCourseEnrollments)
  {
    this.deleteCourseContent = deleteCourseContent;
    this.deleteCourseEnrollments = deleteCourseEnrollments;
  }
}
