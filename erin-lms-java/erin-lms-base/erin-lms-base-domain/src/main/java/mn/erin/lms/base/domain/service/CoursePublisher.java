package mn.erin.lms.base.domain.service;

/**
 * @author Bat-Erdene Tsogoo.
 */
public interface CoursePublisher
{

  /**
   * Publishes course content
   * @param courseId The ID of the publishing course
   * @return True if published, otherwise false
   */
  boolean publishCourseContent(String courseId);

  /**
   * Deletes SCORM content
   * @param courseContentId Deleting course content ID
   */
  void delete(String courseContentId);
}
