/*
 * (C)opyright, 2019, ERIN SYSTEMS LLC
 * The source code is protected by copyright laws and international copyright
 * treaties, as well as other intellectual property laws and treaties.
 * All rights reserved.
 */

package mn.erin.lms.legacy.domain.lms.usecase.course_questionnaire.get_questionnaire;

import java.util.List;

import mn.erin.lms.legacy.domain.lms.usecase.course_test.QuestionInfo;

/**
 * @author Oyungerel Chuluunsukh.
 */
public class GetQuestionnaireOutput
{
  private String id;
  private String name;
  private List<QuestionInfo> questions;

  public GetQuestionnaireOutput(String id, List<QuestionInfo> questions, String name){
    this.id = id;
    this.questions = questions;
    this.name = name;
  }

  public List<QuestionInfo> getQuestions()
  {
    return questions;
  }

  public void setQuestions(List<QuestionInfo> questions)
  {
    this.questions = questions;
  }

  public String getId()
  {
    return id;
  }

  public String getName()
  {
    return name;
  }
}
