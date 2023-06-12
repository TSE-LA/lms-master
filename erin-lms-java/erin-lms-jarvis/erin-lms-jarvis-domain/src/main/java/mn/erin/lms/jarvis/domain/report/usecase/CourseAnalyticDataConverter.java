package mn.erin.lms.jarvis.domain.report.usecase;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.Map;

import mn.erin.lms.jarvis.domain.report.model.analytics.CourseAnalyticData;
import mn.erin.lms.base.scorm.constants.DataModelConstants;
import mn.erin.lms.base.scorm.model.DataModel;
import mn.erin.lms.base.scorm.model.RuntimeData;
import mn.erin.lms.base.scorm.model.SCORMTime;

/**
 * @author Bat-Erdene Tsogoo.
 */
public final class CourseAnalyticDataConverter
{
  private CourseAnalyticDataConverter()
  {
  }

  public static CourseAnalyticData convert(List<RuntimeData> runtimeData, String userDepartmentName, LocalDateTime receivedCertificateDate)
  {
    CourseAnalyticData.Builder builder = new CourseAnalyticData.Builder();

    float progress = 0;
    SCORMTime totalTime = new SCORMTime("PT0.0S");
    LocalDateTime initialLaunchDate = null;
    LocalDateTime lastLaunchDate = null;
    Integer score = null;
    Integer maxScore = null;
    int scoSize = runtimeData.size();
    int interactionsCount = 0;

    for (RuntimeData datum : runtimeData)
    {
      Map<DataModel, Serializable> data = datum.getData();
      if (!datum.getSco().getName().equalsIgnoreCase("асуулга") &&
          !datum.getSco().getName().equalsIgnoreCase("үнэлгээний хуудас"))
      {
        for (Map.Entry<DataModel, Serializable> dataEntry : data.entrySet())
        {
          switch (dataEntry.getKey().getName())
          {
          case DataModelConstants.CMI_PROGRESS_MEASURE:
            progress += Float.parseFloat((String) dataEntry.getValue());
            break;
          case "erin.date.initial_launch":
            LocalDateTime initDate = getDate((String) dataEntry.getValue());
            if ((initialLaunchDate == null) || (initDate != null && initialLaunchDate.isAfter(initDate)))
            {
              initialLaunchDate = initDate;
            }
            break;
          case "erin.date.last_launch":
            LocalDateTime endDate = getDate((String) dataEntry.getValue());
            if (lastLaunchDate == null || (endDate != null && lastLaunchDate.isBefore(endDate)))
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
      else if (datum.getSco().getName().equalsIgnoreCase("үнэлгээний хуудас"))
      {
        for (Map.Entry<DataModel, Serializable> dataEntry : data.entrySet())
        {
          if (DataModelConstants.CMI_SUSPEND_DATA.equalsIgnoreCase(dataEntry.getKey().getName()))
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
      if (datum.getSco().getName().equalsIgnoreCase("тест"))
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
      }
    }

    builder = builder.withTotalTime(SCORMTime.convertToHumanReadableTime(totalTime.getValue()));

    builder = builder.withScore(score);
    builder = builder.havingMaxScore(maxScore);
    builder = builder.startedAt(initialLaunchDate);
    builder = builder.getCertificateDateAt(receivedCertificateDate);
    builder = builder.lastLaunchedAt(lastLaunchDate);
    if (progress == 0 && scoSize == 0)
    {
      builder = builder.withStatus(0.0f);
    }
    else
    {
      builder = builder.withStatus(progress / scoSize);
    }

    builder = builder.havingInteractionsCount(interactionsCount);
    builder = builder.withDepartmentName(userDepartmentName);
    return builder.build();
  }

  private static LocalDateTime getDate(String dateStringRepresentation)
  {
    try
    {
      SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm");
      Date date = formatter.parse(dateStringRepresentation);
      return date.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
    }
    catch (ParseException e)
    {
      return null;
    }
  }
}

