package mn.erin.lms.base.mongo.repository;

import java.util.List;
import java.util.Set;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.repository.query.QueryByExampleExecutor;

import mn.erin.lms.base.mongo.document.enrollment.MongoEnrollSupervisor;

/**
 * @author Galsan Bayart.
 */
public interface MongoEnrollSupervisorRepository extends MongoRepository<MongoEnrollSupervisor, String>, QueryByExampleExecutor<MongoEnrollSupervisor>
{
  Set<MongoEnrollSupervisor> getAllBySupervisorIdsContains(String supervisorId);

  Set<MongoEnrollSupervisor> getAllByExamIdIn(List<String> examIds);
}
