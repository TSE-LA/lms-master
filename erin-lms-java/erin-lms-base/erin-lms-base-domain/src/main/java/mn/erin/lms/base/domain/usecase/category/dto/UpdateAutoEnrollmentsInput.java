package mn.erin.lms.base.domain.usecase.category.dto;

import java.util.Set;

/**
 * @author Munkh
 */
public class UpdateAutoEnrollmentsInput
{
  private Set<String> ids;
  private Boolean autoEnroll;

  public UpdateAutoEnrollmentsInput(Set<String> ids, Boolean autoEnroll)
  {
    this.ids = ids;
    this.autoEnroll = autoEnroll;
  }

  public Set<String> getIds()
  {
    return ids;
  }

  public void setIds(Set<String> ids)
  {
    this.ids = ids;
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
