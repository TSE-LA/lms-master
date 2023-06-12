package mn.erin.lms.base.domain.usecase.assessment.dto;

/**
 * @author Bat-Erdene Tsogoo.
 */
public class CreateAssessmentInput
{
  private final String name;
  private String description;

  public CreateAssessmentInput(String name)
  {
    this.name = name;
  }

  public void setDescription(String description)
  {
    this.description = description;
  }

  public String getDescription()
  {
    return description;
  }

  public String getName()
  {
    return name;
  }
}
