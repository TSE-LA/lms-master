package mn.erin.lms.base.domain.model.course;

import java.time.LocalDateTime;

import mn.erin.domain.base.model.Entity;

/**
 * @author Temuulen Naranbold
 */
public class LearnerCourseHistory implements Entity<LearnerCourseHistory>
{
  private final LearnerCourseHistoryId learnerCourseHistoryId;
  private final CourseId courseId;
  private final String userId;
  private String name;
  private String type;
  private double credit;
  private LocalDateTime completionDate;
  private Float percentage;

  public LearnerCourseHistory(LearnerCourseHistoryId learnerCourseHistoryId, CourseId courseId, String userId, String name, String type, double credit,
      LocalDateTime completionDate, Float percentage)
  {
    this.learnerCourseHistoryId = learnerCourseHistoryId;
    this.courseId = courseId;
    this.userId = userId;
    this.name = name;
    this.type = type;
    this.credit = credit;
    this.completionDate = completionDate;
    this.percentage = percentage;
  }

  public LearnerCourseHistoryId getLearnerCourseHistoryId()
  {
    return learnerCourseHistoryId;
  }

  public CourseId getCourseId()
  {
    return courseId;
  }

  public String getUserId()
  {
    return userId;
  }

  public String getName()
  {
    return name;
  }

  public void setName(String name)
  {
    this.name = name;
  }

  public String getType()
  {
    return type;
  }

  public void setType(String type)
  {
    this.type = type;
  }

  public double getCredit()
  {
    return credit;
  }

  public void setCredit(double credit)
  {
    this.credit = credit;
  }

  public LocalDateTime getCompletionDate()
  {
    return completionDate;
  }

  public void setCompletionDate(LocalDateTime completionDate)
  {
    this.completionDate = completionDate;
  }

  public Float getPercentage()
  {
    return percentage;
  }

  public void setPercentage(Float percentage)
  {
    this.percentage = percentage;
  }

  @Override
  public boolean sameIdentityAs(LearnerCourseHistory other)
  {
    return this.learnerCourseHistoryId.equals(other.learnerCourseHistoryId);
  }
}
