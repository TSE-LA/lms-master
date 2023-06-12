package mn.erin.lms.legacy.infrastructure.unitel.scorm;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import mn.erin.lms.legacy.domain.lms.model.course.Course;
import mn.erin.lms.legacy.domain.lms.model.course.CourseId;
import mn.erin.lms.legacy.domain.lms.model.enrollment.CourseEnrollmentState;
import mn.erin.lms.legacy.domain.lms.repository.CourseRepository;
import mn.erin.lms.legacy.domain.lms.repository.LMSRepositoryException;
import mn.erin.lms.legacy.domain.lms.service.EnrollmentStateService;
import mn.erin.lms.legacy.domain.scorm.constants.DataModelConstants;
import mn.erin.lms.legacy.domain.scorm.model.DataModel;
import mn.erin.lms.legacy.domain.scorm.model.RuntimeData;
import mn.erin.lms.legacy.domain.scorm.model.SCORMContentId;
import mn.erin.lms.legacy.domain.scorm.repository.RuntimeDataRepository;
import mn.erin.lms.legacy.domain.scorm.repository.SCORMRepositoryException;

/**
 * @author Bat-Erdene Tsogoo.
 */
public class EnrollmentStateServiceImpl implements EnrollmentStateService
{
  private static final Logger LOGGER = LoggerFactory.getLogger(EnrollmentStateServiceImpl.class);

  private CourseRepository courseRepository;
  private RuntimeDataRepository runtimeDataRepository;

  @Inject
  public void setCourseRepository(CourseRepository courseRepository)
  {
    this.courseRepository = courseRepository;
  }

  @Inject
  public void setRuntimeDataRepository(RuntimeDataRepository legacyRuntimeDataRepo)
  {
    this.runtimeDataRepository = legacyRuntimeDataRepo;
  }

  @Override
  public CourseEnrollmentState getCourseEnrollmentState(CourseId courseId)
  {
    Course course;
    try
    {
      course = courseRepository.getCourse(courseId);
    }
    catch (LMSRepositoryException e)
    {
      LOGGER.error(e.getMessage(), e);
      return null;
    }

    if (course.getCourseContentId() == null)
    {
      return null;
    }
    SCORMContentId scormContentId = SCORMContentId.valueOf(course.getCourseContentId().getId());
    List<RuntimeData> runtimeData;
    try
    {
      runtimeData = runtimeDataRepository.listRuntimeData(scormContentId);
    }
    catch (SCORMRepositoryException e)
    {
      LOGGER.error(e.getMessage(), e);
      return null;
    }

    if (runtimeData.isEmpty())
    {
      return  CourseEnrollmentState.NEW;
    }

    CourseEnrollmentState courseEnrollmentState = CourseEnrollmentState.COMPLETED;

    List<Date> lastLaunchDates = new ArrayList<>();
    for (RuntimeData runtimeDatum : runtimeData)
    {
      Map<DataModel, Serializable> data = runtimeDatum.getData();
      for (Map.Entry<DataModel, Serializable> datum : data.entrySet())
      {
        if (DataModelConstants.ERIN_DATE_LAST_LAUNCH.equals(datum.getKey().getName()))
        {
          String dateString = (String) datum.getValue();
          SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
          try
          {
            Date lastLaunchDate = formatter.parse(dateString);
            lastLaunchDates.add(lastLaunchDate);
          }
          catch (ParseException ignored)
          {
            // Removed because logging too much
//            LOGGER.error(e.getMessage(), e);
          }
        }
        if (DataModelConstants.CMI_COMPLETION_STATUS.equals(datum.getKey().getName()) &&
            ("incomplete".equals(datum.getValue()) || "not attempted".equals(datum.getValue())))
        {
          courseEnrollmentState = CourseEnrollmentState.IN_PROGRESS;
        }
      }
    }
    List<Date> result = lastLaunchDates.stream()
        .filter(date -> date.after(course.getCourseDetail().getModifiedDate()))
        .collect(Collectors.toList());
    if (result.size() == 0)
    {
      courseEnrollmentState =  CourseEnrollmentState.NEW;
    }

    return courseEnrollmentState;
  }
}
