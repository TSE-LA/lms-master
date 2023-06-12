package mn.erin.lms.base.domain.model.exam.question;

/**
 * @author Galsan Bayart
 */
public class QuestionState
{
  private QuestionStateId questionStateId;
  private String tenantId;
  private String name;

  public QuestionState(String tenantId, String name)
  {
    this.tenantId = tenantId;
    this.name = name;
  }

  public QuestionState(QuestionStateId questionStateId, String tenantId, String name)
  {
    this.questionStateId = questionStateId;
    this.tenantId = tenantId;
    this.name = name;
  }

  public QuestionStateId getQuestionStateId()
  {
    return questionStateId;
  }

  public void setQuestionStateId(QuestionStateId questionStateId)
  {
    this.questionStateId = questionStateId;
  }

  public String getTenantId()
  {
    return tenantId;
  }

  public void setTenantId(String tenantId)
  {
    this.tenantId = tenantId;
  }

  public String getName()
  {
    return name;
  }

  public void setName(String name)
  {
    this.name = name;
  }
}
