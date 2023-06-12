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
public class DeleteCourseContentOutput
{
  private final boolean isDeleted;

  public DeleteCourseContentOutput(boolean isDeleted)
  {
    this.isDeleted = Objects.requireNonNull(isDeleted, "Delete course content output must be boolean!");
  }

  public boolean isDeleted()
  {
    return isDeleted;
  }
}
