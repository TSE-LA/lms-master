/*
 * (C)opyright, 2019, ERIN SYSTEMS LLC
 * The source code is protected by copyright laws and international copyright
 * treaties, as well as other intellectual property laws and treaties.
 * All rights reserved.
 */

package mn.erin.lms.legacy.domain.lms.model.assessment;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import org.apache.commons.lang3.Validate;

import mn.erin.domain.base.model.Entity;

/**
 * @author Oyungerel Chuluunsukh.
 */
public class Test implements Entity<Test>
{
  private final TestId id;
  private final String name;
  private boolean graded;
  private final List<Question> questions = new ArrayList<>();
  private Long timeLimit;
  private Date dueDate;
  private Integer maxAttempts;
  private Double thresholdScore;

  public Test(TestId id, String name)
  {
    this(id, name, false, 0L, null);
  }

  public Test(TestId id, String name, boolean graded, Long timeLimit, Date dueDate)
  {
    this.id = Objects.requireNonNull(id, "Course test cannot be null!");
    this.name = Validate.notBlank(name, "Course test name cannot be blank!");
    this.graded = graded;
    this.timeLimit = timeLimit;
    this.dueDate = dueDate;
  }

  public Date getDueDate()
  {
    return dueDate;
  }

  public Long getTimeLimit()
  {
    return timeLimit;
  }

  public boolean isGraded()
  {
    return graded;
  }

  public void setGraded(boolean graded)
  {
    this.graded = graded;
  }

  public List<Question> getQuestions()
  {
    return questions;
  }

  public void addQuestion(Question question)
  {
    this.questions.add(question);
  }

  public String getName()
  {
    return name;
  }

  public TestId getId()
  {
    return id;
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
    if(thresholdScore != null)
    {
      double overallScore = 0;
      for (Question question : questions)
      {
        List<Answer> answers = question.getAnswers();

        for (Answer answer : answers)
        {
          if (answer.isCorrect())
          {
            overallScore += answer.getScore();
          }
        }
      }

      if (thresholdScore > overallScore)
      {
        throw new IllegalArgumentException("Threshold score cannot be greater than the overall score of the test");
      }

      this.thresholdScore = thresholdScore;
    }
  }

  @Override
  public boolean sameIdentityAs(Test other)
  {
    return other != null && (this.id.equals(other.id));
  }
}
