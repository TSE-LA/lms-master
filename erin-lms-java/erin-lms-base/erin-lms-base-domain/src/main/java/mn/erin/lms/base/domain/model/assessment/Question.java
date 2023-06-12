package mn.erin.lms.base.domain.model.assessment;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.Validate;

import mn.erin.domain.base.model.ValueObject;

/**
 * @author Bat-Erdene Tsogoo.
 */
public class Question implements ValueObject<Question>
{
  private final QuestionType type;
  private final String title;
  private final boolean isRequired;
  private List<Answer> answers = new ArrayList<>();

  public Question(String title, QuestionType type, boolean isRequired)
  {
    this.title = Validate.notBlank(title, "Question title cannot be null!");
    this.type = type;
    this.isRequired = isRequired;
  }

  public String getTitle()
  {
    return title;
  }

  public List<Answer> getAnswers()
  {
    return answers;
  }

  public void addAnswer(Answer answer)
  {
    if (this.type.equals(QuestionType.SINGLE_CHOICE) && answer.isCorrect())
    {
      this.answers.forEach(a -> {
        if (a.isCorrect())
        {
          throw new IllegalArgumentException("A correct answer is already defined for the single-choice question [" + this.title + "]");
        }
      });
    }
    this.answers.add(answer);
  }

  public QuestionType getType()
  {
    return type;
  }

  @Override
  public boolean sameValueAs(Question other)
  {
    return other != null && (this.answers.equals(other.answers) && this.title.equals(other.title) && this.type.equals(other.type));
  }

  public boolean isRequired() {
    return isRequired;
  }
}
