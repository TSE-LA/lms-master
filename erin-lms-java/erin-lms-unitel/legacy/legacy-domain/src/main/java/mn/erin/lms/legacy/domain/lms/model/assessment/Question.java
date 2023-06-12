/*
 * (C)opyright, 2019, ERIN SYSTEMS LLC
 * The source code is protected by copyright laws and international copyright
 * treaties, as well as other intellectual property laws and treaties.
 * All rights reserved.
 */

package mn.erin.lms.legacy.domain.lms.model.assessment;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.Validate;

import mn.erin.domain.base.model.ValueObject;

/**
 * @author Oyungerel Chuluunsukh.
 */
public class Question implements ValueObject<Question>
{
  private final QuestionType type;
  private final String title;
  private final List<Answer> answers = new ArrayList<>();

  public Question(String title, QuestionType type)
  {
    this.title = Validate.notBlank(title, "Question title cannot be null!");
    this.type = type;
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
}
