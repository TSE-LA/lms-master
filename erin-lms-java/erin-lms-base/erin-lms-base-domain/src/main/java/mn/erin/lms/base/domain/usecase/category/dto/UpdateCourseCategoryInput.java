package mn.erin.lms.base.domain.usecase.category.dto;

/**
 * @author Munkh
 */
public class UpdateCourseCategoryInput
{
  private final String id;

  private String name;
  private String description;
  private boolean autoEnroll;

  public UpdateCourseCategoryInput(String id, String name, String description, boolean autoEnroll)
  {
    this.id = id;
    this.name = name;
    this.description = description;
    this.autoEnroll = autoEnroll;
  }

  public String getId()
  {
    return id;
  }

  public String getName()
  {
    return name;
  }

  public void setName(String name)
  {
    this.name = name;
  }

  public String getDescription()
  {
    return description;
  }

  public void setDescription(String description)
  {
    this.description = description;
  }

  public boolean isAutoEnroll()
  {
    return autoEnroll;
  }

  public void setAutoEnroll(boolean autoEnroll)
  {
    this.autoEnroll = autoEnroll;
  }
}
