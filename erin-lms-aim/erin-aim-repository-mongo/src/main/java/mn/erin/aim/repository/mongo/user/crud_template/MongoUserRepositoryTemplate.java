package mn.erin.aim.repository.mongo.user.crud_template;

import java.util.List;

import mn.erin.aim.repository.mongo.user.document.MongoUser;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * @author Munkh
 */
public interface MongoUserRepositoryTemplate extends MongoRepository<MongoUser, ObjectId>
{
  List<MongoUser> findAllByTenantId(String tenantId);
}
