package mn.erin.lms.base.dms;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import mn.erin.domain.dms.model.folder.Folder;
import mn.erin.domain.dms.model.folder.FolderId;
import mn.erin.domain.dms.repository.DocumentRepository;
import mn.erin.domain.dms.repository.FolderRepository;
import mn.erin.lms.base.scorm.model.Asset;
import mn.erin.lms.base.scorm.model.AssetId;
import mn.erin.lms.base.scorm.model.ContentAggregation;
import mn.erin.lms.base.scorm.model.SCORMQuiz;
import mn.erin.lms.base.scorm.model.SCORMWrapper;
import mn.erin.lms.base.scorm.repository.SCORMQuizRepository;
import mn.erin.lms.base.scorm.repository.SCORMRepositoryException;
import mn.erin.lms.base.scorm.service.SCORMQuizDataGenerator;

/**
 * @author Bat-Erdene Tsogoo.
 */
public class DmsSCORMQuizRepository extends DmsSCORMRepository implements SCORMQuizRepository
{
  private static final Logger LOGGER = LoggerFactory.getLogger(DmsSCORMQuizRepository.class);

  private final SCORMQuizDataGenerator scormQuizDataGenerator;

  public DmsSCORMQuizRepository(DocumentRepository documentRepository,
      FolderRepository folderRepository, SCORMQuizDataGenerator scormQuizDataGenerator)
  {
    super(documentRepository, folderRepository);
    this.scormQuizDataGenerator = scormQuizDataGenerator;
  }

  @Override
  public SCORMQuiz createSCORMQuiz(String rootFolderId, String name, List<SCORMQuiz.Question> questions, SCORMWrapper quizWrapper, String docName)
      throws SCORMRepositoryException
  {
    Folder quizFolder = createFolder(FolderId.valueOf(rootFolderId), name);
    byte[] quizDataFile = scormQuizDataGenerator.generateQuizDataJsonFile(questions);

    return createSCORMQuiz(name, quizFolder, quizDataFile, quizWrapper, questions, docName);
  }

  @Override
  public SCORMQuiz createSCORMQuiz(String rootFolderId, String name, List<SCORMQuiz.Question> questions, SCORMWrapper quizWrapper, String docName,
      Integer maxAttempts, Double thresholdScore) throws SCORMRepositoryException
  {
    Folder quizFolder = createFolder(FolderId.valueOf(rootFolderId), name);
    byte[] quizDataFile = scormQuizDataGenerator.generateQuizDataJsonFile(questions, maxAttempts, thresholdScore);
    return createSCORMQuiz(name, quizFolder, quizDataFile, quizWrapper, questions, docName);
  }

  private SCORMQuiz createSCORMQuiz(String name, Folder quizFolder, byte[] quizDataFile, SCORMWrapper quizWrapper, List<SCORMQuiz.Question> questions,
      String docName) throws SCORMRepositoryException
  {
    createDocument(quizFolder.getFolderId(), quizDataFile, docName);
    Asset indexHTML = quizWrapper.getRootEntity();

    copy(quizFolder.getFolderId(), Collections.singleton(indexHTML.getAssetId()));

    Set<AssetId> wrapperDependencies = quizWrapper.getDependencies().stream()
        .map(ContentAggregation.Resource::getAssetId)
        .collect(Collectors.toSet());

    copy(quizFolder.getFolderId(), wrapperDependencies);

    SCORMQuiz scormTest = new SCORMQuiz(name, questions);

    documentRepository.listDocuments(quizFolder.getFolderId())
        .forEach(document -> scormTest.addAsset(AssetId.valueOf(document.getId())));

    return scormTest;
  }
}
