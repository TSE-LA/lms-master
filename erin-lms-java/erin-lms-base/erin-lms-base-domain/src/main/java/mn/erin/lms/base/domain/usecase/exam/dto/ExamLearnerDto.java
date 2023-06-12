package mn.erin.lms.base.domain.usecase.exam.dto;

import java.util.Date;

/**
 * @author Galsan Bayart.
 */
public class ExamLearnerDto {
  private String id;
  private String name;
  private String description;
  private int thresholdScore;
  private Date startDate;
  private Date endDate;
  private String startTime;
  private int duration;
  private String author;
  private String examState;
  private String examPublishStatus;
  private String certificateId;
  private ExamRuntimeData examRuntimeData;


  public ExamLearnerDto(String id, int thresholdScore, String examState) {
    this.id = id;
    this.thresholdScore = thresholdScore;
    this.examState = examState;
  }

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public int getThresholdScore() {
    return thresholdScore;
  }

  public void setThresholdScore(int thresholdScore) {
    this.thresholdScore = thresholdScore;
  }

  public Date getStartDate() {
    return startDate;
  }

  public void setStartDate(Date startDate) {
    this.startDate = startDate;
  }

  public Date getEndDate() {
    return endDate;
  }

  public void setEndDate(Date endDate) {
    this.endDate = endDate;
  }

  public int getDuration() {
    return duration;
  }

  public void setDuration(int duration) {
    this.duration = duration;
  }

  public String getAuthor() {
    return author;
  }

  public void setAuthor(String author) {
    this.author = author;
  }

  public String getCertificateId() {
    return certificateId;
  }

  public void setCertificateId(String certificateId) {
    this.certificateId = certificateId;
  }

  public String getStartTime() {
    return startTime;
  }

  public void setStartTime(String startTime) {
    this.startTime = startTime;
  }

  public String getExamState() {
    return examState;
  }

  public String getName() { return name;}

  public void setName(String name) { this.name = name;}

  public String getDescription() { return description;}

  public void setDescription(String description) { this.description = description;}

  public void setExamState(String examState) {
    this.examState = examState;
  }

  public ExamRuntimeData getExamRuntimeDataDto() {
    return examRuntimeData;
  }

  public void setExamRuntimeDataDto(ExamRuntimeData examRuntimeData) {
    this.examRuntimeData = examRuntimeData;
  }

  public String getExamPublishStatus()
  {
    return examPublishStatus;
  }

  public void setExamPublishStatus(String examPublishStatus)
  {
    this.examPublishStatus = examPublishStatus;
  }
}
