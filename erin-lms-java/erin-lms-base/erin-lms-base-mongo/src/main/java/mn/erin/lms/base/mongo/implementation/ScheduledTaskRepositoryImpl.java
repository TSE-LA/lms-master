package mn.erin.lms.base.mongo.implementation;

import java.util.Collection;
import java.util.stream.Collectors;

import mn.erin.lms.base.domain.model.task.LmsScheduledTask;
import mn.erin.lms.base.domain.repository.task.ScheduledTaskRepository;
import mn.erin.lms.base.mongo.document.task.Completion;
import mn.erin.lms.base.mongo.document.task.MongoScheduledTask;
import mn.erin.lms.base.mongo.repository.MongoScheduledTaskRepository;

/**
 * @author mLkhagvasuren
 */
public class ScheduledTaskRepositoryImpl implements ScheduledTaskRepository
{
  private final MongoScheduledTaskRepository mongoRepo;

  public ScheduledTaskRepositoryImpl(MongoScheduledTaskRepository mongoRepo)
  {
    this.mongoRepo = mongoRepo;
  }

  @Override
  public Collection<LmsScheduledTask> getAllPendingTasks()
  {
    return mongoRepo.findAllByCompletionOrderByDateAsc(Completion.PENDING).stream().map(ScheduledTaskRepositoryImpl::toEntity).collect(Collectors.toList());
  }

  @Override
  public boolean exists(String taskIdentifier)
  {
    return mongoRepo.findById(taskIdentifier).isPresent();
  }

  @Override
  public void remove(String taskIdentifier)
  {
    mongoRepo.deleteById(taskIdentifier);
  }

  @Override
  public void save(LmsScheduledTask task)
  {
    mongoRepo.save(toDocument(task));
  }

  private static MongoScheduledTask toDocument(LmsScheduledTask task)
  {
    MongoScheduledTask mongoTask = new MongoScheduledTask();
    mongoTask.setTaskIdentifier(task.taskIdentifier);
    mongoTask.setDate(task.date);
    mongoTask.setType(task.type);
    mongoTask.setCompletion(Completion.PENDING);
    mongoTask.setProperties(task.getProperties());
    return mongoTask;
  }

  private static LmsScheduledTask toEntity(MongoScheduledTask mongoTask)
  {
    LmsScheduledTask task = new LmsScheduledTask(mongoTask.getTaskIdentifier(), mongoTask.getDate(), mongoTask.getType());
    task.fillProperties(mongoTask.getProperties());
    return task;
  }
}
