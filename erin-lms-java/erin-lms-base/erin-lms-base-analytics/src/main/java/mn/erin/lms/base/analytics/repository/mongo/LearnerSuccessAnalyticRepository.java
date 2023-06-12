package mn.erin.lms.base.analytics.repository.mongo;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

import mn.erin.lms.base.scorm.model.RuntimeData;

/**
 * @author Oyungerel Chuluunsukh
 **/
public interface LearnerSuccessAnalyticRepository
{

  List<Double> listRunTimeDataProgress(String learner, LocalDate startDate, LocalDate endDate);

  boolean isExistThisMonth(int year, int monthValue, String courseType);

  boolean isExist(String learnerId, int year, int monthValue, String courseType);

  List<RuntimeData> listRunTimeDataTestCompleted(String learner, LocalDate startDate, LocalDate endDate);

  void save(String learner, int score, int maxScore, String courseType, double performance, int year, int monthValue);

  Set<String> getAllLearners(String tenantId);
}
