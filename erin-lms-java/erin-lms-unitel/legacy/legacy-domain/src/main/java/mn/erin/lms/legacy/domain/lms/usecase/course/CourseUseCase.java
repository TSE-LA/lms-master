/*
 * (C)opyright, 2019, ERIN SYSTEMS LLC
 * The source code is protected by copyright laws and international copyright
 * treaties, as well as other intellectual property laws and treaties.
 * All rights reserved.
 */

package mn.erin.lms.legacy.domain.lms.usecase.course;

import java.util.NoSuchElementException;
import java.util.Objects;

import mn.erin.domain.base.usecase.UseCase;
import mn.erin.lms.legacy.domain.lms.model.course.Course;
import mn.erin.lms.legacy.domain.lms.model.course.CourseDetail;
import mn.erin.lms.legacy.domain.lms.model.course.CourseId;
import mn.erin.lms.legacy.domain.lms.repository.CourseRepository;
import mn.erin.lms.legacy.domain.lms.repository.LMSRepositoryException;
import mn.erin.lms.legacy.domain.lms.usecase.course.get_course.GetCourseOutput;

/**
 * @author Bat-Erdene Tsogoo.
 */
public abstract class CourseUseCase<I, O> implements UseCase<I, O>
{
  protected final CourseRepository courseRepository;

  public CourseUseCase(CourseRepository courseRepository)
  {
    this.courseRepository = Objects.requireNonNull(courseRepository, "CourseRepository cannot be null!");
  }

  public static GetCourseOutput getCourseOutput(Course course)
  {
    CourseDetail detail = course.getCourseDetail();

    return new GetCourseOutput.Builder(course.getCourseId().getId())
        .withCategory(course.getCourseCategoryId().getId())
        .withContent(course.getCourseContentId() != null ? course.getCourseContentId().getId() : null)
        .withAuthor(course.getAuthorId().getId())
        .withTitle(detail.getTitle())
        .withDescription(detail.getDescription())
        .withNote(detail.getLatestNote())
        .withPublishStatus(detail.getPublishStatus().name())
        .withProperties(detail.getProperties())
        .withUserGroups(course.getUserGroup().getUsers(), course.getUserGroup().getGroups())
        .createdAt(detail.getCreatedDate())
        .modifiedAt(detail.getModifiedDate())
        .havingNotes(detail.getNotes())
        .build();
  }

  protected Course getCourse(CourseId courseId)
  {
    try
    {
      return courseRepository.getCourse(courseId);
    }
    catch (LMSRepositoryException e)
    {
      throw new NoSuchElementException("The course with the ID: [" + courseId + "] does not exist!");
    }
  }
}
