/*
 * (C)opyright, 2019, ERIN SYSTEMS LLC
 * The source code is protected by copyright laws and international copyright
 * treaties, as well as other intellectual property laws and treaties.
 * All rights reserved.
 */

package mn.erin.lms.base.scorm.model;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.Validate;

/**
 * @author Bat-Erdene Tsogoo.
 */
public class SCORMQuiz
{
  private final String name;
  private final List<Question> questions;

  private List<AssetId> quizAssets = new ArrayList<>();

  public SCORMQuiz(String name, List<Question> questions)
  {
    this.name = Validate.notBlank(name, "quiz name cannot be null or blank!");
    this.questions = Validate.notEmpty(questions, "quiz questions cannot be null or empty!");
  }

  public String getName()
  {
    return name;
  }

  public List<Question> getQuestions()
  {
    return questions;
  }

  public List<AssetId> getQuizAssets()
  {
    return quizAssets;
  }

  public void addAsset(AssetId assetId)
  {
    if (assetId != null && !this.quizAssets.contains(assetId))
    {
      this.quizAssets.add(assetId);
    }
  }

  public static class Question
  {
    private String title;
    private String type;
    private boolean isRequired;
    private List<Answer> answers = new ArrayList<>();

    public Question(String title)
    {
      this.title = Validate.notBlank(title, "Question title cannot be null or blank!");
    }

    public Question(String title, String type, boolean isRequired)
    {
      this.title = title;
      this.type = type;
      this.isRequired = isRequired;
    }

    public void addAnswer(Answer answer)
    {
      if (answer != null)
      {
        this.answers.add(answer);
      }
    }

    public String getType()
    {
      return type;
    }

    public boolean isRequired()
    {
      return isRequired;
    }

    public String getTitle()
    {
      return title;
    }

    public List<Answer> getAnswers()
    {
      return answers;
    }
  }

  public static class Answer
  {
    private final String value;
    private double score = 0;
    private boolean correct = false;

    public Answer(String value){
      this(value, false, 0);
    }
    public Answer(String value, boolean correct, double score)
    {
      this.value = value;
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
}
