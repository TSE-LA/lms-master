package mn.erin.lms.base.analytics.usecase.online_course;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import mn.erin.common.excel.ExcelWriterUtil;
import mn.erin.domain.base.usecase.UseCaseException;
import mn.erin.lms.base.analytics.model.course.online_course.OnlineCourseAnalytic;
import mn.erin.lms.base.analytics.model.course.online_course.OnlineCourseStatistics;
import mn.erin.lms.base.analytics.repository.AnalyticsRepositoryRegistry;
import mn.erin.lms.base.analytics.usecase.AnalyticsUseCase;
import mn.erin.lms.base.analytics.usecase.dto.CourseAnalytics;
import mn.erin.lms.base.analytics.usecase.dto.CourseFilter;
import mn.erin.lms.base.analytics.usecase.dto.StatisticsFilter;
import mn.erin.lms.base.domain.service.CourseTypeResolver;

public class GenerateOnlineCourseAnalyticStatisticExcel extends AnalyticsUseCase<CourseFilter, byte[]>
{
  public static final Logger LOGGER = LoggerFactory.getLogger(GenerateOnlineCourseAnalyticStatisticExcel.class);
  private static final String[] ONLINE_COURSE_REPORT_HEADERS = {
      "№",
      "Сургалтын нэр",
      "Ангилал",
      "Хэрэглэгчийн нэр",
      "Алба хэлтэс",
      "Танилцсан хувь /Агуулга/",
      "Танилцсан хувь /Хугацаа/",
      "Анх танилцсан",
      "Сүүлд танилцсан",
      "Үзсэн удаа",
      "Зарцуулсан хугацаа",
      "Oноо",
      "Сорилд зарцуулсан хугацаа",
      "Сертификат",
      "Үнэлгээ"
  };
  private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
  private int statisticCount = 0;

  public GenerateOnlineCourseAnalyticStatisticExcel(AnalyticsRepositoryRegistry analyticsRepositoryRegistry, CourseTypeResolver courseTypeResolver)
  {
    super(analyticsRepositoryRegistry, courseTypeResolver);
  }

  @Override
  public byte[] execute(CourseFilter input) throws UseCaseException
  {
    try (ByteArrayOutputStream os = new ByteArrayOutputStream())
    {
      GenerateOnlineCourseAnalytics generateOnlineCourseAnalytics = new GenerateOnlineCourseAnalytics(analyticsRepositoryRegistry, courseTypeResolver);
      CourseAnalytics courseAnalytics = generateOnlineCourseAnalytics.execute(input);

      List<OnlineCourseAnalytic> onlineCourseAnalytics = courseAnalytics.getAnalytics()
          .stream().map(OnlineCourseAnalytic.class::cast).collect(Collectors.toList());

      Map<String, List<OnlineCourseStatistics>> mappedStatistics = getCourseStatistics(onlineCourseAnalytics, input);
      Object[][] excelData = convertToExcelData(onlineCourseAnalytics, mappedStatistics);

      String sheetTitle = ("Цахим сургалтын тайлан\nТатаж авсан: " +
          LocalDateTime.now().format(DATE_TIME_FORMATTER) +
          "\nЭхлэх огноо: " + input.getDateFilter().getStartDate().format(DATE_TIME_FORMATTER) +
          "\nДуусах огноо: " + input.getDateFilter().getEndDate().format(DATE_TIME_FORMATTER));

      ExcelWriterUtil.write(false, sheetTitle, ONLINE_COURSE_REPORT_HEADERS, excelData, os);

      return os.toByteArray();
    }
    catch (IOException | UseCaseException e)
    {
      throw new UseCaseException(e.getMessage());
    }
  }

  private Object[][] convertToExcelData(List<OnlineCourseAnalytic> onlineCourseAnalytics, Map<String, List<OnlineCourseStatistics>> mappedStatistics)
  {
    Object[][] data = new Object[statisticCount][];
    int index = 0;
    for (OnlineCourseAnalytic courseAnalytic : onlineCourseAnalytics)
    {
      for (OnlineCourseStatistics courseStatistics : mappedStatistics.get(courseAnalytic.getId()))
      {
        String courseTitle = courseAnalytic.getTitle() != null ? courseAnalytic.getTitle() : "";
        String categoryName = courseAnalytic.getCategoryName() != null ? courseAnalytic.getCategoryName() : "";
        String learnerId = courseStatistics.getLearnerId() != null ? courseStatistics.getLearnerId() : "";
        String groupName = courseStatistics.getGroupName() != null ? courseStatistics.getGroupName() : "";
        String status = Double.isNaN(courseStatistics.getStatus()) ? "0.0" : String.valueOf(courseStatistics.getStatus());
        String spentTimeRatio = courseStatistics.getSpentTimeRatio() != null ? courseStatistics.getSpentTimeRatio() : "00:00/00:00";
        String firstViewDate = courseStatistics.getFirstViewDate() != null ? courseStatistics.getFirstViewDate() : "";
        String lastViewDate = courseStatistics.getLastViewDate() != null ? courseStatistics.getLastViewDate() : "";
        int views = courseStatistics.getViews();
        String spentTime = courseStatistics.getSpentTime() != null ? courseStatistics.getSpentTime() : "0/0";
        int score = courseStatistics.getScore();
        String spentTimeOnTest = courseStatistics.getSpentTimeOnTest() != null ? courseStatistics.getSpentTimeOnTest() : "00:00:00";
        String certificateReceivedDate = courseStatistics.getReceivedCertificateDate() != null ? courseStatistics.getReceivedCertificateDate() : "";
        String survey = courseStatistics.getSurvey() != null ? "Бөглөсөн" : "Бөглөөгүй";

        Object[] row = {
            0,
            courseTitle,
            categoryName,
            learnerId,
            groupName,
            status,
            spentTimeRatio,
            firstViewDate,
            lastViewDate,
            views,
            spentTime,
            score,
            spentTimeOnTest,
            certificateReceivedDate,
            survey
        };
        data[index] = row;
        index++;
      }
    }
    return data;
  }

  private Map<String, List<OnlineCourseStatistics>> getCourseStatistics(List<OnlineCourseAnalytic> onlineCourseAnalytics, CourseFilter courseFilter)
  {
    Map<String, List<OnlineCourseStatistics>> mappedAnalytics = new HashMap<>();
    GenerateOnlineCourseStatistics generateOnlineCourseStatistics = new GenerateOnlineCourseStatistics(analyticsRepositoryRegistry, courseTypeResolver);

    for (OnlineCourseAnalytic onlineCourseAnalytic : onlineCourseAnalytics)
    {
      try
      {
        CourseAnalytics statistics = generateOnlineCourseStatistics.execute(
            new StatisticsFilter(courseFilter.getGroupId(), onlineCourseAnalytic.getId(), courseFilter.getDateFilter()));
        mappedAnalytics.put(onlineCourseAnalytic.getId(), statistics.getAnalytics().stream().map(OnlineCourseStatistics.class::cast).collect(Collectors.toList()));
        statisticCount += statistics.getAnalytics().size();
      }
      catch (UseCaseException e)
      {
        LOGGER.error(e.getMessage());
      }
    }
    return mappedAnalytics;
  }
}
