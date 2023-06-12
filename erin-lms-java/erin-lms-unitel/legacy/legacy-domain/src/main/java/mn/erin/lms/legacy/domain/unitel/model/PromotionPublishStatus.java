package mn.erin.lms.legacy.domain.unitel.model;

public enum PromotionPublishStatus
{
  ALL("all"),
  PUBLISHED("published"),
  UNPUBLISHED("unpublished"),
  MODIFIED("modified");

  private String statusName;

  PromotionPublishStatus(String statusName)
  {
    this.statusName = statusName;
  }

  public String getStatusName()
  {
    return this.statusName;
  }
}
