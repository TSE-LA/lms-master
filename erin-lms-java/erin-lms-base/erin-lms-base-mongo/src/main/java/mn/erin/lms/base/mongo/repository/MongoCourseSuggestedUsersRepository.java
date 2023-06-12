package mn.erin.lms.base.mongo.repository;

import org.springframework.data.mongodb.repository.MongoRepository;

import mn.erin.lms.base.mongo.document.course.MongoCourseSuggestedUsers;

/**
 * @author Erdenetulga
 */
public interface MongoCourseSuggestedUsersRepository
    extends MongoRepository<MongoCourseSuggestedUsers, String>
{
  MongoCourseSuggestedUsers findByCourseId(String courseId);

  void deleteByCourseId(String courseId);

}


