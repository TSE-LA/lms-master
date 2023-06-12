/*
 * (C)opyright, 2018, ERIN SYSTEMS LLC
 * The source code is protected by copyright laws and international copyright
 * treaties, as well as other intellectual property laws and treaties.
 * All rights reserved.
 */

package mn.erin.lms.legacy.domain.lms.usecase.course_content.get_course_content;

import java.util.List;
import java.util.Objects;

import mn.erin.lms.legacy.domain.lms.model.content.Attachment;
import mn.erin.lms.legacy.domain.lms.usecase.course_content.ContentModuleInfo;

/**
 * author Tamir Batmagnai.
 */
public class GetCourseContentOutput
{
  private String courseId;
  private List<Attachment> attachments;
  private List<Attachment> additionalFiles;
  private List<ContentModuleInfo> courseModules;

  public GetCourseContentOutput()
  {

  }

  public GetCourseContentOutput(String courseId, List<ContentModuleInfo> courseModules)
  {
    this.courseId = Objects.requireNonNull(courseId);
    this.courseModules = Objects.requireNonNull(courseModules);
  }

  public GetCourseContentOutput(String courseId, List<Attachment> attachments, List<Attachment> additionalFiles, List<ContentModuleInfo> courseModules)
  {
    this.courseId = Objects.requireNonNull(courseId);
    this.attachments = attachments;
    this.additionalFiles = additionalFiles;
    this.courseModules = Objects.requireNonNull(courseModules);
  }

  public String getCourseId()
  {
    return courseId;
  }

  public void setCourseId(String courseId)
  {
    this.courseId = courseId;
  }

  public List<ContentModuleInfo> getCourseModules()
  {
    return courseModules;
  }

  public void setCourseModules(List<ContentModuleInfo> courseModules)
  {
    this.courseModules = courseModules;
  }

  public List<Attachment> getAttachments()
  {
    return attachments;
  }

  public void setAttachments(List<Attachment> attachments)
  {
    this.attachments = attachments;
  }

  public List<Attachment> getAdditionalFiles()
  {
    return additionalFiles;
  }

  public void setAdditionalFiles(List<Attachment> additionalFiles)
  {
    this.additionalFiles = additionalFiles;
  }
}
