package mn.erin.lms.unitel.domain.usecase.dto;

/**
 * @author Munkh
 */
public class ClassroomActivityReportDto
{
  private String id;
  private String name;
  private String courseType;
  private boolean present;
  private int testScore;
  private String certificate;
  private String teacher;
  private String startTime;
  private String endTime;
  private String date;

  private ClassroomActivityReportDto()
  {
  }

  public String getId()
  {
    return id;
  }

  public String getName()
  {
    return name;
  }

  public String getCourseType()
  {
    return courseType;
  }

  public boolean isPresent()
  {
    return present;
  }

  public int getTestScore()
  {
    return testScore;
  }

  public String getCertificate()
  {
    return certificate;
  }

  public String getTeacher()
  {
    return teacher;
  }

  public String getStartTime()
  {
    return startTime;
  }

  public String getEndTime()
  {
    return endTime;
  }

  public String getDate()
  {
    return date;
  }

  public static class Builder
  {
    private final String id;
    private String name;
    private String courseType;
    private boolean present;
    private int testScore;
    private String certificate;
    private String teacher;
    private String startTime;
    private String endTime;
    private String date;

    public Builder(String id)
    {
      this.id = id;
    }

    public Builder withName(String name)
    {
      this.name = name;
      return this;
    }

    public Builder withCourseType(String courseType)
    {
      this.courseType = courseType;
      return this;
    }

    public Builder isPresent(boolean present)
    {
      this.present = present;
      return this;
    }

    public Builder withTestScore(int testScore)
    {
      this.testScore = testScore;
      return this;
    }

    public Builder withCertificate(String certificate)
    {
      this.certificate = certificate;
      return this;
    }

    public Builder withTeacher(String teacher)
    {
      this.teacher = teacher;
      return this;
    }

    public Builder withStartTime(String startTime)
    {
      this.startTime = startTime;
      return this;
    }

    public Builder withEndTime(String endTime)
    {
      this.endTime = endTime;
      return this;
    }

    public Builder withDate(String date)
    {
      this.date = date;
      return this;
    }

    public ClassroomActivityReportDto build()
    {
      ClassroomActivityReportDto output = new ClassroomActivityReportDto();
      output.id = this.id;
      output.name = this.name;
      output.courseType = this.courseType;
      output.present = this.present;
      output.testScore = this.testScore;
      output.certificate = this.certificate;
      output.teacher = this.teacher;
      output.startTime = this.startTime;
      output.endTime = this.endTime;
      output.date = this.date;

      return output;
    }
  }
}
