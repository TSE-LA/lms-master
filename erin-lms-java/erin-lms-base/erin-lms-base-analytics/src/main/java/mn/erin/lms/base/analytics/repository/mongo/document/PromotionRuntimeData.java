package mn.erin.lms.base.analytics.repository.mongo.document;

/**
 * @author Munkh
 */
public  class PromotionRuntimeData
{
  private final String id;
  private double progress;
  private int feedback;
  private int score;
  private int views;

  public PromotionRuntimeData(String id, double progress, int feedback, int score, int views)
  {
    this.id = id;
    this.progress = progress;
    this.feedback = feedback;
    this.score = score;
    this.views = views;
  }

  public String getId()
  {
    return id;
  }

  public double getProgress()
  {
    return progress;
  }

  public void setProgress(double progress)
  {
    this.progress = progress;
  }

  public int getFeedback()
  {
    return feedback;
  }

  public void setFeedback(int feedback)
  {
    this.feedback = feedback;
  }

  public int getScore()
  {
    return score;
  }

  public void setScore(int score)
  {
    this.score = score;
  }

  public int getViews()
  {
    return views;
  }

  public void setViews(int views)
  {
    this.views = views;
  }
}
