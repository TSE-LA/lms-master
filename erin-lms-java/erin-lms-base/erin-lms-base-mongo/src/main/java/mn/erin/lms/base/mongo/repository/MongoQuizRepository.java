package mn.erin.lms.base.mongo.repository;

import org.springframework.data.mongodb.repository.MongoRepository;

import mn.erin.lms.base.mongo.document.assessment.MongoQuiz;

/**
 * @author Bat-Erdene Tsogoo.
 */
public interface MongoQuizRepository extends MongoRepository<MongoQuiz, String>
{
}
