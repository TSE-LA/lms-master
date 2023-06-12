/*
 * (C)opyright, 2019, ERIN SYSTEMS LLC
 * The source code is protected by copyright laws and international copyright
 * treaties, as well as other intellectual property laws and treaties.
 * All rights reserved.
 */

package mn.erin.lms.legacy.domain.scorm.repository;

import java.util.List;

import mn.erin.lms.legacy.domain.scorm.model.SCORMTest;
import mn.erin.lms.legacy.domain.scorm.model.SCORMWrapper;

/**
 * @author Bat-Erdene Tsogoo.
 */
public interface SCORMTestRepository
{
  SCORMTest createSCORMTest(String name, List<SCORMTest.Question> questions, SCORMWrapper testWrapper, String docName)
      throws SCORMRepositoryException;

  SCORMTest createSCORMTest(String name, List<SCORMTest.Question> questions, SCORMWrapper testWrapper, String docName,
      Integer maxAttempts, Double thresholdScore) throws SCORMRepositoryException;
}
