package mn.erin.lms.base.analytics.model.course.online_course;

import mn.erin.lms.base.analytics.model.Analytic;

/**
 * @author Munkh
 */
public class OnlineCourseStatistics implements Analytic
{
  private final String learnerId;
  private final String groupName;
  private String survey;
  private String spentTime;
  private String spentTimeRatio;
  private double status;
  private int score;
  private int maxScore;
  private int views;
  private String firstViewDate;
  private String lastViewDate;
  private String receivedCertificateDate;
  private String spentTimeOnTest;
  private long spentTimeInMilliseconds;

  private OnlineCourseStatistics(String learnerId, String groupName)
  {
    this.learnerId = learnerId;
    this.groupName = groupName;
  }

  public String getLearnerId()
  {
    return learnerId;
  }

  public String getGroupName()
  {
    return groupName;
  }

  public String getSurvey()
  {
    return survey;
  }

  public String getSpentTime()
  {
    return spentTime;
  }

  public double getStatus()
  {
    return status;
  }

  public int getScore()
  {
    return score;
  }

  public int getMaxScore()
  {
    return maxScore;
  }

  public int getViews()
  {
    return views;
  }

  public String getFirstViewDate()
  {
    return firstViewDate;
  }

  public String getLastViewDate()
  {
    return lastViewDate;
  }

  public String getReceivedCertificateDate()
  {
    return receivedCertificateDate;
  }

  public String getSpentTimeOnTest()
  {
    return spentTimeOnTest;
  }

  public long getSpentTimeInMilliseconds()
  {
    return spentTimeInMilliseconds;
  }

  public String getSpentTimeRatio(){ return spentTimeRatio; }


  public void setSpentTimeRatio(String spentTimeRatio){this.spentTimeRatio = spentTimeRatio; }

  public static class Builder
  {
    private final String learnerId;
    private final String groupName;
    private String survey;
    private String spentTime;
    private String spentTimeRatio;
    private double status;
    private int score;
    private int maxScore;
    private int views;
    private String firstViewDate;
    private String lastViewDate;
    private String receivedCertificateDate;
    private String spentTimeOnTest;
    private long spentTimeInMilliseconds;

    public Builder(String learnerId, String groupName)
    {
      this.learnerId = learnerId;
      this.groupName = groupName;
      survey = null;
      spentTime = "00:00:00";
      spentTimeRatio = "0/0";
      status = 0;
      score = 0;
      maxScore = 0;
      views = 0;
      firstViewDate = null;
      lastViewDate = null;
      receivedCertificateDate = null;
      spentTimeInMilliseconds = 0L;
    }

    public Builder withSurvey(String survey)
    {
      this.survey = survey;
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

    public Builder withStatus(double status)
    {
      this.status = status;
      return this;
    }

    public Builder withScore(int score)
    {
      this.score = score;
      return this;
    }

    public Builder withMaxScore(int maxScore)
    {
      this.maxScore = maxScore;
      return this;
    }

    public Builder withViews(int views)
    {
      this.views = views;
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

    public Builder withReceivedCertificateDate(String receivedCertificateDate)
    {
      this.receivedCertificateDate = receivedCertificateDate;
      return this;
    }

    public Builder withSpentTimeOnTest(String spentTimeOnTest)
    {
      this.spentTimeOnTest = spentTimeOnTest;
      return this;
    }

    public Builder withSpentTimeInMilliseconds(Long spentTimeInMilliseconds)
    {
      this.spentTimeInMilliseconds = spentTimeInMilliseconds;
      return this;
    }

    public OnlineCourseStatistics build()
    {
      OnlineCourseStatistics output = new OnlineCourseStatistics(this.learnerId, this.groupName);

      output.survey = this.survey;
      output.spentTime = this.spentTime;
      output.spentTimeRatio = this.spentTimeRatio;
      output.status = this.status;
      output.score = this.score;
      output.maxScore = this.maxScore;
      output.views = this.views;
      output.firstViewDate = this.firstViewDate;
      output.lastViewDate = this.lastViewDate;
      output.receivedCertificateDate = this.receivedCertificateDate;
      output.spentTimeOnTest = this.spentTimeOnTest;
      output.spentTimeInMilliseconds = this.spentTimeInMilliseconds;
      return output;
    }
  }
}
