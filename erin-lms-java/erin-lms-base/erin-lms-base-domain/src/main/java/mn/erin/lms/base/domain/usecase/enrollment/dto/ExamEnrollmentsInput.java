package mn.erin.lms.base.domain.usecase.enrollment.dto;

import java.util.Set;

/**
 * TODO: add javadoc
 *
 * @author EBazarragchaa
 */
public class ExamEnrollmentsInput
{
  private final Set<String> learnerIds;
  private final String examId;

  public ExamEnrollmentsInput(Set<String> learnerIds, String examId)
  {
    this.learnerIds = learnerIds;
    this.examId = examId;
  }

  public String getExamId()
  {
    return examId;
  }

  public Set<String> getLearnerIds()
  {
    return learnerIds;
  }
}
