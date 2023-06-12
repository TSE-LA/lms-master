/*
 * (C)opyright, 2019, ERIN SYSTEMS LLC
 * The source code is protected by copyright laws and international copyright
 * treaties, as well as other intellectual property laws and treaties.
 * All rights reserved.
 */

package mn.erin.lms.legacy.domain.lms.usecase.report;

import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.Validate;

/**
 * @author Bat-Erdene Tsogoo.
 */
public class CourseReportResult
{
  private String courseId;
  private String courseName;
  private String authorName;
  private String categoryName;
  private Date courseCreatedDate;
  private Map<String, Object> courseProperties;
  private Map<String, Object> reportData;
  private List<String> enrolledLearners;

  public String getCourseName()
  {
    return courseName;
  }

  public String getAuthorName()
  {
    return authorName;
  }

  public String getCategoryName()
  {
    return categoryName;
  }

  public Map<String, Object> getCourseProperties()
  {
    return Collections.unmodifiableMap(courseProperties);
  }

  public Map<String, Object> getReportData()
  {
    return Collections.unmodifiableMap(reportData);
  }

  public List<String> getEnrolledLearners()
  {
    return enrolledLearners;
  }

  public String getCourseId()
  {
    return courseId;
  }

  public Date getCourseCreatedDate()
  {
    return courseCreatedDate;
  }

  private CourseReportResult()
  {
  }

  public static class Builder
  {
    private String courseId;
    private String courseName;
    private String authorName;
    private String categoryName;
    private Date courseCreatedDate;
    private Map<String, Object> courseProperties;
    private Map<String, Object> reportData;
    private List<String> enrolledLearners;

    public Builder(String courseName)
    {
      this.courseName = Validate.notBlank(courseName, "Course name cannot be null or blank!");
    }

    public Builder withId(String courseId)
    {
      this.courseId = Validate.notBlank(courseId, "Course ID cannot be null or blank!");
      return this;
    }

    public Builder withAuthor(String authorName)
    {
      this.authorName = Validate.notBlank(authorName, "Author name cannot be null or blank!");
      return this;
    }

    public Builder withCategory(String categoryName)
    {
      this.categoryName = Validate.notBlank(categoryName, "Category name cannot be null or blank!");
      return this;
    }

    public Builder createdAt(Date courseCreatedDate)
    {
      this.courseCreatedDate = courseCreatedDate;
      return this;
    }

    public Builder havingCourseProperties(Map<String, Object> courseProperties)
    {
      this.courseProperties = Validate.notNull(courseProperties, "Course properties cannot be null!");
      return this;
    }

    public Builder havingReportData(Map<String, Object> reportData)
    {
      this.reportData = Validate.notNull(reportData, "Report data cannot be null!");
      return this;
    }

    public Builder withEnrolledLearners(List<String> enrolledLearners)
    {
      this.enrolledLearners = Validate.notNull(enrolledLearners, "Enrolled learners cannot be null!");
      return this;
    }

    public CourseReportResult build()
    {
      CourseReportResult result = new CourseReportResult();
      result.courseName = this.courseName;
      result.authorName = this.authorName;
      result.categoryName = this.categoryName;
      result.courseProperties = this.courseProperties;
      result.reportData = this.reportData;
      result.enrolledLearners = this.enrolledLearners;
      result.courseCreatedDate = this.courseCreatedDate;
      result.courseId = this.courseId;
      return result;
    }
  }
}
