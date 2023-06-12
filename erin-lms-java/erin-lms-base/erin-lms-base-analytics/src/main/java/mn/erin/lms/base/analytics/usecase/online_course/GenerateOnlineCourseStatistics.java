package mn.erin.lms.base.analytics.usecase.online_course;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import org.apache.commons.lang3.Validate;

import mn.erin.domain.aim.model.group.GroupId;
import mn.erin.domain.base.usecase.UseCaseException;
import mn.erin.lms.base.analytics.exception.AnalyticsRepositoryException;
import mn.erin.lms.base.analytics.model.Analytic;
import mn.erin.lms.base.analytics.model.course.online_course.OnlineCourseStatistics;
import mn.erin.lms.base.analytics.repository.AnalyticsRepositoryRegistry;
import mn.erin.lms.base.analytics.usecase.AnalyticsUseCase;
import mn.erin.lms.base.analytics.usecase.dto.CourseAnalytics;
import mn.erin.lms.base.analytics.usecase.dto.StatisticsFilter;
import mn.erin.lms.base.domain.model.course.CourseId;
import mn.erin.lms.base.domain.service.CourseTypeResolver;

/**
 * @author Munkh
 */
public class GenerateOnlineCourseStatistics extends AnalyticsUseCase<StatisticsFilter, CourseAnalytics>
{
  public GenerateOnlineCourseStatistics(AnalyticsRepositoryRegistry analyticsRepositoryRegistry, CourseTypeResolver courseTypeResolver)
  {
    super(analyticsRepositoryRegistry, courseTypeResolver);
  }

  @Override
  public CourseAnalytics execute(StatisticsFilter input) throws UseCaseException
  {
    Validate.notNull(input);
    Validate.notNull(input.getGroupId());
    Validate.notNull(input.getCourseId());

    try
    {
      List<Analytic> analytics = analyticsRepositoryRegistry.getCourseStatisticsRepository().getStatistics(
          CourseId.valueOf(input.getCourseId()),
          GroupId.valueOf(input.getGroupId()),
          input.getDateFilter().getStartDate(),
          input.getDateFilter().getEndDate()
      );

      int totalSpentTime = 0;
      int counter = 0;

      for (Analytic analytic : analytics)
      {
        OnlineCourseStatistics onlineCourseStatistics = (OnlineCourseStatistics) analytic;
        int spentTimeAsSecond = convertToSecond(onlineCourseStatistics.getSpentTime());
        if (spentTimeAsSecond != 0)
        {
          totalSpentTime += spentTimeAsSecond;
          counter++;
        }
      }

      String averageSpentTime = convertToTimeFormat(counter != 0 ? totalSpentTime / counter : 0);

      for (Analytic analytic : analytics)
      {
        OnlineCourseStatistics onlineCourseStatistics = (OnlineCourseStatistics) analytic;
        onlineCourseStatistics.setSpentTimeRatio(onlineCourseStatistics.getSpentTime() + "/" + averageSpentTime);
      }
      return new CourseAnalytics(analytics);
    }
    catch (AnalyticsRepositoryException e)
    {
      throw new UseCaseException(e.getMessage(), e);
    }
  }

  private int convertToSecond(String time)
  {
    String[] splittedTime = time.split(":");
    return (int) (Integer.parseInt(splittedTime[0]) * 3600 + Integer.parseInt(splittedTime[1]) * 60 + Double.parseDouble(splittedTime[2]));
  }

  private String convertToTimeFormat(int second)
  {
    Date d = new Date(second * 1000L);
    SimpleDateFormat df = new SimpleDateFormat("HH:mm:ss");
    df.setTimeZone(TimeZone.getTimeZone("GMT"));
    return df.format(d);
  }
}
