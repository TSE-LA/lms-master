package mn.erin.lms.base.mongo.implementation;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import javax.inject.Inject;

import com.google.gson.Gson;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.FindOneAndUpdateOptions;
import com.mongodb.client.model.ReturnDocument;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;
import org.jetbrains.annotations.NotNull;

import mn.erin.lms.base.aim.user.LearnerId;
import mn.erin.lms.base.aim.user.LmsUser;
import mn.erin.lms.base.aim.LmsUserService;
import mn.erin.lms.base.scorm.RuntimeDataInfo;
import mn.erin.lms.base.scorm.factory.DataModelFactory;
import mn.erin.lms.base.scorm.factory.UnsupportedDataModelException;
import mn.erin.lms.base.scorm.model.DataModel;
import mn.erin.lms.base.scorm.model.RuntimeData;
import mn.erin.lms.base.scorm.model.RuntimeDataId;
import mn.erin.lms.base.scorm.model.SCO;
import mn.erin.lms.base.scorm.model.SCORMContent;
import mn.erin.lms.base.scorm.model.SCORMContentId;
import mn.erin.lms.base.scorm.repository.RuntimeDataRepository;
import mn.erin.lms.base.scorm.repository.SCORMRepositoryException;

import static com.mongodb.client.model.Filters.all;
import static com.mongodb.client.model.Filters.and;
import static com.mongodb.client.model.Filters.eq;

/**
 * @author Bat-Erdene Tsogoo.
 */
public class RuntimeDataRepositoryImpl implements RuntimeDataRepository
{
  private static final String FIELD_ID = "_id";
  private static final String FIELD_SCORM_CONTENT_ID = "scormContentId";
  private static final String FIELD_SCORM_CONTENT_ID_ID = "scormContentId._id";
  private static final String FIELD_SCORM_CONTENT_NAME = "scormContentName";
  private static final String FIELD_LEARNER_ID = "learnerId";
  private static final String FIELD_LEARNER_ID_ID = "learnerId._id";
  private static final String FIELD_SCO_NAME = "scoName";
  private static final String FIELD_RUNTIME_DATA = "runtimeData";

  private static final String ERR_MSG_SCORM_CONTENT_ID = "SCORM content ID cannot be null!";
  private static final String ERR_MSG_LEARNER_ID = "Learner id cannot be null!";
  private static final String ERR_MSG_SCO_NAME = "SCO name cannot be null or blank!";

  private static final String SCORM_DATA_MODEL_DELIMITER = ".";
  private static final String DOT_REPLACEMENT = ",";
  private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss'Z'");

  private final MongoCollection<Document> runtimeDataCollection;
  private final DataModelFactory dataModelFactory;

  private LmsUserService userService;

  public RuntimeDataRepositoryImpl(MongoCollection<Document> runtimeDataCollection, DataModelFactory dataModelFactory)
  {
    this.runtimeDataCollection = runtimeDataCollection;
    this.dataModelFactory = dataModelFactory;
  }

  @Inject
  public void setUserService(LmsUserService userService)
  {
    this.userService = userService;
  }

  @Override
  public List<RuntimeData> listRuntimeData(SCORMContentId scormContentId)
  {
    Validate.notNull(scormContentId, ERR_MSG_SCORM_CONTENT_ID);
    Bson filter = eq(FIELD_SCORM_CONTENT_ID, scormContentId);
    return listRuntimeData(filter);
  }

  @Override
  public List<RuntimeData> listRuntimeData(String learnerId)
  {
    Validate.notBlank(learnerId, "Learner ID cannot be null or blank!");
    Bson filter = eq(FIELD_LEARNER_ID_ID, learnerId);
    return listRuntimeData(filter);
  }

  @Override
  public Map<String, List<String>> listAllRuntimeData()
  {
    FindIterable<Document> documents = runtimeDataCollection.find();

    Iterator<Document> iterator = documents.iterator();

    Map<String, List<String>> allData = new HashMap<>();

    while (iterator.hasNext())
    {
      Document document = iterator.next();

      ObjectId objectId = document.get(FIELD_ID, ObjectId.class);
      String learnerId = document.getString(FIELD_LEARNER_ID_ID);

      allData.putIfAbsent(learnerId, new ArrayList<>());

      allData.get(learnerId).add(objectId.toHexString());
    }

    return allData;
  }

  @Override
  public List<RuntimeData> listRunTimeDataTestCompleted(String learnerId, LocalDate startDate, LocalDate endDate)
  {
    Validate.notBlank(learnerId, "Learner ID cannot be null or blank!");
    Validate.notNull(startDate, "Initial date cannot be null");

    String stageMatch = "{ $match: { "
        + "\"learnerId._id\" : \"" + learnerId + "\" , "
        + "scoName: \"ТЕСТ\", "
        + "\"runtimeData.cmi,score,max.data\" : { $ne: \"unknown\"}, "
        + "\"runtimeData.cmi,score,raw.data\" : { $ne: \"unknown\"}, "
        + "$expr: { $and: [ "
        + "{ $gte: [ "
        + "{ $dateFromString: { "
        + "dateString: \"$runtimeData.erin,date,initial_launch.data\","
        + "format: \"%Y-%m-%d %H:%M:%S\","
        + "onError: ISODate(\"" + endDate.atTime(LocalTime.MAX).format(DATE_TIME_FORMATTER) + "\"), "
        + "} }, "
        + "ISODate(\"" + startDate.atStartOfDay().format(DATE_TIME_FORMATTER) + "\") "
        + "] }, "
        + "{ $lte: [ "
        + "{ $dateFromString: { "
        + "dateString: \"$runtimeData.erin,date,initial_launch.data\", "
        + "format: \"%Y-%m-%d %H:%M:%S\", "
        + "onError: ISODate(\"" + startDate.atStartOfDay().format(DATE_TIME_FORMATTER) + "\"), "
        + "} }, "
        + "ISODate(\"" + endDate.atTime(LocalTime.MAX).format(DATE_TIME_FORMATTER) + "\") "
        + "] }, ] }, } }";

    return runtimeDataCollection.aggregate(Collections.singletonList(Document.parse(stageMatch))).map(this::mapToRuntimeData).into(new ArrayList<>());
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
        + "dateString: \"$runtimeData.erin,date,initial_launch.data\","
        + "format: \"%Y-%m-%d %H:%M:%S\","
        + "onError: ISODate(\"" + endDate.atTime(LocalTime.MAX).format(DATE_TIME_FORMATTER) + "\") "
        + "} }, "
        + "ISODate(\"" + startDate.atStartOfDay().format(DATE_TIME_FORMATTER) + "\") "
        + "] }, "
        + "{ $lte: [ "
        + "{ $dateFromString: { "
        + "dateString: \"$runtimeData.erin,date,initial_launch.data\", "
        + "format: \"%Y-%m-%d %H:%M:%S\", "
        + "onError: ISODate(\"" + startDate.atStartOfDay().format(DATE_TIME_FORMATTER) + "\") "
        + "} }, "
        + "ISODate(\"" + endDate.atTime(LocalTime.MAX).format(DATE_TIME_FORMATTER) + "\") "
        + "] }, ] }, } }";

    String stageGroup = "{ $group: { "
        + "_id: \"$scormContentId._id\", "
        + "progress: { $avg: { $convert: { input: \"$runtimeData.cmi,progress_measure.data\", to: \"double\" } } }, "
        + "} }";

    return runtimeDataCollection.aggregate(Arrays.asList(
        Document.parse(stageMatch),
        Document.parse(stageGroup))).map(document -> document.getDouble("progress")).into(new ArrayList<>());
  }

  @Override
  public List<RuntimeData> listRuntimeData(SCORMContentId scormContentId, String learnerId)
  {
    Bson filter = and(eq(FIELD_LEARNER_ID_ID, learnerId), eq(FIELD_SCORM_CONTENT_ID, scormContentId));
    return listRuntimeData(filter);
  }

  @Override
  public RuntimeData getRuntimeData(SCORMContentId scormContentId, String scoName) throws SCORMRepositoryException
  {
    Validate.notNull(scormContentId, ERR_MSG_SCORM_CONTENT_ID);
    Validate.notBlank(scoName, ERR_MSG_SCO_NAME);

    Bson filter = getFilter(scormContentId, scoName);

    FindIterable<Document> result = runtimeDataCollection.find(filter);
    Document document;
    try
    {
      Iterator<Document> iterator = result.iterator();
      document = iterator.next();
    }
    catch (NoSuchElementException e)
    {
      throw new SCORMRepositoryException("Runtime data of the SCO [" + scoName + "] was not found");
    }
    return mapToRuntimeData(document);
  }

  @Override
  public RuntimeData create(SCO sco)
  {
    Validate.notNull(sco, "SCO is required to create run-time data");
    LmsUser learner = Objects.requireNonNull(userService.getCurrentUser(), "Current user ID is null!");

    Document data = getDefaultRuntimeData(learner.getId().getId());

    Document document = new Document();
    ObjectId objectId = new ObjectId(new Date());

    SCORMContent scormContent = sco.getRootEntity();

    document.put(FIELD_ID, objectId);
    document.put(FIELD_LEARNER_ID, learner.getId());
    document.put(FIELD_SCORM_CONTENT_ID, scormContent.getScormContentId());
    document.put(FIELD_SCORM_CONTENT_NAME, scormContent.getName());
    document.put(FIELD_SCO_NAME, sco.getName());
    document.put(FIELD_RUNTIME_DATA, data);

    runtimeDataCollection.insertOne(document);

    try
    {
      RuntimeData runtimeData = getRuntimeData(scormContent.getScormContentId(), sco.getName());
      runtimeData.setLearnerId(learner.getId().getId());

      return runtimeData;
    }
    catch (SCORMRepositoryException e)
    {
      return null;
    }
  }

  @Override
  public Map<DataModel, Serializable> update(SCORMContentId scormContentId, String scoName, Map<String, Object> data) throws SCORMRepositoryException
  {
    Validate.notNull(scormContentId, ERR_MSG_SCORM_CONTENT_ID);
    Validate.notBlank(scoName, ERR_MSG_SCO_NAME);
    Validate.notEmpty(data, "Data to update cannot be null or empty!");

    Bson filter = getFilter(scormContentId, scoName);

    Document runtimeData = new Document();

    for (Map.Entry<String, Object> dataEntry : data.entrySet())
    {
      DataModel dataModel;

      try
      {
        dataModel = dataModelFactory.getInstance(dataEntry.getKey());
      }
      catch (UnsupportedDataModelException e)
      {
        continue;
      }

      RuntimeDataInfo runtimeDataInfo = RuntimeDataInfo.of(String.valueOf(dataEntry.getValue()), dataModel.getConstraint());
      String fieldName = dataModel.getName().replace(SCORM_DATA_MODEL_DELIMITER, DOT_REPLACEMENT);
      runtimeData.put(fieldName, runtimeDataInfo);
    }

    Document setData = new Document(FIELD_RUNTIME_DATA, runtimeData);
    Document update = new Document("$set", setData);

    FindOneAndUpdateOptions options = new FindOneAndUpdateOptions();
    options.upsert(false);
    options.returnDocument(ReturnDocument.AFTER);

    Document updated = runtimeDataCollection.findOneAndUpdate(filter, update, options);

    RuntimeData result = mapToRuntimeData(updated);
    return result.getData();
  }

  @Override
  public boolean delete(SCORMContentId scormContentId, String learnerId)
  {
    Validate.notNull(scormContentId, ERR_MSG_SCORM_CONTENT_ID);
    Validate.notBlank(learnerId, ERR_MSG_LEARNER_ID);
    runtimeDataCollection.deleteMany(and(eq(FIELD_SCORM_CONTENT_ID, scormContentId), eq(FIELD_LEARNER_ID_ID, learnerId)));
    return true;
  }

  @Override
  public boolean delete(String learnerId)
  {
    Validate.notBlank(learnerId, ERR_MSG_LEARNER_ID);
    runtimeDataCollection.deleteMany(eq("learnerId._id", learnerId));

    return true;
  }

  @Override
  public boolean hasRuntimeData(LearnerId learnerId)
  {
    return runtimeDataCollection.find(eq("learnerId._id", learnerId)).first() != null;
  }

  @Override
  public boolean delete(SCORMContentId scormContentId)
  {
    Validate.notNull(scormContentId, ERR_MSG_SCORM_CONTENT_ID);
    runtimeDataCollection.deleteMany(eq(FIELD_SCORM_CONTENT_ID, scormContentId));
    return true;
  }

  private List<RuntimeData> listRuntimeData(Bson filter)
  {
    FindIterable<Document> documents = runtimeDataCollection.find(filter);

    if (documents == null)
    {
      return Collections.emptyList();
    }

    Iterator<Document> iterator = documents.iterator();

    List<RuntimeData> result = new ArrayList<>();

    while (iterator.hasNext())
    {
      Document document = iterator.next();

      RuntimeData runtimeData = mapToRuntimeData(document);
      result.add(runtimeData);
    }

    return result;
  }

  @NotNull
  protected RuntimeData mapToRuntimeData(Document document)
  {
    Document scormContentIdAsDocument = (Document) document.get(FIELD_SCORM_CONTENT_ID);
    Document learnerIdAsDocument = (Document) document.get(FIELD_LEARNER_ID);

    SCORMContentId scormContentId = SCORMContentId.valueOf((String) scormContentIdAsDocument.get(FIELD_ID));
    String scormContentName = (String) document.get(FIELD_SCORM_CONTENT_NAME);
    String scoName = (String) document.get(FIELD_SCO_NAME);
    String learnerId = (String) learnerIdAsDocument.get(FIELD_ID);

    SCORMContent scormContent = new SCORMContent(scormContentId, scormContentName);

    SCO sco = new SCO(scormContent, scoName);

    ObjectId objectId = document.get(FIELD_ID, ObjectId.class);

    RuntimeDataId runtimeDataId = RuntimeDataId.valueOf(objectId.toHexString());
    RuntimeData runtimeData = new RuntimeData(runtimeDataId, sco);
    runtimeData.setLearnerId(learnerId);

    Document runtimeDataAsDocument = (Document) document.get(FIELD_RUNTIME_DATA);

    Gson gson = new Gson();
    for (Map.Entry<String, Object> dataEntry : runtimeDataAsDocument.entrySet())
    {
      Document runtimeDataInfoAsDocument = (Document) dataEntry.getValue();

      RuntimeDataInfo runtimeDataInfo = gson.fromJson(runtimeDataInfoAsDocument.toJson(), RuntimeDataInfo.class);
      DataModel dataModel = new DataModel(dataEntry.getKey().replace(DOT_REPLACEMENT, SCORM_DATA_MODEL_DELIMITER), runtimeDataInfo.getConstraint());

      runtimeData.updateData(dataModel, (Serializable) runtimeDataInfo.getData());
    }

    return runtimeData;
  }

  private Bson getFilter(SCORMContentId scormContentId, String scoName)
  {
    LmsUser currentUser = Objects.requireNonNull(userService.getCurrentUser(), "Current user ID is null!");

    if (StringUtils.isBlank(scoName))
    {
      return and(eq(FIELD_SCORM_CONTENT_ID, scormContentId),
          eq(FIELD_LEARNER_ID, currentUser.getId()));
    }

    return and(eq(FIELD_SCORM_CONTENT_ID, scormContentId),
        eq(FIELD_LEARNER_ID, currentUser.getId()),
        eq(FIELD_SCO_NAME, scoName));
  }

  private Document getDefaultRuntimeData(String learnerId)
  {
    Set<DataModel> allSupportedDataModels = dataModelFactory.getAllInstances();

    Document data = new Document();

    for (DataModel dataModel : allSupportedDataModels)
    {
      String fieldName = dataModel.getName().replace(SCORM_DATA_MODEL_DELIMITER, DOT_REPLACEMENT);

      if ("cmi,learner_id".equals(fieldName))
      {
        data.put(fieldName, RuntimeDataInfo.of(learnerId, dataModel.getConstraint()));
        continue;
      }

      data.put(fieldName, RuntimeDataInfo.of(dataModel.getDefaultValue(), dataModel.getConstraint()));
    }

    return data;
  }
}
