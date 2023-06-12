/*
 * (C)opyright, 2019, ERIN SYSTEMS LLC
 * The source code is protected by copyright laws and international copyright
 * treaties, as well as other intellectual property laws and treaties.
 * All rights reserved.
 */

package mn.erin.lms.legacy.domain.scorm.usecase.runtime;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import mn.erin.domain.base.usecase.UseCase;
import mn.erin.domain.base.usecase.UseCaseException;
import mn.erin.lms.legacy.domain.scorm.constants.DataModelConstants;
import mn.erin.lms.legacy.domain.scorm.model.DataModel;
import mn.erin.lms.legacy.domain.scorm.model.SCORMContentId;
import mn.erin.lms.legacy.domain.scorm.model.SCORMTime;
import mn.erin.lms.legacy.domain.scorm.repository.RuntimeDataRepository;
import mn.erin.lms.legacy.domain.scorm.repository.SCORMRepositoryException;

/**
 * @author Bat-Erdene Tsogoo.
 */
public class SaveRuntimeData extends SCORMRuntimeUseCase implements UseCase<SaveRuntimeDataInput, Map<String, RuntimeDataInfo>>
{
  private static final Logger LOGGER = LoggerFactory.getLogger(SaveRuntimeData.class);

  public SaveRuntimeData(RuntimeDataRepository runtimeDataRepository)
  {
    super(runtimeDataRepository);
  }

  @Override
  public Map<String, RuntimeDataInfo> execute(SaveRuntimeDataInput input) throws UseCaseException
  {
    if (input == null)
    {
      throw new UseCaseException("Input is missing!");
    }

    SCORMContentId scormContentId = SCORMContentId.valueOf(input.getScormContentId());
    try
    {
      Map<String, Object> data = input.getData();
      updateTime(data);
      updateViewCount(data);

      Map<DataModel, Serializable> updatedCMIData = runtimeDataRepository.update(scormContentId, input.getScoName(), data);
      return convertRuntimeData(updatedCMIData);
    }
    catch (SCORMRepositoryException e)
    {
      LOGGER.error(e.getMessage(), e);
      throw new UseCaseException(e.getMessage(), e);
    }
  }

  private void updateTime(Map<String, Object> data)
  {
    if (data.containsKey(DataModelConstants.CMI_SESSION_TIME) && data.containsKey(DataModelConstants.CMI_TOTAL_TIME))
    {
      SCORMTime sessionTime = new SCORMTime((String) data.get(DataModelConstants.CMI_SESSION_TIME));
      SCORMTime totalTime = new SCORMTime((String) data.get(DataModelConstants.CMI_TOTAL_TIME));

      SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

      if ("PT0.0S".equals(totalTime.getValue()))
      {
        data.put(DataModelConstants.ERIN_DATE_INITIAL_LAUNCH, formatter.format(new Date()));
      }

      totalTime.add(sessionTime);
      data.put(DataModelConstants.CMI_TOTAL_TIME, totalTime.getValue());
      data.put(DataModelConstants.ERIN_DATE_LAST_LAUNCH, formatter.format(new Date()));
    }
  }

  private void updateViewCount(Map<String, Object> data)
  {
    if (data.containsKey(DataModelConstants.CMI_INTERACTIONS_COUNT))
    {
      Integer count = Integer.parseInt((String) data.get(DataModelConstants.CMI_INTERACTIONS_COUNT));
      count++;
      data.put(DataModelConstants.CMI_INTERACTIONS_COUNT, count);
    }
  }
}
