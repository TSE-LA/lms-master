package mn.erin.lms.legacy.domain.unitel.usecase.analytics.excel_report;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import mn.erin.common.excel.ExcelWriterUtil;
import mn.erin.domain.base.usecase.UseCase;
import mn.erin.domain.base.usecase.UseCaseException;
import mn.erin.lms.legacy.domain.lms.repository.CourseRepository;
import mn.erin.lms.legacy.domain.lms.usecase.course.get_course.GetCourse;
import mn.erin.lms.legacy.domain.lms.usecase.course.get_course.GetCourseInput;
import mn.erin.lms.legacy.domain.lms.usecase.course.get_course.GetCourseOutput;
import mn.erin.lms.legacy.domain.unitel.usecase.analytics.get_promotion_analytics.GetPromotionAnalytics;
import mn.erin.lms.legacy.domain.unitel.usecase.analytics.get_promotion_analytics.GetPromotionAnalyticsInput;
import mn.erin.lms.legacy.domain.unitel.usecase.analytics.get_promotion_analytics.PromotionAnalyticsOutput;

public class GenerateExcelReport implements UseCase<GetPromotionAnalyticsInput, byte[]>
{
  private static final String NUMBER = "\u2116";
  private static final String USERNAME = "\u041d\u044d\u0440";
  private static final String DEPARTMENT = "\u0425\u044d\u043b\u0442\u044d\u0441";
  private static final String ROLE = "\u0410\u043B\u0431\u0430";
  private static final String STATUS = "Танилцсан хувь";
  private static final String SCORE = "\u041e\u043d\u043e\u043e";
  private static final String INITIALLAUNCHDATE = "\u0410\u043d\u0445\u0020\u0442\u0430\u043d\u0438\u043b\u0446\u0441\u0430\u043d";
  private static final String LASTLAUNCHDATE = "\u0421\u04af\u04af\u043b\u0434\u0020\u0442\u0430\u043d\u0438\u043b\u0446\u0441\u0430\u043d";
  private static final String TOTALTIME = "\u041d\u0438\u0439\u0442\u0020\u0445\u0443\u0433\u0430\u0446\u0430\u0430";
  private static final String FEEDBACK = "\u0410\u0441\u0443\u0443\u043b\u0433\u0430";
  private static final String ISLATE = "\u0425\u043E\u0446\u043E\u0440\u0441\u043E\u043D\u0020\u0431\u0430\u0439\u0434\u0430\u043B";
  private static final String[] HEADER_ARRAY = { NUMBER, USERNAME, DEPARTMENT, ROLE, STATUS, SCORE, INITIALLAUNCHDATE, LASTLAUNCHDATE, TOTALTIME, ISLATE, FEEDBACK };

  private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");

  private final CourseRepository courseRepository;
  private final GetPromotionAnalytics getPromotionAnalytics;

  public GenerateExcelReport(CourseRepository courseRepository, GetPromotionAnalytics getPromotionAnalytics)
  {
    this.courseRepository = Objects.requireNonNull(courseRepository, "CourseRepository cannot be null!");
    this.getPromotionAnalytics = Objects.requireNonNull(getPromotionAnalytics, "GetPromotionAnalytics use-case cannot be null!");
  }

  @Override
  public byte[] execute(GetPromotionAnalyticsInput input) throws UseCaseException
  {
    LocalDateTime dateTime = LocalDateTime.now();

    Set<PromotionAnalyticsOutput> output = getPromotionAnalytics.execute(input).getAnalyticData();
    GetCourseInput getCourseInput = new GetCourseInput(input.getPromotionId());
    GetCourse getCourse = new GetCourse(courseRepository);
    GetCourseOutput course = getCourse.execute(getCourseInput);
    String sheetName = ("Татаж авсан: " + dateTime.format(dateFormatter));
    String excelTitle = ("Нөхцөлийн нэр: " + course.getTitle());
    String sheetTitle = (excelTitle + "\n" + sheetName);
    Object[][] promotionAnalyticsArray = mapToArray(output);
    try (ByteArrayOutputStream os = new ByteArrayOutputStream())
    {
      ExcelWriterUtil.write(true, sheetTitle, HEADER_ARRAY, promotionAnalyticsArray, os);
      return os.toByteArray();
    }
    catch (IOException e)
    {
      throw new UseCaseException(e.getMessage(), e);
    }
  }

  private Object[][] mapToArray(Collection<PromotionAnalyticsOutput> promotionAnalyticsCollection)
  {
    List<PromotionAnalyticsOutput> sorted = promotionAnalyticsCollection.stream().sorted(Comparator.comparing(PromotionAnalyticsOutput::getUserName))
        .collect(Collectors.toList());

    Object[][] dataTable = new Object[sorted.size()][];
    int i = 0;
    String late = "Хоцорсон";
    String inTime = "Хугацаандаа";
    for (PromotionAnalyticsOutput row : sorted)
    {
      Object[] rowArray = { 0,
          row.getUserName() == null ? "" : row.getUserName(),
          row.getGroup() == null ? "" : row.getGroup(),
          row.getRole() == null ? "" : row.getRole(),
          row.getStatus() == null ? "" : Float.parseFloat(row.getStatus().toString()),
          row.getScore() == null ? "" : Integer.parseInt(row.getScore().toString()),
          row.getInitialLaunchDate() == null ? "" : row.getInitialLaunchDate(),
          row.getLastLaunchDate() == null ? "" : row.getLastLaunchDate(),
          row.getTotalTime() == null ? "" : row.getTotalTime(),
          row.getIsLate() ? late : inTime,
          row.getFeedback() == null ? "" : row.getFeedback()
      };
      dataTable[i++] = rowArray;
    }
    return dataTable;
  }
}
