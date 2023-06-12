/*
 * (C)opyright, 2018, ERIN SYSTEMS LLC
 * The source code is protected by copyright laws and international copyright
 * treaties, as well as other intellectual property laws and treaties.
 * All rights reserved.
 */

package mn.erin.lms.legacy.domain.lms.usecase.course_content.get_course_content;

import java.util.Objects;

/**
 * author Tamir Batmagnai.
 */
public class GetCourseContentInput
{
  private final String courseId;

  public GetCourseContentInput(String courseId)
  {
    this.courseId = Objects.requireNonNull(courseId);
  }

  public String getCourseId()
  {
    return courseId;
  }
}
