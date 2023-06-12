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
public class SCORMQuestionInfo
{
  private final String title;
  private final List<AnswerInfo> answers;
  private final String type;

  public SCORMQuestionInfo(String title, List<AnswerInfo> answers, String type)
  {
    this.title = Validate.notBlank(title, "Question info title cannot be blank!");
    this.answers = Validate.notNull(answers, "Answers cannot be null or empty!");
    this.type = Validate.notEmpty(type, "Type cannot be null or empty!");
  }

  public String getType()
  {
    return type;
  }

  public String getTitle()
  {
    return title;
  }

  public List<AnswerInfo> getAnswers()
  {
    return answers;
  }

  public void addAnswers(AnswerInfo answer)
  {
    this.answers.add(answer);
  }
}
