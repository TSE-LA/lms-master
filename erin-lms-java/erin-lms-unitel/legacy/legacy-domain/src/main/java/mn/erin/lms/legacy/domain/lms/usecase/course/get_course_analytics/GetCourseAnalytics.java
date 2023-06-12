/*
 * (C)opyright, 2019, ERIN SYSTEMS LLC
 * The source code is protected by copyright laws and international copyright
 * treaties, as well as other intellectual property laws and treaties.
 * All rights reserved.
 */

package mn.erin.lms.legacy.domain.lms.usecase.course.get_course_analytics;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import org.apache.commons.lang3.Validate;

import mn.erin.common.datetime.DateTimeUtils;
import mn.erin.lms.legacy.domain.lms.model.course.Course;
import mn.erin.lms.legacy.domain.lms.model.course.CourseAnalyticData;
import mn.erin.lms.legacy.domain.lms.model.course.CourseId;
import mn.erin.lms.legacy.domain.lms.repository.CourseRepository;
import mn.erin.lms.legacy.domain.lms.service.CourseAnalytics;
import mn.erin.lms.legacy.domain.lms.usecase.course.CourseUseCase;

/**
 * @author Bat-Erdene Tsogoo.
 */
public class GetCourseAnalytics extends CourseUseCase<GetCourseAnalyticsInput, List<GetCourseAnalyticsOutput>>
{
  private final CourseAnalytics courseAnalytics;

  public GetCourseAnalytics(CourseRepository courseRepository, CourseAnalytics courseAnalytics)
  {
    super(courseRepository);
    this.courseAnalytics = Objects.requireNonNull(courseAnalytics, "CourseAnalytics cannot be null!");
  }

  @Override
  public List<GetCourseAnalyticsOutput> execute(GetCourseAnalyticsInput input)
  {
    Validate.notNull(input, "Input is required!");

    Course course = getCourse(new CourseId(input.getCourseId()));
    List<CourseAnalyticData> analyticData = courseAnalytics.generateCourseAnalytics(course.getCourseContentId());

    List<GetCourseAnalyticsOutput> output = new ArrayList<>();

    for (CourseAnalyticData datum : analyticData)
    {
      getFilteredData(datum, input.getFilter(), output);
    }

    return output;
  }

  private void getFilteredData(CourseAnalyticData courseAnalyticData, Filter filter, List<GetCourseAnalyticsOutput> result)
  {
    Date startDate = filter.getStartDate();
    Date endDate = filter.getEndDate();
    Date lastLaunchDate = courseAnalyticData.getLastLaunchDate();

    int inRelationToStartDate = DateTimeUtils.compare(lastLaunchDate, startDate);
    int inRelationToEndDate = DateTimeUtils.compare(lastLaunchDate, endDate);

    if ((inRelationToStartDate == 0 || inRelationToStartDate > 0) &&
        (inRelationToEndDate == 0 || inRelationToEndDate < 0))
    {
      GetCourseAnalyticsOutput record = new GetCourseAnalyticsOutput();
      record.setLearnerId(courseAnalyticData.getLearnerId().getId());
      record.setScore(courseAnalyticData.getScore());
      record.setTotalEnrollment(courseAnalyticData.getTotalEnrollment());
      record.setCourseProgress(courseAnalyticData.getCourseProgress());
      record.setInitialLaunchDate(courseAnalyticData.getInitialLaunchDate());
      record.setLastLaunchdate(courseAnalyticData.getLastLaunchDate());
      record.setTotalTime(courseAnalyticData.getTotalTime());
      record.setFeedback(courseAnalyticData.getFeedback());
      result.add(record);
    }
  }
}
