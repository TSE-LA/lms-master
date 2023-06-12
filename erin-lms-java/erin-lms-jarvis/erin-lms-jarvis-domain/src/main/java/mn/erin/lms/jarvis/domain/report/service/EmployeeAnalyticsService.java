package mn.erin.lms.jarvis.domain.report.service;

import java.util.List;
import java.util.Set;

import mn.erin.domain.aim.model.membership.Membership;
import mn.erin.lms.base.aim.user.LearnerId;
import mn.erin.lms.jarvis.domain.report.model.analytics.EmployeeAnalyticData;
import mn.erin.lms.jarvis.domain.report.model.analytics.EmployeePromoPoints;
import mn.erin.lms.jarvis.domain.report.model.analytics.UserActivityData;

/**
 * @author Bat-Erdene Tsogoo.
 */
public interface EmployeeAnalyticsService
{
  List<EmployeePromoPoints> getPromotionPoints(List<Membership> employees);

  Set<EmployeeAnalyticData> getEmployeeAnalyticData(LearnerId learnerId);

  Set<UserActivityData> getUserActivityData(List<LearnerId> learners);
}
