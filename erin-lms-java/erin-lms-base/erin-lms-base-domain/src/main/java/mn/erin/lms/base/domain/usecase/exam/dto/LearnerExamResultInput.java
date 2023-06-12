package mn.erin.lms.base.domain.usecase.exam.dto;

/**
 * @author Temuulen Naranbold
 */
public class LearnerExamResultInput
{
  private String examId;
  private String learnerId;

  public LearnerExamResultInput(String examId, String learnerId)
  {
    this.examId = examId;
    this.learnerId = learnerId;
  }

  public String getExamId()
  {
    return examId;
  }

  public void setExamId(String examId)
  {
    this.examId = examId;
  }

  public String getLearnerId()
  {
    return learnerId;
  }

  public void setLearnerId(String learnerId)
  {
    this.learnerId = learnerId;
  }
}
