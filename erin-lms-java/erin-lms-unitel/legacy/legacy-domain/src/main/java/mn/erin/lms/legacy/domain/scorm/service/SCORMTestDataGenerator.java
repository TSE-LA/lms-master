/*
 * (C)opyright, 2019, ERIN SYSTEMS LLC
 * The source code is protected by copyright laws and international copyright
 * treaties, as well as other intellectual property laws and treaties.
 * All rights reserved.
 */

package mn.erin.lms.legacy.domain.scorm.service;

import java.util.List;

import mn.erin.lms.legacy.domain.scorm.model.SCORMTest;

/**
 * @author Bat-Erdene Tsogoo.
 */
public interface SCORMTestDataGenerator
{
  byte[] generateTestDataJsonFile(List<SCORMTest.Question> questions);

  byte[] generateTestDataJsonFile(List<SCORMTest.Question> questions, Integer maxAttempts, Double thresholdScore);
}
