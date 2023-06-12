/*
 * (C)opyright, 2018, ERIN SYSTEMS LLC
 * The source code is protected by copyright laws and international copyright
 * treaties, as well as other intellectual property laws and treaties.
 * All rights reserved.
 */

package mn.erin.lms.legacy.domain.lms.usecase.course_content.create_course_content;

import java.util.List;
import java.util.Objects;

import mn.erin.domain.base.usecase.UseCase;
import mn.erin.domain.base.usecase.UseCaseException;
import mn.erin.lms.legacy.domain.lms.model.content.Attachment;
import mn.erin.lms.legacy.domain.lms.model.content.CourseContent;
import mn.erin.lms.legacy.domain.lms.model.content.CourseModule;
import mn.erin.lms.legacy.domain.lms.model.course.Course;
import mn.erin.lms.legacy.domain.lms.model.course.CourseId;
import mn.erin.lms.legacy.domain.lms.repository.CourseContentRepository;
import mn.erin.lms.legacy.domain.lms.repository.CourseRepository;
import mn.erin.lms.legacy.domain.lms.repository.LMSRepositoryException;
import mn.erin.lms.legacy.domain.lms.usecase.course_content.ContentModuleInfo;

import static mn.erin.lms.legacy.domain.lms.usecase.course_content.ContentUtils.toCourseModules;

/**
 * author Tamir Batmagnai.
 */
public class CreateCourseContent implements UseCase<CreateCourseContentInput, CreateCourseContentOutput>
{
  private final CourseContentRepository contentRepository;
  private final CourseRepository courseRepository;

  public CreateCourseContent(CourseContentRepository contentRepository, CourseRepository courseRepository)
  {
    this.contentRepository = Objects.requireNonNull(contentRepository);
    this.courseRepository = Objects.requireNonNull(courseRepository);
  }

  @Override
  public CreateCourseContentOutput execute(CreateCourseContentInput input) throws UseCaseException
  {
    if (null == input)
    {
      throw new UseCaseException("Create course input data cannot be null!");
    }

    String id = input.getCourseId();
    CourseId courseId = new CourseId(id);
    List<Attachment> additionalFileList;
    List<Attachment> attachmentList = input.getAttachmentList();
    if (input.getAdditionalFileList() == null)
    {
      additionalFileList = null;
    }
    else
    {
      additionalFileList = input.getAdditionalFileList();
    }

    List<ContentModuleInfo> modulesMap = input.getModules();

    try
    {
      Course course = getCourse(courseId);
      if (null == course)
      {
        throw new UseCaseException("Course doesn't exist with course ID [" + courseId + "]!");
      }

      List<CourseModule> courseModules = toCourseModules(modulesMap);
      CourseContent courseContent = contentRepository.create(courseId, courseModules, attachmentList, additionalFileList);

      return new CreateCourseContentOutput(courseContent.getId().getId(), courseId.getId()
      );
    }
    catch (LMSRepositoryException e)
    {
      throw new UseCaseException(e.getMessage(), e);
    }
  }

  private Course getCourse(CourseId courseId) throws UseCaseException
  {
    Course course = null;
    try
    {
      course = courseRepository.getCourse(courseId);
    }
    catch (Exception e)
    {
      throw new UseCaseException(e.getMessage(), e);
    }
    return course;
  }
}
