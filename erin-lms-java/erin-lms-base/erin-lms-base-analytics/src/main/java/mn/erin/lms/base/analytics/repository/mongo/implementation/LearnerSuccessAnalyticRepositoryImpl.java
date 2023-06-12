package mn.erin.lms.base.analytics.repository.mongo.implementation;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.google.gson.Gson;
import org.apache.commons.lang3.Validate;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;
import org.jetbrains.annotations.NotNull;

import mn.erin.domain.aim.model.group.Group;
import mn.erin.lms.base.analytics.model.course.promotion.PromotionRuntimeData;
import mn.erin.lms.base.analytics.repository.mongo.LearnerSuccessAnalyticRepository;
import mn.erin.lms.base.analytics.repository.mongo.constants.MongoCollectionProvider;
import mn.erin.lms.base.analytics.repository.mongo.constants.MongoFields;
import mn.erin.lms.base.analytics.repository.mongo.utils.QueryBuilder;
import mn.erin.lms.base.analytics.service.UserService;
import mn.erin.lms.base.scorm.model.DataModel;
import mn.erin.lms.base.scorm.model.RuntimeData;
import mn.erin.lms.base.scorm.model.RuntimeDataId;
import mn.erin.lms.base.scorm.model.SCO;
import mn.erin.lms.base.scorm.model.SCORMContent;
import mn.erin.lms.base.scorm.model.SCORMContentId;

import static com.mongodb.client.model.Filters.and;
import static com.mongodb.client.model.Filters.eq;

/**
 * @author Oyungerel Chuluunsukh
 **/
public class LearnerSuccessAnalyticRepositoryImpl extends AnalyticRepository implements LearnerSuccessAnalyticRepository
{


  private static final String SCORM_DATA_MODEL_DELIMITER = ".";
  private static final String DOT_REPLACEMENT = ",";
  private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss'Z'");

  public LearnerSuccessAnalyticRepositoryImpl(MongoCollectionProvider mongoCollectionProvider, UserService userService)
  {
    super(mongoCollectionProvider, userService);
  }

  @Override
  public List<Double> listRunTimeDataProgress(String learnerId, LocalDate startDate, LocalDate endDate)
  {
    String stageMatch = "{ $match: { "
        + "\"learnerId._id\" : \"" + learnerId + "\" , "
        + "scoName: { $nin: [ \"Үнэлгээний хуудас\", \"ТЕСТ\" ] }, "
        + "$expr: { $and: [ "
        + "{ $gte: [ "
        + "{ $dateFromString: { "
        + "dateString: \"$runtimeData.erin,date,initial_launch.value\","
        + "format: \"%Y-%m-%d %H:%M:%S\","
        + "onError: ISODate(\"" + endDate.atTime(LocalTime.MAX).format(DATE_TIME_FORMATTER) + "\") "
        + "} }, "
        + "ISODate(\"" + startDate.atStartOfDay().format(DATE_TIME_FORMATTER) + "\") "
        + "] }, "
        + "{ $lte: [ "
        + "{ $dateFromString: { "
        + "dateString: \"$runtimeData.erin,date,initial_launch.value\", "
        + "format: \"%Y-%m-%d %H:%M:%S\", "
        + "onError: ISODate(\"" + startDate.atStartOfDay().format(DATE_TIME_FORMATTER) + "\") "
        + "} }, "
        + "ISODate(\"" + endDate.atTime(LocalTime.MAX).format(DATE_TIME_FORMATTER) + "\") "
        + "] }, ] }, } }";

    String stageGroup = "{ $group: { "
        + "_id: \"$scormContentId._id\", "
        + "progress: { $avg: { $convert: { input: \"$runtimeData.cmi,progress_measure.value\", to: \"double\" } } }, "
        + "} }";

    return mongoCollectionProvider.getLegacySCORMCollection().aggregate(Arrays.asList(
        Document.parse(stageMatch),
        Document.parse(stageGroup))).map(document -> document.getDouble("progress")).into(new ArrayList<>());
  }

  @Override
  public boolean isExistThisMonth(int year, int monthValue, String courseType)
  {
    Bson filter = and(eq(MongoFields.FIELD_COURSE_TYPE, courseType), eq(MongoFields.FIELD_YEAR, year), eq(MongoFields.FIELD_MONTH, monthValue));
    return mongoCollectionProvider.getLearnerSuccessCollection().countDocuments(filter) > 0;
  }

  @Override
  public boolean isExist(String learnerId, int year, int monthValue, String courseType)
  {
    Bson filter = and(eq(MongoFields.FIELD_LEARNER_ID, learnerId), eq(MongoFields.FIELD_YEAR, year), eq(MongoFields.FIELD_MONTH, monthValue), eq(MongoFields.FIELD_COURSE_TYPE, courseType));
    return mongoCollectionProvider.getLearnerSuccessCollection().countDocuments(filter) > 0;
  }

  @Override
  public List<RuntimeData> listRunTimeDataTestCompleted(String learnerId, LocalDate startDate, LocalDate endDate)
  {
    Validate.notBlank(learnerId, "Learner ID cannot be null or blank!");
    Validate.notNull(startDate, "Initial date cannot be null");

    String stageMatch = "{ $match: { "
        + "\"learnerId._id\" : \"" + learnerId + "\" , "
        + "scoName: \"ТЕСТ\", "
        + "\"runtimeData.cmi,score,max.value\" : { $ne: \"unknown\"}, "
        + "\"runtimeData.cmi,score,raw.value\" : { $ne: \"unknown\"}, "
        + "$expr: { $and: [ "
        + "{ $gte: [ "
        + "{ $dateFromString: { "
        + "dateString: \"$runtimeData.erin,date,initial_launch.value\","
        + "format: \"%Y-%m-%d %H:%M:%S\","
        + "onError: ISODate(\"" + endDate.atTime(LocalTime.MAX).format(DATE_TIME_FORMATTER) + "\"), "
        + "} }, "
        + "ISODate(\"" + startDate.atStartOfDay().format(DATE_TIME_FORMATTER) + "\") "
        + "] }, "
        + "{ $lte: [ "
        + "{ $dateFromString: { "
        + "dateString: \"$runtimeData.erin,date,initial_launch.value\", "
        + "format: \"%Y-%m-%d %H:%M:%S\", "
        + "onError: ISODate(\"" + startDate.atStartOfDay().format(DATE_TIME_FORMATTER) + "\"), "
        + "} }, "
        + "ISODate(\"" + endDate.atTime(LocalTime.MAX).format(DATE_TIME_FORMATTER) + "\") "
        + "] }, ] }, } }";

    List<Document> pipeline = Arrays.asList(QueryBuilder.parseStage(stageMatch));

    return mongoCollectionProvider.getLegacySCORMCollection()
        .aggregate(pipeline)
        .map(this::mapToRuntimeData)
        .into(new ArrayList<>());
  }

  @Override
  public void save(String learner, int score, int maxScore, String courseType, double performance, int year, int monthValue)
  {
    Document learnerSuccess = new Document();
    ObjectId objectId = new ObjectId(new Date());
    learnerSuccess.put(MongoFields.FIELD_ID, objectId);
    learnerSuccess.put(MongoFields.FIELD_LEARNER_ID, learner);
    learnerSuccess.put(MongoFields.FIELD_SCORE, score);
    learnerSuccess.put(MongoFields.FIELD_SCORM_MAX_SCORE, maxScore);
    learnerSuccess.put(MongoFields.FIELD_COURSE_TYPE, courseType);
    learnerSuccess.put(MongoFields.FIELD_PERFORMANCE, performance);
    learnerSuccess.put(MongoFields.FIELD_YEAR, year);
    learnerSuccess.put(MongoFields.FIELD_MONTH, monthValue);

    mongoCollectionProvider.getLearnerSuccessCollection().insertOne(learnerSuccess);
  }

  @Override
  public Set<String> getAllLearners(String tenantId)
  {
    List<Group> rootGroups;
    rootGroups = getAllRootGroups(tenantId);
    Set<String> allGroups = new HashSet<>();
    for (Group rootGroup : rootGroups)
    {
      allGroups.addAll(getSubGroupIds(rootGroup.getId().getId()));
    }
    return getLearners(allGroups);
  }

  @NotNull
  protected RuntimeData mapToRuntimeData(Document document)
  {
    Document scormContentIdAsDocument = (Document) document.get(MongoFields.FIELD_SCORM_CONTENT_ID);
    Document learnerIdAsDocument = (Document) document.get(MongoFields.FIELD_LEARNER_ID);

    SCORMContentId scormContentId = SCORMContentId.valueOf((String) scormContentIdAsDocument.get(MongoFields.FIELD_ID));
    String scormContentName = (String) document.get(MongoFields.FIELD_SCORM_CONTENT_NAME);
    String scoName = (String) document.get(MongoFields.FIELD_SCORM_SCO_NAME);
    String learnerId = (String) learnerIdAsDocument.get(MongoFields.FIELD_ID);

    SCORMContent scormContent = new SCORMContent(scormContentId, scormContentName);

    SCO sco = new SCO(scormContent, scoName);

    ObjectId objectId = document.get(MongoFields.FIELD_ID, ObjectId.class);

    RuntimeDataId runtimeDataId = RuntimeDataId.valueOf(objectId.toHexString());
    RuntimeData runtimeData = new RuntimeData(runtimeDataId, sco);
    runtimeData.setLearnerId(learnerId);

    Document runtimeDataAsDocument = (Document) document.get(MongoFields.FIELD_RUNTIME_DATA);

    Gson gson = new Gson();
    for (Map.Entry<String, Object> dataEntry : runtimeDataAsDocument.entrySet())
    {
      Document runtimeDataInfoAsDocument = (Document) dataEntry.getValue();

      PromotionRuntimeData runtimeDataInfo = gson.fromJson(runtimeDataInfoAsDocument.toJson(), PromotionRuntimeData.class);
      DataModel dataModel = new DataModel(dataEntry.getKey().replace(DOT_REPLACEMENT, SCORM_DATA_MODEL_DELIMITER), runtimeDataInfo.getConstraint());

      runtimeData.updateData(dataModel, (Serializable) runtimeDataInfo.getValue());
    }

    return runtimeData;
  }
}
