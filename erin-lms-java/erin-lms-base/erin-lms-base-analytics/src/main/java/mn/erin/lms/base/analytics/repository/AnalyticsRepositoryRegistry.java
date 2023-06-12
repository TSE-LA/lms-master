package mn.erin.lms.base.analytics.repository;

import org.springframework.stereotype.Service;

import mn.erin.lms.base.analytics.repository.mongo.CourseAnalyticsRepository;
import mn.erin.lms.base.analytics.repository.mongo.CourseStatisticsRepository;
import mn.erin.lms.base.analytics.repository.mongo.ExamAnalyticsRepository;
import mn.erin.lms.base.analytics.repository.mongo.LearnerActivityRepository;
import mn.erin.lms.base.analytics.repository.mongo.LearnerAnalyticsRepository;
import mn.erin.lms.base.analytics.repository.mongo.LearnerSuccessAnalyticRepository;
import mn.erin.lms.base.analytics.repository.mongo.PromotionAnalyticsRepository;
import mn.erin.lms.base.analytics.repository.mongo.SurveyAnalyticsRepository;
import mn.erin.lms.base.analytics.repository.mongo.implementation.AnalyticRepository;

/**
 * @author Munkh
 */
@Service
public interface AnalyticsRepositoryRegistry
{
  CourseAnalyticsRepository getCourseAnalyticsRepository();
  CourseStatisticsRepository getCourseStatisticsRepository();
  LearnerAnalyticsRepository getLearnerAnalyticsRepository();
  SurveyAnalyticsRepository getSurveyAnalyticsRepository();
  PromotionAnalyticsRepository getPromotionAnalyticsRepository();
  LearnerActivityRepository getLearnerActivityRepository();
  AnalyticRepository getAnalyticRepository();
  ExamAnalyticsRepository getExamAnalyticRepository();
  LearnerSuccessAnalyticRepository getLearnerAnalyticSuccessRepository();
}
