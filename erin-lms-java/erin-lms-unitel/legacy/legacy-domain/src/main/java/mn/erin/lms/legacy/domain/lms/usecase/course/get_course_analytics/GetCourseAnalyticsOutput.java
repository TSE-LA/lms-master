/*
 * (C)opyright, 2019, ERIN SYSTEMS LLC
 * The source code is protected by copyright laws and international copyright
 * treaties, as well as other intellectual property laws and treaties.
 * All rights reserved.
 */

package mn.erin.lms.legacy.domain.lms.usecase.course.get_course_analytics;

import java.util.Date;

/**
 * @author Bat-Erdene Tsogoo.
 */
public class GetCourseAnalyticsOutput
{
  private String learnerId;
  private Float courseProgress;
  private Integer score;
  private Integer totalEnrollment;
  private Date initialLaunchDate;
  private Date lastLaunchdate;
  private String totalTime;
  private String feedback;

  public String getLearnerId()
  {
    return learnerId;
  }

  public Float getCourseProgress()
  {
    return courseProgress;
  }

  public Integer getScore()
  {
    return score;
  }

  public Date getInitialLaunchDate()
  {
    return initialLaunchDate;
  }

  public Date getLastLaunchdate()
  {
    return lastLaunchdate;
  }

  public String getTotalTime()
  {
    return totalTime;
  }

  public String getFeedback()
  {
    return feedback;
  }

  public void setLearnerId(String learnerId)
  {
    this.learnerId = learnerId;
  }

  public void setCourseProgress(Float courseProgress)
  {
    this.courseProgress = courseProgress;
  }

  public void setScore(Integer score)
  {
    this.score = score;
  }

  public void setInitialLaunchDate(Date initialLaunchDate)
  {
    this.initialLaunchDate = initialLaunchDate;
  }

  public void setLastLaunchdate(Date lastLaunchdate)
  {
    this.lastLaunchdate = lastLaunchdate;
  }

  public void setTotalTime(String totalTime)
  {
    this.totalTime = totalTime;
  }

  public void setFeedback(String feedback)
  {
    this.feedback = feedback;
  }

  public Integer getTotalEnrollment()
  {
    return totalEnrollment;
  }

  public void setTotalEnrollment(Integer totalEnrollment)
  {
    this.totalEnrollment = totalEnrollment;
  }
}
