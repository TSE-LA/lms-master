package mn.erin.lms.unitel.domain.service;

import java.util.List;
import java.util.Set;

import mn.erin.lms.base.aim.user.LearnerId;
import mn.erin.lms.unitel.domain.model.analytics.EmployeeAnalyticData;
import mn.erin.lms.unitel.domain.model.analytics.EmployeePromoPoints;
import mn.erin.lms.unitel.domain.model.analytics.UserActivityData;
import mn.erin.lms.unitel.domain.model.user.Membership;

/**
 * @author Bat-Erdene Tsogoo.
 */
public interface EmployeeAnalyticsService
{
  List<EmployeePromoPoints> getPromotionPoints(List<Membership> employees);

  Set<EmployeeAnalyticData> getEmployeeAnalyticData(LearnerId learnerId);

  Set<UserActivityData> getUserActivityData(List<LearnerId> learners);
}
