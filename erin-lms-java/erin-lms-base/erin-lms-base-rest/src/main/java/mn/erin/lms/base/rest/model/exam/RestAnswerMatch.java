package mn.erin.lms.base.rest.model.exam;

import java.util.ArrayList;
import java.util.List;


/**
 * @author Galsan Bayart.
 */
public class RestAnswerMatch
{
  List<RestAnswer> firstLine = new ArrayList<>();
  List<RestAnswer> secondLine = new ArrayList<>();

  public RestAnswerMatch(List<RestAnswer> firstLine, List<RestAnswer> secondLine)
  {
    this.firstLine = firstLine;
    this.secondLine = secondLine;
  }

  public RestAnswerMatch()
  {
  }

  public List<RestAnswer> getFirstLine()
  {
    return firstLine;
  }

  public void setFirstLine(List<RestAnswer> firstLine)
  {
    this.firstLine = firstLine;
  }

  public List<RestAnswer> getSecondLine()
  {
    return secondLine;
  }

  public void setSecondLine(List<RestAnswer> secondLine)
  {
    this.secondLine = secondLine;
  }
}
