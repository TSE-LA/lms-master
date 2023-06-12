/*
 * (C)opyright, 2019, ERIN SYSTEMS LLC
 * The source code is protected by copyright laws and international copyright
 * treaties, as well as other intellectual property laws and treaties.
 * All rights reserved.
 */

package mn.erin.lms.legacy.domain.scorm.usecase.runtime;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import mn.erin.lms.legacy.domain.scorm.model.DataModel;
import mn.erin.lms.legacy.domain.scorm.model.RuntimeData;
import mn.erin.lms.legacy.domain.scorm.model.SCO;
import mn.erin.lms.legacy.domain.scorm.repository.RuntimeDataRepository;

/**
 * @author Bat-Erdene Tsogoo.
 */
abstract class SCORMRuntimeUseCase
{
  protected final RuntimeDataRepository runtimeDataRepository;

  SCORMRuntimeUseCase(RuntimeDataRepository runtimeDataRepository)
  {
    this.runtimeDataRepository = Objects.requireNonNull(runtimeDataRepository, "RuntimeDataRepository cannot be null!");
  }

  Map<String, RuntimeDataInfo> convertRuntimeData(Map<DataModel, Serializable> runtimeData)
  {
    Map<String, RuntimeDataInfo> result = new HashMap<>();

    for (Map.Entry<DataModel, Serializable> data : runtimeData.entrySet())
    {
      DataModel dataModel = data.getKey();
      if (dataModel != null)
      {
        result.put(dataModel.getName(),
            RuntimeDataInfo.of(data.getValue(), dataModel.getConstraint()));
      }
    }

    return result;
  }

  RuntimeDataOutput toResult(RuntimeData runtimeData)
  {
    String runtimeDataId = runtimeData.getRuntimeDataId().getId();
    SCO sco = runtimeData.getSco();

    String scoName = sco.getName();
    String scormContentId = sco.getRootEntity().getScormContentId().getId();

    Map<String, RuntimeDataInfo> convertedData = convertRuntimeData(runtimeData.getData());

    return new RuntimeDataOutput(runtimeDataId, scormContentId, scoName, convertedData);
  }
}
