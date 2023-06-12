/*
 * (C)opyright, 2019, ERIN SYSTEMS LLC
 * The source code is protected by copyright laws and international copyright
 * treaties, as well as other intellectual property laws and treaties.
 * All rights reserved.
 */

package mn.erin.lms.legacy.domain.lms.usecase.course.delete_course;

import org.apache.commons.lang3.Validate;

/**
 * @author Oyungerel Chuluunsukh.
 */
public class DeleteCourseInput
{
  private final String courseId;

  public DeleteCourseInput(String courseId)
  {
    this.courseId = Validate.notBlank(courseId, "Course id cannot be blank!");
  }

  public String getCourseId()
  {
    return this.courseId;
  }
}
