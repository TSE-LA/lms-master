package mn.erin.lms.jarvis.domain.report.repository;

import java.util.List;
import java.util.Set;
import mn.erin.lms.base.aim.user.LearnerId;
import mn.erin.lms.jarvis.domain.report.model.LearnerSuccess;

/**
 * @author Galsan Bayart
 */
public interface LearnerSuccessRepository
{
  void save(LearnerSuccess learnerSuccess);

  void saveAll(List<LearnerSuccess> learnerSuccesses);

  List<LearnerSuccess> getSuccesses(LearnerId learnerId, int year, boolean isFirstHalf);

  List<LearnerSuccess> getGroupMembersSuccesses(Set<String> learnerIds, int year, boolean isFirstHalf);

  List<LearnerSuccess> getAllByLearner(String learnerId);

  boolean isExist(LearnerId learnerId, int year, int month);

  boolean isExistThisMonth(int year, int month);
}
