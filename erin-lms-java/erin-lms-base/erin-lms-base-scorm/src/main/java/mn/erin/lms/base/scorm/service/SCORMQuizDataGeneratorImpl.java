/*
 * (C)opyright, 2019, ERIN SYSTEMS LLC
 * The source code is protected by copyright laws and international copyright
 * treaties, as well as other intellectual property laws and treaties.
 * All rights reserved.
 */

package mn.erin.lms.base.scorm.service;

import java.nio.charset.StandardCharsets;
import java.util.List;

import org.apache.commons.lang3.Validate;
import org.json.JSONArray;
import org.json.JSONObject;

import mn.erin.lms.base.scorm.model.SCORMQuiz;

/**
 * @author Bat-Erdene Tsogoo.
 */
public class SCORMQuizDataGeneratorImpl implements SCORMQuizDataGenerator
{
  @Override
  public byte[] generateQuizDataJsonFile(List<SCORMQuiz.Question> questions)
  {
    Validate.notEmpty(questions, "Questions cannot be null or empty!");

    JSONObject data = getJsonDataObject(questions);
    return data.toString().getBytes(StandardCharsets.UTF_8);
  }

  @Override
  public byte[] generateQuizDataJsonFile(List<SCORMQuiz.Question> questions, Integer maxAttempts, Double thresholdScore)
  {
    Validate.notEmpty(questions, "Questions cannot be null or empty!");
    Validate.notNull(maxAttempts, "Max attempts cannot be null");
    Validate.notNull(thresholdScore, "Threshold score cannot be null!");

    JSONObject data = getJsonDataObject(questions);
    data.put("maxAttempts", maxAttempts);
    data.put("thresholdScore", thresholdScore);

    return data.toString().getBytes(StandardCharsets.UTF_8);
  }

  private JSONObject getJsonDataObject(List<SCORMQuiz.Question> questions)
  {
    JSONArray data = new JSONArray();

    for (SCORMQuiz.Question question : questions)
    {
      JSONObject datum = new JSONObject();
      datum.put("question", question.getTitle());
      datum.put("questionType", question.getType());
      datum.put("isRequired", question.isRequired());

      JSONArray answers = new JSONArray();
      question.getAnswers().forEach(answer -> {
        JSONObject answerData = new JSONObject();
        answerData.put("value", answer.getValue());
        answerData.put("isCorrect", answer.isCorrect());
        answerData.put("score", answer.getScore());
        answers.put(answerData);
      });
      datum.put("answers", answers);

      data.put(datum);
    }

    JSONObject object = new JSONObject();
    object.put("data", data);
    return object;
  }
}
