package mn.erin.lms.legacy.domain.unitel.model;

public enum PromotionType
{
  DIRECT("Direct"), MASS("Mass");

  private String type;

  PromotionType(String type)
  {
    this.type = type;
  }

  public String getType()
  {
    return this.type;
  }
}
