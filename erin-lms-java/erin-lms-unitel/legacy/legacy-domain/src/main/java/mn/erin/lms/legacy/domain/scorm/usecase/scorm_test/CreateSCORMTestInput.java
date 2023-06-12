/*
 * (C)opyright, 2019, ERIN SYSTEMS LLC
 * The source code is protected by copyright laws and international copyright
 * treaties, as well as other intellectual property laws and treaties.
 * All rights reserved.
 */

package mn.erin.lms.legacy.domain.scorm.usecase.scorm_test;

import java.util.List;

import org.apache.commons.lang3.Validate;

/**
 * @author Bat-Erdene Tsogoo.
 */
public class CreateSCORMTestInput
{
  private String name;
  private List<SCORMQuestionInfo> questions;
  private Integer maxAttemps;
  private Double thresholdScore;

  public CreateSCORMTestInput(String name, List<SCORMQuestionInfo> questions)
  {
    this.name = Validate.notBlank(name, "Test name cannot be null or blank!");
    this.questions = Validate.notEmpty(questions, "Questions cannot be null or empty!");
  }

  public String getName()
  {
    return name;
  }

  public List<SCORMQuestionInfo> getQuestions()
  {
    return questions;
  }

  public Integer getMaxAttemps()
  {
    return maxAttemps;
  }

  public void setMaxAttemps(Integer maxAttemps)
  {
    this.maxAttemps = maxAttemps;
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
