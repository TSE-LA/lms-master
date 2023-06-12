/*
 * (C)opyright, 2019, ERIN SYSTEMS LLC
 * The source code is protected by copyright laws and international copyright
 * treaties, as well as other intellectual property laws and treaties.
 * All rights reserved.
 */

package mn.erin.lms.legacy.infrastructure.scorm.repository.mongo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import com.mongodb.client.FindIterable;
import org.apache.commons.lang3.Validate;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.springframework.data.mongodb.core.MongoTemplate;

import mn.erin.lms.legacy.domain.scorm.factory.DataModelFactory;
import mn.erin.lms.legacy.domain.scorm.model.RuntimeData;
import mn.erin.lms.legacy.domain.scorm.model.SCORMContentId;

import static com.mongodb.client.model.Filters.eq;

/**
 * @author Bat-Erdene Tsogoo.
 */
public class RuntimeDataStore extends MongoRuntimeDataRepository
{
  public RuntimeDataStore(MongoTemplate mongoTemplate,
      DataModelFactory dataModelFactory)
  {
    super(mongoTemplate, dataModelFactory);
  }

  @Override
  public List<RuntimeData> listRuntimeData(String learnerId)
  {
    Validate.notBlank(learnerId, "Learner ID cannot be null or blank!");
    Bson filter = eq("learnerId._id", learnerId);

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

  @Override
  public List<RuntimeData> listRuntimeData(SCORMContentId scormContentId)
  {
    Validate.notNull(scormContentId, "SCORM content ID cannot be null!");
    Bson filter = eq("scormContentId", scormContentId);

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
}
