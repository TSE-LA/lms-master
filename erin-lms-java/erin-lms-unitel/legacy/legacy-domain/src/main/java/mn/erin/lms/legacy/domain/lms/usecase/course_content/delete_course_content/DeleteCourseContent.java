/*
 * (C)opyright, 2018, ERIN SYSTEMS LLC
 * The source code is protected by copyright laws and international copyright
 * treaties, as well as other intellectual property laws and treaties.
 * All rights reserved.
 */

package mn.erin.lms.legacy.domain.lms.usecase.course_content.delete_course_content;

import java.util.Objects;

import org.apache.commons.lang3.Validate;

import mn.erin.domain.base.usecase.UseCase;
import mn.erin.lms.legacy.domain.lms.model.course.CourseId;
import mn.erin.lms.legacy.domain.lms.repository.CourseContentRepository;

/**
 * author Tamir Batmagnai.
 */
public class DeleteCourseContent implements UseCase<DeleteCourseContentInput, DeleteCourseContentOutput>
{

  private final CourseContentRepository contentRepository;

  public DeleteCourseContent(CourseContentRepository contentRepository)
  {
    this.contentRepository = Objects.requireNonNull(contentRepository, "Course content repository cannot be null!");
  }

  @Override
  public DeleteCourseContentOutput execute(DeleteCourseContentInput input)
  {
    Validate.notNull(input, "Delete course content input cannot be null!");
    Validate.notNull(input.getCourseId(), "Course content id cannot be null!");

    boolean isDeleted = false;

    isDeleted = contentRepository.deleteById(new CourseId(input.getCourseId()));

    return new DeleteCourseContentOutput(isDeleted);
  }
}
