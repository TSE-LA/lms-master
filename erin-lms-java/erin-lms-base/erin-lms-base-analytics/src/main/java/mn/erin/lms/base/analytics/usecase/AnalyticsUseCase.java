package mn.erin.lms.base.analytics.usecase;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import mn.erin.domain.base.usecase.UseCase;
import mn.erin.lms.base.analytics.model.Analytic;
import mn.erin.lms.base.analytics.model.learner.LearnerAnalytic;
import mn.erin.lms.base.analytics.repository.AnalyticsRepositoryRegistry;
import mn.erin.lms.base.analytics.usecase.dto.AnalyticDto;
import mn.erin.lms.base.analytics.usecase.dto.LearnerAnalyticDto;
import mn.erin.lms.base.domain.service.CourseTypeResolver;

/**
 * @author Munkh
 */
public abstract class AnalyticsUseCase<I, O> implements UseCase<I, O>
{
  protected final AnalyticsRepositoryRegistry analyticsRepositoryRegistry;
  protected CourseTypeResolver courseTypeResolver;

  protected AnalyticsUseCase(AnalyticsRepositoryRegistry analyticsRepositoryRegistry, CourseTypeResolver courseTypeResolver)
  {
    this.analyticsRepositoryRegistry = analyticsRepositoryRegistry;
    this.courseTypeResolver = courseTypeResolver;
  }

  protected AnalyticsUseCase(AnalyticsRepositoryRegistry analyticsRepositoryRegistry)
  {
    this.analyticsRepositoryRegistry = analyticsRepositoryRegistry;
  }
  protected List<AnalyticDto> toLearnerAnalyticDtos(List<Analytic> analytics)
  {
    List<AnalyticDto> result = new ArrayList<>();
    try
    {
      for (Analytic analytic : analytics)
      {
        LearnerAnalytic learnerAnalytic = (LearnerAnalytic) analytic;
        LearnerAnalyticDto dto = new LearnerAnalyticDto.Builder()
            .withCourseId(learnerAnalytic.getCourseId().getId())
            .withTitle(learnerAnalytic.getTitle())
            .withCategory(learnerAnalytic.getCategory())
            .withCourseType(learnerAnalytic.getCourseType())
            .withScore(learnerAnalytic.getScore())
            .withStatus(learnerAnalytic.getStatus())
            .withViews(learnerAnalytic.getViews())
            .hasSurvey(learnerAnalytic.hasSurvey())
            .withSpentTime(learnerAnalytic.getSpentTime())
            .withSpentTimeRatio(learnerAnalytic.getSpentTimeRatio())
            .withCertificate(localDateTimeToString(learnerAnalytic.getReceivedCertificateDate()))
            .withFirstViewDate(localDateTimeToString(learnerAnalytic.getFirstViewDate()))
            .withLastViewDate(localDateTimeToString(learnerAnalytic.getLastViewDate()))
            .withSpentTimeOnTest(learnerAnalytic.getSpentTimeOnTest())
            .build();
        result.add(dto);
      }
    }
    catch (ClassCastException ignored)
    {
      // TODO: Decide what to do. Possibly ignored.
    }
    return result;
  }

  private String localDateTimeToString(LocalDateTime ldt)
  {
    if (ldt == null)
    {
      return null;
    }

    return ldt.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
  }
}
