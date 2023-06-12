package mn.erin.lms.base.domain.model.exam;

import java.util.List;

/**
 * @author Galsan Bayart.
 */
public class ExamResult
{
  List<ExamResultEntity> examResults;

  public ExamResult(List<ExamResultEntity> examResults)
  {
    this.examResults = examResults;
  }

  public List<ExamResultEntity> getExamResults()
  {
    return examResults;
  }

  public void setExamResults(List<ExamResultEntity> examResults)
  {
    this.examResults = examResults;
  }
}
