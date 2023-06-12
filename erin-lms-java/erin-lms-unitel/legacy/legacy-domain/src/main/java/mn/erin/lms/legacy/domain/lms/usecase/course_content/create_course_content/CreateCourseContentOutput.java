/*
 * (C)opyright, 2018, ERIN SYSTEMS LLC
 * The source code is protected by copyright laws and international copyright
 * treaties, as well as other intellectual property laws and treaties.
 * All rights reserved.
 */

package mn.erin.lms.legacy.domain.lms.usecase.course_content.create_course_content;

import java.util.Objects;

/**
 * author Tamir Batmagnai.
 */
public class CreateCourseContentOutput
{
  private String courseContentId;
  private String courseId;

  public CreateCourseContentOutput(String courseContentId, String courseId)
  {
    this.courseContentId = Objects.requireNonNull(courseContentId);
    this.courseId = Objects.requireNonNull(courseId);
  }

  public String getCourseContentId()
  {
    return courseContentId;
  }

  public String getCourseId()
  {
    return courseId;
  }
}
