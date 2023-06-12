/*
 * (C)opyright, 2018, ERIN SYSTEMS LLC
 * The source code is protected by copyright laws and international copyright
 * treaties, as well as other intellectual property laws and treaties.
 * All rights reserved.
 */

package mn.erin.lms.legacy.domain.lms.usecase.course_content.create_course_content;

import java.util.List;
import java.util.Objects;

import mn.erin.lms.legacy.domain.lms.model.content.Attachment;
import mn.erin.lms.legacy.domain.lms.usecase.course_content.ContentModuleInfo;

/**
 * author Tamir Batmagnai.
 */
public class CreateCourseContentInput
{
  private final String courseId;
  private List<ContentModuleInfo> modules;
  private List<Attachment> attachmentList;
  private List<Attachment> additionalFileList;

  public CreateCourseContentInput(String courseId, List<ContentModuleInfo> modules)
  {
    this.courseId = Objects.requireNonNull(courseId);
    this.modules = Objects.requireNonNull(modules);
  }

  public CreateCourseContentInput(String id, List<ContentModuleInfo> modules, List<Attachment> attachmentList, List<Attachment> additionalList)
  {
    this.courseId = Objects.requireNonNull(id);
    this.modules = Objects.requireNonNull(modules);

    this.attachmentList = attachmentList;
    this.additionalFileList = additionalList;
  }

  public String getCourseId()
  {
    return courseId;
  }

  public List<ContentModuleInfo> getModules()
  {
    return modules;
  }

  public void setModules(List<ContentModuleInfo> modules)
  {
    this.modules = modules;
  }

  public List<Attachment> getAttachmentList() {
    return attachmentList;
  }

  public void setAttachmentList(List<Attachment> attachmentList) {
    this.attachmentList = attachmentList;
  }

  public List<Attachment> getAdditionalFileList() {
    return additionalFileList;
  }

  public void setAdditionalFileList(List<Attachment> additionalFileList) {
    this.additionalFileList = additionalFileList;
  }
}
