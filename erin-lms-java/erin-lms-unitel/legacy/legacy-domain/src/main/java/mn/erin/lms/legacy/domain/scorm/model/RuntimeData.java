/*
 * (C)opyright, 2019, ERIN SYSTEMS LLC
 * The source code is protected by copyright laws and international copyright
 * treaties, as well as other intellectual property laws and treaties.
 * All rights reserved.
 */

package mn.erin.lms.legacy.domain.scorm.model;

import java.io.Serializable;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import org.apache.commons.lang3.Validate;

import mn.erin.domain.base.model.Entity;

/**
 * The communication between LMS and SCORM content happens through "Run-Time" calls where data models
 * {@link DataModel} are used to track a learner's progress. Each SCO has their own run-time data
 * populated and SCOs do not exchange data with other SCOs.
 *
 * When a learner launches a SCO of a SCORM content, a new instance of Runtime Data will be created,
 * and that instance will represent a learning record of the learner on that particular SCO of a SCORM content.
 *
 * @author Bat-Erdene Tsogoo.
 */
public class RuntimeData implements Entity<RuntimeData>
{
  private final RuntimeDataId id;
  private final SCO sco;

  private String learnerId;

  private Map<DataModel, Serializable> data = new HashMap<>();

  public RuntimeData(RuntimeDataId id, SCO sco)
  {
    this.id = Objects.requireNonNull(id, "Runtime data ID cannot be null!");
    this.sco = Objects.requireNonNull(sco, "SCO cannot be null!");
  }

  @Override
  public boolean sameIdentityAs(RuntimeData other)
  {
    return other != null && this.id.equals(other.id);
  }

  public void updateData(DataModel dataModel, Serializable data)
  {
    Validate.notNull(dataModel, "Data model element cannot be null!");
    Validate.notNull(data, "Data cannot be null!");

    this.data.put(dataModel, data);
  }

  public void clearData()
  {
    this.data.clear();
  }

  public RuntimeDataId getRuntimeDataId()
  {
    return id;
  }

  public SCO getSco()
  {
    return sco;
  }

  public Map<DataModel, Serializable> getData()
  {
    return Collections.unmodifiableMap(data);
  }

  public String getLearnerId()
  {
    return learnerId;
  }

  public void setLearnerId(String learnerId)
  {
    this.learnerId = learnerId;
  }
}
