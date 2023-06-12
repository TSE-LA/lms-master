/*
 * (C)opyright, 2019, ERIN SYSTEMS LLC
 * The source code is protected by copyright laws and international copyright
 * treaties, as well as other intellectual property laws and treaties.
 * All rights reserved.
 */

package mn.erin.lms.legacy.infrastructure.scorm.rest;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import mn.erin.lms.legacy.domain.scorm.repository.RuntimeDataRepository;
import mn.erin.lms.legacy.domain.scorm.repository.SCORMContentRepository;
import mn.erin.lms.legacy.domain.scorm.repository.SCORMTestRepository;
import mn.erin.lms.legacy.domain.scorm.repository.SCORMWrapperRepository;
import mn.erin.lms.legacy.domain.scorm.usecase.runtime.ClearLearnerRuntimeData;
import mn.erin.lms.legacy.domain.scorm.usecase.scorm.CreateSCORMContent;
import mn.erin.lms.legacy.domain.scorm.usecase.scorm.DeleteSCORMContent;
import mn.erin.lms.legacy.domain.scorm.usecase.scorm_questionnaire.CreateSCORMQuestionnaire;
import mn.erin.lms.legacy.domain.scorm.usecase.scorm_test.CreateSCORMTest;
import mn.erin.lms.legacy.infrastructure.scorm.repository.dms.AlfrescoSCORMBeanConfig;
import mn.erin.lms.legacy.infrastructure.scorm.repository.mongo.MongoSCORMBeanConfig;

/**
 * @author Bat-Erdene Tsogoo.
 */
@Configuration
@Import({ AlfrescoSCORMBeanConfig.class, MongoSCORMBeanConfig.class })
public class SCORMUseCaseBeanConfig
{
  @Bean
  public CreateSCORMContent createSCORMContent(SCORMContentRepository legacySCORMContentRepo,
      SCORMWrapperRepository legacySCORMWrapperRepo)
  {
    return new CreateSCORMContent(legacySCORMContentRepo, legacySCORMWrapperRepo);
  }

  @Bean
  public DeleteSCORMContent deleteSCORMContent(SCORMContentRepository legacySCORMContentRepo,
      RuntimeDataRepository legacyRuntimeDataRepo)
  {
    return new DeleteSCORMContent(legacySCORMContentRepo, legacyRuntimeDataRepo);
  }

  @Bean
  public ClearLearnerRuntimeData clearLearnerRuntimeData(
      RuntimeDataRepository legacyRuntimeDataRepo)
  {
    return new ClearLearnerRuntimeData(legacyRuntimeDataRepo);
  }

  @Bean
  public CreateSCORMTest createSCORMTest(SCORMTestRepository legacySCORMTestRepo,
      SCORMWrapperRepository legacySCORMWrapperRepo)
  {
    return new CreateSCORMTest(legacySCORMTestRepo, legacySCORMWrapperRepo);
  }
  @Bean
  public CreateSCORMQuestionnaire createSCORMQuestionnaire(SCORMTestRepository legacySCORMTestRepo,
      SCORMWrapperRepository legacySCORMWrapperRepo)
  {
    return new CreateSCORMQuestionnaire(legacySCORMTestRepo, legacySCORMWrapperRepo);
  }
}
