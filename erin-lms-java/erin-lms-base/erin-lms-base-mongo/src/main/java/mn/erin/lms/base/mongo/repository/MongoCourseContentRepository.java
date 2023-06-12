package mn.erin.lms.base.mongo.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.repository.query.QueryByExampleExecutor;

import mn.erin.lms.base.mongo.document.content.MongoCourseContent;

/**
 * @author Bat-Erdene Tsogoo.
 */
public interface MongoCourseContentRepository extends MongoRepository<MongoCourseContent, String>,
    QueryByExampleExecutor<MongoCourseContent>
{
  MongoCourseContent findFirstByCourseId(String courseId);
  boolean existsByCourseId(String courseId);
}
