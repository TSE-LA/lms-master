package mn.erin.lms.base.mongo.document.exam;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;

import mn.erin.lms.base.domain.usecase.exam.dto.ExamInteractionDto;
import mn.erin.lms.base.domain.usecase.exam.dto.ExamRuntimeStatus;

/**
 * @author Byambajav
 */
public class MongoExamRuntimeData
{
  @Id
  private String id;
  @Indexed
  private final String learnerId;
  @Indexed
  private final String examId;
  @Indexed
  private ExamRuntimeStatus status = ExamRuntimeStatus.UNSET;
  private final int duration;
  private final int maxAttempt;
  private final double thresholdScore;
  private double maxScore;

  private List<ExamInteractionDto> examInteraction = new ArrayList<>();

  public MongoExamRuntimeData(String id, String examId, String learnerId, double maxScore, int duration, int maxAttempt, double thresholdScore)
  {
    this.id = id;
    this.examId = examId;
    this.learnerId = learnerId;
    this.maxScore = maxScore;
    this.duration = duration;
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

  public String getExamId()
  {
    return examId;
  }

  public double getThresholdScore()
  {
    return thresholdScore;
  }

  public double getMaxScore()
  {
    return maxScore;
  }

  public void setMaxScore(int maxScore)
  {
    this.maxScore = maxScore;
  }

  public int getDuration()
  {
    return duration;
  }

  public String getLearnerId()
  {
    return learnerId;
  }

  public int getMaxAttempt()
  {
    return maxAttempt;
  }

  public List<ExamInteractionDto> getExamInteraction()
  {
    return examInteraction;
  }

  public ExamRuntimeStatus getStatus()
  {
    return status;
  }

  public void setStatus(ExamRuntimeStatus status)
  {
    this.status = status;
  }

  public void setExamInteraction(List<ExamInteractionDto> examInteraction)
  {
    this.examInteraction = examInteraction;
  }
}
