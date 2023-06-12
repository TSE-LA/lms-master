package mn.erin.lms.base.domain.repository;

import java.util.List;

import mn.erin.lms.base.domain.model.course.CourseId;
import mn.erin.lms.base.aim.user.LearnerId;
import mn.erin.lms.base.domain.model.classroom_course.Attendance;
import mn.erin.lms.base.domain.model.classroom_course.ClassroomCourseAttendance;

/**
 * @author Bat-Erdene Tsogoo.
 */
public interface ClassroomCourseAttendanceRepository
{

  /**
   * Saves a classroom course attendances
   *
   * @param courseAttendance  The course attendance to save
   */
  void save(ClassroomCourseAttendance courseAttendance);

  /**
   * Fetches a classroom course attendances
   *
   * @param courseId  The course id
   * @return A classroom course attendance
   * @throws LmsRepositoryException If failed to find a course attendance
   */
  ClassroomCourseAttendance findByCourseId(CourseId courseId) throws LmsRepositoryException;

  /**
   * Fetches all classroom course attendances
   *
   * @return A list of classroom course attendance
   */
  List<ClassroomCourseAttendance> fetchAll();

  /**
   * Finds an attendance by learner id and course id
   *
   * @param courseId  The course id
   * @param learnerId  The learner id
   * @return An attendance
   * @throws LmsRepositoryException If failed to find an attendance
   */
  Attendance findByCourseIdAndLearnerId(CourseId courseId, LearnerId learnerId) throws LmsRepositoryException;

  /**
   * Finds an attendance by learner id
   *
   * @param learnerId  The learner id
   * @return A list of classroom course attendances
   */
  List<ClassroomCourseAttendance> findByLearnerId(LearnerId learnerId);

  /**
   * Deletes a classroom course attendance by course id
   *
   * @param courseId  The course id
   * @return if deleted or not
   */
  boolean deleteCourseAttendance(CourseId courseId);
}
