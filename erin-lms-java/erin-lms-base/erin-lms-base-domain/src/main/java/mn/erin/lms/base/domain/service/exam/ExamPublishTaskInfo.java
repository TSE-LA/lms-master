package mn.erin.lms.base.domain.service.exam;

import java.util.Set;

import mn.erin.lms.base.domain.model.exam.Exam;
import mn.erin.lms.base.domain.model.exam.ExamId;

/**
 * @author mLkhagvasuren
 */
public class ExamPublishTaskInfo
{
  private ExamId examId;
  private Set<String> enrolledLearners;

  public ExamPublishTaskInfo()
  {
  }

  public ExamPublishTaskInfo(Exam exam)
  {
    this.examId = exam.getId();
    this.enrolledLearners = exam.getEnrolledLearners();
  }

  public ExamId getExamId()
  {
    return examId;
  }

  public void setExamId(ExamId examId)
  {
    this.examId = examId;
  }

  public Set<String> getEnrolledLearners()
  {
    return enrolledLearners;
  }

  public void setEnrolledLearners(Set<String> enrolledLearners)
  {
    this.enrolledLearners = enrolledLearners;
  }
}
