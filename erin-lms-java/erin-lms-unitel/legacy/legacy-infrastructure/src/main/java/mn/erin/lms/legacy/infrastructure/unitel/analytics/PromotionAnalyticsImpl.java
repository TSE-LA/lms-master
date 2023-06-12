package mn.erin.lms.legacy.infrastructure.unitel.analytics;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import javax.inject.Inject;

import mn.erin.domain.aim.model.user.UserId;
import mn.erin.domain.aim.repository.GroupRepository;
import mn.erin.domain.aim.repository.MembershipRepository;
import mn.erin.lms.legacy.domain.lms.model.course.AuthorId;
import mn.erin.lms.legacy.domain.lms.model.course.Course;
import mn.erin.lms.legacy.domain.lms.model.course.CourseCategoryId;
import mn.erin.lms.legacy.domain.lms.model.course.PublishStatus;
import mn.erin.lms.legacy.domain.lms.model.enrollment.LearnerId;
import mn.erin.lms.legacy.domain.lms.repository.CourseGroupRepository;
import mn.erin.lms.legacy.domain.scorm.constants.DataModelConstants;
import mn.erin.lms.legacy.domain.scorm.model.DataModel;
import mn.erin.lms.legacy.domain.scorm.model.RuntimeData;
import mn.erin.lms.legacy.domain.scorm.model.SCORMContentId;
import mn.erin.lms.legacy.domain.scorm.model.SCORMTime;
import mn.erin.lms.legacy.domain.scorm.repository.RuntimeDataRepository;
import mn.erin.lms.legacy.domain.scorm.repository.SCORMRepositoryException;
import mn.erin.lms.legacy.domain.unitel.model.PromotionAnalyticData;
import mn.erin.lms.legacy.domain.unitel.service.PromotionAnalytics;
import mn.erin.lms.legacy.infrastructure.lms.repository.CourseAnalyticsImpl;
import org.apache.commons.lang3.Validate;
import org.bson.conversions.Bson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.mongodb.core.MongoTemplate;

import static com.mongodb.client.model.Filters.and;
import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Filters.gte;
import static com.mongodb.client.model.Filters.lte;

/**
 * @author Bat-Erdene Tsogoo.
 */
public class PromotionAnalyticsImpl extends CourseAnalyticsImpl implements PromotionAnalytics
{
  private static final Logger LOGGER = LoggerFactory.getLogger(PromotionAnalyticsImpl.class);

  protected final RuntimeDataRepository runtimeDataRepository;

  @Inject
  private CourseGroupRepository courseGroupRepository;
  @Inject
  private GroupRepository groupRepository;
  @Inject
  private MembershipRepository membershipRepository;

  public PromotionAnalyticsImpl(MongoTemplate mongoTemplate, RuntimeDataRepository runtimeDataRepository)
  {
    super(mongoTemplate);
    this.runtimeDataRepository = Objects.requireNonNull(runtimeDataRepository, "RuntimeDataRepository cannot be null!");
  }

  @Override
  public Set<PromotionAnalyticData> getPromotionAnalyticData(LearnerId learnerId)
  {
    List<RuntimeData> runtimeData = runtimeDataRepository.listRuntimeData(learnerId.getId());
    Set<PromotionAnalyticData> result = new HashSet<>();

    Map<String, List<RuntimeData>> sortedByContent = sortByContentId(runtimeData);

    for (Map.Entry<String, List<RuntimeData>> dataEntry : sortedByContent.entrySet())
    {
      result.add(convert(dataEntry.getKey(), dataEntry.getValue()));
    }

    return result;
  }

  @Override
  public Set<PromotionAnalyticData> getPromotionAnalyticData(String promotionContentId)
  {
    Set<PromotionAnalyticData> result = new HashSet<>();
    try
    {
      List<RuntimeData> runtimeData = runtimeDataRepository.listRuntimeData(SCORMContentId.valueOf(promotionContentId));

      Map<String, List<RuntimeData>> sortedByUser = sortByUser(runtimeData);
      for (Map.Entry<String, List<RuntimeData>> dataEntry : sortedByUser.entrySet())
      {
        result.add(convert(dataEntry.getKey(), dataEntry.getValue()));
      }
    }
    catch (SCORMRepositoryException e)
    {
      return Collections.emptySet();
    }

    return result;
  }

  @Override
  public Integer countCreatedCourses(CourseCategoryId courseCategoryId, PublishStatus publishStatus, Map<String, Object> properties, Set<String> groupIds)
  {
    Validate.notNull(courseCategoryId, "Course category ID cannot be null!");
    Validate.notNull(publishStatus, "Publish status cannot be null!");

    List<Bson> filters = getCountFilterBsons(courseCategoryId, publishStatus, properties);

    Bson filter = and(filters);
    List<Course> courses = getCourseList(filter);
    courses = courses.stream().filter(course -> groupIds.contains(course.getCourseId().getId())).collect(Collectors.toList());

    return courses.size();
  }

  @Override
  public Integer countCreatedCourses(CourseCategoryId courseCategoryId, PublishStatus publishStatus, AuthorId authorId, Map<String, Object> properties,
      Set<String> groupIds)
  {
    Validate.notNull(courseCategoryId, "Course category ID cannot be null!");
    Validate.notNull(publishStatus, "Publish status cannot be null!");
    Validate.notNull(authorId, "Author id cannot be null!");

    List<Bson> filters = getCountFilterBsons(courseCategoryId, publishStatus, properties);
    filters.add(eq(FIELD_AUTHOR_ID, new UserId(authorId.getId())));

    Bson filter = and(filters);
    List<Course> courses = getCourseList(filter);
    courses = courses.stream().filter(course -> groupIds.contains(course.getCourseId().getId())).collect(Collectors.toList());
    return courses.size();
  }

  private List<Bson> getCountFilterBsons(CourseCategoryId courseCategoryId, PublishStatus publishStatus, Map<String, Object> properties)
  {
    Map<String, Object> filteredProperties = new HashMap<>(properties);

    Date startDate = (Date) properties.get(PROPERTY_KEY_START_DATE);
    Date endDate = (Date) properties.get(PROPERTY_KEY_END_DATE);

    filteredProperties.remove(PROPERTY_KEY_START_DATE);
    filteredProperties.remove(PROPERTY_KEY_END_DATE);

    List<Bson> filters = getPropertiesFilters(filteredProperties);
    filters.add(and(lte("courseDetail.createdDate", endDate), gte("courseDetail.createdDate", startDate)));
    filters.add(eq(FIELD_COURSE_CATEGORY_ID, courseCategoryId));
    filters.add(eq(EMBEDDED_FIELD_PUBLISH_STATUS, publishStatus.name()));
    return filters;
  }

  protected PromotionAnalyticData convert(String contentId, List<RuntimeData> runtimeData)
  {
    PromotionAnalyticData.Builder builder = new PromotionAnalyticData.Builder();
    builder = builder.ofContentId(contentId);

    float progress = 0;
    SCORMTime totalTime = new SCORMTime("PT0.0S");
    Date initialLaunchDate = null;
    Date lastLaunchDate = null;
    Integer score = null;
    Integer maxScore = null;
    Integer totalEnrollment = 0;
    int scoSize = runtimeData.size();
    int interactionsCount = 0;

    for (RuntimeData datum : runtimeData)
    {
      Map<DataModel, Serializable> data = datum.getData();
      if (!datum.getSco().getName().equalsIgnoreCase("асуулга") &&
          !datum.getSco().getName().equalsIgnoreCase("тест"))
      {
        for (Map.Entry<DataModel, Serializable> dataEntry : data.entrySet())
        {
          switch (dataEntry.getKey().getName())
          {
          case DataModelConstants.CMI_PROGRESS_MEASURE:
            progress += Float.parseFloat((String) dataEntry.getValue());
            break;
          case "erin.date.initial_launch":
            Date initDate = getDate((String) dataEntry.getValue());
            if ((initialLaunchDate == null) || (initDate != null && initialLaunchDate.getTime() > initDate.getTime()))
            {
              initialLaunchDate = initDate;
            }
            break;
          case "erin.date.last_launch":
            Date endDate = getDate((String) dataEntry.getValue());
            if (lastLaunchDate == null || (endDate != null && lastLaunchDate.getTime() < endDate.getTime()))
            {
              lastLaunchDate = endDate;
            }
            break;
          case DataModelConstants.CMI_TOTAL_TIME:
            totalTime.add(new SCORMTime((String) dataEntry.getValue()));
            break;
          case DataModelConstants.CMI_INTERACTIONS_COUNT:
            int count = Integer.parseInt((String) dataEntry.getValue());
            interactionsCount = Math.max(count, interactionsCount);
            break;
          default:
            break;
          }
        }
      }
      else if (datum.getSco().getName().equalsIgnoreCase("тест"))
      {
        for (Map.Entry<DataModel, Serializable> dataEntry : data.entrySet())
        {
          if (DataModelConstants.CMI_SCORE_RAW.equalsIgnoreCase(dataEntry.getKey().getName()))
          {
            String value = (String) dataEntry.getValue();
            score = "unknown".equals(value) ? null : Integer.parseInt(value);
          }
          if (DataModelConstants.CMI_SCORE_MAX.equalsIgnoreCase(dataEntry.getKey().getName()))
          {
            String maxScoreValue = (String) dataEntry.getValue();
            maxScore = "unknown".equals(maxScoreValue) ? null : Integer.parseInt(maxScoreValue);
          }
        }
        scoSize -= 1;
      }
      else
      {
        for (Map.Entry<DataModel, Serializable> dataEntry : data.entrySet())
        {
          if ("cmi.comments_from_learner.1.comment".equals(dataEntry.getKey().getName()))
          {
            builder = builder.withFeedback((String) dataEntry.getValue());
          }
          if (DataModelConstants.CMI_TOTAL_TIME.equals(dataEntry.getKey().getName()))
          {
            totalTime.add(new SCORMTime((String) dataEntry.getValue()));
          }
        }
        scoSize -= 1;
      }
    }

    builder = builder.withTotalTime(SCORMTime.convertToHumanReadableTime(totalTime.getValue()));

    builder = builder.withScore(score);
    builder = builder.havingMaxScore(maxScore);
    builder = builder.withTotalEnrollment(totalEnrollment);
    builder = builder.startedAt(initialLaunchDate);
    builder = builder.lastLaunchedAt(lastLaunchDate);
    builder = builder.withStatus(progress / scoSize);
    builder = builder.havingInteractionsCount(interactionsCount);
    return builder.build();
  }

  private Date getDate(String dateStringRepresentation)
  {
    try
    {
      SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm");
      return formatter.parse(dateStringRepresentation);
    }
    catch (ParseException e)
    {
      return null;
    }
  }

  private Map<String, List<RuntimeData>> sortByContentId(List<RuntimeData> allRuntimeData)
  {
    Map<String, List<RuntimeData>> sorted = new HashMap<>();

    Set<String> contentIds = allRuntimeData.stream().map(datum -> datum.getSco().getRootEntity().getScormContentId().getId())
        .collect(Collectors.toSet());

    for (String contentId : contentIds)
    {
      List<RuntimeData> data = allRuntimeData.stream().filter(datum -> contentId.equals(datum.getSco().getRootEntity().getScormContentId().getId()))
          .collect(Collectors.toList());
      sorted.put(contentId, data);
    }

    return sorted;
  }

  private Map<String, List<RuntimeData>> sortByUser(List<RuntimeData> allRuntimeData)
  {
    Map<String, List<RuntimeData>> sorted = new HashMap<>();

    for (RuntimeData datum : allRuntimeData)
    {
      for (Map.Entry<DataModel, Serializable> entry : datum.getData().entrySet())
      {
        if (DataModelConstants.CMI_LEARNER_ID.equals(entry.getKey().getName()))
        {
          String user = (String) entry.getValue();
          sorted.computeIfAbsent(user, k -> new ArrayList<>());
          sorted.get(user).add(datum);
        }
      }
    }

    return sorted;
  }
}
