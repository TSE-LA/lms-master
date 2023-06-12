package mn.erin.lms.jarvis.domain.report.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import mn.erin.lms.base.domain.model.assessment.AssessmentId;

/**
 * @author Bat-Erdene Tsogoo.
 */
public class AssessmentReport
{
  private final String reportId;
  private final AssessmentId assessmentId;
  private List<AssessmentReportItem> items = new ArrayList<>();
  private LocalDateTime date;

  public AssessmentReport(String reportId,AssessmentId assessmentId)
  {
    this.assessmentId = assessmentId;
    this.reportId = reportId;
  }

  public AssessmentReport(String reportId, AssessmentId assessmentId, List<AssessmentReportItem> items, LocalDateTime date)
  {
    this.reportId = reportId;
    this.assessmentId = assessmentId;
    this.items = items;
    this.date = date;
  }

  public void add(AssessmentReportItem item)
  {
    this.items.add(item);
  }

  public AssessmentId getAssessmentId()
  {
    return assessmentId;
  }

  public List<AssessmentReportItem> getItems()
  {
    return items;
  }

  public String getReportId()
  {
    return reportId;
  }

  public void setItems(List<AssessmentReportItem> items)
  {
    this.items = items;
  }

  public LocalDateTime getDate()
  {
    return date;
  }

  public void setDate(LocalDateTime date)
  {
    this.date = date;
  }
}
