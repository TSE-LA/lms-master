package mn.erin.lms.base.dms;

import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import mn.erin.domain.dms.model.document.Document;
import mn.erin.domain.dms.model.document.DocumentContent;
import mn.erin.domain.dms.model.document.DocumentId;
import mn.erin.domain.dms.model.folder.Folder;
import mn.erin.domain.dms.model.folder.FolderId;
import mn.erin.domain.dms.repository.DMSRepositoryException;
import mn.erin.domain.dms.repository.DocumentRepository;
import mn.erin.domain.dms.repository.FolderRepository;
import mn.erin.lms.base.scorm.model.Asset;
import mn.erin.lms.base.scorm.model.AssetId;
import mn.erin.lms.base.scorm.model.ContentAggregation;
import mn.erin.lms.base.scorm.model.SCORMWrapper;
import mn.erin.lms.base.scorm.repository.SCORMRepositoryException;
import mn.erin.lms.base.scorm.repository.SCORMWrapperRepository;

/**
 * @author Bat-Erdene Tsogoo.
 */
public class DmsSCORMWrapperRepository extends DmsSCORMRepository implements SCORMWrapperRepository
{
  private static final Logger LOGGER = LoggerFactory.getLogger(DmsSCORMWrapperRepository.class);

  public DmsSCORMWrapperRepository(DocumentRepository documentRepository,
      FolderRepository folderRepository)
  {
    super(documentRepository, folderRepository);
  }

  @Override
  public SCORMWrapper getDefaultWrapper() throws SCORMRepositoryException
  {
    return getWrapper("Carousel");
  }

  @Override
  public SCORMWrapper getWrapper(String type) throws SCORMRepositoryException
  {
    Folder wrapperFolder;
    try
    {
      wrapperFolder = folderRepository.getFolder(getBaseWrapperFolder().getFolderId(), type);
    }
    catch (DMSRepositoryException e)
    {
      LOGGER.error(e.getMessage(), e);
      throw new SCORMRepositoryException("Folder of the wrapper: [" + type + "] was not found!", e);
    }

    return getWrapper(wrapperFolder.getFolderId(), type);
  }

  private Asset getIndexHTML(FolderId defaultWrapperFolderId) throws SCORMRepositoryException
  {
    Document indexHTML;

    try
    {
      indexHTML = documentRepository.getDocument(defaultWrapperFolderId, DmsSCORMConstants.INDEX_HTML_FILE);
    }
    catch (DMSRepositoryException e)
    {
      throw new SCORMRepositoryException(DmsSCORMConstants.INDEX_HTML_FILE + " was not found in the wrapper folder", e);
    }

    AssetId assetId = AssetId.valueOf(indexHTML.getDocumentId().getId());
    DocumentContent content = new DocumentContent(indexHTML.getContent().getContent());

    return new Asset(assetId, indexHTML.getDocumentName(), content);
  }

  private List<AssetId> getXmlSchemaDefinitionFiles(FolderId scormBaseWrapperFolderId) throws SCORMRepositoryException
  {
    try
    {
      Folder xsdFolder = folderRepository.getFolder(scormBaseWrapperFolderId, DmsSCORMConstants.XML_SCHEMA_DEFINITION_FILES_FOLDER);
      List<DocumentId> xsds = documentRepository.listDocuments(xsdFolder.getFolderId()).stream()
          .map(document -> DocumentId.valueOf(document.getId()))
          .collect(Collectors.toList());

      return xsds.stream()
          .map(documentId -> AssetId.valueOf(documentId.getId()))
          .collect(Collectors.toList());
    }
    catch (DMSRepositoryException e)
    {
      LOGGER.error(e.getMessage(), e);
      throw new SCORMRepositoryException("XSDs folder was not found!", e);
    }
  }

  private SCORMWrapper getWrapper(FolderId wrapperFolderId, String type) throws SCORMRepositoryException
  {
    List<AssetId> xmlSchemaDefinitionFiles = getXmlSchemaDefinitionFiles(getBaseWrapperFolder().getFolderId());
    Asset indexHTML = getIndexHTML(wrapperFolderId);

    SCORMWrapper wrapper = new SCORMWrapper(indexHTML, type);
    xmlSchemaDefinitionFiles.forEach(wrapper::addXsd);

    List<Document.Info> documents = documentRepository.listDocuments(wrapperFolderId);

    documents.forEach(document -> {
      AssetId dependencyId = AssetId.valueOf(document.getId());
      ContentAggregation.Resource dependency = new ContentAggregation.Resource(dependencyId, document.getName());
      wrapper.addDependency(dependency);
    });

    return wrapper;
  }
}
