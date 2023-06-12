/*
 * (C)opyright, 2019, ERIN SYSTEMS LLC
 * The source code is protected by copyright laws and international copyright
 * treaties, as well as other intellectual property laws and treaties.
 * All rights reserved.
 */

package mn.erin.lms.legacy.domain.lms.model.report;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.apache.commons.lang3.Validate;

import mn.erin.domain.base.model.Aggregate;
import mn.erin.lms.legacy.domain.lms.model.course.Course;
import mn.erin.lms.legacy.domain.lms.model.enrollment.LearnerId;

/**
 * @author Bat-Erdene Tsogoo.
 */
public class CourseReport implements Aggregate<Course>
{
  private final Course course;

  private Map<String, Object> reportData = new HashMap<>();
  private List<LearnerId> enrolledLearners = new ArrayList<>();

  public CourseReport(Course course)
  {
    this.course = Objects.requireNonNull(course, "Course report must have a course as its root entity!");
  }

  public void putReportData(String dataKey, Object dataValue)
  {
    Validate.notBlank(dataKey, "Data key cannot be null or blank!");
    Validate.notNull(dataValue, "Data value cannot be null!");
    this.reportData.put(dataKey, dataValue);
  }

  public void addLearner(LearnerId learnerId)
  {
    if (learnerId != null)
    {
      this.enrolledLearners.add(learnerId);
    }
  }

  public Map<String, Object> getReportData()
  {
    return Collections.unmodifiableMap(reportData);
  }

  public List<LearnerId> getEnrolledLearners()
  {
    return enrolledLearners;
  }

  @Override
  public Course getRootEntity()
  {
    return this.course;
  }
}
