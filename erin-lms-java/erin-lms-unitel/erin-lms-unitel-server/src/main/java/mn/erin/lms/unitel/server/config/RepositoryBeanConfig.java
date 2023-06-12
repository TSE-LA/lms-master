package mn.erin.lms.unitel.server.config;

import javax.inject.Inject;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import mn.erin.alfresco.connector.config.AlfrescoConnectorBeanConfig;
import mn.erin.domain.dms.repository.DocumentRepository;
import mn.erin.domain.dms.repository.FolderRepository;
import mn.erin.lms.base.dms.DmsSCORMContentRepository;
import mn.erin.lms.base.dms.DmsSCORMQuizRepository;
import mn.erin.lms.base.dms.DmsSCORMWrapperRepository;
import mn.erin.lms.base.domain.repository.LmsRepositoryRegistry;
import mn.erin.lms.base.domain.repository.task.ScheduledTaskRepository;
import mn.erin.lms.base.mongo.implementation.ScheduledTaskRepositoryImpl;
import mn.erin.lms.base.mongo.repository.MongoScheduledTaskRepository;
import mn.erin.lms.base.scorm.repository.SCORMContentRepository;
import mn.erin.lms.base.scorm.repository.SCORMQuizRepository;
import mn.erin.lms.base.scorm.repository.SCORMRepositoryRegistry;
import mn.erin.lms.base.scorm.repository.SCORMWrapperRepository;
import mn.erin.lms.base.scorm.service.ManifestJSONParserImpl;
import mn.erin.lms.base.scorm.service.SCORMQuizDataGeneratorImpl;
import mn.erin.lms.base.scorm.service.StAXManifestXMLParser;
import mn.erin.lms.unitel.mongo.config.UnitelLmsMongoBeanConfig;
import mn.erin.lms.unitel.server.registry.UnitelLmsRepositoryRegistryImpl;
import mn.erin.lms.unitel.server.registry.UnitelSCORMRepositoryRegistry;

/**
 * @author Bat-Erdene Tsogoo.
 */
@Configuration
@Import({ AlfrescoConnectorBeanConfig.class, UnitelLmsMongoBeanConfig.class })
public class RepositoryBeanConfig
{
  private FolderRepository folderRepository;
  private DocumentRepository documentRepository;

  @Inject
  public void setFolderRepository(FolderRepository folderRepository)
  {
    this.folderRepository = folderRepository;
  }

  @Inject
  public void setDocumentRepository(DocumentRepository documentRepository)
  {
    this.documentRepository = documentRepository;
  }

  @Bean
  public LmsRepositoryRegistry repositoryRegistry()
  {
    return new UnitelLmsRepositoryRegistryImpl();
  }

  @Bean
  public SCORMRepositoryRegistry scormRepositoryRegistry()
  {
    return new UnitelSCORMRepositoryRegistry();
  }

  @Bean
  public SCORMContentRepository scormContentRepository()
  {
    return new DmsSCORMContentRepository(documentRepository, folderRepository, new StAXManifestXMLParser(), new ManifestJSONParserImpl());
  }

  @Bean
  public SCORMQuizRepository scormQuizRepository()
  {
    return new DmsSCORMQuizRepository(documentRepository, folderRepository, new SCORMQuizDataGeneratorImpl());
  }

  @Bean
  public SCORMWrapperRepository scormWrapperRepository()
  {
    return new DmsSCORMWrapperRepository(documentRepository, folderRepository);
  }

  @Bean
  public ScheduledTaskRepository scheduledTaskRepository(MongoScheduledTaskRepository mongoRepo)
  {
    return new ScheduledTaskRepositoryImpl(mongoRepo);
  }
}
