package mn.erin.lms.jarvis.domain.report.usecase.dto;

import java.time.LocalDate;
import java.util.Map;
import java.util.Set;

/**
 * @author Bat-Erdene Tsogoo.
 */
public class CourseReportDto
{
  private String courseId;
  private String courseName;
  private boolean hasQuiz;
  private String authorId;
  private LocalDate courseCreatedDate;
  private Map<String, String> courseProperties;
  private Map<String, Object> reportData;
  private Set<String> enrolledLearners;

  private CourseReportDto()
  {
  }

  public String getCourseId()
  {
    return courseId;
  }

  public String getCourseName()
  {
    return courseName;
  }

  public boolean hasQuiz()
  {
    return hasQuiz;
  }

  public String getAuthorId()
  {
    return authorId;
  }

  public LocalDate getCourseCreatedDate()
  {
    return courseCreatedDate;
  }

  public Map<String, String> getCourseProperties()
  {
    return courseProperties;
  }

  public Map<String, Object> getReportData()
  {
    return reportData;
  }

  public Set<String> getEnrolledLearners()
  {
    return enrolledLearners;
  }

  public static class Builder
  {
    private String courseId;
    private String courseName;
    private boolean hasQuiz;
    private String authorId;
    private LocalDate courseCreatedDate;
    private Map<String, String> courseProperties;
    private Map<String, Object> reportData;
    private Set<String> enrolledLearners;

    public Builder(String courseId)
    {
      this.courseId = courseId;
    }

    public Builder withName(String courseName)
    {
      this.courseName = courseName;
      return this;
    }

    public Builder withAuthor(String authorId)
    {
      this.authorId = authorId;
      return this;
    }

    public Builder createdAt(LocalDate courseCreatedDate)
    {
      this.courseCreatedDate = courseCreatedDate;
      return this;
    }

    public Builder withProperties(Map<String, String> courseProperties)
    {
      this.courseProperties = courseProperties;
      return this;
    }

    public Builder withReportData(Map<String, Object> reportData)
    {
      this.reportData = reportData;
      return this;
    }

    public Builder withEnrolledLearners(Set<String> enrolledLearners)
    {
      this.enrolledLearners = enrolledLearners;
      return this;
    }

    public Builder hasQuiz(boolean hasQuiz)
    {
      this.hasQuiz = hasQuiz;
      return this;
    }

    public CourseReportDto build()
    {
      CourseReportDto courseReport = new CourseReportDto();

      courseReport.courseId = this.courseId;
      courseReport.courseName = this.courseName;
      courseReport.authorId = this.authorId;
      courseReport.courseCreatedDate = this.courseCreatedDate;
      courseReport.courseProperties = this.courseProperties;
      courseReport.reportData = this.reportData;
      courseReport.enrolledLearners = this.enrolledLearners;
      courseReport.hasQuiz = this.hasQuiz;

      return courseReport;
    }
  }
}
