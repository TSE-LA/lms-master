/*
 * (C)opyright, 2019, ERIN SYSTEMS LLC
 * The source code is protected by copyright laws and international copyright
 * treaties, as well as other intellectual property laws and treaties.
 * All rights reserved.
 */

package mn.erin.lms.legacy.domain.scorm.usecase.scorm_questionnaire;

import java.util.List;

import org.apache.commons.lang3.Validate;

import mn.erin.lms.legacy.domain.scorm.usecase.scorm_test.SCORMQuestionInfo;

/**
 * @author Oyungerel Chuluunsukh.
 */
public class CreateSCORMQuestionnaireInput
{
  private List<SCORMQuestionInfo> questions;
  private String name;

  public CreateSCORMQuestionnaireInput(List<SCORMQuestionInfo> questions, String name){
    this.questions = Validate.notEmpty(questions, "Questions cannot be null or empty!");
    this.name = Validate.notBlank(name, "Name cannot be null or empty!");
  }

  public List<SCORMQuestionInfo> getQuestions()
  {
    return questions;
  }

  public String getName()
  {
    return name;
  }
}
