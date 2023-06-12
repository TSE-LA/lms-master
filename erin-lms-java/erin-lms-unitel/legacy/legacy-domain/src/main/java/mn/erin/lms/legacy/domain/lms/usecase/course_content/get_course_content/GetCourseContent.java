/*
 * (C)opyright, 2018, ERIN SYSTEMS LLC
 * The source code is protected by copyright laws and international copyright
 * treaties, as well as other intellectual property laws and treaties.
 * All rights reserved.
 */

package mn.erin.lms.legacy.domain.lms.usecase.course_content.get_course_content;

import java.util.List;
import java.util.Objects;

import org.apache.commons.lang3.Validate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import mn.erin.domain.base.usecase.UseCase;
import mn.erin.domain.base.usecase.UseCaseException;
import mn.erin.lms.legacy.domain.lms.model.content.Attachment;
import mn.erin.lms.legacy.domain.lms.model.content.CourseContent;
import mn.erin.lms.legacy.domain.lms.model.content.CourseModule;
import mn.erin.lms.legacy.domain.lms.model.course.CourseId;
import mn.erin.lms.legacy.domain.lms.repository.CourseContentRepository;
import mn.erin.lms.legacy.domain.lms.repository.LMSRepositoryException;
import mn.erin.lms.legacy.domain.lms.usecase.course_content.ContentModuleInfo;

import static mn.erin.lms.legacy.domain.lms.usecase.course_content.ContentUtils.toModuleInfos;

/**
 * author Tamir Batmagnai.
 */
public class GetCourseContent implements UseCase<GetCourseContentInput, GetCourseContentOutput>
{
  private static final Logger LOGGER = LoggerFactory.getLogger(GetCourseContent.class);

  private static final String ERROR_MSG_FOR_INPUT = "Course content input data cannot be null!";
  private static final String ERR_MSG_GET_COURSE_CONTENT = "Failed to get course content with ID [%s]";

  private final CourseContentRepository contentRepository;

  public GetCourseContent(CourseContentRepository contentRepository)
  {
    this.contentRepository = Objects.requireNonNull(contentRepository);
  }

  @Override
  public GetCourseContentOutput execute(GetCourseContentInput input) throws UseCaseException
  {
    Validate.notNull(input, ERROR_MSG_FOR_INPUT);

    try
    {
      String id = input.getCourseId();
      CourseId courseId = new CourseId(id);

      CourseContent courseContent = contentRepository.get(courseId);
      return toCourseOutput(courseContent);
    }
    catch (LMSRepositoryException e)
    {
      LOGGER.error(e.getMessage(), e);
      throw new UseCaseException(String.format(ERR_MSG_GET_COURSE_CONTENT, input.getCourseId()), e);
    }
  }

  private GetCourseContentOutput toCourseOutput(CourseContent courseContent)
  {
    if (null == courseContent)
    {
      return null;
    }

    CourseId courseId = courseContent.getCourseId();

    List<Attachment> attachs = courseContent.getAttachmentsList();
    List<Attachment> additionalFiles = courseContent.getAdditionalFileList();
    List<CourseModule> courseModules = courseContent.getModulesList();
    List<ContentModuleInfo> moduleInfos = toModuleInfos(courseModules);

    return new GetCourseContentOutput(courseId.getId(), attachs, additionalFiles, moduleInfos);
  }
}
