/*
 * (C)opyright, 2018, ERIN SYSTEMS LLC
 * The source code is protected by copyright laws and international copyright
 * treaties, as well as other intellectual property laws and treaties.
 * All rights reserved.
 */

package mn.erin.lms.legacy.infrastructure.lms.rest.course_content.rest_entities;

import java.util.List;

/**
 * author Tamir Batmagnai.
 */
public class RestUpdatedContent {
    private String courseId;
    private List<RestUpdatedModule> modules;
    private List<RestAttachment> attachments;
    private List<RestAttachment> additionalFiles;

    public RestUpdatedContent() {
    }

    public RestUpdatedContent(String courseId, List<RestUpdatedModule> modules, List<RestAttachment> attachments, List<RestAttachment> additionalFiles) {
        this.courseId = courseId;
        this.modules = modules;
        this.attachments = attachments;
        this.additionalFiles = additionalFiles;
    }

    public String getCourseId() {
        return courseId;
    }

    public List<RestUpdatedModule> getModules() {
        return modules;
    }

    public void setCourseId(String courseId) {
        this.courseId = courseId;
    }

    public void setModules(List<RestUpdatedModule> modules) {
        this.modules = modules;
    }

  public List<RestAttachment> getAttachments() {
    return attachments;
  }

  public void setAttachments(List<RestAttachment> attachments) {
    this.attachments = attachments;
  }

  public List<RestAttachment> getAdditionalFiles() {
    return additionalFiles;
  }

  public void setAdditionalFiles(List<RestAttachment> additionalFiles) {
    this.additionalFiles = additionalFiles;
  }
}
