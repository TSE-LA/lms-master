package mn.erin.lms.base.analytics.repository.mongo.implementation;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import com.mongodb.client.MongoCollection;
import org.apache.commons.collections4.BidiMap;
import org.apache.commons.collections4.bidimap.DualHashBidiMap;
import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import mn.erin.domain.aim.model.group.Group;
import mn.erin.domain.aim.model.group.GroupId;
import mn.erin.domain.aim.model.membership.Membership;
import mn.erin.common.datetime.TimeUtils;
import mn.erin.lms.base.analytics.exception.AnalyticsRepositoryException;
import mn.erin.lms.base.analytics.model.Analytic;
import mn.erin.lms.base.analytics.model.course.promotion.PromotionAnalytic;
import mn.erin.lms.base.analytics.repository.mongo.PromotionAnalyticsRepository;
import mn.erin.lms.base.analytics.repository.mongo.constants.MongoCollectionProvider;
import mn.erin.lms.base.analytics.repository.mongo.constants.MongoFields;
import mn.erin.lms.base.analytics.repository.mongo.constants.MongoPromotionQueries;
import mn.erin.lms.base.analytics.repository.mongo.document.MongoAggregatedRuntime;
import mn.erin.lms.base.analytics.repository.mongo.document.PromotionRuntimeData;
import mn.erin.lms.base.analytics.repository.mongo.utils.QueryBuilder;
import mn.erin.lms.base.analytics.service.UserService;
import mn.erin.lms.base.analytics.usecase.dto.LearnerActivity;
import mn.erin.lms.base.domain.model.course.CourseType;
import mn.erin.lms.base.domain.util.DepartmentPathUtil;
import mn.erin.lms.base.scorm.model.SCORMTime;

import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Filters.in;
import static mn.erin.lms.base.analytics.repository.mongo.constants.MongoPromotionQueries.*;
import static mn.erin.lms.base.analytics.repository.mongo.utils.ArrayStringConverter.toArrayString;

/**
 * @author Munkh
 */
public class PromotionAnalyticsRepositoryImpl extends AnalyticRepository implements PromotionAnalyticsRepository
{
  private static final Logger LOGGER = LoggerFactory.getLogger("performance");
  private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss'Z'");
  private static final DateFormat DATE_TIME_FORMAT = new SimpleDateFormat("yyyy-MM-dd");
  private static final String PUBLISHED = "PUBLISHED";
  public PromotionAnalyticsRepositoryImpl(MongoCollectionProvider mongoCollectionProvider,
      UserService userService)
  {
    super(mongoCollectionProvider, userService);
  }

  @Override
  public List<Analytic> getAllAnalytics(GroupId groupId, LocalDate startDate, LocalDate endDate) throws AnalyticsRepositoryException
  {
    return getAnalytics(groupId, null, null, startDate, endDate);
  }

  @Override
  public List<Analytic> getAllAnalytics(GroupId groupId, String categoryName, LocalDate startDate, LocalDate endDate) throws AnalyticsRepositoryException
  {
    return getAnalytics(groupId, categoryName, null, startDate, endDate);
  }

  @Override
  public List<Analytic> getAllAnalytics(GroupId groupId, CourseType courseType, LocalDate startDate, LocalDate endDate) throws AnalyticsRepositoryException
  {
    return getAnalytics(groupId, null, courseType, startDate, endDate);
  }

  @Override
  public List<Analytic> getAllAnalytics(GroupId groupId, String categoryName, CourseType courseType, LocalDate startDate, LocalDate endDate)
      throws AnalyticsRepositoryException
  {
    return getAnalytics(groupId, categoryName, courseType, startDate, endDate);
  }

  private List<Analytic> getAnalytics(GroupId groupId, String categoryName, CourseType courseType, LocalDate startDate, LocalDate endDate)
      throws AnalyticsRepositoryException
  {
    String matcher;

    if (categoryName != null && courseType != null)
    {
      matcher = STAGE_PROMOTION_MATCH_FULL;
    }
    else if (categoryName == null && courseType != null)
    {
      matcher = STAGE_PROMOTION_MATCH_BY_STATE;
    }
    else if (categoryName != null && courseType == null)
    {
      matcher = STAGE_PROMOTION_MATCH_BY_CATEGORY_ID;
    }
    else
    {
      matcher = STAGE_PROMOTION_MATCH;
    }

    Instant start = Instant.now();
    LOGGER.warn("Starting to generate promotion report from [{}] to [{}]", startDate, endDate);
    Set<String> groups = getSubGroupIds(groupId.getId());
    Set<String> courses = getAllCourses(groups);
    Set<String> learners = getLearners(groups);

    Map<String, String> matchReplacement = new HashMap<>();
    matchReplacement.put("courses", toArrayString(courses));
    matchReplacement.put("learners", toArrayString(learners));
    matchReplacement.put("publishStatus", PUBLISHED);
    matchReplacement.put("startDate", startDate.atStartOfDay().format(DATE_TIME_FORMATTER));
    matchReplacement.put("endDate", endDate.atTime(LocalTime.MAX).format(DATE_TIME_FORMATTER));
    if (categoryName != null)
    {
      matchReplacement.put("categoryId", getCategoryId(categoryName));
    }
    if (courseType != null)
    {
      matchReplacement.put("courseType", courseType.getType());
    }

    MongoCollection<Document> courseCollection = mongoCollectionProvider.getLegacyCourseCollection();
    List<Document> courseDocuments = courseCollection.aggregate(Arrays.asList(
        QueryBuilder.parseStage(STAGE_PROMOTION_PROJECT),
        QueryBuilder.buildStage(matcher, matchReplacement),
        QueryBuilder.parseStage(STAGE_PROMOTION_PROJECT_FINAL)
    )).into(new ArrayList<>());

    Map<String, List<String>> enrollments = getEnrollments(courses, learners);
    Map<String, Integer> questions = getQuestions(courses);

    Map<String, List<String>> contents = new HashMap<>();
    for (Document course: courseDocuments)
    {
      String id = course.getString(MongoFields.FIELD_ID);
      String contentId = course.getString(MongoFields.FIELD_CONTENT_ID);
      contents.put(contentId, enrollments.get(id));
    }

    Instant beforeRuntime = Instant.now();
    Map<String, PromotionRuntimeData> runtimes = getRuntimeData(contents, learners);
    Instant afterRuntime = Instant.now();
    LOGGER.warn("Getting promotion report runtime data: [{}]", humanReadableFormat(Duration.between(beforeRuntime, afterRuntime)));

    List<Analytic> result = new ArrayList<>();
    for (Document document: courseDocuments)
    {
      String id = document.getString(MongoFields.FIELD_ID);
      String contentId = document.getString(MongoFields.FIELD_CONTENT_ID);

      if (enrollments.get(id) == null || enrollments.get(id).isEmpty())
      {
        continue;
      }

      int totalEnrollment = enrollments.get(id).size();

      boolean hasFeedback = document.getBoolean(MongoFields.FIELD_HAS_FEEDBACK) != null && document.getBoolean(MongoFields.FIELD_HAS_FEEDBACK);
      boolean hasTest = document.getBoolean(MongoFields.FIELD_HAS_TEST) != null && document.getBoolean(MongoFields.FIELD_HAS_TEST);
      Date createdDate = document.getDate(MongoFields.FIELD_CREATED_DATE);

      PromotionAnalytic.Builder builder = new PromotionAnalytic.Builder(id)
          .withCode(document.getString(MongoFields.FIELD_CODE))
          .withName(document.getString(MongoFields.FIELD_NAME))
          .withAuthor(document.getString(MongoFields.FIELD_AUTHOR))
          .hasTest(hasTest)
          .withQuestionCount(hasTest && questions.get(id) != null ? questions.get(id) : 0)
          .withCreatedDate(createdDate != null ? DATE_TIME_FORMAT.format(createdDate) : null)
          .withTotalEnrollment(totalEnrollment);

      PromotionRuntimeData runtimeData = runtimes.get(contentId);

      if (runtimeData != null && totalEnrollment != 0)
      {
        builder = builder.withViews(runtimeData.getViews())
            .withFeedback(hasFeedback ? runtimeData.getFeedback() : 0)
            .withScore(hasTest && runtimeData.getScore() != 0 ? (double) runtimeData.getScore() / totalEnrollment : 0)
            .withStatus(runtimeData.getProgress() != 0 ? runtimeData.getProgress() / totalEnrollment : 0);
      }

      result.add(builder.build());
    }
    Instant end = Instant.now();
    LOGGER.warn("Overall generating promotion report time: [{}]", humanReadableFormat(Duration.between(start, end)));

    return result;
  }

  private Map<String, PromotionRuntimeData> getRuntimeData(Map<String, List<String>> contents, Set<String> learners)
  {
    Map<String, String> replacement = new HashMap<>();
    replacement.put("learners", toArrayString(learners));
    replacement.put("contents", toArrayString(contents.keySet()));

    MongoCollection<Document> scormCollection = mongoCollectionProvider.getLegacySCORMCollection();

    // Gets progress and views
    List<Document> documents = scormCollection.aggregate(Arrays.asList(
        QueryBuilder.buildStage(STAGE_PROMOTION_SCORM_MATCH, replacement),
        QueryBuilder.parseStage(STAGE_PROMOTION_SCORM_GROUP)
//        QueryBuilder.parseStage(STAGE_UNWIND_PROMOTION_SCORM_GROUP),
//        QueryBuilder.parseStage(STAGE_PROMOTION_SCORM_GROUP_BY_CONTENT)
    )).into(new ArrayList<>());

    Map<String, Double> mappedProgress = contents.entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey, content -> 0.0));
    Map<String, Integer> mappedToInteger = contents.entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey, content -> 0));
    Map<String, Integer> mappedViews = new HashMap<>(mappedToInteger);
    Map<String, Integer> mappedFeedbacks = new HashMap<>(mappedToInteger);
    Map<String, Integer> mappedScores = new HashMap<>(mappedToInteger);

    for (Document document: documents)
    {
      String contentId = ((Document) document.get(MongoFields.FIELD_ID)).getString(MongoFields.FIELD_CONTENT_ID);
      String learnerId = ((Document) document.get(MongoFields.FIELD_ID)).getString(MongoFields.FIELD_LEARNER_ID);

      if (contents.get(contentId) == null ||
          contents.get(contentId).isEmpty() ||
          !contents.get(contentId).contains(learnerId))
      {
        continue;
      }

      double progress = mappedProgress.get(contentId);
      progress += document.getDouble(MongoFields.FIELD_PROGRESS);
      mappedProgress.put(contentId, progress);

      List<Integer> views = (List<Integer>) document.get(MongoFields.FIELD_VIEWS);
      if (views != null && !views.isEmpty() && views.stream().reduce(Integer::sum).orElse(0) > 0)
      {
        mappedViews.put(contentId, mappedViews.get(contentId) + 1);
      }
    }

    // Gets feedback count
    List<Document> feedbacks = scormCollection.aggregate(Arrays.asList(
        QueryBuilder.buildStage(STAGE_PROMOTION_SCORM_FEEDBACK_MATCH, replacement),
        QueryBuilder.parseStage(STAGE_PROMOTION_SCORM_FEEDBACK_PROJECT)
    )).into(new ArrayList<>());

    for (Document document: feedbacks)
    {
      String contentId = document.getString(MongoFields.FIELD_CONTENT_ID);
      String learnerId = document.getString(MongoFields.FIELD_LEARNER_ID);
      if (contents.get(contentId) != null && contents.get(contentId).contains(learnerId))
      {
        mappedFeedbacks.put(contentId, mappedFeedbacks.get(contentId) + 1);
      }
    }

    // Gets scores
    List<Document> tests = scormCollection.aggregate(Arrays.asList(
        QueryBuilder.buildStage(STAGE_PROMOTION_SCORM_TEST_MATCH, replacement),
        QueryBuilder.parseStage(STAGE_PROMOTION_SCORM_TEST_PROJECT)
    )).into(new ArrayList<>());

    for (Document document: tests)
    {
      String contentId = document.getString(MongoFields.FIELD_CONTENT_ID);
      String learnerId = document.getString(MongoFields.FIELD_LEARNER_ID);
      if (contents.get(contentId) != null && contents.get(contentId).contains(learnerId))
      {
        mappedScores.put(contentId, mappedScores.get(contentId) + document.getInteger(MongoFields.FIELD_SCORE, 0));
      }
    }

    Map<String, PromotionRuntimeData> result = new HashMap<>();

    for (String id: contents.keySet())
    {
      result.put(id, new PromotionRuntimeData(
          id,
          mappedProgress.get(id) != null ? mappedProgress.get(id) : 0.0,
          mappedFeedbacks.get(id) != null ? mappedFeedbacks.get(id) : 0,
          mappedScores.get(id) != null ? mappedScores.get(id) : 0,
          mappedViews.get(id) != null ? mappedViews.get(id) : 0
      ));
    }

    return result;
  }

  private Set<String> getAllCourses(Set<String> groups)
  {
    MongoCollection<Document> courseGroupCollection = mongoCollectionProvider.getLegacyCourseGroupCollection();
    Iterator<Document> iterator = courseGroupCollection.find(in(MongoFields.FIELD_GROUP_ID, groups)).iterator();

    Set<String> result = new HashSet<>();

    while (iterator.hasNext())
    {
      Document document = iterator.next();
      Document courseId = (Document) document.get(MongoFields.FIELD_COURSE_ID);
      result.add(courseId.getString(MongoFields.FIELD_ID));
    }

    return result;
  }

  private String getCategoryId(String categoryName) throws AnalyticsRepositoryException
  {
    MongoCollection<Document> categoryCollection = mongoCollectionProvider.getLegacyCategoryCollection();
    Iterator<Document> iterator = categoryCollection.find(eq(MongoFields.FIELD_CATEGORY_NAME, categoryName)).iterator();
    if (!iterator.hasNext())
    {
      throw new AnalyticsRepositoryException("Category with name ["+ categoryName +"] not found!");
    }

    return iterator.next().getObjectId(MongoFields.FIELD_ID).toHexString();
  }

  private Map<String, List<String>> getEnrollments(Set<String> courses, Set<String> learners)
  {
    Map<String, String> replacement = new HashMap<>();
    replacement.put("courses", toArrayString(courses));
    replacement.put("learners", toArrayString(learners));
    List<Document> enrollments = mongoCollectionProvider.getLegacyEnrollmentCollection().aggregate(Arrays.asList(
        QueryBuilder.buildStage(STAGE_MATCH_ENROLLMENTS, replacement),
        QueryBuilder.parseStage(STAGE_GROUP_ENROLLMENTS)
    )).into(new ArrayList<>());

    Map<String, List<String>> result = new HashMap<>();
    for (Document document: enrollments)
    {
      result.put(document.getString(MongoFields.FIELD_ID), (List<String>) document.get(MongoFields.FIELD_ENROLLMENTS));
    }

    return result;
  }

  private Map<String, Integer> getQuestions(Set<String> courses)
  {
    Map<String, String> replacement = new HashMap<>();
    replacement.put("courses", toArrayString(courses));
    List<Document> tests = mongoCollectionProvider.getLegacyAssessmentCollection().aggregate(Arrays.asList(
        QueryBuilder.buildStage(STAGE_MATCH_TESTS, replacement),
        QueryBuilder.parseStage(STAGE_OPEN_UP_TEST_IDS_OF_TESTS),
        QueryBuilder.parseStage(STAGE_PROJECT_TESTS)
    )).into(new ArrayList<>());

    Map<String, String> mappedTests = new HashMap<>();
    for (Document document: tests)
    {
      mappedTests.put(document.getString(MongoFields.FIELD_ID), document.getString(MongoFields.FIELD_ASSESSMENT_TEST_ID));
    }

    replacement.put("tests", toArrayString(new HashSet<>(mappedTests.values())));
    List<Document> questions = mongoCollectionProvider.getLegacyTestCollection().aggregate(Arrays.asList(
        QueryBuilder.parseStage(STAGE_PROJECT_QUESTIONS),
        QueryBuilder.buildStage(STAGE_MATCH_QUESTIONS, replacement)
    )).into(new ArrayList<>());

    Map<String, Integer> mappedQuestions = new HashMap<>();
    for (Document document: questions)
    {
      mappedQuestions.put(document.getString(MongoFields.FIELD_ID), document.getInteger(MongoFields.FIELD_QUESTION_COUNT, 0));
    }

    Map<String, Integer> result = new HashMap<>();

    for (Map.Entry<String, String> entry: mappedTests.entrySet())
    {
      result.put(entry.getKey(), mappedQuestions.get(entry.getValue()));
    }

    return result;
  }

  // Activity
  @Override
  public List<LearnerActivity> getAllActivities(GroupId groupId)
  {
    List<Group> subGroups = getSubGroups(groupId.getId());
    Map<String, String> mappedGroups = new HashMap<>();

    for (Group group: subGroups)
    {
      mappedGroups.put(group.getId().getId(), group.getName());
    }

    // Memberships to get role name
    List<Membership> memberships = getMemberships(mappedGroups.keySet());
    Map<String, Membership> mappedMemberships = new HashMap<>();

    for (Membership membership: memberships)
    {
      mappedMemberships.put(membership.getUsername(), membership);
    }

    Set<String> learners = getLearners(mappedGroups.keySet());

    MongoCollection<Document> courseCollection = mongoCollectionProvider.getLegacyCourseCollection();
    // First query: gets all the courses
    List<Document> allCourseDocuments = courseCollection.aggregate(
        Collections.singletonList(
            Document.parse(MongoPromotionQueries.STAGE_PROMOTION_COURSE_MATCH)
        )).into(new ArrayList<>());

    BidiMap<String, String> mappedCourses = new DualHashBidiMap<>();

    for (Document document: allCourseDocuments)
    {
      String id = document.getString(MongoFields.FIELD_ID);
      String contentId = document.getString(MongoFields.FIELD_CONTENT_ID);
      if (id != null && contentId != null)
      {
        mappedCourses.put(id, contentId);
      }
    }

    MongoCollection<Document> enrollmentCollection = mongoCollectionProvider.getLegacyEnrollmentCollection();
    // Second query: gets all enrollments
    List<Document> enrollmentPipeline = Arrays.asList(
        Document.parse(String.format(
            MongoPromotionQueries.STAGE_PROMOTION_ENROLLMENT_MATCH,
            toArrayString(learners),
            toArrayString(mappedCourses.keySet())
        )),
        Document.parse(MongoPromotionQueries.STAGE_PROMOTION_ENROLLMENT_GROUP)
    );

    List<Document> enrollmentDocuments = enrollmentCollection.aggregate(enrollmentPipeline).into(new ArrayList<>());

    Map<String, List<String>> mappedEnrollments = new HashMap<>();

    for (Document document: enrollmentDocuments)
    {
      String learnerId = document.getString(MongoFields.FIELD_ID);
      List<String> courses = (List<String>) document.get(MongoFields.FIELD_COURSES);
      mappedEnrollments.put(learnerId, courses);
    }

    MongoCollection<Document> scormCollection = mongoCollectionProvider.getLegacySCORMCollection();
    // Third query: gets all runtime data
    Map<String, String> scormMatcher = new HashMap<>();
    scormMatcher.put("learners", toArrayString(learners));
    scormMatcher.put("contents", toArrayString(mappedCourses.values()));
    List<Document> scormPipeline = Arrays.asList(
        QueryBuilder.buildStage(MongoPromotionQueries.STAGE_PROMOTION_SCORM_MATCH, scormMatcher),
        QueryBuilder.parseStage(MongoPromotionQueries.STAGE_PROMOTION_SCORM_GROUP_BY_LEARNER)
    );

    List<Document> scormDocuments = scormCollection.aggregate(scormPipeline).allowDiskUse(true).into(new ArrayList<>());

    List<LearnerActivity> result = new ArrayList<>();

    for (Document document: scormDocuments)
    {
      String learner = document.getString(MongoFields.FIELD_ID);

      if (!mappedEnrollments.containsKey(learner))
      {
        continue;
      }

      List<Document> runtimeDocuments = (List<Document>) document.get(MongoFields.FIELD_RUNTIME_DATA);

      Map<String, MongoAggregatedRuntime> mappedByContent = new HashMap<>();
      for (Document runtimeDocument: runtimeDocuments)
      {
        String contentId = runtimeDocument.getString(MongoFields.FIELD_CONTENT_ID);

        // Runtime data enrollment validation
        if (!mappedCourses.containsValue(contentId) ||
            !mappedEnrollments.get(learner).contains(mappedCourses.getKey(contentId)))
        {
          continue;
        }

        double progress = runtimeDocument.getDouble(MongoFields.FIELD_PROGRESS);
        String spentTime = runtimeDocument.getString(MongoFields.FIELD_SPENT_TIME);

        if (mappedByContent.containsKey(contentId))
        {
          MongoAggregatedRuntime runtime = mappedByContent.get(contentId);
          runtime.addTotalProgress(progress);
          runtime.addSpentTimes(spentTime);
        }
        else
        {
          List<String> times = new ArrayList<>();
          times.add(spentTime);
          MongoAggregatedRuntime runtime = new MongoAggregatedRuntime(progress, times);
          mappedByContent.put(contentId, runtime);
        }
      }

      double totalProgress = 0;
      List<Long> totalTime = new ArrayList<>();
      for( Map.Entry<String, MongoAggregatedRuntime> contentId: mappedByContent.entrySet())
      {
        MongoAggregatedRuntime runtime = mappedByContent.get(contentId.getKey());
        totalProgress += runtime.getTotalProgress() != 0 ? runtime.getTotalProgress() / runtime.getCount() : 0;
        for (String time: runtime.getSpentTimes())
        {
          totalTime.add(TimeUtils.convertToLongRepresentation(SCORMTime.convertToHumanReadableTime(time)));
        }
      }

      Membership membership = mappedMemberships.get(learner);

      result.add(new LearnerActivity(
          learner,
          totalProgress / mappedEnrollments.get(learner).size(),
          membership.getRoleId().getId(),
          DepartmentPathUtil.getPath(groupId.getId(), new HashSet<>(subGroups)).get(membership.getGroupId().getId()).getPath(),
          TimeUtils.convertToStringRepresentation(getMedianTime(totalTime))
      ));

      learners.remove(learner);
    }

    for (String learner: learners)
    {
      Membership membership = mappedMemberships.get(learner);
      result.add(new LearnerActivity(
          learner,
          0,
          membership.getRoleId().getId(),
          DepartmentPathUtil.getPath(groupId.getId(), new HashSet<>(subGroups)).get(membership.getGroupId().getId()).getPath(),
          SCORMTime.convertToHumanReadableTime("PT0.0S")
      ));
    }

    return result;
  }
}
