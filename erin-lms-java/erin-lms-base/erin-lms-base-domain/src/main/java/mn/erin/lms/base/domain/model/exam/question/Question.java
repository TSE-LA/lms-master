package mn.erin.lms.base.domain.model.exam.question;

import java.util.Date;
import java.util.List;
import java.util.Objects;

import org.apache.commons.lang3.Validate;

import mn.erin.domain.base.model.Entity;

/**
 * @author Galsan Bayart
 */
public class Question implements Entity<Question>
{
  private final QuestionId id;
  private final String author;
  private final Date createdDate;
  private String value;
  private QuestionDetail detail;
  private QuestionType type;
  private List<Answer> answers;
  private QuestionStatus status;
  private String modifiedUser;
  private Date modifiedDate;
  private int score;

  private Question(QuestionId id, String value, String author, Date createdDate)
  {
    this.id = Objects.requireNonNull(id);
    this.value = Validate.notBlank(value);
    this.author = Validate.notBlank(author);
    this.createdDate = Objects.requireNonNull(createdDate);
  }

  public QuestionId getId()
  {
    return id;
  }

  public void setValue(String value)
  {
    this.value = value;
  }

  public String getValue()
  {
    return value;
  }

  public String getAuthor()
  {
    return author;
  }

  public Date getCreatedDate()
  {
    return createdDate;
  }

  public QuestionDetail getDetail()
  {
    return detail;
  }

  public void setDetail(QuestionDetail detail)
  {
    this.detail = detail;
  }

  public QuestionType getType()
  {
    return type;
  }

  public void setType(QuestionType type)
  {
    this.type = type;
  }

  public List<Answer> getAnswers()
  {
    return answers;
  }

  public void setAnswers(List<Answer> answers)
  {
    this.answers = answers;
  }

  public QuestionStatus getStatus()
  {
    return status;
  }

  public void setStatus(QuestionStatus status)
  {
    this.status = status;
  }

  public String getModifiedUser()
  {
    return modifiedUser;
  }

  public void setModifiedUser(String modifiedUser)
  {
    this.modifiedUser = modifiedUser;
  }

  public Date getModifiedDate()
  {
    return modifiedDate;
  }

  public void setModifiedDate(Date modifiedDate)
  {
    this.modifiedDate = modifiedDate;
  }

  public int getScore()
  {
    return score;
  }

  public void setScore(int score)
  {
    this.score = score;
  }

  @Override
  public boolean sameIdentityAs(Question other)
  {
    return this.id.sameValueAs(other.id);
  }

  public static class Builder
  {
    private final QuestionId id;
    private final String value;
    private final String author;
    private final Date createdDate;
    private QuestionDetail detail;
    private QuestionType type;
    private List<Answer> answers;
    private QuestionStatus status;
    private String modifiedUser;
    private Date modifiedDate;
    private int score;

    public Builder(QuestionId id, String value, String author, Date createdDate)
    {
      this.id = id;
      this.value = value;
      this.author = author;
      this.createdDate = createdDate;
    }

    public Builder withDetail(QuestionDetail detail)
    {
      this.detail = detail;
      return this;
    }

    public Builder withType(QuestionType type)
    {
      this.type = type;
      return this;
    }

    public Builder withAnswers(List<Answer> answers)
    {
      this.answers = answers;
      return this;
    }

    public Builder withStatus(QuestionStatus status)
    {
      this.status = status;
      return this;
    }

    public Builder withModifiedUser(String modifiedUser)
    {
      this.modifiedUser = modifiedUser;
      return this;
    }

    public Builder withModifiedDate(Date modifiedDate)
    {
      this.modifiedDate = modifiedDate;
      return this;
    }

    public Builder withScore(int score)
    {
      this.score = score;
      return this;
    }

    public Question build()
    {
      Question output = new Question(this.id, this.value, this.author, this.createdDate);
      output.detail = this.detail;
      output.type = this.type;
      output.answers = this.answers;
      output.status = this.status;
      output.modifiedUser = this.modifiedUser;
      output.modifiedDate = this.modifiedDate;
      output.score = this.score;
      return output;
    }
  }
}
