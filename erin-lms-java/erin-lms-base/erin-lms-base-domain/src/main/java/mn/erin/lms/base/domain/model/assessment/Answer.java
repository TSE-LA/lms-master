package mn.erin.lms.base.domain.model.assessment;

import java.util.Objects;

import org.apache.commons.lang3.Validate;

import mn.erin.domain.base.model.ValueObject;

/**
 * @author Bat-Erdene Tsogoo.
 */
public class Answer implements ValueObject<Answer>
{
  private final String value;
  private final boolean correct;
  private final Double score;

  public Answer(String value, boolean correct, Double score)
  {
    this.value = Validate.notNull(value, "Answer value cannot be null!");
    this.score = Objects.requireNonNull(score, "Answer score cannot be null!");
    this.correct = correct;
  }

  public String getValue()
  {
    return value;
  }

  public boolean isCorrect()
  {
    return correct;
  }

  public Double getScore()
  {
    return score;
  }

  @Override
  public boolean sameValueAs(Answer other)
  {
    return other != null && (this.value.equals(other.value) && this.correct == other.correct && this.score.equals(other.score));
  }

  @Override
  public boolean equals(Object obj)
  {
    if (obj instanceof Answer)
    {
      return sameValueAs((Answer) obj);
    }
    return false;
  }

  @Override
  public int hashCode()
  {
    return Objects.hash(value, correct, score);
  }

  @Override
  public String toString()
  {
    return "Answer{" +
        "value='" + value + '\'' +
        ", correct=" + correct +
        ", score=" + score +
        '}';
  }
}
