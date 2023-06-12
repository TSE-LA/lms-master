/*
 * (C)opyright, 2019, ERIN SYSTEMS LLC
 * The source code is protected by copyright laws and international copyright
 * treaties, as well as other intellectual property laws and treaties.
 * All rights reserved.
 */

package mn.erin.lms.legacy.domain.scorm.usecase.runtime;

import java.util.HashSet;
import java.util.Set;

import org.apache.commons.lang3.Validate;

/**
 * @author Bat-Erdene Tsogoo.
 */
public class RuntimeDataInput
{
  private final String scormContentId;
  private String scoName;
  private Set<String> learnerIds = new HashSet<>();

  public RuntimeDataInput(String scormContentId)
  {
    this.scormContentId = Validate.notBlank(scormContentId, "SCORM content ID is required!");
  }

  public RuntimeDataInput(String scormContentId, String scoName)
  {
    this.scormContentId = Validate.notBlank(scormContentId, "SCORM content ID is required!");
    this.scoName = Validate.notBlank(scoName, "SCO name cannot be null or blank!");
  }

  public String getScormContentId()
  {
    return scormContentId;
  }

  public String getScoName()
  {
    return scoName;
  }

  public void setScoName(String scoName)
  {
    this.scoName = Validate.notBlank(scoName, "SCO name cannot be null or blank!");
  }

  public Set<String> getLearnerIds()
  {
    return learnerIds;
  }

  public void addLearnerId(String learnerIds)
  {
    Validate.notNull(learnerIds, "Learner id cannot be null!");
    this.learnerIds.add(learnerIds);
  }
}
