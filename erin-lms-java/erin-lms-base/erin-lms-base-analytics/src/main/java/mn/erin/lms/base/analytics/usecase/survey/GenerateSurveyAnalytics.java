package mn.erin.lms.base.analytics.usecase.survey;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;

import mn.erin.lms.base.analytics.model.survey.SurveyAnalytic;
import mn.erin.lms.base.analytics.repository.AnalyticsRepositoryRegistry;
import mn.erin.lms.base.analytics.usecase.AnalyticsUseCase;
import mn.erin.lms.base.analytics.usecase.dto.SurveyAnalytics;
import mn.erin.lms.base.analytics.usecase.dto.SurveyFilter;
import mn.erin.lms.base.domain.model.assessment.AssessmentId;
import mn.erin.lms.base.domain.model.course.CourseId;
import mn.erin.lms.base.domain.service.CourseTypeResolver;

/**
 * @author Munkh
 */
public class GenerateSurveyAnalytics extends AnalyticsUseCase<SurveyFilter, SurveyAnalytics>
{
  public GenerateSurveyAnalytics(AnalyticsRepositoryRegistry analyticsRepositoryRegistry, CourseTypeResolver courseTypeResolver)
  {
    super(analyticsRepositoryRegistry, courseTypeResolver);
  }

  @Override
  public SurveyAnalytics execute(SurveyFilter input)
  {
    Validate.notNull(input);
    Validate.notNull(input.getSurveyId());

    List<SurveyAnalytic> analytics;
    if (StringUtils.isBlank(input.getCourseId()))
    {
      analytics = analyticsRepositoryRegistry.getSurveyAnalyticsRepository().getAllBySurveyId(
          AssessmentId.valueOf(input.getSurveyId()),
          input.getDateFilter().getStartDate(),
          input.getDateFilter().getEndDate()
      );
    }
    else
    {
      analytics = analyticsRepositoryRegistry.getSurveyAnalyticsRepository().getAllBySurveyAndCourseId(
          AssessmentId.valueOf(input.getSurveyId()),
          CourseId.valueOf(input.getCourseId()),
          input.getDateFilter().getStartDate(),
          input.getDateFilter().getEndDate()
      );
    }

    return new SurveyAnalytics(analytics);
  }
}
