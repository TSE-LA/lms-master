package mn.erin.lms.base.dms;

import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import mn.erin.domain.dms.model.document.DocumentId;
import mn.erin.domain.dms.model.folder.Folder;
import mn.erin.domain.dms.model.folder.FolderId;
import mn.erin.domain.dms.repository.DMSRepositoryException;
import mn.erin.domain.dms.repository.DocumentRepository;
import mn.erin.domain.dms.repository.FolderRepository;
import mn.erin.lms.base.scorm.model.AssetId;
import mn.erin.lms.base.scorm.repository.SCORMRepositoryException;

/**
 * @author Bat-Erdene Tsogoo.
 */
public abstract class DmsSCORMRepository
{
  private static final Logger LOGGER = LoggerFactory.getLogger(DmsSCORMRepository.class);
  protected static final String PATH_DELIMITER = "/";

  protected final DocumentRepository documentRepository;
  protected final FolderRepository folderRepository;

  private Folder scormRootFolder;
  private Folder scormBaseWrapperFolder;

  public DmsSCORMRepository(DocumentRepository documentRepository, FolderRepository folderRepository)
  {
    this.documentRepository = documentRepository;
    this.folderRepository = folderRepository;
  }

  Folder getBaseWrapperFolder() throws SCORMRepositoryException
  {
    try
    {
      if (this.scormBaseWrapperFolder != null)
      {
        return scormBaseWrapperFolder;
      }

      scormBaseWrapperFolder = folderRepository.getFolder(getRootFolder().getFolderId(), DmsSCORMConstants.BASE_SCORM_WRAPPER_FOLDER);

      return scormBaseWrapperFolder;
    }
    catch (DMSRepositoryException e)
    {
      LOGGER.error(e.getMessage(), e);
      throw new SCORMRepositoryException("Base SCORM wrapper folder was not found!", e);
    }
  }

  Folder getRootFolder() throws SCORMRepositoryException
  {
    try
    {
      if (this.scormRootFolder != null)
      {
        return scormRootFolder;
      }

      scormRootFolder = folderRepository.getFolder(FolderId.valueOf("-root-"), DmsSCORMConstants.BASE_SCORM_FOLDER);
      return scormRootFolder;
    }
    catch (DMSRepositoryException e)
    {
      LOGGER.error(e.getMessage(), e);
      throw new SCORMRepositoryException("SCORM root folder was not found!", e);
    }
  }

  protected void createDocument(FolderId folderId, byte[] manifestJsonData, String name) throws SCORMRepositoryException
  {
    try
    {
      documentRepository.create(folderId, name, manifestJsonData);
    }
    catch (DMSRepositoryException e)
    {
      LOGGER.error(e.getMessage(), e);
      throw new SCORMRepositoryException("Failed to create a document [" + name + "]", e);
    }
  }

  protected Folder createFolder(FolderId parentFolderId, String name) throws SCORMRepositoryException
  {
    try
    {
      return folderRepository.create(parentFolderId, name);
    }
    catch (DMSRepositoryException e)
    {
      LOGGER.error(e.getMessage(), e);
      throw new SCORMRepositoryException("Failed to create the folder [" + name + "] inside a folder with the ID: ["
          + parentFolderId.getId() + "]", e);
    }
  }

  protected void copy(FolderId folderId, Set<AssetId> assets) throws SCORMRepositoryException
  {
    try
    {
      for (AssetId asset : assets)
      {
        documentRepository.copy(folderId, DocumentId.valueOf(asset.getId()));
      }
    }
    catch (DMSRepositoryException e)
    {
      LOGGER.error(e.getMessage(), e);
      throw new SCORMRepositoryException("Failed to copy the assets into the folder with the ID: [" + folderId.getId() + "]", e);
    }
  }
}
