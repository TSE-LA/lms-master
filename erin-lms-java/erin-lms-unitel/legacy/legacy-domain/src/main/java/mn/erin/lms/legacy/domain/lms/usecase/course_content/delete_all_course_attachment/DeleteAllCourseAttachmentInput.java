package mn.erin.lms.legacy.domain.lms.usecase.course_content.delete_all_course_attachment;

import java.util.Objects;

/**
 * @author Erdenetulga
 */
public class DeleteAllCourseAttachmentInput {
    private final String courseId;

    public DeleteAllCourseAttachmentInput(String courseId) {
        this.courseId = Objects.requireNonNull(courseId, "Course id cannot be null!");
    }

    public String getCourseId() {
        return courseId;
    }
}
