package mn.erin.lms.base.domain.usecase.assessment.dto;

/**
 * @author Bat-Erdene Tsogoo.
 */
public class UpdateAssessmentInput
{
  private final String id;
  private final String name;
  private final String description;

  public UpdateAssessmentInput(String id, String name, String description)
  {
    this.id = id;
    this.name = name;
    this.description = description;
  }

  public String getId()
  {
    return id;
  }

  public String getName()
  {
    return name;
  }

  public String getDescription()
  {
    return description;
  }
}
