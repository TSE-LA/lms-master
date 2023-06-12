package mn.erin.lms.jarvis.mongo.implementation;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import mn.erin.lms.base.aim.user.LearnerId;
import mn.erin.lms.base.mongo.util.IdGenerator;
import mn.erin.lms.jarvis.domain.report.model.LearnerSuccess;
import mn.erin.lms.jarvis.domain.report.repository.LearnerSuccessRepository;
import mn.erin.lms.jarvis.mongo.document.report.MongoLearnerSuccess;
import mn.erin.lms.jarvis.mongo.repository.MongoLearnerSuccessRepository;

/**
 * @author Galsan Bayart
 */
public class LearnerSuccessRepositoryImpl implements LearnerSuccessRepository
{

  private final MongoLearnerSuccessRepository mongoLearnerSuccessRepository;

  public LearnerSuccessRepositoryImpl(MongoLearnerSuccessRepository mongoLearnerSuccessRepository)
  {
    this.mongoLearnerSuccessRepository = mongoLearnerSuccessRepository;
  }

  @Override
  public void save(LearnerSuccess learnerSuccess)
  {
    mongoLearnerSuccessRepository.save(mapToMongoLearnerSuccess(learnerSuccess));
  }

  public void saveAll(List<LearnerSuccess> learnerSuccesses){
    mongoLearnerSuccessRepository.saveAll(mapToMongoLearnerSuccessList(learnerSuccesses));
  }

  @Override
  public List<LearnerSuccess> getSuccesses(LearnerId learnerId, int year, boolean isFirstHalf)
  {
    List<MongoLearnerSuccess> learnerSuccesses = mongoLearnerSuccessRepository
        .getAllByLearnerIdAndYearAndMonthBetween(learnerId.getId(), year, isFirstHalf ? 0 : 6, isFirstHalf ? 7 : 13);

    return mapToLearnerSuccess(learnerSuccesses);
  }

  @Override
  public List<LearnerSuccess> getGroupMembersSuccesses(Set<String> learnerIds, int year, boolean isFirstHalf)
  {
    List<MongoLearnerSuccess> learnerSuccesses = mongoLearnerSuccessRepository
        .getAllByLearnerIdInAndYearAndMonthBetween(learnerIds, year, isFirstHalf ? 0 : 7, isFirstHalf ? 6 : 13);

    return mapToLearnerSuccess(learnerSuccesses);
  }

  @Override
  public List<LearnerSuccess> getAllByLearner(String learnerId)
  {
    return mapToLearnerSuccess(mongoLearnerSuccessRepository.getAllByLearnerId(learnerId));
  }

  @Override
  public boolean isExist(LearnerId learnerId, int year, int month)
  {
    return mongoLearnerSuccessRepository.findByLearnerIdAndYearAndMonth(learnerId.getId(), year, month).isPresent();
  }

  @Override
  public boolean isExistThisMonth(int year, int month)
  {
    return mongoLearnerSuccessRepository.countByYearAndMonth(year, month) > 0;
  }

  private List<MongoLearnerSuccess> mapToMongoLearnerSuccessList(List<LearnerSuccess> learnerSuccess)
  {
    List<MongoLearnerSuccess> mongoLearnerSuccesses = new ArrayList<>();
    for (LearnerSuccess success : learnerSuccess)
    {
      mongoLearnerSuccesses.add(mapToMongoLearnerSuccess(success));
    }
    return mongoLearnerSuccesses;
  }

  private MongoLearnerSuccess mapToMongoLearnerSuccess(LearnerSuccess learnerSuccess)
  {
    String id = IdGenerator.generateId();
    MongoLearnerSuccess mongoLearnerSuccess = new MongoLearnerSuccess();
    mongoLearnerSuccess.setId(id);
    mongoLearnerSuccess.setCourseType(learnerSuccess.getCourseType());
    mongoLearnerSuccess.setLearnerId(learnerSuccess.getLearnerId());
    mongoLearnerSuccess.setMonth(learnerSuccess.getMonth());
    mongoLearnerSuccess.setScore(learnerSuccess.getScore());
    mongoLearnerSuccess.setPerformance(learnerSuccess.getPerformance());
    mongoLearnerSuccess.setMaxScore(learnerSuccess.getMaxScore());
    mongoLearnerSuccess.setYear(learnerSuccess.getYear());
    mongoLearnerSuccess.setMonth(learnerSuccess.getMonth());
    return mongoLearnerSuccess;
  }

  private List<LearnerSuccess> mapToLearnerSuccess(List<MongoLearnerSuccess> mongoLearnerSuccesses)
  {
    List<LearnerSuccess> learnerSuccesses = new ArrayList<>();

    LearnerSuccess learnerSuccess;

    for (MongoLearnerSuccess mongoLearnerSuccess : mongoLearnerSuccesses)
    {
      learnerSuccess = new LearnerSuccess(mongoLearnerSuccess.getLearnerId());
      learnerSuccess.setMonth(mongoLearnerSuccess.getMonth());
      learnerSuccess.setScore(mongoLearnerSuccess.getScore());
      learnerSuccess.setPerformance(mongoLearnerSuccess.getPerformance());
      learnerSuccess.setMaxScore(mongoLearnerSuccess.getMaxScore());
      learnerSuccess.setYear(mongoLearnerSuccess.getYear());
      learnerSuccess.setMonth(mongoLearnerSuccess.getMonth());
      learnerSuccesses.add(learnerSuccess);
    }
    return learnerSuccesses;
  }
}