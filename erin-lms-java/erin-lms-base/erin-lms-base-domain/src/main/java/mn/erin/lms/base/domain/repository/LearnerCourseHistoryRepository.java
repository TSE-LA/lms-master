package mn.erin.lms.base.domain.repository;

import java.time.LocalDateTime;
import java.util.List;

import mn.erin.lms.base.domain.model.course.LearnerCourseHistory;
import mn.erin.lms.base.domain.model.course.LearnerCourseHistoryId;

/**
 * @author Temuulen Naranbold
 */
public interface LearnerCourseHistoryRepository
{
  /**
   * Creates the learner course history by required info
   *
   * @param courseId       Course ID
   * @param userId         User ID
   * @param name           Course name or title
   * @param type           Course type(represents online or classroom)
   * @param credit         Course credit
   * @param completionDate Date of history
   * @param percentage     Online course progress percentage
   * @return Created history
   */
  LearnerCourseHistory create(String courseId, String userId, String name, String type, double credit, LocalDateTime completionDate, Float percentage);

  /**
   * Updates the learner course history by required info
   *
   * @param courseId       Course ID
   * @param userId         User ID
   * @param name           Course name or title
   * @param type           Course type(represents online or classroom)
   * @param credit         Course credit
   * @param completionDate Date of history
   * @param percentage     Online course progress percentage
   * @return Updated history
   */
  LearnerCourseHistory update(String courseId, String userId, String name, String type, double credit, LocalDateTime completionDate, Float percentage);

  /**
   * Gets by course and use IDs
   *
   * @param courseId Course ID
   * @param userId   User ID
   * @return Found history by given parameters
   */
  LearnerCourseHistory get(String courseId, String userId);

  /**
   * Gets history by its ID
   *
   * @param learnerCourseHistoryId LearnerCourseHistory ID
   * @return Found history
   */
  LearnerCourseHistory getById(LearnerCourseHistoryId learnerCourseHistoryId);

  /**
   * Get all by username
   *
   * @param username User name
   * @return All existing histories
   */
  List<LearnerCourseHistory> getAllByUserId(String username);

  /**
   * Get all by user ID and between given date
   *
   * @param userId    User ID
   * @param startDate Starting day of history
   * @param endDate   Ending day of history
   * @return Found histories
   */
  List<LearnerCourseHistory> getAllByUserIdAndDate(String userId, LocalDateTime startDate, LocalDateTime endDate);

  /**
   * Check exists by user and course ID
   *
   * @param courseId Course ID
   * @param userId   User ID
   * @return True if exists otherwise false
   */
  boolean exists(String courseId, String userId);
}
