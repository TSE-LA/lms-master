package mn.erin.lms.base.domain.service;

import java.io.File;
import java.io.IOException;
import java.util.List;

import mn.erin.domain.base.model.Content;
import mn.erin.lms.base.domain.model.content.CourseModule;
import mn.erin.lms.base.domain.model.course.Course;

/**
 * @author Bat-Erdene Tsogoo.
 */
public interface ThumbnailService
{
  String getThumbnailUrl(String courseId);

  String getDefaultThumbnailUrl();

  /**
   * @deprecated
   * This method is no longer deemed valid.
   * Use updateThumbnailToGIF  instead.
   *
   * @param videoFile Video to cut
   * @return mp4 content
   */
  @Deprecated
  Content createThumbnailFromVideo(String videoFile);

  /**
   * Generate thumbnail from thumbnail name and updates the course thumbnail
   * @param course The updating course
   * @param thumbnailName Find thumbnail by name and generate from it
   */
  void updateThumbnailToGIF(Course course, String thumbnailName);

  /**
   * Generate thumbnail from given file and updates the course thumbnail
   * @param courseId The ID of the updating course
   * @param file Create thumbnail from given file
   */
  String updateThumbnailToGIF(String courseId, File file) throws IOException;

  void setThumbnailUrlFromCourseContents(List<CourseModule> modules, Course course, String courseFolderId);
}
