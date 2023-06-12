/*
 * (C)opyright, 2019, ERIN SYSTEMS LLC
 * The source code is protected by copyright laws and international copyright
 * treaties, as well as other intellectual property laws and treaties.
 * All rights reserved.
 */

package mn.erin.lms.legacy.infrastructure.lms.rest.course_assessment.rest_models;

import java.util.List;

/**
 * @author Oyungerel Chuluunsukh.
 */
public class RestCourseAssessment
{
  private String courseId;
  private String testId;
  private List<RestCourseTest> courseTests;
  private Integer maxAttempts;
  private Double thresholdScore;

  public List<RestCourseTest> getCourseTests()
  {
    return courseTests;
  }

  public void setCourseTests(List<RestCourseTest> courseTests)
  {
    this.courseTests = courseTests;
  }

  public String getCourseId()
  {
    return courseId;
  }

  public void setCourseId(String courseId)
  {
    this.courseId = courseId;
  }

  public String getTestId()
  {
    return testId;
  }

  public void setTestId(String testId)
  {
    this.testId = testId;
  }

  public Integer getMaxAttempts()
  {
    return maxAttempts;
  }

  public void setMaxAttempts(Integer maxAttempts)
  {
    this.maxAttempts = maxAttempts;
  }

  public Double getThresholdScore()
  {
    return thresholdScore;
  }

  public void setThresholdScore(Double thresholdScore)
  {
    this.thresholdScore = thresholdScore;
  }
}

