/*
 * (C)opyright, 2019, ERIN SYSTEMS LLC
 * The source code is protected by copyright laws and international copyright
 * treaties, as well as other intellectual property laws and treaties.
 * All rights reserved.
 */

package mn.erin.lms.legacy.domain.lms.model.course;

import java.util.Date;

import mn.erin.domain.base.model.ValueObject;
import mn.erin.lms.legacy.domain.lms.model.enrollment.LearnerId;

/**
 * @author Bat-Erdene Tsogoo.
 */
public class CourseAnalyticData implements ValueObject<CourseAnalyticData>
{
  private LearnerId learnerId;
  private Float courseProgress;
  private Integer score;
  private Integer totalEnrollment;
  private Date initialLaunchDate;
  private Date lastLaunchDate;
  private String totalTime;
  private String feedback;

  private CourseAnalyticData()
  {

  }

  public LearnerId getLearnerId()
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

  public Integer getTotalEnrollment()
  {
    return totalEnrollment;
  }

  public Date getInitialLaunchDate()
  {
    return initialLaunchDate;
  }

  public Date getLastLaunchDate()
  {
    return lastLaunchDate;
  }

  public String getTotalTime()
  {
    return totalTime;
  }

  public String getFeedback()
  {
    return feedback;
  }

  @Override
  public boolean sameValueAs(CourseAnalyticData other)
  {
    return other != null && (this.learnerId == other.learnerId);
  }


  public static class Builder
  {
    private String learnerId;
    private Float courseProgress;
    private Integer score;
    private Integer totalEnrollment;
    private Date intiialLaunchDate;
    private Date lastLaunchDate;
    private String totalTime;
    private String feedback;

    public Builder(String learnerId)
    {
      this.learnerId = learnerId;
    }

    public Builder withProgress(Float courseProgress)
    {
      this.courseProgress = courseProgress;
      return this;
    }

    public Builder withScore(Integer score)
    {
      this.score = score;
      return this;
    }
    public Builder withTotalEnrollment(Integer totalEnrollment)
    {
      this.totalEnrollment = totalEnrollment;
      return this;
    }

    public Builder startedAt(Date intiialLaunchDate)
    {
      this.intiialLaunchDate = intiialLaunchDate;
      return this;
    }

    public Builder lastLaunchedAt(Date lastLaunchDate)
    {
      this.lastLaunchDate = lastLaunchDate;
      return this;
    }

    public Builder spent(String totalTime)
    {
      this.totalTime = totalTime;
      return this;
    }

    public Builder withFeedback(String feedback)
    {
      this.feedback = feedback;
      return this;
    }

    public CourseAnalyticData build()
    {
      CourseAnalyticData courseAnalyticData = new CourseAnalyticData();
      courseAnalyticData.learnerId = new LearnerId(this.learnerId);
      courseAnalyticData.score = this.score;
      courseAnalyticData.totalEnrollment = this.totalEnrollment;
      courseAnalyticData.courseProgress = this.courseProgress;
      courseAnalyticData.initialLaunchDate = this.intiialLaunchDate;
      courseAnalyticData.lastLaunchDate = this.lastLaunchDate;
      courseAnalyticData.totalTime = this.totalTime;
      courseAnalyticData.feedback = this.feedback;

      return courseAnalyticData;
    }
  }
}
