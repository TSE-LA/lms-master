package mn.erin.lms.base.domain.usecase.exam.dto;


/**
 * @author Galsan Bayart.
 */
public class ExamConfigureDto
{
  int duration;
  String showAnswerResult;
  boolean perPage;

  public ExamConfigureDto(int duration, String showAnswerResult, boolean perPage)
  {
    this.duration = duration;
    this.showAnswerResult = showAnswerResult;
    this.perPage = perPage;
  }

  public int getDuration()
  {
    return duration;
  }

  public void setDuration(int duration)
  {
    this.duration = duration;
  }

  public String getShowAnswerResult()
  {
    return showAnswerResult;
  }

  public void setShowAnswerResult(String showAnswerResult)
  {
    this.showAnswerResult = showAnswerResult;
  }

  public boolean isPerPage()
  {
    return perPage;
  }

  public void setPerPage(boolean perPage)
  {
    this.perPage = perPage;
  }
}
