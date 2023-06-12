package mn.erin.lms.legacy.domain.unitel.usecase.promotion;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.Validate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import mn.erin.common.datetime.DateTimeUtils;
import mn.erin.lms.legacy.domain.lms.model.course.CompanyId;
import mn.erin.lms.legacy.domain.lms.model.course.Course;
import mn.erin.lms.legacy.domain.lms.model.course.CourseCategory;
import mn.erin.lms.legacy.domain.lms.model.course.CourseDetail;
import mn.erin.lms.legacy.domain.lms.repository.CourseCategoryRepository;
import mn.erin.lms.legacy.domain.lms.repository.CourseRepository;
import mn.erin.lms.legacy.domain.lms.repository.LMSRepositoryException;
import mn.erin.lms.legacy.domain.unitel.model.PromotionState;

import static mn.erin.lms.legacy.domain.unitel.PromotionConstants.PROPERTY_END_DATE;
import static mn.erin.lms.legacy.domain.unitel.PromotionConstants.PROPERTY_STATE;

public class ChangeCurrentPromotionState extends PromotionUseCase<Void, Void>
{
  private static final Logger LOGGER = LoggerFactory.getLogger(ChangeCurrentPromotionState.class);

  private final CourseRepository courseRepository;
  private final CourseCategoryRepository courseCategoryRepository;

  public ChangeCurrentPromotionState(CourseRepository courseRepository, CourseCategoryRepository courseCategoryRepository)
  {
    this.courseRepository = Validate.notNull(courseRepository, "Course repo cannot be null");
    this.courseCategoryRepository = Validate.notNull(courseCategoryRepository, "Course category repo cannot be null!");
  }

  @Override
  public Void execute(Void input)
  {
    final Calendar cal = Calendar.getInstance();

    cal.set(Calendar.HOUR_OF_DAY, 0);
    cal.set(Calendar.MINUTE, 0);

    cal.set(Calendar.SECOND, 0);
    cal.set(Calendar.MILLISECOND, 0);

    Date todayDate = cal.getTime();

    LOGGER.info("####### CHANGE CURRENT PROMOTION STATE USE CASE INVOKED : TODAY DATE = [{}]", todayDate);

    Collection<CourseCategory> categories = courseCategoryRepository.listAll(new CompanyId("-unitel-"));
    List<Course> courses = new ArrayList<>();
    Map<String, Object> properties = new HashMap<>();
    properties.put(PROPERTY_STATE, PromotionState.CURRENT.getStateName().toUpperCase());
    for (CourseCategory category : categories)
    {
      List<Course> categoryCourses = courseRepository.getCourseList(category.getCategoryId(), properties);
      courses.addAll(categoryCourses);
    }
    for (Course course : courses)
    {
      Map<String, Object> courseProperties = course.getCourseDetail().getProperties();
      Date promoEndDate = null;
      try
      {
        SimpleDateFormat formatter = new SimpleDateFormat(DateTimeUtils.SHORT_ISO_DATE_FORMAT);
        promoEndDate = formatter.parse((String) courseProperties.get(PROPERTY_END_DATE));
      }
      catch (ParseException e)
      {
        LOGGER.error(e.getMessage());
      }
      if (promoEndDate != null && promoEndDate.before(todayDate))
      {
        LOGGER.info("########### PROMOTION END DATE = [{}] AND TODAY DATE = [{}],"
            + " Promotion end date is before today date.", promoEndDate, todayDate);

        CourseDetail currentCourseDetail = course.getCourseDetail();
        currentCourseDetail.addProperty(PROPERTY_STATE, PromotionState.EXPIRED.getStateName().toUpperCase());
        try
        {
          courseRepository.update(course.getCourseId(), currentCourseDetail);
        }
        catch (LMSRepositoryException e)
        {
          LOGGER.error(e.getMessage(), e);
        }
      }
    }

    return null;
  }
}
