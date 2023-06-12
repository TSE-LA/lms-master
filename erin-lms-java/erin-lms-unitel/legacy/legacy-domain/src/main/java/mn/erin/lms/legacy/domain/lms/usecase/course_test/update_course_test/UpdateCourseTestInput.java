/*
 * (C)opyright, 2019, ERIN SYSTEMS LLC
 * The source code is protected by copyright laws and international copyright
 * treaties, as well as other intellectual property laws and treaties.
 * All rights reserved.
 */

package mn.erin.lms.legacy.domain.lms.usecase.course_test.update_course_test;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.Validate;

import mn.erin.lms.legacy.domain.lms.usecase.course_test.QuestionInfo;

/**
 * @author Oyungerel Chuluunsukh.
 */
public class UpdateCourseTestInput
{
  private String courseTestId;
  private List<QuestionInfo> questionInfos = new ArrayList<>();
  private Integer maxAttempts;
  private Double thresholdScore;

  public UpdateCourseTestInput(String courseTestId)
  {
    this.courseTestId = Validate.notBlank(courseTestId, "Course test id cannot be blank!");
  }

  public String getCourseTestId()
  {
    return courseTestId;
  }

  public void setCourseTestId(String courseTestId)
  {
    this.courseTestId = courseTestId;
  }

  public List<QuestionInfo> getQuestionInfos()
  {
    return questionInfos;
  }

  public void addQuestionInfo(QuestionInfo questionInfo)
  {
    this.questionInfos.add(questionInfo);
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
