/*
 * (C)opyright, 2019, ERIN SYSTEMS LLC
 * The source code is protected by copyright laws and international copyright
 * treaties, as well as other intellectual property laws and treaties.
 * All rights reserved.
 */

package mn.erin.lms.legacy.infrastructure.scorm.repository.dms;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import javax.inject.Inject;

import org.apache.commons.lang3.Validate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import mn.erin.domain.dms.model.folder.Folder;
import mn.erin.domain.dms.repository.DocumentRepository;
import mn.erin.domain.dms.repository.FolderRepository;
import mn.erin.lms.legacy.domain.scorm.model.Asset;
import mn.erin.lms.legacy.domain.scorm.model.AssetId;
import mn.erin.lms.legacy.domain.scorm.model.ContentAggregation;
import mn.erin.lms.legacy.domain.scorm.model.SCORMTest;
import mn.erin.lms.legacy.domain.scorm.model.SCORMWrapper;
import mn.erin.lms.legacy.domain.scorm.repository.SCORMRepositoryException;
import mn.erin.lms.legacy.domain.scorm.repository.SCORMTestRepository;
import mn.erin.lms.legacy.domain.scorm.service.SCORMTestDataGenerator;

/**
 * @author Bat-Erdene Tsogoo.
 */
public class DmsSCORMTestRepository extends DmsSCORMRepository implements SCORMTestRepository
{
  private static final Logger LOGGER = LoggerFactory.getLogger(DmsSCORMTestRepository.class);

  private SCORMTestDataGenerator scormTestDataGenerator;

  DmsSCORMTestRepository(DocumentRepository documentRepository, FolderRepository folderRepository)
  {
    super(documentRepository, folderRepository);
  }

  @Inject
  public void setScormTestDataGenerator(SCORMTestDataGenerator legacySCORMTestDataGenerator)
  {
    this.scormTestDataGenerator = legacySCORMTestDataGenerator;
  }

  @Override
  public SCORMTest
  createSCORMTest(String name, List<SCORMTest.Question> questions, SCORMWrapper testWrapper, String docName) throws SCORMRepositoryException
  {
    validate(name, questions, testWrapper);
    Folder testFolder = createFolder(getBaseContentFolder().getFolderId(), name);
    byte[] testDataFile = scormTestDataGenerator.generateTestDataJsonFile(questions);
    return createSCORMTest(name, testFolder, testDataFile, testWrapper, questions, docName);
  }

  @Override
  public SCORMTest createSCORMTest(String name, List<SCORMTest.Question> questions, SCORMWrapper testWrapper, String docName, Integer maxAttempts,
      Double thresholdScore) throws SCORMRepositoryException
  {
    validate(name, questions, testWrapper);
    Folder testFolder = createFolder(getBaseContentFolder().getFolderId(), name);
    byte[] testDataFile = scormTestDataGenerator.generateTestDataJsonFile(questions, maxAttempts, thresholdScore);
    return createSCORMTest(name, testFolder, testDataFile, testWrapper, questions, docName);
  }

  private SCORMTest createSCORMTest(String name, Folder testFolder, byte[] testDataFile, SCORMWrapper testWrapper, List<SCORMTest.Question> questions,
      String docName) throws SCORMRepositoryException
  {
    createDocument(testFolder.getFolderId(), testDataFile, docName);
    Asset indexHTML = testWrapper.getRootEntity();

    copy(testFolder.getFolderId(), Collections.singleton(indexHTML.getAssetId()));

    Set<AssetId> wrapperDependencies = testWrapper.getDependencies().stream()
        .map(ContentAggregation.Resource::getAssetId)
        .collect(Collectors.toSet());

    copy(testFolder.getFolderId(), wrapperDependencies);

    SCORMTest scormTest = new SCORMTest(name, questions);

    documentRepository.listDocuments(testFolder.getFolderId())
        .forEach(document -> scormTest.addAsset(AssetId.valueOf(document.getId())));

    return scormTest;
  }

  private void validate(String name, List<SCORMTest.Question> questions, SCORMWrapper testWrapper)
  {
    Validate.notBlank(name, "Test name cannot be null or blank!");
    Validate.notEmpty(questions, "Questions cannot be null or empty!");
    Validate.notNull(testWrapper, "Test wrapper cannot be null!");
  }
}
