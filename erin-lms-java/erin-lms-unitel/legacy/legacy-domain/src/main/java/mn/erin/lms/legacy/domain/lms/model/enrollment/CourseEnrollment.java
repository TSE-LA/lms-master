/*
 * (C)opyright, 2019, ERIN SYSTEMS LLC
 * The source code is protected by copyright laws and international copyright
 * treaties, as well as other intellectual property laws and treaties.
 * All rights reserved.
 */

package mn.erin.lms.legacy.domain.lms.model.enrollment;

import java.util.Date;
import java.util.Objects;

import mn.erin.domain.base.model.Entity;
import mn.erin.lms.legacy.domain.lms.model.course.CourseId;

/**
 * @author Oyungerel Chuluunsukh.
 */
public class CourseEnrollment implements Entity<CourseEnrollment>
{
  private CourseEnrollmentId id;
  private LearnerId learnerId;
  private CourseId courseId;
  private CourseEnrollmentState state;
  private Date enrolledDate;

  public CourseEnrollment(CourseEnrollmentId id, LearnerId learnerId, CourseId courseId, CourseEnrollmentState state)
  {
    this.id = Objects.requireNonNull(id, "Course enrollment id cannot be null!");
    this.learnerId = Objects.requireNonNull(learnerId,"Course enrollment learner id cannot be null!");
    this.courseId = Objects.requireNonNull(courseId,"Course enrollment course id cannot be null!");
    this.state = Objects.requireNonNull(state,"Course enrollment state cannot be null!");
  }

  @Override
  public boolean sameIdentityAs(CourseEnrollment other)
  {
    return false;
  }

  public CourseEnrollmentId getId()
  {
    return id;
  }

  public void setId(CourseEnrollmentId id)
  {
    this.id = id;
  }

  public LearnerId getLearnerId()
  {
    return learnerId;
  }

  public void setLearnerId(LearnerId learnerId)
  {
    this.learnerId = learnerId;
  }

  public CourseId getCourseId()
  {
    return courseId;
  }

  public void setCourseId(CourseId courseId)
  {
    this.courseId = courseId;
  }

  public CourseEnrollmentState getState()
  {
    return state;
  }

  public void setState(CourseEnrollmentState state)
  {
    this.state = state;
  }

  public Date getEnrolledDate()
  {
    return enrolledDate;
  }

  public void setEnrolledDate(Date enrolledDate)
  {
    this.enrolledDate = enrolledDate;
  }
}
