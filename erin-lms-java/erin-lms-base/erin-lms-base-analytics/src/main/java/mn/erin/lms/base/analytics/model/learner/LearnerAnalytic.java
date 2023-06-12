package mn.erin.lms.base.analytics.model.learner;

import java.time.LocalDateTime;

import mn.erin.lms.base.analytics.model.Analytic;
import mn.erin.lms.base.domain.model.course.CourseId;

/**
 * @author Munkh
 */
public class LearnerAnalytic implements Analytic
{
  private final CourseId courseId;
  private String title;
  private String category;
  private String courseType;
  private boolean survey;
  private boolean completed;
  private int score;
  private double status;
  private int views;
  private String spentTime;
  private String spentTimeRatio;
  private LocalDateTime receivedCertificateDate;
  private LocalDateTime firstViewDate;
  private LocalDateTime lastViewDate;
  private String spentTimeOnTest;

  private LearnerAnalytic(CourseId courseId)
  {
    this.courseId = courseId;
  }

  public CourseId getCourseId()
  {
    return courseId;
  }

  public String getTitle()
  {
    return title;
  }

  public String getCategory()
  {
    return category;
  }

  public String getCourseType()
  {
    return courseType;
  }

  public boolean hasSurvey()
  {
    return survey;
  }

  public boolean hasCompleted()
  {
    return completed;
  }

  public int getScore()
  {
    return score;
  }

  public double getStatus()
  {
    return status;
  }

  public int getViews()
  {
    return views;
  }

  public String getSpentTime()
  {
    return spentTime;
  }

  public String getSpentTimeRatio()
  {
    return spentTimeRatio;
  }

  public LocalDateTime getReceivedCertificateDate()
  {
    return receivedCertificateDate;
  }

  public LocalDateTime getFirstViewDate()
  {
    return firstViewDate;
  }

  public LocalDateTime getLastViewDate()
  {
    return lastViewDate;
  }

  public String getSpentTimeOnTest()
  {
    return spentTimeOnTest;
  }

  public void setSpentTimeRatio(String spentTimeRatio)
  {
    this.spentTimeRatio = spentTimeRatio;
  }

  public static class Builder
  {
    private final CourseId courseId;
    private String title;
    private String category;
    private String courseType;
    private boolean survey;
    private boolean completed;
    private int score;
    private double status;
    private int views;
    private String spentTime;
    private String spentTimeRatio;
    private LocalDateTime receivedCertificateDate;
    private LocalDateTime firstViewDate;
    private LocalDateTime lastViewDate;
    private String spentTimeOnTest;

    public Builder(CourseId courseId)
    {
      this.courseId = courseId;
      survey = false;
      completed = false;
      score = 0;
      status = 0;
      views = 0;
      spentTime = "00:00:00";
      spentTimeOnTest = "00:00:00";
    }

    public Builder withTitle(String title)
    {
      this.title = title;
      return this;
    }

    public Builder withCategory(String category)
    {
      this.category = category;
      return this;
    }

    public Builder withCourseType(String courseType)
    {
      this.courseType = courseType;
      return this;
    }

    public Builder hasSurvey(boolean survey)
    {
      this.survey = survey;
      return this;
    }

    public Builder withScore(int score)
    {
      this.score = score;
      return this;
    }

    public Builder withStatus(double status)
    {
      this.status = status;
      this.completed = status == 100.0;
      return this;
    }

    public Builder withViews(int views)
    {
      this.views = views;
      return this;
    }

    public Builder withSpentTime(String spentTime)
    {
      this.spentTime = spentTime;
      return this;
    }

    public Builder withSpentTimeRatio(String spentTimeRatio)
    {
      this.spentTimeRatio = spentTimeRatio;
      return this;
    }

    public Builder withReceivedCertificateDate(LocalDateTime receivedCertificateDate)
    {
      this.receivedCertificateDate = receivedCertificateDate;
      return this;
    }

    public Builder withFirstViewDate(LocalDateTime firstViewDate)
    {
      this.firstViewDate = firstViewDate;
      return this;
    }

    public Builder withLastViewDate(LocalDateTime lastViewDate)
    {
      this.lastViewDate = lastViewDate;
      return this;
    }

    public Builder withSpentTimeOnTest(String spentTimeOnTest)
    {
      this.spentTimeOnTest = spentTimeOnTest;
      return this;
    }

    public LearnerAnalytic build()
    {
      LearnerAnalytic output = new LearnerAnalytic(courseId);

      output.title = this.title;
      output.category = this.category;
      output.courseType = this.courseType;
      output.survey = this.survey;
      output.completed = this.completed;
      output.score = this.score;
      output.status = this.status;
      output.views = this.views;
      output.spentTime = this.spentTime;
      output.spentTimeRatio = this.spentTimeRatio;
      output.receivedCertificateDate = this.receivedCertificateDate;
      output.firstViewDate = this.firstViewDate;
      output.lastViewDate = this.lastViewDate;
      output.spentTimeOnTest = this.spentTimeOnTest;

      return output;
    }
  }
}

