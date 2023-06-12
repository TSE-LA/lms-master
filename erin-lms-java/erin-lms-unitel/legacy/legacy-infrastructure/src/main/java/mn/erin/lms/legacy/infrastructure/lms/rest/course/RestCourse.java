/*
 * (C)opyright, 2019, ERIN SYSTEMS LLC
 * The source code is protected by copyright laws and international copyright
 * treaties, as well as other intellectual property laws and treaties.
 * All rights reserved.
 */

package mn.erin.lms.legacy.infrastructure.lms.rest.course;

/**
 * @author Bat-Erdene Tsogoo.
 */
public class RestCourse
{
  private String categoryId;
  private RestCourseDetail courseDetail;


  public String getCategoryId()
  {
    return categoryId;
  }

  public RestCourseDetail getCourseDetail()
  {
    return courseDetail;
  }

}
