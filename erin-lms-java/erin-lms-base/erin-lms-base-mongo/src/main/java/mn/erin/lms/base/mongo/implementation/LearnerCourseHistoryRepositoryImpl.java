package mn.erin.lms.base.mongo.implementation;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import mn.erin.lms.base.domain.model.course.CourseId;
import mn.erin.lms.base.domain.model.course.LearnerCourseHistory;
import mn.erin.lms.base.domain.model.course.LearnerCourseHistoryId;
import mn.erin.lms.base.domain.repository.LearnerCourseHistoryRepository;
import mn.erin.lms.base.mongo.document.course.MongoLearnerCourseHistory;
import mn.erin.lms.base.mongo.repository.MongoLearnerCourseHistoryRepository;

/**
 * @author Temuulen Naranbold
 */
public class LearnerCourseHistoryRepositoryImpl implements LearnerCourseHistoryRepository
{
  private final MongoLearnerCourseHistoryRepository mongoLearnerCourseHistoryRepository;

  public LearnerCourseHistoryRepositoryImpl(MongoLearnerCourseHistoryRepository mongoLearnerCourseHistoryRepository)
  {
    this.mongoLearnerCourseHistoryRepository = mongoLearnerCourseHistoryRepository;
  }

  @Override
  public LearnerCourseHistory create(String courseId, String userId, String name, String type, double credit, LocalDateTime completionDate, Float percentage)
  {
    MongoLearnerCourseHistory mongoLearnerCourseHistory = new MongoLearnerCourseHistory(courseId, userId, name, type, credit, completionDate, percentage);
    return map(mongoLearnerCourseHistoryRepository.save(mongoLearnerCourseHistory));
  }

  @Override
  public LearnerCourseHistory update(String courseId, String userId, String name, String type, double credit, LocalDateTime completionDate, Float percentage)
  {
    Optional<MongoLearnerCourseHistory> optional = mongoLearnerCourseHistoryRepository.findByCourseIdAndUserId(courseId, userId);
    if (optional.isPresent())
    {
      MongoLearnerCourseHistory mongoLearnerCourseHistory = optional.get();
      mongoLearnerCourseHistory.setName(name);
      mongoLearnerCourseHistory.setType(type);
      mongoLearnerCourseHistory.setCredit(credit);
      mongoLearnerCourseHistory.setCompletionDate(completionDate);
      mongoLearnerCourseHistory.setPercentage(percentage);
      return map(mongoLearnerCourseHistoryRepository.save(mongoLearnerCourseHistory));
    }
    return null;
  }

  @Override
  public LearnerCourseHistory get(String courseId, String userId)
  {
    return null;
  }

  @Override
  public boolean exists(String courseId, String userId)
  {
    return mongoLearnerCourseHistoryRepository.existsByCourseIdAndUserId(courseId, userId);
  }

  @Override
  public LearnerCourseHistory getById(LearnerCourseHistoryId learnerCourseHistoryId)
  {
    Optional<MongoLearnerCourseHistory> optional = mongoLearnerCourseHistoryRepository.findById(learnerCourseHistoryId.getId());
    return optional.map(this::map).orElse(null);
  }

  @Override
  public List<LearnerCourseHistory> getAllByUserId(String userId)
  {
    return mongoLearnerCourseHistoryRepository.findAllByUserId(userId).stream().map(this::map).collect(Collectors.toList());
  }

  @Override
  public List<LearnerCourseHistory> getAllByUserIdAndDate(String userId, LocalDateTime startDate, LocalDateTime endDate)
  {
    return mongoLearnerCourseHistoryRepository.findAllByUserIdAndCompletionDateBetween(userId, startDate, endDate).stream().map(this::map).collect(Collectors.toList());
  }

  private LearnerCourseHistory map(MongoLearnerCourseHistory mongoLearnerCourseHistory)
  {
    return new LearnerCourseHistory(
        LearnerCourseHistoryId.valueOf(mongoLearnerCourseHistory.getId().toHexString()),
        CourseId.valueOf(mongoLearnerCourseHistory.getCourseId()),
        mongoLearnerCourseHistory.getUserId(),
        mongoLearnerCourseHistory.getName(),
        mongoLearnerCourseHistory.getType(),
        mongoLearnerCourseHistory.getCredit(),
        mongoLearnerCourseHistory.getCompletionDate(),
        mongoLearnerCourseHistory.getPercentage()
    );
  }
}
