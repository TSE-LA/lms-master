/*
 * (C)opyright, 2019, ERIN SYSTEMS LLC
 * The source code is protected by copyright laws and international copyright
 * treaties, as well as other intellectual property laws and treaties.
 * All rights reserved.
 */

package mn.erin.lms.legacy.infrastructure.scorm.repository.mongo;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
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
import org.springframework.data.mongodb.core.MongoTemplate;

import mn.erin.lms.base.aim.AccessIdentityManagement;
import mn.erin.lms.base.aim.user.LearnerId;
import mn.erin.lms.legacy.domain.scorm.factory.DataModelFactory;
import mn.erin.lms.legacy.domain.scorm.factory.UnsupportedDataModelException;
import mn.erin.lms.legacy.domain.scorm.model.DataModel;
import mn.erin.lms.legacy.domain.scorm.model.RuntimeData;
import mn.erin.lms.legacy.domain.scorm.model.RuntimeDataId;
import mn.erin.lms.legacy.domain.scorm.model.SCO;
import mn.erin.lms.legacy.domain.scorm.model.SCORMContent;
import mn.erin.lms.legacy.domain.scorm.model.SCORMContentId;
import mn.erin.lms.legacy.domain.scorm.repository.RuntimeDataRepository;
import mn.erin.lms.legacy.domain.scorm.repository.SCORMRepositoryException;

import static com.mongodb.client.model.Filters.and;
import static com.mongodb.client.model.Filters.eq;

/**
 * @author Bat-Erdene Tsogoo.
 */
public class MongoRuntimeDataRepository implements RuntimeDataRepository
{
  private static final String COLLECTION_NAME = "Run-Time Data";
  private static final String FIELD_ID = "_id";
  private static final String FIELD_SCORM_CONTENT_ID = "scormContentId";
  private static final String FIELD_SCORM_CONTENT_NAME = "scormContentName";
  private static final String FIELD_LEARNER_ID = "learnerId";
  private static final String FIELD_SCO_NAME = "scoName";
  private static final String FIELD_RUNTIME_DATA = "runtimeData";

  private static final String ERR_MSG_SCORM_CONTENT_ID = "SCORM content ID cannot be null!";
  private static final String ERR_MSG_ID = "Learner id cannot be null!";
  private static final String ERR_MSG_SCO_NAME = "SCO name cannot be null or blank!";

  private static final String SCORM_DATA_MODEL_DELIMITER = ".";
  private static final String DOT_REPLACEMENT = ",";

  private AccessIdentityManagement accessIdentityManagement;

  private final DataModelFactory dataModelFactory;
  protected final MongoCollection<Document> runtimeDataCollection;

  public MongoRuntimeDataRepository(MongoTemplate mongoTemplate, DataModelFactory dataModelFactory)
  {
    this.dataModelFactory = Objects.requireNonNull(dataModelFactory, "DataModelFactory cannot be null!");
    this.runtimeDataCollection = mongoTemplate.getCollection(COLLECTION_NAME);
  }

  @Inject
  public void setAccessIdentityManagement(AccessIdentityManagement accessIdentityManagement)
  {
    this.accessIdentityManagement = accessIdentityManagement;
  }

  @Override
  public List<RuntimeData> listRuntimeData(SCORMContentId scormContentId) throws SCORMRepositoryException
  {
    Validate.notNull(scormContentId, ERR_MSG_SCORM_CONTENT_ID);

    Bson filter = getFilter(scormContentId, null);

    FindIterable<Document> documents = runtimeDataCollection.find(filter);

    if (documents == null)
    {
      throw new SCORMRepositoryException("No runtime data was found on the SCORM content with the ID: [" + scormContentId.getId() + "]");
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

  @Override
  public List<RuntimeData> listRuntimeData(String learnerId)
  {
    throw new UnsupportedOperationException();
  }

  @Override
  public RuntimeData getRuntimeData(SCORMContentId scormContentId, String scoName) throws SCORMRepositoryException
  {
    Validate.notNull(scormContentId, ERR_MSG_SCORM_CONTENT_ID);
    Validate.notBlank(scoName, ERR_MSG_SCO_NAME);

    Bson filter = getFilter(scormContentId, scoName);

    FindIterable<Document> result = runtimeDataCollection.find(filter);

    if (result == null)
    {
      throw new SCORMRepositoryException("Runtime data of the SCO [" + scoName + "] was not found");
    }

    Iterator<Document> iterator = result.iterator();

    Document document = iterator.next();

    return mapToRuntimeData(document);
  }

  @Override
  public RuntimeData create(SCO sco)
  {
    Validate.notNull(sco, "SCO is required to create run-time data");
    LearnerId learnerId = new LearnerId(accessIdentityManagement.getCurrentUsername());

    Document data = getDefaultRuntimeData(learnerId.getId());

    Document document = new Document();
    ObjectId objectId = new ObjectId(new Date());

    SCORMContent scormContent = sco.getRootEntity();

    document.put(FIELD_ID, objectId);
    document.put(FIELD_LEARNER_ID, learnerId);
    document.put(FIELD_SCORM_CONTENT_ID, scormContent.getScormContentId());
    document.put(FIELD_SCORM_CONTENT_NAME, scormContent.getName());
    document.put(FIELD_SCO_NAME, sco.getName());
    document.put(FIELD_RUNTIME_DATA, data);

    runtimeDataCollection.insertOne(document);

    try
    {
      RuntimeData runtimeData = getRuntimeData(scormContent.getScormContentId(), sco.getName());
      runtimeData.setLearnerId(learnerId.getId());

      return runtimeData;
    }
    catch (SCORMRepositoryException e)
    {
      return null;
    }
  }

  @Override
  public Map<DataModel, Serializable> update(SCORMContentId scormContentId, String scoName, Map<String, Object> data)
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

      RuntimeDataInfo runtimeDataInfo = new RuntimeDataInfo(dataModel.getConstraint(), String.valueOf(dataEntry.getValue()));
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
  public void clear(SCORMContentId scormContentId)
  {
    Validate.notNull(scormContentId, ERR_MSG_SCORM_CONTENT_ID);

    Bson filter = getFilter(scormContentId, null);

    Document setData = new Document();
    setData.put(FIELD_RUNTIME_DATA, getDefaultRuntimeData(accessIdentityManagement.getCurrentUsername()));

    Document update = new Document();
    update.append("$set", setData);

    runtimeDataCollection.updateMany(filter, update);
  }

  @Override
  public void clearLearnersRuntimeData(SCORMContentId scormContentId, String id)
  {
    Validate.notNull(scormContentId, ERR_MSG_SCORM_CONTENT_ID);
    Validate.notNull(id, ERR_MSG_ID);
    runtimeDataCollection.deleteMany(and(eq(FIELD_SCORM_CONTENT_ID, scormContentId), eq("learnerId._id", id)));
  }

  @Override
  public void clear(SCORMContentId scormContentId, String scoName)
  {
    Validate.notNull(scormContentId, ERR_MSG_SCORM_CONTENT_ID);
    Validate.notBlank(scoName, ERR_MSG_SCO_NAME);

    Bson filter = getFilter(scormContentId, scoName);

    Document setData = new Document();
    setData.put(FIELD_RUNTIME_DATA, getDefaultRuntimeData(accessIdentityManagement.getCurrentUsername()));

    Document update = new Document();
    update.append("$set", setData);

    runtimeDataCollection.findOneAndUpdate(filter, update);
  }

  @Override
  public void delete(RuntimeDataId runtimeDataId) throws SCORMRepositoryException
  {
    Validate.notNull(runtimeDataId, "Runtime data ID cannot be null!");

    if (!ObjectId.isValid(runtimeDataId.getId()))
    {
      throw new SCORMRepositoryException("Invalid runtime data ID: " + runtimeDataId.getId());
    }

    runtimeDataCollection.findOneAndDelete(eq(FIELD_ID, new ObjectId(runtimeDataId.getId())));
  }

  @Override
  public void delete(SCORMContentId scormContentId)
  {
    Validate.notNull(scormContentId, ERR_MSG_SCORM_CONTENT_ID);

    runtimeDataCollection.deleteMany(eq(FIELD_SCORM_CONTENT_ID, scormContentId));
  }

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

      runtimeData.updateData(dataModel, runtimeDataInfo.getValue());
    }

    return runtimeData;
  }

  private Bson getFilter(SCORMContentId scormContentId, String scoName)
  {
    LearnerId learnerId = LearnerId.valueOf(accessIdentityManagement.getCurrentUsername());

    if (StringUtils.isBlank(scoName))
    {
      return and(eq(FIELD_SCORM_CONTENT_ID, scormContentId),
          eq(FIELD_LEARNER_ID, learnerId));
    }

    return and(eq(FIELD_SCORM_CONTENT_ID, scormContentId),
        eq(FIELD_LEARNER_ID, learnerId),
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
        data.put(fieldName, new RuntimeDataInfo(dataModel.getConstraint(), learnerId));
        continue;
      }

      data.put(fieldName, new RuntimeDataInfo(dataModel.getConstraint(), dataModel.getDefaultValue()));
    }

    return data;
  }
}
