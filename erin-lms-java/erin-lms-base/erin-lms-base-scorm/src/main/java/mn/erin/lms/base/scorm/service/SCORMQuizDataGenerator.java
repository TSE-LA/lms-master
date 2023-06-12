/*
 * (C)opyright, 2019, ERIN SYSTEMS LLC
 * The source code is protected by copyright laws and international copyright
 * treaties, as well as other intellectual property laws and treaties.
 * All rights reserved.
 */

package mn.erin.lms.base.scorm.service;

import java.util.List;

import mn.erin.lms.base.scorm.model.SCORMQuiz;

/**
 * @author Bat-Erdene Tsogoo.
 */
public interface SCORMQuizDataGenerator
{
  byte[] generateQuizDataJsonFile(List<SCORMQuiz.Question> questions);

  byte[] generateQuizDataJsonFile(List<SCORMQuiz.Question> questions, Integer maxAttempts, Double thresholdScore);
}
