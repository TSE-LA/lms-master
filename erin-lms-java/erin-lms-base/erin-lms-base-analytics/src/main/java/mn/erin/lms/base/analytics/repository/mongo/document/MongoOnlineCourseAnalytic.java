package mn.erin.lms.base.analytics.repository.mongo.document;

/**
 * @author Munkh
 */
public class MongoOnlineCourseAnalytic
{
  private final String id;
  private String title;
  private String courseType;
  private String categoryName;
  private String courseContentId;
  private boolean hasCertificate;
  private int learnersCount;
  private double totalProgress;
  private int viewersCount;
  private int repeatedViewersCount;

  private MongoOnlineCourseAnalytic(String id)
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

  public int getLearnersCount()
  {
    return learnersCount;
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

  public static class Builder {
    private final String id;
    private String title;
    private String courseType;
    private String categoryName;
    private String courseContentId;
    private boolean hasCertificate;
    private int learnersCount;
    private double totalProgress;
    private int viewersCount;
    private int repeatedViewersCount;

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

    public Builder withLearnersCount(int learnersCount)
    {
      this.learnersCount = learnersCount;
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

    public MongoOnlineCourseAnalytic build()
    {
      MongoOnlineCourseAnalytic output = new MongoOnlineCourseAnalytic(this.id);

      output.title = this.title;
      output.courseType = this.courseType;
      output.categoryName = this.categoryName;
      output.courseContentId = this.courseContentId;
      output.hasCertificate = this.hasCertificate;
      output.learnersCount = this.learnersCount;
      output.totalProgress = this.totalProgress;
      output.viewersCount = this.viewersCount;
      output.repeatedViewersCount = this.repeatedViewersCount;

      return output;
    }
  }
}
