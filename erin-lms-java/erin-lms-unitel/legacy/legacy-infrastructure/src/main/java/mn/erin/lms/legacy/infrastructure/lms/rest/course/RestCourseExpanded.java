/*
 * (C)opyright, 2018, ERIN SYSTEMS LLC
 * The source code is protected by copyright laws and international copyright
 * treaties, as well as other intellectual property laws and treaties.
 * All rights reserved.
 */

package mn.erin.lms.legacy.infrastructure.lms.rest.course;

import mn.erin.lms.legacy.infrastructure.lms.rest.course_content.rest_entities.RestCourseContent;

/**
 * author Tamir Batmagnai.
 */
public class RestCourseExpanded
{
  private RestCourse restCourse;
  private RestCourseContent courseContent;

  public RestCourseExpanded()
  {
  }

  public RestCourseExpanded(RestCourse restCourse, RestCourseContent courseContent)
  {
    this.restCourse = restCourse;
    this.courseContent = courseContent;
  }

  public RestCourse getRestCourse()
  {
    return restCourse;
  }

  public void setRestCourse(RestCourse restCourse)
  {
    this.restCourse = restCourse;
  }

  public RestCourseContent getCourseContent()
  {
    return courseContent;
  }

  public void setCourseContent(RestCourseContent courseContent)
  {
    this.courseContent = courseContent;
  }

}
