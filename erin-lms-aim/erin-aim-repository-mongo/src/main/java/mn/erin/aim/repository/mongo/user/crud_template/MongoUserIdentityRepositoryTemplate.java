package mn.erin.aim.repository.mongo.user.crud_template;

import java.util.List;
import java.util.Optional;

import mn.erin.aim.repository.mongo.user.document.MongoUserIdentity;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * @author Munkh
 */
public interface MongoUserIdentityRepositoryTemplate extends MongoRepository<MongoUserIdentity, ObjectId>
{
  List<MongoUserIdentity> findAllByUserId(ObjectId userId);

  Optional<MongoUserIdentity> findByUsernameAndSource(String username, String source);

  List<MongoUserIdentity> findAllBySource(String source);

  Optional<MongoUserIdentity> findByUserIdAndSource(ObjectId userId, String source);

  void deleteByUserIdAndSource(ObjectId userId, String source);

  void deleteAllByUserId(ObjectId userId);
}
