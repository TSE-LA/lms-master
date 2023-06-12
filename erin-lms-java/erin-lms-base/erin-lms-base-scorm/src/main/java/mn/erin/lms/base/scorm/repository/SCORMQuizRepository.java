/*
 * (C)opyright, 2019, ERIN SYSTEMS LLC
 * The source code is protected by copyright laws and international copyright
 * treaties, as well as other intellectual property laws and treaties.
 * All rights reserved.
 */

package mn.erin.lms.base.scorm.repository;

import java.util.List;

import mn.erin.lms.base.scorm.model.SCORMQuiz;
import mn.erin.lms.base.scorm.model.SCORMWrapper;

/**
 * @author Bat-Erdene Tsogoo.
 */
public interface SCORMQuizRepository
{
  SCORMQuiz createSCORMQuiz(String rootFolderId, String name, List<SCORMQuiz.Question> questions, SCORMWrapper quizWrapper, String docName)
      throws SCORMRepositoryException;

  SCORMQuiz createSCORMQuiz(String rootFolderId, String name, List<SCORMQuiz.Question> questions, SCORMWrapper quizWrapper, String docName,
      Integer maxAttempts, Double thresholdScore) throws SCORMRepositoryException;
}
