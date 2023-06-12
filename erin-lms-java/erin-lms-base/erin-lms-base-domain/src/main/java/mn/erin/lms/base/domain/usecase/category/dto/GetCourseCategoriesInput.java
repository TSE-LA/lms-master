package mn.erin.lms.base.domain.usecase.category.dto;

import org.apache.commons.lang3.Validate;

/**
 * @author Bat-Erdene Tsogoo.
 */
public class GetCourseCategoriesInput
{
  private final String organizationId;
  private final String parentCategoryId;
  private Boolean autoEnroll;

  public GetCourseCategoriesInput(String organizationId, String parentCategoryId)
  {
    this.organizationId = Validate.notBlank(organizationId);
    this.parentCategoryId = parentCategoryId;
  }

  public String getOrganizationId()
  {
    return organizationId;
  }

  public String getParentCategoryId()
  {
    return parentCategoryId;
  }

  public Boolean isAutoEnroll()
  {
    return autoEnroll;
  }

  public void setAutoEnroll(Boolean autoEnroll)
  {
    this.autoEnroll = autoEnroll;
  }
}
