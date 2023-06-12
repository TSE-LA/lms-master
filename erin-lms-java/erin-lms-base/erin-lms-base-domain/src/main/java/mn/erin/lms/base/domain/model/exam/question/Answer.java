package mn.erin.lms.base.domain.model.exam.question;

import mn.erin.domain.base.model.ValueObject;

/**
 * @author Temuulen Naranbold
 */
public class Answer implements ValueObject<Answer>
{
  private final String value;
  private int index;
  private int matchIndex;
  private int column;
  private boolean correct;
  private int weight;

  public Answer(String value, int index, int matchIndex, int column, boolean correct, int weight)
  {
    this.value = value;
    this.index = index;
    this.matchIndex = matchIndex;
    this.column = column;
    this.correct = correct;
    this.weight = weight;
  }

  public String getValue()
  {
    return value;
  }

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

  @Override
  public boolean sameValueAs(Answer other)
  {
    return other != null && (this.value.equals(other.value) && this.weight == other.weight) &&
        this.correct == other.correct && this.column == other.column && this.index == other.index &&
        this.matchIndex == other.matchIndex;
  }
}
