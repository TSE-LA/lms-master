/*
 * (C)opyright, 2019, ERIN SYSTEMS LLC
 * The source code is protected by copyright laws and international copyright
 * treaties, as well as other intellectual property laws and treaties.
 * All rights reserved.
 */

package mn.erin.lms.legacy.domain.lms.usecase.course.get_course;

import org.apache.commons.lang3.Validate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import mn.erin.domain.base.usecase.UseCaseException;
import mn.erin.lms.legacy.domain.lms.model.course.Course;
import mn.erin.lms.legacy.domain.lms.model.course.CourseId;
import mn.erin.lms.legacy.domain.lms.repository.CourseRepository;
import mn.erin.lms.legacy.domain.lms.repository.LMSRepositoryException;
import mn.erin.lms.legacy.domain.lms.usecase.course.CourseUseCase;

/**
 * @author Oyungerel Chuluunsukh.
 */
public class GetCourse extends CourseUseCase<GetCourseInput, GetCourseOutput>
{
  private static final Logger LOGGER = LoggerFactory.getLogger(GetCourse.class);

  public GetCourse(CourseRepository courseRepository)
  {
    super(courseRepository);
  }

  @Override
  public GetCourseOutput execute(GetCourseInput input) throws UseCaseException
  {
    Validate.notNull(input, "Input is required to get a course");

    try
    {
      CourseId courseId = new CourseId(input.getCourseId());
      Course course = courseRepository.getCourse(courseId);

      return getCourseOutput(course);
    }
    catch (LMSRepositoryException e)
    {
      LOGGER.error(e.getMessage(), e);
      throw new UseCaseException("Failed to get a course with the ID: [" + input.getCourseId() + "]", e);
    }
  }
}
