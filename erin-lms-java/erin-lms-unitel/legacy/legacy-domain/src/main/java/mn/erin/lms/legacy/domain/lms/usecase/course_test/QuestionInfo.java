/*
 * (C)opyright, 2019, ERIN SYSTEMS LLC
 * The source code is protected by copyright laws and international copyright
 * treaties, as well as other intellectual property laws and treaties.
 * All rights reserved.
 */

package mn.erin.lms.legacy.domain.lms.usecase.course_test;


import java.util.ArrayList;
import java.util.List;

/**
 * @author Oyungerel Chuluunsukh.
 */
public class QuestionInfo
{
  private String title;
  private String type;
  private List<AnswerInfo> answers = new ArrayList<>();

  public QuestionInfo(String title, String type)
  {
    this.title = title;
    this.type = type;
  }

  public String getTitle()
  {
    return title;
  }

  public void setTitle(String title)
  {
    this.title = title;
  }

  public List<AnswerInfo> getAnswers()
  {
    return answers;
  }

  public void addAnswers(AnswerInfo answer)
  {
    this.answers.add(answer);
  }

  public String getType()
  {
    return type;
  }

  public void setType(String type)
  {
    this.type = type;
  }
}
