package mn.erin.lms.unitel.domain.repository;

import java.util.List;
import java.util.Set;

import mn.erin.lms.base.aim.user.LearnerId;
import mn.erin.lms.unitel.domain.model.analytics.LearnerSuccess;

/**
 * @author Byambajav
 */
public interface LearnerSuccessRepository
{
  void save(LearnerSuccess learnerSuccess);

  void saveAll(List<LearnerSuccess> learnerSuccesses);

  List<LearnerSuccess> getSuccesses(LearnerId learnerId, String courseType, int year, boolean isFirstHalf);

  List<LearnerSuccess> getGroupMembersSuccesses(Set<String> learnerIds, int year, boolean isFirstHalf);

  List<LearnerSuccess> getAllByLearner(String learnerId);

  boolean isExist(LearnerId learnerId, int year, int month);

  boolean isExistThisMonth(int year, int month, String courseType);
}

