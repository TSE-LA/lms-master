/*
 * (C)opyright, 2018, ERIN SYSTEMS LLC
 * The source code is protected by copyright laws and international copyright
 * treaties, as well as other intellectual property laws and treaties.
 * All rights reserved.
 */

package mn.erin.lms.legacy.domain.lms.usecase.course_content.delete_course_content;

import java.util.Objects;

/**
 * author Tamir Batmagnai.
 */
public class DeleteCourseContentInput
{
  private final String courseId;

  public DeleteCourseContentInput(String courseId)
  {
    this.courseId = Objects.requireNonNull(courseId, "Course id cannot be null!");
  }

  public String getCourseId()
  {
    return courseId;
  }
}
