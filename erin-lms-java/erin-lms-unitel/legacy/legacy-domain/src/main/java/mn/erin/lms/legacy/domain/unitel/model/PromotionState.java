package mn.erin.lms.legacy.domain.unitel.model;

public enum PromotionState
{
  MAIN("main"),
  CURRENT("current"),
  EXPIRED("expired");

  private String stateName;

  PromotionState(String stateName)
  {
    this.stateName = stateName;
  }

  public String getStateName()
  {
    return this.stateName;
  }
}
