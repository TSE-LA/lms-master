/*
 * (C)opyright, 2019, ERIN SYSTEMS LLC
 * The source code is protected by copyright laws and international copyright
 * treaties, as well as other intellectual property laws and treaties.
 * All rights reserved.
 */

package mn.erin.lms.legacy.domain.lms.usecase.course.update_course;

import java.io.Serializable;
import java.util.Map;

import org.apache.commons.lang3.Validate;

import mn.erin.domain.base.usecase.UseCaseException;
import mn.erin.lms.legacy.domain.lms.model.course.Course;
import mn.erin.lms.legacy.domain.lms.model.course.CourseDetail;
import mn.erin.lms.legacy.domain.lms.model.course.CourseId;
import mn.erin.lms.legacy.domain.lms.repository.CourseRepository;
import mn.erin.lms.legacy.domain.lms.usecase.course.get_course.GetCourseOutput;

/**
 * @author Bat-Erdene Tsogoo.
 */
public class UpdateCourseProperties extends UpdateCourseUseCase<UpdateCoursePropertiesInput, GetCourseOutput>
{
  public UpdateCourseProperties(CourseRepository courseRepository)
  {
    super(courseRepository);
  }

  @Override
  public GetCourseOutput execute(UpdateCoursePropertiesInput input) throws UseCaseException
  {
    Validate.notNull(input, "Input is required to update course properties!");

    CourseId courseId = new CourseId(input.getCourseId());
    Course course = getCourse(courseId);
    CourseDetail courseDetail = course.getCourseDetail();

    Map<String, Object> properties = input.getCourseProperties();

    for (Map.Entry<String, Object> property : properties.entrySet())
    {
      courseDetail.addProperty(property.getKey(), (Serializable) property.getValue());
    }

    return updateCourse(courseId, courseDetail);
  }
}
