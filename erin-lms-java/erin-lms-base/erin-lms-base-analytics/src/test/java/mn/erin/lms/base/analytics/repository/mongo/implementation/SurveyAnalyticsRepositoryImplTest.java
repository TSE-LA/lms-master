package mn.erin.lms.base.analytics.repository.mongo.implementation;

import java.time.LocalDate;
import java.util.List;
import javax.inject.Inject;

import junit.framework.TestCase;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import mn.erin.lms.base.analytics.model.survey.SurveyAnalytic;
import mn.erin.lms.base.analytics.repository.mongo.SurveyAnalyticsRepository;
import mn.erin.lms.base.analytics.repository.mongo.config.AnalyticsTestBeanConfig;
import mn.erin.lms.base.domain.model.assessment.AssessmentId;

/**
 * @author Munkh
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { AnalyticsTestBeanConfig.class })
public class SurveyAnalyticsRepositoryImplTest
{
  @Inject
  private SurveyAnalyticsRepository surveyAnalyticsRepository;

  @Test
  @Ignore
  public void execution_test()
  {
    List<SurveyAnalytic> analyticList = surveyAnalyticsRepository.getAllBySurveyId(
        AssessmentId.valueOf("5fd7c3aa1293c94b38b38ae9"),
        LocalDate.of(2020, 1, 1),
        LocalDate.now()
    );

    Assert.assertTrue("Survey should not be empty", analyticList.size() > 0);
  }
}
