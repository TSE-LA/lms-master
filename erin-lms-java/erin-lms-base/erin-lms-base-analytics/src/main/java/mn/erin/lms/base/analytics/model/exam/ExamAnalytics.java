package mn.erin.lms.base.analytics.model.exam;

import mn.erin.lms.base.analytics.model.Analytic;

/**
 * @author Byambajav
 */
public class ExamAnalytics implements Analytic
{
  private final String id;
  private String title;
  private String categoryName;
  private String status;
  private int duration;
  private int totalRuntime;
  private int passedCount;
  private int averageScore;
  private int averageSpentTime;
  private int maxScore;
  private int questionCount;
  private Object enrollmentCount;

  public ExamAnalytics(String id)
  {
    this.id = id;
  }

  public String getId()
  {
    return id;
  }

  public String getTitle()
  {
    return title;
  }

  public void setTitle(String title)
  {
    this.title = title;
  }

  public String getCategoryName()
  {
    return categoryName;
  }

  public void setCategoryName(String categoryName)
  {
    this.categoryName = categoryName;
  }

  public String getStatus()
  {
    return status;
  }

  public void setStatus(String status)
  {
    this.status = status;
  }

  public int getPassedCount()
  {
    return passedCount;
  }

  public void setPassedCount(int passedCount)
  {
    this.passedCount = passedCount;
  }

  public int getAverageScore()
  {
    return averageScore;
  }

  public void setAverageScore(int averageScore)
  {
    this.averageScore = averageScore;
  }

  public int getAverageSpentTime()
  {
    return averageSpentTime;
  }

  public void setAverageSpentTime(int averageSpentTime)
  {
    this.averageSpentTime = averageSpentTime;
  }

  public int getDuration()
  {
    return duration;
  }

  public void setDuration(int duration)
  {
    this.duration = duration;
  }

  public int getTotalRuntime()
  {
    return totalRuntime;
  }

  public void setTotalRuntime(int totalRuntime)
  {
    this.totalRuntime = totalRuntime;
  }

  public int getMaxScore()
  {
    return maxScore;
  }

  public void setMaxScore(int maxScore)
  {
    this.maxScore = maxScore;
  }

  public int getQuestionCount()
  {
    return questionCount;
  }

  public void setQuestionCount(int questionCount)
  {
    this.questionCount = questionCount;
  }

  public Object getEnrollmentCount()
  {
    return enrollmentCount;
  }

  public void setEnrollmentCount(Object enrollmentCount)
  {
    this.enrollmentCount = enrollmentCount;
  }

  public static class Builder
  {
    private final String id;
    private String title;
    private String categoryName;
    private String status;
    private int duration;
    private int totalRuntime;
    private int passedCount;
    private int averageScore;
    private int averageSpentTime;
    private int maxScore;
    private int questionCount;
    private Object enrollmentCount;

    public Builder(String id)
    {
      this.id = id;
    }

    public Builder withTitle(String title)
    {
      this.title = title;
      return this;
    }

    public Builder withCategoryName(String categoryName)
    {
      this.categoryName = categoryName;
      return this;
    }

    public Builder withStatus(String status)
    {
      this.status = status;
      return this;
    }

    public Builder withDuration(int duration)
    {
      this.duration = duration;
      return this;
    }

    public Builder withPassedCount(int passedCount)
    {
      this.passedCount = passedCount;
      return this;
    }

    public Builder withAverageScore(int averageScore)
    {
      this.averageScore = averageScore;
      return this;
    }

    public Builder withAverageSpentTime(int averageSpentTime)
    {
      this.averageSpentTime = averageSpentTime;
      return this;
    }

    public Builder withTotalRuntime(int totalRuntime)
    {
      this.totalRuntime = totalRuntime;
      return this;
    }

    public Builder withMaxScore(int maxScore)
    {
      this.maxScore = maxScore;
      return this;
    }

    public Builder withQuestionCount(int questionCount)
    {
      this.questionCount = questionCount;
      return this;
    }

    public Builder withEnrollmentCount(Object enrollmentCount)
    {
      this.enrollmentCount = enrollmentCount;
      return this;
    }

    public ExamAnalytics build()
    {
      ExamAnalytics output = new ExamAnalytics(this.id);
      output.title = this.title;
      output.categoryName = this.categoryName;
      output.status = this.status;
      output.duration = this.duration;
      output.averageScore = this.averageScore;
      output.averageSpentTime = this.averageSpentTime;
      output.passedCount = this.passedCount;
      output.totalRuntime = this.totalRuntime;
      output.maxScore = this.maxScore;
      output.enrollmentCount = this.enrollmentCount;
      output.questionCount = this.questionCount;
      return output;
    }
  }
}
