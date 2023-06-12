/*
 * (C)opyright, 2018, ERIN SYSTEMS LLC
 * The source code is protected by copyright laws and international copyright
 * treaties, as well as other intellectual property laws and treaties.
 * All rights reserved.
 */

package mn.erin.lms.legacy.domain.lms.usecase.course_content.update_course_content;

import java.util.List;
import java.util.Objects;

import mn.erin.domain.base.usecase.UseCase;
import mn.erin.domain.base.usecase.UseCaseException;
import mn.erin.lms.legacy.domain.lms.model.content.Attachment;
import mn.erin.lms.legacy.domain.lms.model.content.CourseModule;
import mn.erin.lms.legacy.domain.lms.model.course.CourseId;
import mn.erin.lms.legacy.domain.lms.repository.CourseContentRepository;
import mn.erin.lms.legacy.domain.lms.repository.LMSRepositoryException;
import mn.erin.lms.legacy.domain.lms.usecase.course_content.ContentModuleInfo;
import mn.erin.lms.legacy.domain.lms.usecase.course_content.get_course_content.GetCourseContentOutput;

import static mn.erin.lms.legacy.domain.lms.usecase.course_content.ContentUtils.toCourseModules;

/**
 * author Tamir Batmagnai.
 */
public class UpdateCourseContent implements UseCase<UpdateCourseContentInput, GetCourseContentOutput>
{
  private final CourseContentRepository contentRepository;

  public UpdateCourseContent(CourseContentRepository contentRepository)
  {
    this.contentRepository = Objects.requireNonNull(contentRepository);
  }

  @Override
  public GetCourseContentOutput execute(UpdateCourseContentInput input) throws UseCaseException
  {
    if (null == input)
    {
      throw new UseCaseException("Update course input data cannot be null!");
    }

    String id = input.getCourseId();
    List<Attachment> additionalFiles;
    List<Attachment> attachments;
    if (input.getAdditionalFiles() == null)
    {
      additionalFiles = null;
    }
    else
    {
      additionalFiles = input.getAdditionalFiles();
    }
    if (input.getAttachments() == null)
    {
      attachments = null;
    }
    else
    {
      attachments = input.getAttachments();
    }
    List<ContentModuleInfo> modulesInfoList = input.getModules();
    List<CourseModule> courseModules = toCourseModules(modulesInfoList);

    try
    {
      CourseId courseId = new CourseId(id);
      contentRepository.update(courseId, courseModules, attachments, additionalFiles);

      return new GetCourseContentOutput(id, input.getAttachments(), input.getAdditionalFiles(), modulesInfoList);
    }
    catch (LMSRepositoryException e)
    {
      throw new UseCaseException(e.getMessage(), e);
    }
  }
}
