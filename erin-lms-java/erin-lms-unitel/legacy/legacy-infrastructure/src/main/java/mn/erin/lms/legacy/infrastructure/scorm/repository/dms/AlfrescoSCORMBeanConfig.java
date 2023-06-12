/*
 * (C)opyright, 2019, ERIN SYSTEMS LLC
 * The source code is protected by copyright laws and international copyright
 * treaties, as well as other intellectual property laws and treaties.
 * All rights reserved.
 */

package mn.erin.lms.legacy.infrastructure.scorm.repository.dms;

import javax.inject.Inject;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import mn.erin.alfresco.connector.config.AlfrescoConnectorBeanConfig;
import mn.erin.domain.dms.repository.DocumentRepository;
import mn.erin.domain.dms.repository.FolderRepository;
import mn.erin.lms.legacy.domain.scorm.repository.SCORMContentRepository;
import mn.erin.lms.legacy.domain.scorm.repository.SCORMTestRepository;
import mn.erin.lms.legacy.domain.scorm.repository.SCORMWrapperRepository;
import mn.erin.lms.legacy.domain.scorm.service.ManifestJSONParser;
import mn.erin.lms.legacy.domain.scorm.service.ManifestXMLParser;
import mn.erin.lms.legacy.domain.scorm.service.SCORMTestDataGenerator;
import mn.erin.lms.legacy.infrastructure.scorm.base.service.ManifestJSONParserImpl;
import mn.erin.lms.legacy.infrastructure.scorm.base.service.SCORMTestDataGeneratorImpl;
import mn.erin.lms.legacy.infrastructure.scorm.base.service.StAXManifestXMLParser;

/**
 * @author Bat-Erdene Tsogoo.
 */
@Configuration
@Import({ AlfrescoConnectorBeanConfig.class })
public class AlfrescoSCORMBeanConfig
{
  @Inject
  private FolderRepository folderRepository;

  @Inject
  private DocumentRepository documentRepository;

  @Bean(name = "legacyManifestXMLParser")
  public ManifestXMLParser manifestXMLParser()
  {
    return new StAXManifestXMLParser();
  }

  @Bean(name = "legacyManifestJSONParser")
  public ManifestJSONParser manifestJSONParser()
  {
    return new ManifestJSONParserImpl();
  }

  @Bean(name = "legacySCORMTestDataGenerator")
  public SCORMTestDataGenerator scormTestDataGenerator()
  {
    return new SCORMTestDataGeneratorImpl();
  }

  @Bean(name = "legacySCORMContentRepo")
  public SCORMContentRepository scormContentRepository()
  {
    return new DmsSCORMContentRepository(documentRepository, folderRepository);
  }

  @Bean(name = "legacySCORMWrapperRepo")
  public SCORMWrapperRepository scormWrapperRepository()
  {
    return new DmsSCORMWrapperRepository(documentRepository, folderRepository);
  }

  @Bean(name = "legacySCORMTestRepo")
  public SCORMTestRepository scormTestRepository()
  {
    return new DmsSCORMTestRepository(documentRepository, folderRepository);
  }
}
