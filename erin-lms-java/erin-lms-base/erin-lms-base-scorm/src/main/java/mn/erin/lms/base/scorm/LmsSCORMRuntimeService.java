package mn.erin.lms.base.scorm;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import mn.erin.lms.base.domain.model.course.Course;
import mn.erin.lms.base.domain.model.course.CourseId;
import mn.erin.lms.base.domain.repository.CourseRepository;
import mn.erin.lms.base.domain.repository.LmsRepositoryException;
import mn.erin.lms.base.domain.service.ProgressTrackingService;
import mn.erin.lms.base.scorm.constants.DataModelConstants;
import mn.erin.lms.base.scorm.model.DataModel;
import mn.erin.lms.base.scorm.model.RuntimeData;
import mn.erin.lms.base.scorm.model.SCORMContentId;
import mn.erin.lms.base.scorm.model.SCORMTime;
import mn.erin.lms.base.scorm.repository.RuntimeDataRepository;
import mn.erin.lms.base.scorm.repository.SCORMRepositoryException;
import mn.erin.lms.base.scorm.service.AssessmentReportException;
import mn.erin.lms.base.scorm.service.AssessmentReportService;

import static mn.erin.lms.base.scorm.RuntimeDataConverter.convertRuntimeData;

/**
 * @author Bat-Erdene Tsogoo.
 */
public class LmsSCORMRuntimeService implements ProgressTrackingService
{
  private static final Logger LOGGER = LoggerFactory.getLogger(LmsSCORMRuntimeService.class);

  private RuntimeDataRepository runtimeDataRepository;
  private CourseRepository courseRepository;
  private AssessmentReportService assessmentReportService;

  @Inject
  public void setRuntimeDataRepository(RuntimeDataRepository runtimeDataRepository)
  {
    this.runtimeDataRepository = runtimeDataRepository;
  }

  @Inject
  public void setCourseRepository(CourseRepository courseRepository)
  {
    this.courseRepository = courseRepository;
  }

  @Inject
  public void setAssessmentReportService(AssessmentReportService assessmentReportService)
  {
    this.assessmentReportService = assessmentReportService;
  }

  @Override
  @SuppressWarnings("unchecked")
  public Map<String, Object> saveLearnerData(String learnerId, String courseId, String moduleName, Map<String, Object> data)
  {
    String scormContentId = getSCORMContentId(courseId);
    if (scormContentId == null)
    {
      return Collections.emptyMap();
    }

    updateTime(data);
    updateViewCount(data);
    try
    {
      Map<DataModel, Serializable> runtimeData = runtimeDataRepository.update(SCORMContentId.valueOf(scormContentId), moduleName, data);
      Map<String, RuntimeDataInfo> converted = convertRuntimeData(runtimeData);

      try
      {
        if (moduleName.equalsIgnoreCase("үнэлгээний хуудас"))
        {
          assessmentReportService.save(data, courseId);
        }
      }
      catch (AssessmentReportException e)
      {
        LOGGER.error(e.getMessage(), e);
      }

      return converted.entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }
    catch (SCORMRepositoryException e)
    {
      LOGGER.error(e.getMessage(), e);
      return Collections.emptyMap();
    }
  }

  @Override
  public Float getLearnerProgress(String learnerId, String courseId)
  {
    String scormContentId = getSCORMContentId(courseId);
    if (scormContentId == null)
    {
      return 0f;
    }

    List<RuntimeData> runtimeData = runtimeDataRepository.listRuntimeData(SCORMContentId.valueOf(scormContentId), learnerId);

    int dataSample = 0;
    float overallProgress = 0f;
    for (RuntimeData datum : runtimeData)
    {
      if (!("тест".equalsIgnoreCase(datum.getSco().getName()) || "асуулга".equalsIgnoreCase(datum.getSco().getName()) ||
          "үнэлгээний хуудас".equalsIgnoreCase(datum.getSco().getName())))
      {
        for (Map.Entry<DataModel, Serializable> dataEntry : datum.getData().entrySet())
        {
          if (DataModelConstants.CMI_PROGRESS_MEASURE.equals(dataEntry.getKey().getName()))
          {
            overallProgress += Float.parseFloat((String) dataEntry.getValue());
          }
        }
        dataSample++;
      }
    }

    return dataSample == 0 ? 0 : overallProgress / dataSample;
  }

  @Override
  public boolean deleteLearnerData(String learnerId, String courseId)
  {
    String scormContentId = getSCORMContentId(courseId);
    if (scormContentId == null)
    {
      return false;
    }

    return runtimeDataRepository.delete(SCORMContentId.valueOf(scormContentId), learnerId);
  }

  @Override
  public boolean deleteLearnerData(String learnerId)
  {
    return runtimeDataRepository.delete(learnerId);
  }

  @Override
  public boolean deleteCourseData(String courseId)
  {
    String scormContentId = getSCORMContentId(courseId);
    if (scormContentId == null)
    {
      return false;
    }

    return runtimeDataRepository.delete(SCORMContentId.valueOf(scormContentId));
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

  private String getSCORMContentId(String courseId)
  {
    try
    {
      Course course = courseRepository.fetchById(CourseId.valueOf(courseId));
      return course.getCourseContentId() != null ? course.getCourseContentId().getId() : null;
    }
    catch (LmsRepositoryException e)
    {
      LOGGER.error(e.getMessage(), e);
      return null;
    }
  }
}

