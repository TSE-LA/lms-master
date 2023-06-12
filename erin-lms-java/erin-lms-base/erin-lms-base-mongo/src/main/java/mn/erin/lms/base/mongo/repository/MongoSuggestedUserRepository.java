package mn.erin.lms.base.mongo.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.repository.query.QueryByExampleExecutor;

import mn.erin.lms.base.mongo.document.exam.MongoSuggestedUser;

/**
 * @author Galsan Bayart.
 */
public interface MongoSuggestedUserRepository extends MongoRepository<MongoSuggestedUser, String>, QueryByExampleExecutor<MongoSuggestedUser>
{
}
