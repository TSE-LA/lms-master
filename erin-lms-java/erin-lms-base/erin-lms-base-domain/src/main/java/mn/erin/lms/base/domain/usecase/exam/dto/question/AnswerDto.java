package mn.erin.lms.base.domain.usecase.exam.dto.question;

/**
 * @author Oyungerel Chuluunsukh
 **/
public class AnswerDto
{
  private String value;
  private int index;
  private int matchIndex;
  private int column;
  private boolean correct;
  private int weight;

  public int getIndex()
  {
    return index;
  }

  public void setIndex(int index)
  {
    this.index = index;
  }

  public int getMatchIndex()
  {
    return matchIndex;
  }

  public void setMatchIndex(int matchIndex)
  {
    this.matchIndex = matchIndex;
  }

  public int getColumn()
  {
    return column;
  }

  public void setColumn(int column)
  {
    this.column = column;
  }

  public boolean isCorrect()
  {
    return correct;
  }

  public void setCorrect(boolean correct)
  {
    this.correct = correct;
  }

  public int getWeight()
  {
    return weight;
  }

  public void setWeight(int weight)
  {
    this.weight = weight;
  }

  public String getValue()
  {
    return value;
  }

  public void setValue(String value)
  {
    this.value = value;
  }
}
