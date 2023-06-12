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
import mn.erin.lms.legacy.domain.scorm.repository.SCORMWrapperRepository;
import mn.erin.lms.legacy.infrastructure.scorm.repository.dms.AlfrescoSCORMBeanConfig;
import mn.erin.lms.legacy.infrastructure.scorm.repository.mongo.MongoSCORMBeanConfig;

/**
 * @author Bat-Erdene Tsogoo.
 */
@Configuration
@Import({ AlfrescoSCORMBeanConfig.class, MongoSCORMBeanConfig.class })
public class SCORMRestBeanConfig
{
  @Bean(name = "legacySCORMContentRestApi")
  public SCORMContentRestApi scormContentRestApi(SCORMContentRepository legacySCORMContentRepo,
      SCORMWrapperRepository legacySCORMWrapperRepo, RuntimeDataRepository legacyRuntimeDataRepo)
  {
    return new SCORMContentRestApi(legacySCORMContentRepo, legacySCORMWrapperRepo, legacyRuntimeDataRepo);
  }
}
