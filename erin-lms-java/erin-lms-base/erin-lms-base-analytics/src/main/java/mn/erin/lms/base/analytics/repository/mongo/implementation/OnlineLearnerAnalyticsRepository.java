package mn.erin.lms.base.analytics.repository.mongo.implementation;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.mongodb.client.MongoCollection;
import org.bson.Document;
import org.jetbrains.annotations.NotNull;

import mn.erin.lms.base.aim.user.LearnerId;
import mn.erin.lms.base.analytics.model.Analytic;
import mn.erin.lms.base.analytics.model.learner.LearnerAnalytic;
import mn.erin.lms.base.analytics.repository.mongo.LearnerAnalyticsRepository;
import mn.erin.lms.base.analytics.repository.mongo.constants.MongoCollectionProvider;
import mn.erin.lms.base.analytics.repository.mongo.constants.MongoFields;
import mn.erin.lms.base.analytics.repository.mongo.utils.QueryBuilder;
import mn.erin.lms.base.analytics.service.UserService;
import mn.erin.lms.base.domain.model.category.CourseCategoryId;
import mn.erin.lms.base.domain.model.course.CourseId;
import mn.erin.lms.base.scorm.model.SCORMTime;

import static mn.erin.lms.base.analytics.repository.mongo.constants.MongoLearnerQueries.STAGE_COURSE_GROUP;
import static mn.erin.lms.base.analytics.repository.mongo.constants.MongoLearnerQueries.STAGE_COURSE_MATCH;
import static mn.erin.lms.base.analytics.repository.mongo.constants.MongoLearnerQueries.STAGE_COURSE_PROJECT;
import static mn.erin.lms.base.analytics.repository.mongo.constants.MongoLearnerQueries.STAGE_COURSE_PROJECT_FINAL;
import static mn.erin.lms.base.analytics.repository.mongo.constants.MongoLearnerQueries.STAGE_LEARNER_CERTIFICATE_LOOKUP;
import static mn.erin.lms.base.analytics.repository.mongo.constants.MongoLearnerQueries.STAGE_LOOKUP_COURSE_CATEGORY_NAME;
import static mn.erin.lms.base.analytics.repository.mongo.constants.MongoLearnerQueries.STAGE_RUNTIME_DATA_LOOKUP;
import static mn.erin.lms.base.analytics.repository.mongo.utils.ArrayStringConverter.toArrayString;

/**
 * @author Munkh
 */
public class OnlineLearnerAnalyticsRepository extends AnalyticRepository implements LearnerAnalyticsRepository
{
  private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss'Z'");
  private static final String DATE_TIME_FORMAT = "%Y-%m-%d %H:%M:%S";

  public OnlineLearnerAnalyticsRepository(MongoCollectionProvider mongoCollectionProvider, UserService userService)
  {
    super(mongoCollectionProvider, userService);
  }

  @Override
  public List<Analytic> getAnalytics(LearnerId learnerId, CourseCategoryId categoryId, LocalDate startDate, LocalDate endDate)
  {
    Set<String> enrolledCourses = getEnrolledCourses(learnerId.getId());
    Set<String> subCategories = getSubCategories(categoryId.getId());

    MongoCollection<Document> courseCollection = mongoCollectionProvider.getCourseCollection();

    Map<String, String> matchReplacement = new HashMap<>();
    matchReplacement.put("startDate", startDate.atStartOfDay().format(DATE_TIME_FORMATTER));
    matchReplacement.put("endDate", endDate.atTime(LocalTime.MAX).format(DATE_TIME_FORMATTER));
    matchReplacement.put("enrolledCourses", toArrayString(enrolledCourses));
    matchReplacement.put("subCategories", toArrayString(subCategories));

    Map<String, String> certificateLookupReplacement = new HashMap<>();
    certificateLookupReplacement.put("learnerId", learnerId.getId());

    Map<String, String> runtimeDataLookupReplacement = new HashMap<>();
    runtimeDataLookupReplacement.put("learnerId", learnerId.getId());
    runtimeDataLookupReplacement.put("dateTimeFormat", DATE_TIME_FORMAT);

    List<Document> pipeline = Arrays.asList(
        QueryBuilder.buildStage(STAGE_COURSE_MATCH, matchReplacement),
        QueryBuilder.parseStage(STAGE_COURSE_GROUP),
        QueryBuilder.parseStage(STAGE_LOOKUP_COURSE_CATEGORY_NAME),
        QueryBuilder.buildStage(STAGE_LEARNER_CERTIFICATE_LOOKUP, certificateLookupReplacement),
        QueryBuilder.buildStage(STAGE_RUNTIME_DATA_LOOKUP, runtimeDataLookupReplacement),
        QueryBuilder.parseStage(STAGE_COURSE_PROJECT),
        QueryBuilder.parseStage(STAGE_COURSE_PROJECT_FINAL)
    );
    return courseCollection.aggregate(pipeline).map(this::toAnalytic).into(new ArrayList<>());
  }

  @NotNull
  private LearnerAnalytic toAnalytic(Document document)
  {
    LearnerAnalytic.Builder builder = new LearnerAnalytic.Builder(CourseId.valueOf(document.getString(MongoFields.FIELD_ID)))
        .withTitle(document.getString(MongoFields.FIELD_LEARNER_COURSE_TITLE))
        .withCategory(document.getString(MongoFields.FIELD_LEANER_COURSE_CATEGORY))
        .withCourseType(document.getString(MongoFields.FIELD_LEARNER_COURSE_COURSE_TYPE));

    Object receivedCertificateDateObject = document.get(MongoFields.FIELD_LEARNER_RECEIVED_CERTIFICATE_DATE);

    LocalDateTime receivedCertificateDate = null;
    if (receivedCertificateDateObject instanceof LocalDateTime)
    {
      receivedCertificateDate = (LocalDateTime) receivedCertificateDateObject;
    }
    else if (receivedCertificateDateObject instanceof Date)
    {
      receivedCertificateDate = ((Date) receivedCertificateDateObject).toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
    }

    builder = builder.withReceivedCertificateDate(receivedCertificateDate);

    SCORMTime totalTime = new SCORMTime("PT0.0S");
    List<String> spentTime = (List<String>) document.get(MongoFields.FIELD_LEARNER_SPENT_TIME);
    if (spentTime != null)
    {
      for (String time : spentTime)
      {
        totalTime.add(new SCORMTime(time));
      }
    }
    String spentTimeOnTest = (document.getString(MongoFields.FIELD_LEARNER_TEST_SPENT_TIME)) != null
        ? (document.getString(MongoFields.FIELD_LEARNER_TEST_SPENT_TIME)) : "PT0.0S";
    String spentTimeOnTestConverted = SCORMTime.convertToHumanReadableTime(spentTimeOnTest);
    builder = builder.withSpentTime(SCORMTime.convertToHumanReadableTime(totalTime.getValue()));
    builder = builder.withSpentTimeRatio("");
    builder = builder.withSpentTimeOnTest(spentTimeOnTestConverted);
    builder = builder.withStatus(document.get(MongoFields.FIELD_LEARNER_PROGRESS) != null ? document.getDouble(MongoFields.FIELD_LEARNER_PROGRESS) : 0);
    builder = builder.withViews(document.get(MongoFields.FIELD_LEARNER_VIEWS) != null ? document.getInteger(MongoFields.FIELD_LEARNER_VIEWS) : 0);
    builder = builder.hasSurvey(document.get(MongoFields.FIELD_LEARNER_SURVEY) != null);

    try
    {
      builder = builder.withScore(Integer.parseInt(document.getString(MongoFields.FIELD_LEARNER_SCORE)));
    }
    catch (NullPointerException | NumberFormatException ignored)
    {
      // Ignored because score already 0
    }

    String firstViewDate = document.getString(MongoFields.FIELD_LEARNER_FIRST_VIEW_DATE);
    builder = builder.withFirstViewDate(getDate(firstViewDate));

    String lastViewDate = document.getString(MongoFields.FIELD_LEARNER_LAST_VIEW_DATE);
    builder = builder.withLastViewDate(getDate(lastViewDate));

    return builder.build();
  }
}
