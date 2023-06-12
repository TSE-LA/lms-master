/*
 * (C)opyright, 2019, ERIN SYSTEMS LLC
 * The source code is protected by copyright laws and international copyright
 * treaties, as well as other intellectual property laws and treaties.
 * All rights reserved.
 */

package mn.erin.lms.legacy.domain.lms.model.content;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import mn.erin.domain.base.model.Entity;
import mn.erin.lms.legacy.domain.lms.model.course.CourseId;

/**
 * @author Oyungerel Chuluunsukh.
 */
public class CourseContent implements Entity<CourseContent> {
    private final CourseContentId id;
    private final CourseId courseId;
    private List<CourseModule> modulesList = new ArrayList<>();
    private List<Attachment> attachmentsList = new ArrayList<>();
    private List<Attachment> additionalFileList = new ArrayList<>();


    public CourseContent(CourseContentId id, CourseId courseId) {
        this.id = Objects.requireNonNull(id, "Course content id cannot be null!");
        this.courseId = Objects.requireNonNull(courseId, "Course id cannot be null!");
    }

    public List<CourseModule> getModulesList() {
        return modulesList;
    }

    public CourseContentId getId() {
        return id;
    }

    public void addModule(CourseModule module) {
        if (module != null && !this.modulesList.contains(module)) {
            this.modulesList.add(module);
        }
    }

    @Override
    public boolean sameIdentityAs(CourseContent other) {
        return other != null && (this.id.equals(other.id));
    }

    public CourseId getCourseId() {
        return courseId;
    }

    public List<Attachment> getAttachmentsList() {
        return attachmentsList;
    }

    public void setAttachmentsList(Attachment attachmentsList) {
        if (attachmentsList != null && !this.additionalFileList.contains(attachmentsList))
        {
            this.attachmentsList.add(attachmentsList);
        }
    }

    public List<Attachment> getAdditionalFileList() {
        return additionalFileList;
    }

    public void setAdditionalFileList(Attachment additionalFileList) {
        if (additionalFileList != null && !this.additionalFileList.contains(additionalFileList))
        {
            this.additionalFileList.add(additionalFileList);
        }
    }
}
