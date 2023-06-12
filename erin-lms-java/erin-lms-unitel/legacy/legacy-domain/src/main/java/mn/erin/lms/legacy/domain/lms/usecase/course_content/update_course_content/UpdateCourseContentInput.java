/*
 * (C)opyright, 2018, ERIN SYSTEMS LLC
 * The source code is protected by copyright laws and international copyright
 * treaties, as well as other intellectual property laws and treaties.
 * All rights reserved.
 */

package mn.erin.lms.legacy.domain.lms.usecase.course_content.update_course_content;

import java.util.List;

import mn.erin.lms.legacy.domain.lms.model.content.Attachment;
import mn.erin.lms.legacy.domain.lms.usecase.course_content.ContentModuleInfo;

/**
 * author Tamir Batmagnai.
 */
public class UpdateCourseContentInput
{
  private String courseId;
  private List<ContentModuleInfo> modules;
  private List<Attachment> attachments;
  private List<Attachment> additionalFiles;

  public UpdateCourseContentInput(String courseId, List<ContentModuleInfo> modules, List<Attachment> attachments, List<Attachment> additionalFiles)
  {
    this.courseId = courseId;
    this.modules = modules;
    this.attachments = attachments;
    this.additionalFiles = additionalFiles;
  }

  public String getCourseId()
  {
    return courseId;
  }

  public List<ContentModuleInfo> getModules()
  {
    return modules;
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
