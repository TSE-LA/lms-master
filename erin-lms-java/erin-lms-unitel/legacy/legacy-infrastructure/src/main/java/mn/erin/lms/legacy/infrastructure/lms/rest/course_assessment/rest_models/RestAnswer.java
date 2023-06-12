/*
 * (C)opyright, 2019, ERIN SYSTEMS LLC
 * The source code is protected by copyright laws and international copyright
 * treaties, as well as other intellectual property laws and treaties.
 * All rights reserved.
 */

package mn.erin.lms.legacy.infrastructure.lms.rest.course_assessment.rest_models;

public class RestAnswer
{
  private String value;
  private boolean correct;

  public RestAnswer()
  {

  }

  public RestAnswer(String value, boolean correct)
  {
    this.value = value;
    this.correct = correct;
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
}
