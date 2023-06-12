package mn.erin.lms.base.mongo.repository;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import mn.erin.lms.base.mongo.document.course.MongoClassroomCourseAttendance;

/**
 * @author Bat-Erdene Tsogoo.
 */
public interface MongoClassroomCourseAttendanceRepository extends MongoRepository<MongoClassroomCourseAttendance, String>
{
  @Query(value = "{'attendances': {$elemMatch: {'learnerId': ?0}}}")
  List<MongoClassroomCourseAttendance> findAllByAttendancesRegex(String learnerId);
}
