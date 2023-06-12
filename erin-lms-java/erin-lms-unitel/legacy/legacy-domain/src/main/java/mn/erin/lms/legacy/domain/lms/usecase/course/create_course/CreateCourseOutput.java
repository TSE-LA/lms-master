/*
 * (C)opyright, 2019, ERIN SYSTEMS LLC
 * The source code is protected by copyright laws and international copyright
 * treaties, as well as other intellectual property laws and treaties.
 * All rights reserved.
 */

package mn.erin.lms.legacy.domain.lms.usecase.course.create_course;

public class CreateCourseOutput
{
  private final String id;

  public CreateCourseOutput(String id)
  {
    this.id = id;
  }

  public String getId()
  {
    return id;
  }
}
