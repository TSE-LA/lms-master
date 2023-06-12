package mn.erin.lms.base.analytics.model.course.online_course;

import java.util.Set;

import mn.erin.lms.base.analytics.model.Analytic;

/**
 * @author Munkh
 */
public class OnlineCourseAnalytic implements Analytic
{
  private final String id;
  private String title;
  private String courseType;
  private String categoryName;
  private String courseContentId;
  private double totalProgress;
  private boolean hasCertificate;
  private int receivedCertificateCount;
  private int learnersCount;
  private Set<String> learners;
  private int viewersCount;
  private int repeatedViewersCount;
  private int completedViewersCount;
  private String spentTimeOnTest;
  private int maxScore;
  private Integer averageScore;

  private OnlineCourseAnalytic(String id)
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

  public String getCourseType()
  {
    return courseType;
  }

  public String getCategoryName()
  {
    return categoryName;
  }

  public String getCourseContentId()
  {
    return courseContentId;
  }

  public boolean isHasCertificate()
  {
    return hasCertificate;
  }

  public int getReceivedCertificateCount()
  {
    return receivedCertificateCount;
  }

  public int getLearnersCount()
  {
    return learnersCount;
  }

  public Set<String> getLearners()
  {
    return learners;
  }

  public double getTotalProgress()
  {
    return totalProgress;
  }

  public int getViewersCount()
  {
    return viewersCount;
  }

  public int getRepeatedViewersCount()
  {
    return repeatedViewersCount;
  }

  public int getCompletedViewersCount()
  {
    return completedViewersCount;
  }

  public String getSpentTimeOnTest()
  {
    return spentTimeOnTest;
  }

  public int getMaxScore() { return maxScore;}

  public Integer getAverageScore() { return averageScore;}


  public static class Builder
  {
    private final String id;
    private String title;
    private String courseType;
    private String categoryName;
    private String courseContentId;
    private double totalProgress;
    private boolean hasCertificate;
    private int receivedCertificateCount;
    private int learnersCount;
    private Set<String> learners;
    private int viewersCount;
    private int repeatedViewersCount;
    private int completedViewersCount;
    private String spentTimeOnTest;
    private int maxScore;
    private Integer averageScore;

    public Builder(String id)
    {
      this.id = id;
    }

    public Builder withTitle(String title)
    {
      this.title = title;
      return this;
    }

    public Builder withCourseType(String courseType)
    {
      this.courseType = courseType;
      return this;
    }

    public Builder withCategoryName(String categoryName)
    {
      this.categoryName = categoryName;
      return this;
    }

    public Builder withCourseContentId(String courseContentId)
    {
      this.courseContentId = courseContentId;
      return this;
    }

    public Builder withCertificate(boolean hasCertificate)
    {
      this.hasCertificate = hasCertificate;
      return this;
    }

    public Builder withReceivedCertificateCount(int receivedCertificateCount)
    {
      this.receivedCertificateCount = receivedCertificateCount;
      return this;
    }

    public Builder withLearners(Set<String> learners)
    {
      this.learners = learners;
      this.learnersCount = learners.size();
      return this;
    }

    public Builder withTotalProgress(double totalProgress)
    {
      this.totalProgress = totalProgress;
      return this;
    }

    public Builder withViewersCount(int viewersCount)
    {
      this.viewersCount = viewersCount;
      return this;
    }

    public Builder withRepeatedViewersCount(int repeatedViewersCount)
    {
      this.repeatedViewersCount = repeatedViewersCount;
      return this;
    }

    public Builder withCompletedViewersCount(int completedViewersCount)
    {
      this.completedViewersCount = completedViewersCount;
      return this;
    }

    public Builder withSpentTimeOnTest(String spentTimeOnTest)
    {
      this.spentTimeOnTest = spentTimeOnTest;
      return this;
    }

    public Builder withMaxScore(int maxScore)
    {
      this.maxScore = maxScore;
      return this;
    }

    public Builder withAverageScore(Integer averageScore)
    {
      this.averageScore = averageScore;
      return this;
    }

    public OnlineCourseAnalytic build()
    {
      OnlineCourseAnalytic output = new OnlineCourseAnalytic(this.id);

      output.title = this.title;
      output.courseType = this.courseType;
      output.categoryName = this.categoryName;
      output.courseContentId = this.courseContentId;
      output.totalProgress = this.totalProgress;
      output.hasCertificate = this.hasCertificate;
      output.receivedCertificateCount = this.receivedCertificateCount;
      output.learnersCount = this.learnersCount;
      output.learners = this.learners;
      output.viewersCount = this.viewersCount;
      output.repeatedViewersCount = this.repeatedViewersCount;
      output.completedViewersCount = this.completedViewersCount;
      output.spentTimeOnTest = this.spentTimeOnTest;
      output.averageScore = this.averageScore;
      output.maxScore = this.maxScore;
      return output;
    }
  }
}
