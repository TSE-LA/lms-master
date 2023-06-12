/*
 * (C)opyright, 2019, ERIN SYSTEMS LLC
 * The source code is protected by copyright laws and international copyright
 * treaties, as well as other intellectual property laws and treaties.
 * All rights reserved.
 */

package mn.erin.lms.legacy.domain.lms.usecase.course.get_course_analytics;

import org.apache.commons.lang3.Validate;

/**
 * @author Bat-Erdene Tsogoo.
 */
public class GetCourseAnalyticsInput
{
  private final String courseId;
  private final Filter filter;

  public GetCourseAnalyticsInput(String courseId, Filter filter)
  {
    this.courseId = Validate.notBlank(courseId, "Course ID cannot be null!");
    this.filter = filter;
  }

  public String getCourseId()
  {
    return courseId;
  }

  public Filter getFilter()
  {
    return filter;
  }
}
