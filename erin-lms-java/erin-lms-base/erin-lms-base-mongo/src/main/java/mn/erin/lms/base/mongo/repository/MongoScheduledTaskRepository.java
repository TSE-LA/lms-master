package mn.erin.lms.base.mongo.repository;

import java.util.Collection;

import org.springframework.data.mongodb.repository.MongoRepository;

import mn.erin.lms.base.mongo.document.task.Completion;
import mn.erin.lms.base.mongo.document.task.MongoScheduledTask;

/**
 * @author mLkhagvasuren
 */
public interface MongoScheduledTaskRepository extends MongoRepository<MongoScheduledTask, String>
{
  Collection<MongoScheduledTask> findAllByCompletionOrderByDateAsc(Completion completion);
}
