package mn.erin.lms.base.analytics.usecase.online_course;

import java.util.*;

import mn.erin.domain.aim.model.group.GroupId;
import mn.erin.domain.base.usecase.UseCaseException;
import mn.erin.lms.base.analytics.model.Analytic;
import mn.erin.lms.base.analytics.model.course.online_course.OnlineCourseAnalytic;
import mn.erin.lms.base.analytics.repository.AnalyticsRepositoryRegistry;
import mn.erin.lms.base.analytics.usecase.AnalyticsUseCase;
import mn.erin.lms.base.analytics.usecase.dto.CourseAnalytics;
import mn.erin.lms.base.analytics.usecase.dto.CourseFilter;
import mn.erin.lms.base.domain.model.category.CourseCategoryId;
import mn.erin.lms.base.domain.service.CourseTypeResolver;
import mn.erin.lms.base.domain.service.UnknownCourseTypeException;

import com.google.common.collect.Sets;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;

/**
 * @author Munkh
 */
public class GenerateOnlineCourseAnalytics extends AnalyticsUseCase<CourseFilter, CourseAnalytics>
{

  public GenerateOnlineCourseAnalytics(AnalyticsRepositoryRegistry analyticsRepositoryRegistry, CourseTypeResolver courseTypeResolver)
  {
    super(analyticsRepositoryRegistry, courseTypeResolver);
  }

  @Override
  public CourseAnalytics execute(CourseFilter input) throws UseCaseException
  {
    Validate.notNull(input);
    Validate.notNull(input.getGroupId());

    if (StringUtils.isBlank(input.getCategoryId()))
    {
      input.setCategoryId("online-course");
    }

    List<Analytic> analytics;

    if (!StringUtils.isBlank(input.getCourseType()))
    {
      try
      {
        analytics = analyticsRepositoryRegistry.getCourseAnalyticsRepository().getAll(
            GroupId.valueOf(input.getGroupId()),
            CourseCategoryId.valueOf(input.getCategoryId()),
            courseTypeResolver.resolve(input.getCourseType()),
            input.getDateFilter().getStartDate(),
            input.getDateFilter().getEndDate()
        );
      }
      catch (UnknownCourseTypeException e)
      {
        throw new UseCaseException(e.getMessage(), e);
      }
    }
    else
    {
      analytics = analyticsRepositoryRegistry.getCourseAnalyticsRepository().getAll(
          GroupId.valueOf(input.getGroupId()),
          CourseCategoryId.valueOf(input.getCategoryId()),
          input.getDateFilter().getStartDate(),
          input.getDateFilter().getEndDate()
      );
    }

    CourseAnalytics courseAnalytics = new CourseAnalytics(analytics);

    courseAnalytics.addAnalyticData("groupEnrollmentCount", getGroupEnrollmentCount(analytics, input.getGroupId()));

    return courseAnalytics;
  }

  private Map<String, Integer> getGroupEnrollmentCount(List<Analytic> analytics, String parentGroupId)
  {
    Set<String> childGroups = analyticsRepositoryRegistry.getAnalyticRepository().getChildGroups(parentGroupId);

    Map<String, String> reverseMap = new HashMap<>();

    for (String group: childGroups)
    {
      Set<String> learners = analyticsRepositoryRegistry.getAnalyticRepository().getLearners(Sets.newHashSet(group));
      for (String learner: learners)
      {
        reverseMap.put(learner, group);
      }
    }

    Map<String, Integer> groupEnrollmentCount = new HashMap<>();

    for (String groupId: childGroups)
    {
      String groupName = analyticsRepositoryRegistry.getAnalyticRepository().getGroupName(groupId);
      if (groupName != null)
      {
        groupEnrollmentCount.put(groupName, 0);
      }
    }

    for (Analytic analytic: analytics)
    {
      Set<String> learners = ((OnlineCourseAnalytic) analytic).getLearners();

      for (String learner: learners)
      {
        String groupId = reverseMap.get(learner);
        if (groupId == null)
        {
          continue;
        }

        String groupName = analyticsRepositoryRegistry.getAnalyticRepository().getGroupName(groupId);
        if (groupName == null)
        {
          continue;
        }

        if (groupEnrollmentCount.containsKey(groupName))
        {
          int count = groupEnrollmentCount.get(groupName) + 1;
          groupEnrollmentCount.put(groupName, count);
        }
      }
    }
    return groupEnrollmentCount;
  }
}
