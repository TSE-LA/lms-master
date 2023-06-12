/*
 * (C)opyright, 2019, ERIN SYSTEMS LLC
 * The source code is protected by copyright laws and international copyright
 * treaties, as well as other intellectual property laws and treaties.
 * All rights reserved.
 */

package mn.erin.lms.legacy.domain.scorm.usecase.scorm_test;

import org.apache.commons.lang3.Validate;

/**
 * @author Bat-Erdene Tsogoo.
 */
public class AnswerInfo
{
  private String value;
  private boolean correct;
  private double score;

  public AnswerInfo(String value, boolean correct, double score)
  {
    this.value = Validate.notBlank(value, "Answer info value cannot be blank!");
    this.correct = correct;
    this.score = score;
  }

  public String getValue()
  {
    return value;
  }

  public boolean isCorrect()
  {
    return correct;
  }

  public double getScore()
  {
    return score;
  }
}
