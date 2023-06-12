package mn.erin.lms.base.mongo.document.exam.question;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Galsan Bayart.
 */
public class MongoMatchAnswer
{
  List<MongoMatchAnswerEntity> firstLine = new ArrayList<>();
  List<MongoMatchAnswerEntity> secondLine = new ArrayList<>();

  public MongoMatchAnswer(List<MongoMatchAnswerEntity> firstLine, List<MongoMatchAnswerEntity> secondLine)
  {
    this.firstLine = firstLine;
    this.secondLine = secondLine;
  }

  public List<MongoMatchAnswerEntity> getFirstLine()
  {
    return firstLine;
  }

  public void setFirstLine(List<MongoMatchAnswerEntity> firstLine)
  {
    this.firstLine = firstLine;
  }

  public List<MongoMatchAnswerEntity> getSecondLine()
  {
    return secondLine;
  }

  public void setSecondLine(List<MongoMatchAnswerEntity> secondLine)
  {
    this.secondLine = secondLine;
  }

}
