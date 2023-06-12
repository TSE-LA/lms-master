package mn.erin.aim.repository.mongo.user.crud_template;

import java.util.Optional;

import mn.erin.aim.repository.mongo.user.document.MongoUserProfile;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * @author Munkh
 */
public interface MongoUserProfileRepositoryTemplate extends MongoRepository<MongoUserProfile, ObjectId>
{
  Optional<MongoUserProfile> findByUserId(ObjectId userId);
  void deleteByUserId(ObjectId userId);

  Optional<MongoUserProfile> findByContact_PhoneNumber(String phoneNumber);
}
