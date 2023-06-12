/*
 * (C)opyright, 2019, ERIN SYSTEMS LLC
 * The source code is protected by copyright laws and international copyright
 * treaties, as well as other intellectual property laws and treaties.
 * All rights reserved.
 */

package mn.erin.lms.legacy.infrastructure.scorm.repository.dms;

import java.util.Objects;
import java.util.Set;
import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import mn.erin.domain.dms.model.document.DocumentId;
import mn.erin.domain.dms.model.folder.Folder;
import mn.erin.domain.dms.model.folder.FolderId;
import mn.erin.domain.dms.repository.DMSRepositoryException;
import mn.erin.domain.dms.repository.DocumentRepository;
import mn.erin.domain.dms.repository.FolderRepository;
import mn.erin.lms.legacy.domain.scorm.model.AssetId;
import mn.erin.lms.legacy.domain.scorm.repository.SCORMRepositoryException;
import mn.erin.lms.legacy.infrastructure.scorm.base.service.SCORMPackagingService;

/**
 * @author Bat-Erdene Tsogoo.
 */
abstract class DmsSCORMRepository
{
  private static final Logger LOGGER = LoggerFactory.getLogger(DmsSCORMRepository.class);

  final DocumentRepository documentRepository;
  final FolderRepository folderRepository;

  private Folder scormRootFolder;
  private Folder scormBaseWrapperFolder;

  protected SCORMPackagingService scormPackagingService;

  DmsSCORMRepository(DocumentRepository documentRepository, FolderRepository folderRepository)
  {
    this.folderRepository = Objects.requireNonNull(folderRepository, "FolderRepository cannot be null!");
    this.documentRepository = Objects.requireNonNull(documentRepository, "DocumentRepository cannot be null!");
  }

  @Inject
  public void setScormPackagingService(SCORMPackagingService scormPackagingService)
  {
    this.scormPackagingService = scormPackagingService;
  }

  Folder getBaseContentFolder() throws SCORMRepositoryException
  {
    try
    {
      String scormFolderId = scormPackagingService.getPackageId();

      if (scormFolderId == null)
      {
        return folderRepository.getFolder(getRootFolder().getFolderId(), DmsSCORMConstants.BASE_SCORM_CONTENT_FOLDER);
      }
      else
      {
        return folderRepository.getFolder(FolderId.valueOf(scormFolderId));
      }
    }
    catch (DMSRepositoryException e)
    {
      LOGGER.error(e.getMessage(), e);
      throw new SCORMRepositoryException("Base SCORM content folder was not found!", e);
    }
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
