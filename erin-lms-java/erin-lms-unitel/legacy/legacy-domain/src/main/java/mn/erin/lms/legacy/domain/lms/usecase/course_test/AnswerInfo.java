/*
 * (C)opyright, 2019, ERIN SYSTEMS LLC
 * The source code is protected by copyright laws and international copyright
 * treaties, as well as other intellectual property laws and treaties.
 * All rights reserved.
 */

package mn.erin.lms.legacy.domain.lms.usecase.course_test;

/**
 * @author Oyungerel Chuluunsukh.
 */
public class AnswerInfo
{
  private String value;
  private boolean correct;
  private Double score;

  public AnswerInfo(String value, boolean correct, Double score)
  {
    this.value = value;
    this.correct = correct;
    this.score = score;
  }

  public String getValue()
  {
    return value;
  }

  public void setValue(String value)
  {
    this.value = value;
  }

  public boolean isCorrect()
  {
    return correct;
  }

  public void setCorrect(boolean correct)
  {
    this.correct = correct;
  }

  public Double getScore()
  {
    return score;
  }

  public void setScore(Double score)
  {
    this.score = score;
  }
}
