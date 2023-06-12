package mn.erin.lms.base.domain.model.enrollment;

import mn.erin.domain.base.model.Entity;

/**
 * @author Galsan Bayart
 */
public class ExamEnrollment implements Entity<ExamEnrollment>
{
  private final ExamEnrollmentId id;
  private final String examId;
  private final String learnerId;
  private final String permission;

  public ExamEnrollment(ExamEnrollmentId id, String examId, String learnerId, String permission)
  {
    this.id = id;
    this.examId = examId;
    this.learnerId = learnerId;
    this.permission = permission;
  }

  public String getExamId()
  {
    return examId;
  }

  public String getLearnerId()
  {
    return learnerId;
  }

  @Override
  public boolean sameIdentityAs(ExamEnrollment other)
  {
    return this.id.equals(other.id);
  }

  public ExamEnrollmentId getId()
  {
    return id;
  }
}
