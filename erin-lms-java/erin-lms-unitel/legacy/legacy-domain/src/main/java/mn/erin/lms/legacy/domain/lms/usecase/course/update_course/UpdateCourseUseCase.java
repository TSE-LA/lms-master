/*
 * (C)opyright, 2019, ERIN SYSTEMS LLC
 * The source code is protected by copyright laws and international copyright
 * treaties, as well as other intellectual property laws and treaties.
 * All rights reserved.
 */

package mn.erin.lms.legacy.domain.lms.usecase.course.update_course;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import mn.erin.domain.base.usecase.UseCaseException;
import mn.erin.lms.legacy.domain.lms.model.course.Course;
import mn.erin.lms.legacy.domain.lms.model.course.CourseCategoryId;
import mn.erin.lms.legacy.domain.lms.model.course.CourseDetail;
import mn.erin.lms.legacy.domain.lms.model.course.CourseId;
import mn.erin.lms.legacy.domain.lms.repository.CourseRepository;
import mn.erin.lms.legacy.domain.lms.repository.LMSRepositoryException;
import mn.erin.lms.legacy.domain.lms.usecase.course.CourseUseCase;
import mn.erin.lms.legacy.domain.lms.usecase.course.get_course.GetCourseOutput;

/**
 * @author Bat-Erdene Tsogoo.
 */
public abstract class UpdateCourseUseCase<I, O> extends CourseUseCase<I, O>
{
  protected static final Logger LOGGER = LoggerFactory.getLogger(UpdateCourseUseCase.class);
  private static final String FAILED_TO_UPDATE_COURSE_FULLY = "Failed to update course fully!";
  private static final String FAILED_TO_UPDATE_COURSE_DETAIL = "Failed to update course detail!";

  UpdateCourseUseCase(CourseRepository courseRepository)
  {
    super(courseRepository);
  }

  protected GetCourseOutput updateCourse(CourseId courseId, CourseDetail courseDetail) throws UseCaseException
  {
    try
    {
      Course updatedCourse = courseRepository.update(courseId, courseDetail);
      return getCourseOutput(updatedCourse);
    }
    catch (LMSRepositoryException e)
    {
      LOGGER.error(FAILED_TO_UPDATE_COURSE_DETAIL);
      throw new UseCaseException(FAILED_TO_UPDATE_COURSE_DETAIL, e);
    }
  }

  protected GetCourseOutput updateCourseFully(CourseId courseId, CourseCategoryId categoryId, CourseDetail courseDetail) throws UseCaseException
  {
    try
    {
      Course updatedCourse = courseRepository.update(courseId, categoryId, courseDetail);
      return getCourseOutput(updatedCourse);
    }
    catch (LMSRepositoryException e)
    {
      LOGGER.error(FAILED_TO_UPDATE_COURSE_FULLY);
      throw new UseCaseException(FAILED_TO_UPDATE_COURSE_FULLY, e);
    }
  }
}
