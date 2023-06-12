package mn.erin.lms.base.domain.usecase.exam.dto;

import java.util.ArrayList;
import java.util.List;

import mn.erin.domain.base.model.Entity;

public class ExamRuntimeData implements Entity<ExamRuntimeData>
{
  private String id;
  private final String examId;
  private final double maxScore;
  private final int duration;
  private final String learnerId;
  private final int maxAttempt;
  private final double thresholdScore;

  private List<ExamInteractionDto> interactions = new ArrayList<>();

  public ExamRuntimeData(String id, String examId, String learnerId, double maxScore, int duration, int maxAttempt, double thresholdScore)
  {
    this.id = id;
    this.examId = examId;
    this.maxScore = maxScore;
    this.duration = duration;
    this.learnerId = learnerId;
    this.maxAttempt = maxAttempt;
    this.thresholdScore = thresholdScore;
  }


  public String getId()
  {
    return id;
  }

  public void setId(String id)
  {
    this.id = id;
  }

  public List<ExamInteractionDto> getInteractions()
  {
    return interactions;
  }

  public void setInteractions(List<ExamInteractionDto> interactions)
  {
    this.interactions = interactions;
  }

  public double getThresholdScore()
  {
    return thresholdScore;
  }

  public int getDuration()
  {
    return duration;
  }

  public String getLearnerId()
  {
    return learnerId;
  }

  public double getMaxScore()
  {
    return maxScore;
  }

  public String getExamId()
  {
    return examId;
  }

  public int getMaxAttempt()
  {
    return maxAttempt;
  }

  public ExamInteractionDto getInteractionWithMaxScore()
  {
    if (interactions.isEmpty())
    {
      return null;
    }
    double maxScore = 0;
    int interactionIndex = 0;
    for (int i = 0; i < interactions.size(); i++)
    {
      ExamInteractionDto interaction = interactions.get(i);
      if (interaction.getScore() >= maxScore)
      {
        maxScore = interaction.getScore();
        interactionIndex = i;
      }
    }
    return interactions.get(interactionIndex);
  }

  @Override
  public boolean sameIdentityAs(ExamRuntimeData other)
  {
    return id.equals(other.id);
  }
}
