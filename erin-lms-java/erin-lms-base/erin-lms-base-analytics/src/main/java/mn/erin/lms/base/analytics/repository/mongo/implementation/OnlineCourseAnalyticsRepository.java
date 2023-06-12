package mn.erin.lms.base.analytics.repository.mongo.implementation;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.google.common.collect.Sets;
import com.mongodb.client.MongoCollection;
import org.bson.Document;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import mn.erin.domain.aim.model.group.GroupId;
import mn.erin.lms.base.analytics.model.Analytic;
import mn.erin.lms.base.analytics.model.course.online_course.OnlineCourseAnalytic;
import mn.erin.lms.base.analytics.repository.mongo.CourseAnalyticsRepository;
import mn.erin.lms.base.analytics.repository.mongo.constants.MongoCollectionProvider;
import mn.erin.lms.base.analytics.repository.mongo.constants.MongoFields;
import mn.erin.lms.base.analytics.repository.mongo.utils.QueryBuilder;
import mn.erin.lms.base.analytics.service.UserService;
import mn.erin.lms.base.domain.model.category.CourseCategoryId;
import mn.erin.lms.base.domain.model.course.CourseType;
import mn.erin.lms.base.scorm.model.SCORMTime;

import static mn.erin.lms.base.analytics.repository.mongo.utils.ArrayStringConverter.toArrayString;
import static mn.erin.lms.base.analytics.repository.mongo.constants.MongoCourseQueries.*;

/**
 * @author Munkh
 */
public class OnlineCourseAnalyticsRepository extends AnalyticRepository implements CourseAnalyticsRepository
{
  private static final Logger LOGGER = LoggerFactory.getLogger("performance");
  private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss'Z'");

  public OnlineCourseAnalyticsRepository(MongoCollectionProvider mongoCollectionProvider, UserService userService)
  {
    super(mongoCollectionProvider, userService);
  }

  @Override
  public List<Analytic> getAll(GroupId groupId, CourseCategoryId categoryId, LocalDate startDate, LocalDate endDate)
  {
    return getAnalytics(groupId, categoryId, null, startDate, endDate);
  }

  @Override
  public List<Analytic> getAll(GroupId groupId, CourseCategoryId categoryId, CourseType courseType, LocalDate startDate, LocalDate endDate)
  {
    return getAnalytics(groupId, categoryId, courseType, startDate, endDate);
  }

  private List<Analytic> getAnalytics(GroupId groupId, CourseCategoryId categoryId, CourseType courseType, LocalDate startDate, LocalDate endDate)
  {
    Instant start = Instant.now();
    Set<String> subGroups = getSubGroupIds(groupId.getId());
    Set<String> subCategories = getSubCategories(categoryId.getId());
    Set<String> learners = getLearners(subGroups);
    Set<String> parentGroups = getParentGroups(groupId.getId());

    MongoCollection<Document> courseCollection = mongoCollectionProvider.getCourseCollection();

    Map<String, String> matchReplacement = new HashMap<>();
    matchReplacement.put("publishStatus", "PUBLISHED");
    matchReplacement.put("startDate", startDate.atStartOfDay().format(DATE_TIME_FORMATTER));
    matchReplacement.put("endDate", endDate.atTime(LocalTime.MAX).format(DATE_TIME_FORMATTER));
    matchReplacement.put("subCategories", toArrayString(subCategories));
    matchReplacement.put("subGroups", toArrayString(subGroups));
    matchReplacement.put("parentGroups", toArrayString(parentGroups));
    matchReplacement.put("allGroups", toArrayString(merge(subGroups, parentGroups)));
    if (courseType != null)
    {
      matchReplacement.put("courseType", courseType.getType());
    }

    Map<String, String> enrollmentLookupReplacement = new HashMap<>();
    enrollmentLookupReplacement.put("learners", toArrayString(learners));

    List<Document> pipeline = Arrays.asList(
        QueryBuilder.buildStage(courseType == null ? STAGE_COURSE_MATCH : STAGE_COURSE_MATCH_BY_TYPE, matchReplacement),
        QueryBuilder.parseStage(STAGE_COURSE_PROJECT),
        QueryBuilder.parseStage(STAGE_LOOKUP_COURSE_CATEGORY_NAME),
        QueryBuilder.parseStage(STAGE_UNWIND_COURSE_CATEGORY_NAME),
        QueryBuilder.buildStage(STAGE_LOOKUP_COURSE_ENROLLMENT, enrollmentLookupReplacement),
        QueryBuilder.parseStage(STAGE_UNWIND_COURSE_ENROLLMENT),
        QueryBuilder.parseStage(STAGE_COURSE_AND_COURSE_ENROLLMENT_GROUP),
        QueryBuilder.parseStage(STAGE_LOOKUP_RUNTIME_DATA),
        QueryBuilder.parseStage(STAGE_LOOKUP_COURSE_CERTIFICATE),
        QueryBuilder.parseStage(STAGE_LOOKUP_SPENT_TIME_ON_TEST),
        QueryBuilder.parseStage(STAGE_COURSE_PROJECT_FINAL)
    );
    List<Analytic> result = courseCollection.aggregate(pipeline).map(this::toAnalytic).into(new ArrayList<>());
    Instant end = Instant.now();
    LOGGER.warn("Overall generating online course report time: [{}]", humanReadableFormat(Duration.between(start, end)));
    return result;
  }

  @NotNull
  private OnlineCourseAnalytic toAnalytic(Document document)
  {
    Document runtimeData = (Document) document.get(MongoFields.FIELD_COURSE_RUNTIME_DATA);
    Document certificate = (Document) document.get(MongoFields.FIELD_COURSE_RECEIVED_CERTIFICATE_COUNT);
    SCORMTime totalTime = new SCORMTime("PT0.0S");

    int learnerSizeWithTestData = 0;
    List<String> spentTime = (List<String>) document.get(MongoFields.FIELD_LEARNER_TEST_SPENT_TIME);
    if (spentTime != null)
    {
      for (String time : spentTime)
      {
        totalTime.add(new SCORMTime(time));
        learnerSizeWithTestData++;
      }
    }

    return new OnlineCourseAnalytic.Builder(document.getString(MongoFields.FIELD_ID))
        .withTitle(document.getString(MongoFields.FIELD_COURSE_TITLE))
        .withCourseType(document.getString(MongoFields.FIELD_COURSE_COURSE_TYPE))
        .withCategoryName(document.getString(MongoFields.FIELD_COURSE_CATEGORY_NAME))
        .withCourseContentId(document.getString(MongoFields.FIELD_COURSE_COURSE_CONTENT_ID))
        .withCertificate(document.getBoolean(MongoFields.FIELD_COURSE_HAS_CERTIFICATE, false))
        .withLearners(Sets.newHashSet((List < String >) document.get(MongoFields.FIELD_COURSE_LEARNERS)))
        .withReceivedCertificateCount(certificate != null ? certificate.getInteger(MongoFields.FIELD_COURSE_CERTIFICATE_COUNT, 0) : 0)
        .withTotalProgress(runtimeData != null ? runtimeData.getDouble(MongoFields.FIELD_COURSE_TOTAL_PROGRESS) : 0)
        .withViewersCount(runtimeData != null ? runtimeData.getInteger(MongoFields.FIELD_COURSE_VIEWERS_COUNT, 0) : 0)
        .withRepeatedViewersCount(runtimeData != null ? runtimeData.getInteger(MongoFields.FIELD_COURSE_REPEATED_VIEWERS_COUNT, 0) : 0)
        .withCompletedViewersCount(runtimeData != null ? runtimeData.getInteger(MongoFields.FIELD_COURSE_COMPLETED_VIEWERS_COUNT) : 0)
        .withSpentTimeOnTest(getAverageSpentTimeOnTest(totalTime, learnerSizeWithTestData))
        .withAverageScore(getAverageScore((List<String>)document.get(MongoFields.FIELD_LEARNER_SCORE), learnerSizeWithTestData))
        .withMaxScore(getMaxScore((List<String>)document.get(MongoFields.FIELD_LEARNER_MAX_SCORE)))
        .build();
  }

  private Set<String> merge(Set<String> first, Set<String> second)
  {
    Set<String> result = new HashSet<>();
    result.addAll(first);
    result.addAll(second);
    return result;
  }

  private int getAverageScore(List<String> learnerScores, int learnerSize) {
    int result = 0;
    if(learnerSize > 0 && !learnerScores.isEmpty()) {
      for(String score: learnerScores)
      {
        result += Integer.parseInt(score);
      }
      return result/learnerSize;
    }
    return result;
  }

  private int getMaxScore(List<String> maxScores){
    if(!maxScores.isEmpty()) {
     return Integer.parseInt(maxScores.get(0));
    }
    else {
      return 0;
    }
  }
  private String getAverageSpentTimeOnTest(SCORMTime totalSpentTime, int learnerSize){
    if(learnerSize > 0) {
      return SCORMTime.convertToHumanReadableTime(SCORMTime.convertMillisecondsToSCORMTime(SCORMTime.convertSCORMTimeToMilliseconds(totalSpentTime.getValue())/
          (long) learnerSize));
    }
    else {
      return SCORMTime.convertToHumanReadableTime(totalSpentTime.getValue());
    }
  }
}
