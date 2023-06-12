package mn.erin.lms.base.analytics.usecase.dto;

/**
 * @author Munkh
 */
public class LearnerAnalyticDto implements AnalyticDto
{
  private String courseId;
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
  private String certificate;
  private String firstViewDate;
  private String lastViewDate;
  private String spentTimeOnTest;

  public String getCourseId()
  {
    return courseId;
  }

  public void setCourseId(String courseId)
  {
    this.courseId = courseId;
  }

  public String getTitle()
  {
    return title;
  }

  public void setTitle(String title)
  {
    this.title = title;
  }

  public String getCategory()
  {
    return category;
  }

  public void setCategory(String category)
  {
    this.category = category;
  }

  public String getCourseType()
  {
    return courseType;
  }

  public void setCourseType(String courseType)
  {
    this.courseType = courseType;
  }

  public boolean isSurvey()
  {
    return survey;
  }

  public void setSurvey(boolean survey)
  {
    this.survey = survey;
  }

  public boolean isCompleted()
  {
    return completed;
  }

  public void setCompleted(boolean completed)
  {
    this.completed = completed;
  }

  public int getScore()
  {
    return score;
  }

  public void setScore(int score)
  {
    this.score = score;
  }

  public double getStatus()
  {
    return status;
  }

  public void setStatus(double status)
  {
    this.status = status;
  }

  public int getViews()
  {
    return views;
  }

  public void setViews(int views)
  {
    this.views = views;
  }

  public String getSpentTime()
  {
    return spentTime;
  }

  public String getSpentTimeRatio()
  {
    return spentTimeRatio;
  }

  public void setSpentTime(String spentTime)
  {
    this.spentTime = spentTime;
  }

  public void setSpentTimeRatio(String spentTimeRatio)
  {
    this.spentTimeRatio = spentTimeRatio;
  }

  public String getCertificate()
  {
    return certificate;
  }

  public void setCertificate(String certificate)
  {
    this.certificate = certificate;
  }

  public String getFirstViewDate()
  {
    return firstViewDate;
  }

  public void setFirstViewDate(String firstViewDate)
  {
    this.firstViewDate = firstViewDate;
  }

  public String getLastViewDate()
  {
    return lastViewDate;
  }

  public void setLastViewDate(String lastViewDate)
  {
    this.lastViewDate = lastViewDate;
  }

  public String getSpentTimeOnTest()
  {
    return spentTimeOnTest;
  }

  public static class Builder
  {
    private String courseId;
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
    private String certificate;
    private String firstViewDate;
    private String lastViewDate;
    private String spentTimeOnTest;

    public Builder withCourseId(String courseId)
    {
      this.courseId = courseId;
      return this;
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

    public Builder withCertificate(String certificate)
    {
      this.certificate = certificate;
      return this;
    }

    public Builder withFirstViewDate(String firstViewDate)
    {
      this.firstViewDate = firstViewDate;
      return this;
    }

    public Builder withLastViewDate(String lastViewDate)
    {
      this.lastViewDate = lastViewDate;
      return this;
    }

    public Builder withSpentTimeOnTest(String spentTimeOnTest)
    {
      this.spentTimeOnTest = spentTimeOnTest;
      return this;
    }

    public LearnerAnalyticDto build()
    {
      LearnerAnalyticDto output = new LearnerAnalyticDto();

      output.courseId = this.courseId;
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
      output.certificate = this.certificate;
      output.firstViewDate = this.firstViewDate;
      output.lastViewDate = this.lastViewDate;
      output.spentTimeOnTest = this.spentTimeOnTest;

      return output;
    }
  }
}
