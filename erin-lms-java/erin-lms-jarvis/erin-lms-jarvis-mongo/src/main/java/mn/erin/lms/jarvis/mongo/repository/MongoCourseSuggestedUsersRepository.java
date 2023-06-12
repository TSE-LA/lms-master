package mn.erin.lms.jarvis.mongo.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.repository.query.QueryByExampleExecutor;

import mn.erin.lms.jarvis.mongo.document.report.MongoCourseSuggestedUsers;


public interface MongoCourseSuggestedUsersRepository
    extends MongoRepository<MongoCourseSuggestedUsers, String>, QueryByExampleExecutor<MongoCourseSuggestedUsers>
{
  MongoCourseSuggestedUsers findByCourseId(String courseId);

  void deleteByCourseId(String courseId);

}


