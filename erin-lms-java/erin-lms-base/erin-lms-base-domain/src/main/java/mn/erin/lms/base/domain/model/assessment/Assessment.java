package mn.erin.lms.base.domain.model.assessment;

import java.time.LocalDateTime;

import mn.erin.domain.base.model.Entity;
import mn.erin.lms.base.aim.user.AuthorId;

/**
 * @author Bat-Erdene Tsogoo.
 */
public class Assessment implements Entity<Assessment>
{
  private final AssessmentId assessmentId;
  private final String name;
  private final AuthorId authorId;

  private AssessmentStatus status = AssessmentStatus.INACTIVE;

  private QuizId quizId;
  private String description;

  private LocalDateTime createdDate;
  private LocalDateTime modifiedDate;

  public Assessment(AssessmentId assessmentId, String name, AuthorId authorId)
  {
    this.assessmentId = assessmentId;
    this.name = name;
    this.authorId = authorId;
  }

  public void activate()
  {
    this.status = AssessmentStatus.ACTIVE;
  }

  public AssessmentStatus getStatus()
  {
    return status;
  }

  public AssessmentId getAssessmentId()
  {
    return assessmentId;
  }

  public LocalDateTime getCreatedDate()
  {
    return createdDate;
  }

  public LocalDateTime getModifiedDate()
  {
    return modifiedDate;
  }

  public String getName()
  {
    return name;
  }

  public AuthorId getAuthorId()
  {
    return authorId;
  }

  public void setQuizId(QuizId quizId)
  {
    this.quizId = quizId;
  }

  public void setCreatedDate(LocalDateTime createdDate)
  {
    this.createdDate = createdDate;
  }

  public void setModifiedDate(LocalDateTime modifiedDate)
  {
    this.modifiedDate = modifiedDate;
  }

  public QuizId getQuizId()
  {
    return quizId;
  }

  public String getDescription()
  {
    return description;
  }

  public void setDescription(String description)
  {
    this.description = description;
  }

  @Override
  public boolean sameIdentityAs(Assessment assessment)
  {
    return this.assessmentId.sameValueAs(assessment.assessmentId);
  }
}
