package mn.erin.lms.jarvis.mongo.document.report;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * @author Bat-Erdene Tsogoo.
 */
@Document
public class MongoAssessmentReport
{
  @Id
  private String id;

  @Indexed
  private String assessmentId;

  private List<MongoAssessmentReportItem> items;

  @Indexed
  private LocalDateTime date;

  public MongoAssessmentReport()
  {
  }

  public MongoAssessmentReport(String id, String assessmentId, LocalDateTime date,
      List<MongoAssessmentReportItem> items)
  {
    this.id = id;
    this.items = items;
    this.assessmentId = assessmentId;
    this.date = date;
  }

  public String getId()
  {
    return id;
  }

  public List<MongoAssessmentReportItem> getItems()
  {
    return items;
  }

  public LocalDateTime getDate()
  {
    return date;
  }

  public void setDate(LocalDateTime date)
  {
    this.date = date;
  }

  public String getAssessmentId()
  {
    return assessmentId;
  }

  public void setId(String id)
  {
    this.id = id;
  }

  public void setAssessmentId(String assessmentId)
  {
    this.assessmentId = assessmentId;
  }

  public void setItems(List<MongoAssessmentReportItem> items)
  {
    this.items = items;
  }
}
