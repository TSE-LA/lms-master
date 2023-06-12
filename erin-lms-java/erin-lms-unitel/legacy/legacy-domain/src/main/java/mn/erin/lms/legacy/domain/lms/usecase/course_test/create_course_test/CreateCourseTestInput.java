/*
 * (C)opyright, 2019, ERIN SYSTEMS LLC
 * The source code is protected by copyright laws and international copyright
 * treaties, as well as other intellectual property laws and treaties.
 * All rights reserved.
 */

package mn.erin.lms.legacy.domain.lms.usecase.course_test.create_course_test;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.Validate;

import mn.erin.lms.legacy.domain.lms.usecase.course_test.QuestionInfo;

/**
 * @author Oyungerel Chuluunsukh.
 */
public class CreateCourseTestInput
{
  private List<QuestionInfo> questions = new ArrayList<>();
  private String name;
  private boolean graded;
  private Long timeLimit;
  private Date dueDate;
  private Integer maxAttempts;
  private Double thresholdScore;

  public CreateCourseTestInput(String name, boolean graded, Long timeLimit, Date dueDate)
  {
    this.name = Validate.notBlank(name, "Name cannot be blank!");
    this.graded = graded;
    this.timeLimit = timeLimit;
    this.dueDate = dueDate;
  }

  public List<QuestionInfo> getQuestions()
  {
    return questions;
  }

  public void addQuestions(QuestionInfo question)
  {
    this.questions.add(question);
  }

  public String getName()
  {
    return name;
  }

  public void setName(String name)
  {
    this.name = name;
  }

  public boolean isGraded()
  {
    return graded;
  }

  public void setGraded(boolean graded)
  {
    this.graded = graded;
  }

  public Long getTimeLimit()
  {
    return timeLimit;
  }

  public void setTimeLimit(Long timeLimit)
  {
    this.timeLimit = timeLimit;
  }

  public Date getDueDate()
  {
    return dueDate;
  }

  public void setDueDate(Date dueDate)
  {
    this.dueDate = dueDate;
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
