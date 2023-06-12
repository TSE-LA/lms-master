package mn.erin.lms.base.dms;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.lang3.Validate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import mn.erin.domain.dms.model.document.Document;
import mn.erin.domain.dms.model.document.DocumentId;
import mn.erin.domain.dms.model.folder.Folder;
import mn.erin.domain.dms.model.folder.FolderId;
import mn.erin.domain.dms.repository.DMSRepositoryException;
import mn.erin.domain.dms.repository.DocumentRepository;
import mn.erin.domain.dms.repository.FolderRepository;
import mn.erin.lms.base.scorm.model.Asset;
import mn.erin.lms.base.scorm.model.AssetId;
import mn.erin.lms.base.scorm.model.ContentAggregation;
import mn.erin.lms.base.scorm.model.ManifestXMLFile;
import mn.erin.lms.base.scorm.model.SCO;
import mn.erin.lms.base.scorm.model.SCORMContent;
import mn.erin.lms.base.scorm.model.SCORMContentId;
import mn.erin.lms.base.scorm.model.SCORMWrapper;
import mn.erin.lms.base.scorm.repository.SCORMContentRepository;
import mn.erin.lms.base.scorm.repository.SCORMRepositoryException;
import mn.erin.lms.base.scorm.service.ManifestJSONParser;
import mn.erin.lms.base.scorm.service.ManifestParserException;
import mn.erin.lms.base.scorm.service.ManifestXMLParser;

/**
 * @author Bat-Erdene Tsogoo.
 */
public class DmsSCORMContentRepository extends DmsSCORMRepository implements SCORMContentRepository
{
  private static final Logger LOGGER = LoggerFactory.getLogger(DmsSCORMContentRepository.class);

  private final ManifestXMLParser manifestXMLParser;
  private final ManifestJSONParser manifestJSONParser;

  public DmsSCORMContentRepository(DocumentRepository documentRepository, FolderRepository folderRepository,
      ManifestXMLParser manifestXMLParser, ManifestJSONParser manifestJSONParser)
  {
    super(documentRepository, folderRepository);
    this.manifestXMLParser = manifestXMLParser;
    this.manifestJSONParser = manifestJSONParser;
  }

  @Override
  public SCORMContent create(String rootFolderId, SCORMWrapper scormWrapper, ContentAggregation contentAggregation) throws SCORMRepositoryException
  {
    Validate.notNull(scormWrapper, "SCORM wrapper cannot be null!");
    Validate.notNull(contentAggregation, "Content aggregation cannot be null!");

    Folder scormContentFolder = createFolder(FolderId.valueOf(rootFolderId), contentAggregation.getScormContentName());
    LOGGER.info("SCORM content folder with the node ID: {} has been created", scormContentFolder.getFolderId().getId());

    long startTime = System.currentTimeMillis();

    try
    {
      for (ContentAggregation.Organization organization : contentAggregation.getOrganizations())
      {
        Set<ContentAggregation.Resource> resources = organization.getResources();

        Folder sco = createFolder(scormContentFolder.getFolderId(), organization.getShortID());

        Set<AssetId> assets = new LinkedHashSet<>();
        Set<AssetId> dependencies = new HashSet<>();

        for (ContentAggregation.Resource resource : resources)
        {
          assets.add(resource.getAssetId());
          resolveDependencies(resource, dependencies);
        }

        // All the assets and their dependencies will be aggregated into the SCO folder
        copy(sco.getFolderId(), assets);
        copy(sco.getFolderId(), dependencies);
      }

      // Now that the structure of the SCORM content is laid out, all that left to do is to package it with the essential components.
      scormify(scormContentFolder.getFolderId(), scormWrapper, contentAggregation);
    }
    catch (SCORMRepositoryException e)
    {
      try
      {
        folderRepository.delete(scormContentFolder.getFolderId());
      }
      catch (DMSRepositoryException ex)
      {
        LOGGER.error("Unable to rollback a recently created SCORM content folder with the ID {}",
            scormContentFolder.getFolderId().getId(), ex);
      }

      throw e;
    }

    LOGGER.info("Total time spent for creating a SCORM package: {} milliseconds", System.currentTimeMillis() - startTime);

    SCORMContentId scormContentId = SCORMContentId.valueOf(scormContentFolder.getFolderId().getId());
    return new SCORMContent(scormContentId, contentAggregation.getScormContentName());
  }

  @Override
  public Set<SCO> getShareableContentObjects(SCORMContentId scormContentId) throws SCORMRepositoryException
  {
    Validate.notNull(scormContentId, "SCORM content ID cannot be null!");

    ManifestXMLFile manifestXMLFile = getManifestXMLFile(scormContentId);
    try
    {
      return manifestXMLParser.readManifestXMLFile(manifestXMLFile);
    }
    catch (ManifestParserException e)
    {
      LOGGER.error(e.getMessage(), e);
      throw new SCORMRepositoryException("Failed to read the manifest XML file of "
          + "the SCORM content with the ID [" + scormContentId.getId() + "]");
    }
  }

  @Override
  public ContentAggregation.Resource getResource(AssetId assetId) throws SCORMRepositoryException
  {
    try
    {
      Document.Info document = documentRepository.getDocument(DocumentId.valueOf(assetId.getId()));
      AssetId id = AssetId.valueOf(document.getId());
      return new ContentAggregation.Resource(id, document.getName());
    }
    catch (DMSRepositoryException e)
    {
      LOGGER.error(e.getMessage(), e);
      throw new SCORMRepositoryException("Asset with the ID: [" + assetId.getId() + "] does not exist!");
    }
  }

  @Override
  public void delete(SCORMContentId scormContentId) throws SCORMRepositoryException
  {
    Validate.notNull(scormContentId, "SCORM content ID cannot be null!");
    FolderId scormContentFolderId = FolderId.valueOf(scormContentId.getId());

    try
    {
      folderRepository.delete(scormContentFolderId);
    }
    catch (DMSRepositoryException e)
    {
      LOGGER.error(e.getMessage(), e);
      throw new SCORMRepositoryException("Failed to delete SCORM content with the ID: [" + scormContentFolderId.getId() + "]", e);
    }
  }

  @Override
  public Collection<SCORMContent> listAll()
  {
    throw new UnsupportedOperationException();
  }

  /**
   * Packages all the essential SCORM components/files into the SCORM content folder.
   *
   * @param scormContentFolderId The ID of the SCORM content folder.
   * @param scormWrapper         The wrapper which is used to enable the Run-Time communication.
   * @param contentAggregation   The structure of the SCORM content.
   * @throws SCORMRepositoryException if failed to scormify the content.
   */
  private void scormify(FolderId scormContentFolderId, SCORMWrapper scormWrapper, ContentAggregation contentAggregation)
      throws SCORMRepositoryException
  {
    // Add the necessary XML Schema Definition files for the manifest XML file.
    copy(scormContentFolderId, scormWrapper.getXmlSchemaDefinitionFiles());

    // Shared folder contains the SCORM wrapper files and it'll be used across all SCOs as a common dependency.
    Folder sharedFolder = createFolder(scormContentFolderId, DmsSCORMConstants.SHARED_FOLDER);

    // This is the index.html file, the actual wrapper, where our Assets will be displayed
    Asset indexHTML = scormWrapper.getRootEntity();

    // Copy the index.html into the shared folder.
    copy(sharedFolder.getFolderId(), Collections.singleton(indexHTML.getAssetId()));

    // Collect the list of dependencies of the wrapper. These may include CSS, JavaScript or image files.
    Set<AssetId> wrapperDependencies = scormWrapper.getDependencies().stream()
        .map(ContentAggregation.Resource::getAssetId)
        .collect(Collectors.toSet());

    // Copy the dependencies into the shared folder
    copy(sharedFolder.getFolderId(), wrapperDependencies);

    try
    {
      // Generate the JSON file containing the Content Aggregation data.
      byte[] manifestJsonData = manifestJSONParser.generateManifestJSONFile(contentAggregation);
      // Create the JSON file in the shared folder, where the wrapper reads the data.
      createDocument(sharedFolder.getFolderId(), manifestJsonData, DmsSCORMConstants.MANIFEST_JSON_FILE);
    }
    catch (ManifestParserException e)
    {
      LOGGER.error(e.getMessage(), e);
      throw new SCORMRepositoryException("Failed to generate manifest JSON file", e);
    }

    String wrapperPathComponent = DmsSCORMConstants.SHARED_FOLDER + PATH_DELIMITER + scormWrapper.getQueryStringParameter();

    // Collect the list of dependency names along with the file path so as to write them as a common dependency in the manifest XML file.
    List<String> commonFiles = scormWrapper.getDependencies().stream()
        .map(dependency -> DmsSCORMConstants.SHARED_FOLDER + PATH_DELIMITER + dependency.getAssetName())
        .collect(Collectors.toList());

    try
    {
      // Generate the manifest XML ("imsmanifest.xml") file.
      byte[] imsManifestFile = manifestXMLParser.generateManifestXMLFile(contentAggregation, commonFiles, wrapperPathComponent);
      createDocument(scormContentFolderId, imsManifestFile, DmsSCORMConstants.MANIFEST_XML_FILE);
    }
    catch (ManifestParserException e)
    {
      LOGGER.error(e.getMessage(), e);
      throw new SCORMRepositoryException("Failed to generate manifest XML file", e);
    }
  }

  /**
   * Resolves the resource dependencies recursively
   *
   * @param resource     The resource, of which dependencies will be resolved
   * @param dependencies All the resolved dependencies will be added into this Set.
   */
  private void resolveDependencies(ContentAggregation.Resource resource, Set<AssetId> dependencies)
  {
    if (resource.getDependencies() == null || resource.getDependencies().isEmpty())
    {
      return;
    }

    for (ContentAggregation.Resource dependency : resource.getDependencies())
    {
      dependencies.add(dependency.getAssetId());
      resolveDependencies(dependency, dependencies);
    }
  }

  private ManifestXMLFile getManifestXMLFile(SCORMContentId scormContentId) throws SCORMRepositoryException
  {
    try
    {
      FolderId scormContentFolderId = FolderId.valueOf(scormContentId.getId());
      Folder scormContentFolder = folderRepository.getFolder(scormContentFolderId);

      Document manifestXML = documentRepository.getDocument(scormContentFolderId, DmsSCORMConstants.MANIFEST_XML_FILE);
      AssetId manifestXMLAssetId = AssetId.valueOf(manifestXML.getDocumentId().getId());

      SCORMContent scormContent = new SCORMContent(scormContentId, scormContentFolder.getFolderName());

      return new ManifestXMLFile(manifestXMLAssetId, manifestXML.getDocumentName(), manifestXML.getContent(), scormContent);
    }
    catch (DMSRepositoryException e)
    {
      LOGGER.error(e.getMessage(), e);
      throw new SCORMRepositoryException("Failed to get the manifest XML file!", e);
    }
  }
}
