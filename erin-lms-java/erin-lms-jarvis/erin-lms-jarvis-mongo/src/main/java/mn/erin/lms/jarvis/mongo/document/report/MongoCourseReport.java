package mn.erin.lms.jarvis.mongo.document.report;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * @author Bat-Erdene Tsogoo.
 */
@Document
public class MongoCourseReport
{
  @Id
  private String id;
  private List<MongoReportData> reportData = new ArrayList<>();

  private MongoCourseReport()
  {
  }

  public MongoCourseReport(String id)
  {
    this.id = id;
  }

  public MongoCourseReport(String id, Integer questionsCount)
  {
    this.id = id;
  }

  public void addData(MongoReportData mongoReportData)
  {
    this.reportData.add(mongoReportData);
  }

  public String getId()
  {
    return id;
  }

  public List<MongoReportData> getReportData()
  {
    return reportData;
  }
}
