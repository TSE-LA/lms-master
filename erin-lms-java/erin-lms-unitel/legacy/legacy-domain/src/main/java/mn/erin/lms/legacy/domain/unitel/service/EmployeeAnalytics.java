package mn.erin.lms.legacy.domain.unitel.service;

import java.util.List;
import java.util.Set;

import mn.erin.domain.aim.model.membership.Membership;
import mn.erin.lms.legacy.domain.lms.model.enrollment.LearnerId;
import mn.erin.lms.legacy.domain.unitel.model.EmployeeAnalyticData;
import mn.erin.lms.legacy.domain.unitel.model.EmployeePromotionPoints;
import mn.erin.lms.legacy.domain.unitel.model.UserActivityData;

/**
 * @author Bat-Erdene Tsogoo.
 */
public interface EmployeeAnalytics
{
  EmployeePromotionPoints getPromotionPoint(String username);

  List<EmployeePromotionPoints> getPromotionPoints(List<Membership> employees);

  Set<EmployeeAnalyticData> getEmployeeAnalyticData(LearnerId learnerId);

  Set<UserActivityData> getUserActivityData(List<LearnerId> learners);
}
