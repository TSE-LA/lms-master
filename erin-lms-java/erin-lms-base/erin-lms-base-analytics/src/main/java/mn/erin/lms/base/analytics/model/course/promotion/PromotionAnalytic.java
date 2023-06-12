package mn.erin.lms.base.analytics.model.course.promotion;

import mn.erin.lms.base.analytics.model.Analytic;

/**
 * @author Munkh
 */
public class PromotionAnalytic implements Analytic
{
  private final String id;
  private String contentId;
  private String code;
  private String name;
  private String author;
  private String createdDate;
  private int totalEnrollment;
  private int views;
  private double status;
  private int questionCount;
  private double score;
  private int feedback;
  private boolean hasTest;

  public PromotionAnalytic(String id)
  {
    this.id = id;
  }

  public String getId()
  {
    return id;
  }

  public String getContentId()
  {
    return contentId;
  }

  public String getCode()
  {
    return code;
  }

  public String getName()
  {
    return name;
  }

  public String getAuthor()
  {
    return author;
  }

  public String getCreatedDate()
  {
    return createdDate;
  }

  public int getTotalEnrollment()
  {
    return totalEnrollment;
  }

  public int getViews()
  {
    return views;
  }

  public double getStatus()
  {
    return status;
  }

  public int getQuestionCount()
  {
    return questionCount;
  }

  public double getScore()
  {
    return score;
  }

  public int getFeedback()
  {
    return feedback;
  }

  public boolean isHasTest()
  {
    return hasTest;
  }

  public static class Builder {
    private final String id;
    private String contentId;
    private String code;
    private String name;
    private String author;
    private String createdDate;
    private int totalEnrollment;
    private int views;
    private double status;
    private int questionCount;
    private double score;
    private int feedback;
    private boolean hasTest;

    public Builder(String id)
    {
      this.id = id;
    }

    public Builder withContentId(String contentId)
    {
      this.contentId = contentId;
      return this;
    }

    public Builder withCode(String code)
    {
      this.code = code;
      return this;
    }

    public Builder withName(String name)
    {
      this.name = name;
      return this;
    }

    public Builder withAuthor(String author)
    {
      this.author = author;
      return this;
    }

    public Builder withCreatedDate(String createdDate)
    {
      this.createdDate = createdDate;
      return this;
    }

    public Builder withTotalEnrollment(int totalEnrollment)
    {
      this.totalEnrollment = totalEnrollment;
      return this;
    }

    public Builder withViews(int views)
    {
      this.views = views;
      return this;
    }

    public Builder withStatus(double status)
    {
      this.status = status;
      return this;
    }

    public Builder withQuestionCount(int questionCount)
    {
      this.questionCount = questionCount;
      return this;
    }

    public Builder withScore(double score)
    {
      this.score = score;
      return this;
    }

    public Builder withFeedback(int feedback)
    {
      this.feedback = feedback;
      return this;
    }

    public Builder hasTest(boolean hasTest)
    {
      this.hasTest = hasTest;
      return this;
    }

    public PromotionAnalytic build()
    {
      PromotionAnalytic output = new PromotionAnalytic(this.id);

      output.contentId = this.contentId;
      output.code = this.code;
      output.name = this.name;
      output.author = this.author;
      output.createdDate = this.createdDate;
      output.totalEnrollment = this.totalEnrollment;
      output.views = this.views;
      output.status = this.status;
      output.questionCount = this.questionCount;
      output.score = this.score;
      output.feedback = this.feedback;
      output.hasTest = this.hasTest;

      return output;
    }
  }
}
