package mn.erin.lms.base.analytics.repository.mongo.implementation;

import java.time.LocalDate;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import org.apache.commons.lang3.StringUtils;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;
import org.jetbrains.annotations.NotNull;

import mn.erin.common.datetime.TimeUtils;
import mn.erin.domain.aim.model.group.Group;
import mn.erin.domain.aim.model.group.GroupId;
import mn.erin.domain.aim.model.membership.Membership;
import mn.erin.lms.base.analytics.exception.AnalyticsRepositoryException;
import mn.erin.lms.base.analytics.model.Analytic;
import mn.erin.lms.base.analytics.model.course.online_course.OnlineCourseStatistics;
import mn.erin.lms.base.analytics.repository.mongo.CourseStatisticsRepository;
import mn.erin.lms.base.analytics.repository.mongo.constants.MongoCollectionProvider;
import mn.erin.lms.base.analytics.repository.mongo.constants.MongoFields;
import mn.erin.lms.base.analytics.repository.mongo.utils.QueryBuilder;
import mn.erin.lms.base.analytics.service.UserService;
import mn.erin.lms.base.domain.model.course.CourseId;
import mn.erin.lms.base.scorm.model.SCORMTime;

import static com.mongodb.client.model.Filters.and;
import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Filters.in;
import static mn.erin.lms.base.analytics.repository.mongo.constants.MongoStatisticQueries.*;
import static mn.erin.lms.base.analytics.repository.mongo.utils.ArrayStringConverter.toArrayString;

/**
 * @author Munkh
 */
public class OnlineCourseStatisticsRepository extends AnalyticRepository implements CourseStatisticsRepository
{
  private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss'Z'");
  public OnlineCourseStatisticsRepository(MongoCollectionProvider mongoCollectionProvider,
      UserService userService)
  {
    super(mongoCollectionProvider, userService);
  }

  @Override
  public List<Analytic> getStatistics(CourseId courseId, GroupId groupId, LocalDate startDate, LocalDate endDate) throws AnalyticsRepositoryException
  {
    String contentId = getContentId(courseId);

    if (StringUtils.isBlank(contentId))
    {
      throw new AnalyticsRepositoryException("Course content ID with course ID: [" + courseId.getId() + "] is blank!");
    }

    List<Group> subGroups = getSubGroups(groupId.getId());
    Map<String, String> mappedGroups = new HashMap<>();

    for (Group group: subGroups)
    {
      mappedGroups.put(group.getId().getId(), group.getName());
    }
    Set<String> learners = getLearners(mappedGroups.keySet());
    Set<String> enrolledLearners = getEnrolledLearners(courseId, learners);

    List<Membership> memberships = getMemberships(mappedGroups.keySet());
    Map<String, Membership> mappedMemberships = new HashMap<>();

    for (Membership membership: memberships)
    {
      mappedMemberships.put(membership.getUsername(), membership);
    }

    MongoCollection<Document> scormCollection = mongoCollectionProvider.getSCORMCollection();

    Map<String, String> replacement = new HashMap<>();
    replacement.put("courseId", courseId.getId());
    replacement.put("contentId", contentId);
    replacement.put("startDate", startDate.atStartOfDay().format(DATE_TIME_FORMATTER));
    replacement.put("endDate", endDate.atTime(LocalTime.MAX).format(DATE_TIME_FORMATTER));
    replacement.put("enrolledLearners", toArrayString(enrolledLearners));

    List<Document> pipeline = Arrays.asList(
        QueryBuilder.buildStage(STAGE_STATISTICS_MATCH, replacement),
        QueryBuilder.parseStage(STAGE_STATISTICS_GROUP),
        QueryBuilder.buildStage(STAGE_LOOKUP_STATISTICS_CERTIFICATE, replacement),
        QueryBuilder.parseStage(STAGE_LOOKUP_STATISTICS_SURVEY),
        QueryBuilder.parseStage(STAGE_LOOKUP_STATISTICS_TEST),
        QueryBuilder.buildStage(STAGE_STATISTICS_PROJECT, replacement),
        QueryBuilder.buildStage(STAGE_STATISTICS_PROJECT_FINAL, replacement)
    );

    List<Analytic> result = scormCollection.aggregate(pipeline).map(document -> {
      String learner = document.getString(MongoFields.FIELD_ID);
      enrolledLearners.remove(learner);
      return toAnalytic(document, mappedGroups.get(mappedMemberships.get(learner).getGroupId().getId()));
    }).into(new ArrayList<>());

    // Fill empty learners
    result.addAll(
        enrolledLearners.stream()
            .map(learner -> new OnlineCourseStatistics.Builder(learner, mappedGroups.get(mappedMemberships.get(learner).getGroupId().getId())))
            .map(OnlineCourseStatistics.Builder::build)
            .collect(Collectors.toList())
    );

    return result;
  }

  private String getContentId(CourseId courseId) throws AnalyticsRepositoryException
  {
    MongoCollection<Document> courseCollection = mongoCollectionProvider.getCourseCollection();

    Bson filter = eq(MongoFields.FIELD_ID, new ObjectId(courseId.getId()));
    FindIterable<Document> result = courseCollection.find(filter);
    Iterator<Document> iterator = result.iterator();

    if (!iterator.hasNext())
    {
      throw new AnalyticsRepositoryException("Course with course ID: [" + courseId.getId() + "] not found!");
    }

    return iterator.next().getString(MongoFields.FIELD_COURSE_COURSE_CONTENT_ID);
  }

  private Set<String> getEnrolledLearners(CourseId courseId, Set<String> learners)
  {
    MongoCollection<Document> enrollmentCollection = mongoCollectionProvider.getCourseEnrollmentCollection();
    Bson filter = and(
        eq(MongoFields.FIELD_ENROLLMENT_COURSE_ID, courseId.getId()),
        in(MongoFields.FIELD_ENROLLMENT_LEARNER_ID, learners)
    );
    FindIterable<Document> result = enrollmentCollection.find(filter);
    Iterator<Document> iterator = result.iterator();

    Set<String> enrolledLearners = new HashSet<>();

    while (iterator.hasNext())
    {
      enrolledLearners.add(iterator.next().getString(MongoFields.FIELD_ENROLLMENT_LEARNER_ID));
    }

    return enrolledLearners;
  }

  @NotNull
  private Analytic toAnalytic(Document document, String groupName)
  {
    List<Long> totalTime = new ArrayList<>();
    // TODO: Added to the spent time after fixes on the runtime data
//    String surveySpentTime = document.getString(MongoFields.FIELD_LEARNER_SURVEY_SPENT_TIME);
    String spentTimeOnTest = document.getString(MongoFields.FIELD_LEARNER_TEST_SPENT_TIME)!=null ?
        SCORMTime.convertToHumanReadableTime(document.getString(MongoFields.FIELD_LEARNER_TEST_SPENT_TIME)): "00:00:00";
    List<Long> totalSpentTimeInMilliseconds = new ArrayList<>();
    for (String time: (List<String>) document.get(MongoFields.FIELD_LEARNER_SPENT_TIME))
    {
      totalTime.add(TimeUtils.convertToLongRepresentation(SCORMTime.convertToHumanReadableTime(time)));
      totalSpentTimeInMilliseconds.add(SCORMTime.convertSCORMTimeToMilliseconds(time));
    }
    Optional<Long> spentTimeInMilliseconds = totalSpentTimeInMilliseconds.stream().reduce(Long::sum);
    Optional<Long> spentTime = totalTime.stream().reduce(Long::sum);
    return new OnlineCourseStatistics.Builder(document.getString(MongoFields.FIELD_ID), groupName)
        .withSurvey(document.getString(MongoFields.FIELD_LEARNER_SURVEY))
        .withSpentTime(TimeUtils.convertToStringRepresentation(spentTime.orElse(0L)))
        .withStatus(document.getDouble(MongoFields.FIELD_LEARNER_PROGRESS))
        .withViews(document.getInteger(MongoFields.FIELD_LEARNER_VIEWS, 0))
        .withScore(document.getInteger(MongoFields.FIELD_LEARNER_SCORE, 0))
        .withMaxScore(document.getInteger(MongoFields.FIELD_LEARNER_MAX_SCORE, 0))
        .withFirstViewDate(document.getString(MongoFields.FIELD_LEARNER_FIRST_VIEW_DATE))
        .withLastViewDate(document.getString(MongoFields.FIELD_LEARNER_LAST_VIEW_DATE))
        .withReceivedCertificateDate(document.getString(MongoFields.FIELD_LEARNER_RECEIVED_CERTIFICATE_DATE))
        .withSpentTimeOnTest(spentTimeOnTest)
        .withSpentTimeInMilliseconds(spentTimeInMilliseconds.orElse(0L))
        .build();
  }
}
