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
public class RestCourseTest
{
  private String question;
  private List<RestAnswer> answers;

  public RestCourseTest(){}

  public RestCourseTest(String question, List<RestAnswer> answers)
  {
    this.question = question;
    this.answers = answers;
  }

  public String getQuestion()
  {
    return question;
  }

  public void setQuestion(String question)
  {
    this.question = question;
  }

  public List<RestAnswer> getAnswers()
  {
    return answers;
  }

  public void setAnswers(List<RestAnswer> answers)
  {
    this.answers = answers;
  }
}
