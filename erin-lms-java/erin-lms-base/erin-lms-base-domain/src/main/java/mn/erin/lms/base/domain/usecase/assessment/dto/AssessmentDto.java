package mn.erin.lms.base.domain.usecase.assessment.dto;

import java.util.Date;

/**
 * @author Bat-Erdene Tsogoo.
 */
public class AssessmentDto
{
  private String id;
  private String name;
  private String authorId;
  private String quizId;
  private Date createdDate;
  private String description;
  private Date modifiedDate;
  private String status;
  private Integer questionCount;

  public String getId()
  {
    return id;
  }

  public void setId(String id)
  {
    this.id = id;
  }

  public String getName()
  {
    return name;
  }

  public void setName(String name)
  {
    this.name = name;
  }

  public String getStatus()
  {
    return status;
  }

  public void setStatus(String status)
  {
    this.status = status;
  }

  public String getAuthorId()
  {
    return authorId;
  }

  public void setAuthorId(String authorId)
  {
    this.authorId = authorId;
  }

  public String getQuizId()
  {
    return quizId;
  }

  public void setQuizId(String quizId)
  {
    this.quizId = quizId;
  }

  public Date getCreatedDate()
  {
    return createdDate;
  }

  public void setCreatedDate(Date createdDate)
  {
    this.createdDate = createdDate;
  }

  public Date getModifiedDate()
  {
    return modifiedDate;
  }

  public void setModifiedDate(Date modifiedDate)
  {
    this.modifiedDate = modifiedDate;
  }

  public Integer getQuestionCount()
  {
    return questionCount;
  }

  public void setQuestionCount(Integer questionCount)
  {
    this.questionCount = questionCount;
  }

  public String getDescription()
  {
    return description;
  }

  public void setDescription(String description)
  {
    this.description = description;
  }
}
