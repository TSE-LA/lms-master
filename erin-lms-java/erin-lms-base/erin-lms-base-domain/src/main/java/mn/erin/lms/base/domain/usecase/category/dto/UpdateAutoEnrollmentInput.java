package mn.erin.lms.base.domain.usecase.category.dto;

/**
 * @author Munkh
 */
public class UpdateAutoEnrollmentInput
{
  private String id;
  private Boolean autoEnroll;

  public UpdateAutoEnrollmentInput(String id, Boolean autoEnroll)
  {
    this.id = id;
    this.autoEnroll = autoEnroll;
  }

  public String getId()
  {
    return id;
  }

  public void setId(String id)
  {
    this.id = id;
  }

  public Boolean getAutoEnroll()
  {
    return autoEnroll;
  }

  public void setAutoEnroll(Boolean autoEnroll)
  {
    this.autoEnroll = autoEnroll;
  }
}
