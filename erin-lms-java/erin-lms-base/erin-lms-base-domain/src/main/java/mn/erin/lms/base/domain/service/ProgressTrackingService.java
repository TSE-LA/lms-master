package mn.erin.lms.base.domain.service;

import java.util.Map;

/**
 * @author Bat-Erdene Tsogoo.
 */
public interface ProgressTrackingService
{
  /**
   * Save or track learner's course data
   *
   * @param learnerId  The ID of the learner
   * @param courseId   The ID of the course
   * @param moduleName The name of the course module
   * @param data       Analytic data
   * @return saved data
   */
  Map<String, Object> saveLearnerData(String learnerId, String courseId, String moduleName, Map<String, Object> data);

  /**
   * Fetches the learner progress on a given course
   *
   * @param learnerId The ID of the learner
   * @param courseId  The ID of the course
   * @return the progress, in range between 0 to 100.
   */
  Float getLearnerProgress(String learnerId, String courseId);

  /**
   * Deletes learner's course data
   *
   * @param learnerId The ID of the learner
   * @param courseId  The ID of the course
   * @return true if deleted; otherwise false
   */
  boolean deleteLearnerData(String learnerId, String courseId);

  /**
   * Deletes learner's all data
   *
   * @param learnerId The ID of the learner
   * @return true if deleted; otherwise false
   */
  boolean deleteLearnerData(String learnerId);

  /**
   * Deletes all track data associated with a course
   *
   * @param courseId The ID of the course
   * @return true if deleted; otherwise false
   */
  boolean deleteCourseData(String courseId);
}
