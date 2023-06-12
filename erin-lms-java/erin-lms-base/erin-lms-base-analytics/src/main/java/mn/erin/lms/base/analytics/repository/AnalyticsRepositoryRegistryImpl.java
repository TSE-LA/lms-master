package mn.erin.lms.base.analytics.repository;

import javax.inject.Inject;

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
public class AnalyticsRepositoryRegistryImpl implements AnalyticsRepositoryRegistry
{
  private CourseAnalyticsRepository courseAnalyticsRepository;
  private CourseStatisticsRepository courseStatisticsRepository;
  private LearnerAnalyticsRepository learnerAnalyticsRepository;
  private SurveyAnalyticsRepository surveyAnalyticsRepository;
  private PromotionAnalyticsRepository promotionAnalyticsRepository;
  private LearnerActivityRepository learnerActivityRepository;
  private AnalyticRepository analyticRepository;
  private ExamAnalyticsRepository examAnalyticsRepository;
  private LearnerSuccessAnalyticRepository learnerSuccessAnalyticRepository;

  @Inject
  public void setCourseAnalyticsRepository(CourseAnalyticsRepository courseAnalyticsRepository)
  {
    this.courseAnalyticsRepository = courseAnalyticsRepository;
  }

  @Inject
  public void setCourseStatisticsRepository(CourseStatisticsRepository courseStatisticsRepository)
  {
    this.courseStatisticsRepository = courseStatisticsRepository;
  }

  @Inject
  public void setLearnerAnalyticsRepository(LearnerAnalyticsRepository learnerAnalyticsRepository)
  {
    this.learnerAnalyticsRepository = learnerAnalyticsRepository;
  }

  @Inject
  public void setSurveyAnalyticsRepository(SurveyAnalyticsRepository surveyAnalyticsRepository)
  {
    this.surveyAnalyticsRepository = surveyAnalyticsRepository;
  }

  @Inject
  public void setPromotionAnalyticsRepository(PromotionAnalyticsRepository promotionAnalyticsRepository)
  {
    this.promotionAnalyticsRepository = promotionAnalyticsRepository;
  }

  @Inject
  void setExamAnalyticsRepository(ExamAnalyticsRepository examAnalyticsRepository)
  {
    this.examAnalyticsRepository = examAnalyticsRepository;
  }

  @Inject
  public void setLearnerActivityRepository(LearnerActivityRepository learnerActivityRepository)
  {
    this.learnerActivityRepository = learnerActivityRepository;
  }

  @Inject
  public void setLearnerSuccessAnalyticRepository(LearnerSuccessAnalyticRepository learnerSuccessAnalyticRepository){
    this.learnerSuccessAnalyticRepository = learnerSuccessAnalyticRepository;
  }
  @Inject
  public void setAnalyticRepository(AnalyticRepository analyticRepository)
  {
    this.analyticRepository = analyticRepository;
  }

  @Override
  public CourseAnalyticsRepository getCourseAnalyticsRepository()
  {
    return courseAnalyticsRepository;
  }

  @Override
  public CourseStatisticsRepository getCourseStatisticsRepository()
  {
    return courseStatisticsRepository;
  }

  @Override
  public LearnerAnalyticsRepository getLearnerAnalyticsRepository()
  {
    return learnerAnalyticsRepository;
  }

  @Override
  public SurveyAnalyticsRepository getSurveyAnalyticsRepository()
  {
    return surveyAnalyticsRepository;
  }

  @Override
  public PromotionAnalyticsRepository getPromotionAnalyticsRepository()
  {
    return promotionAnalyticsRepository;
  }

  @Override
  public LearnerActivityRepository getLearnerActivityRepository()
  {
    return learnerActivityRepository;
  }

  @Override
  public AnalyticRepository getAnalyticRepository()
  {
    return analyticRepository;
  }

  @Override
  public ExamAnalyticsRepository getExamAnalyticRepository()
  {
    return examAnalyticsRepository;
  }

  @Override
  public LearnerSuccessAnalyticRepository getLearnerAnalyticSuccessRepository()
  {
    return learnerSuccessAnalyticRepository;
  }
}
