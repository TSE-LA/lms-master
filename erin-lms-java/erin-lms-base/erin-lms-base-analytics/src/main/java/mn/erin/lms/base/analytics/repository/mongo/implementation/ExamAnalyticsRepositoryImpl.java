package mn.erin.lms.base.analytics.repository.mongo.implementation;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.validation.constraints.NotNull;

import com.mongodb.client.MongoCollection;
import org.bson.Document;

import mn.erin.lms.base.analytics.model.Analytic;
import mn.erin.lms.base.analytics.model.exam.ExamAnalytics;
import mn.erin.lms.base.analytics.repository.mongo.ExamAnalyticsRepository;
import mn.erin.lms.base.analytics.repository.mongo.constants.MongoCollectionProvider;
import mn.erin.lms.base.analytics.repository.mongo.constants.MongoFields;
import mn.erin.lms.base.analytics.repository.mongo.utils.QueryBuilder;
import mn.erin.lms.base.analytics.service.UserService;

import static mn.erin.lms.base.analytics.repository.mongo.constants.MongoExamAnalyticQueries.STAGE_EXAM_MATCH;
import static mn.erin.lms.base.analytics.repository.mongo.constants.MongoExamAnalyticQueries.STAGE_EXAM_PROJECT_FINAL;
import static mn.erin.lms.base.analytics.repository.mongo.constants.MongoExamAnalyticQueries.STAGE_LOOKUP_EXAM_CATEGORY_NAME;
import static mn.erin.lms.base.analytics.repository.mongo.constants.MongoExamAnalyticQueries.STAGE_LOOKUP_EXAM_RUNTIME_DATA;

import static mn.erin.lms.base.analytics.repository.mongo.utils.ArrayStringConverter.toArrayString;

/**
 * @author Byambajav
 */
public class ExamAnalyticsRepositoryImpl extends AnalyticRepository implements ExamAnalyticsRepository
{
  private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss'Z'");

  public ExamAnalyticsRepositoryImpl(MongoCollectionProvider mongoCollectionProvider, UserService userService)
  {
    super(mongoCollectionProvider, userService);
  }

  @Override
  public List<Analytic> getAll(Set<String> groups, Set<String>  categories, Set<String>  statuses, Set<String>  types, LocalDate startDate, LocalDate endDate)
  {
    return getAnalytic(groups, categories, statuses, types, startDate, endDate);
  }

  private List<Analytic> getAnalytic(Set<String> groups, Set<String>  categories, Set<String>  statuses, Set<String>  types, LocalDate startDate, LocalDate endDate)
  {
    MongoCollection<Document> examCollection = mongoCollectionProvider.getExamCollection();
    Map<String, String> matchReplacement = new HashMap<>();
    matchReplacement.put("categories", toArrayString(categories));
    matchReplacement.put("groups", toArrayString(groups));
    matchReplacement.put("startDate", startDate.atStartOfDay().format(DATE_TIME_FORMATTER));
    matchReplacement.put("endDate", endDate.atTime(LocalTime.MAX).format(DATE_TIME_FORMATTER));
    matchReplacement.put("statuses", toArrayString(statuses));
    matchReplacement.put("types", toArrayString(types));

    List<Document> pipeline = Arrays.asList(
        QueryBuilder.buildStage(STAGE_EXAM_MATCH, matchReplacement),
        QueryBuilder.parseStage(STAGE_LOOKUP_EXAM_CATEGORY_NAME),
        QueryBuilder.parseStage(STAGE_LOOKUP_EXAM_RUNTIME_DATA),
        QueryBuilder.parseStage(STAGE_EXAM_PROJECT_FINAL));

    return examCollection.aggregate(pipeline).map(this::toAnalytic).into(new ArrayList<>());
  }

  @NotNull
  private ExamAnalytics toAnalytic(Document document)
  {
    return new ExamAnalytics.Builder(document.getString(MongoFields.FIELD_ID))
        .withTitle(document.getString(MongoFields.FIELD_EXAM_TITLE))
        .withStatus(document.getString(MongoFields.FIELD_EXAM_STATUS))
        .withCategoryName(document.getString(MongoFields.FIELD_EXAM_CATEGORY_NAME))
        .withPassedCount(document.getInteger(MongoFields.FIELD_EXAM_PASSED_COUNT))
        .withDuration(document.getInteger(MongoFields.FIELD_EXAM_DURATION))
        .withAverageScore(document.getInteger(MongoFields.FIELD_EXAM_AVERAGE_SCORE))
        .withAverageSpentTime(document.getInteger(MongoFields.FIELD_EXAM_AVERAGE_SPENT_TIME))
        .withTotalRuntime(document.getInteger(MongoFields.FIELD_EXAM_TOTAL_RUNTIME))
        .withMaxScore(document.getInteger(MongoFields.FIELD_EXAM_MAX_SCORE))
        .withEnrollmentCount(document.get(MongoFields.FIELD_EXAM_ENROLLMENT_COUNT))
        .withQuestionCount(document.getInteger(MongoFields.FIELD_EXAM_QUESTION_COUNT))
        .build();
  }
}
