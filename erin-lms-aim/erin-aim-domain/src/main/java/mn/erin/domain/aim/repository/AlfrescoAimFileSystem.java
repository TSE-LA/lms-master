/*
 * (C)opyright, 2021, ERIN SYSTEMS LLC
 * The source code is protected by copyright laws and international copyright
 * treaties, as well as other intellectual property laws and treaties.
 * All rights reserved.
 */

package mn.erin.domain.aim.repository;

import java.io.File;
import java.nio.file.Files;
import java.util.Collections;
import java.util.List;

import mn.erin.domain.dms.model.content_model.Property;
import mn.erin.domain.dms.model.document.Document;
import mn.erin.domain.dms.model.document.DocumentId;
import mn.erin.domain.dms.model.folder.Folder;
import mn.erin.domain.dms.model.folder.FolderId;
import mn.erin.domain.dms.model.folder.FolderType;
import mn.erin.domain.dms.repository.DMSRepositoryException;
import mn.erin.domain.dms.repository.DocumentRepository;
import mn.erin.domain.dms.repository.FolderRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Temuulen 2021
 */public class AlfrescoAimFileSystem implements AimFileSystem
{
  private static final Logger LOGGER = LoggerFactory.getLogger(AlfrescoAimFileSystem.class);

  private static final FolderId ROOT = FolderId.valueOf("-root-");
  private static final String BASE_USER_FOLDER = "Users";
  private static final String EXTENSION_SPLITTER_REGEX = "\\.(?=[^\\.]+$)";
  private DocumentRepository documentRepository;
  private FolderRepository folderRepository;

  public AlfrescoAimFileSystem(DocumentRepository documentRepository, FolderRepository folderRepository)
  {
    this.documentRepository = documentRepository;
    this.folderRepository = folderRepository;
  }

  @Override
  public String createUserFolder(String userId, String username)
  {
    try
    {
      Property property = new Property("cm:description", username);
      FolderType folderType = new FolderType("lms:User", Collections.singleton(property));
      Folder folder = folderRepository.create(getBaseUserFolderId(), userId, folderType);
      return folder.getFolderName();
    }
    catch (DMSRepositoryException e)
    {
      LOGGER.error(e.getMessage(), e);
      return null;
    }
  }

  @Override
  public String getUserFolderId(String userId)
  {
    FolderId userFolderId = getFolderId(getBaseUserFolderId(), userId);
    return userFolderId != null ? userFolderId.getId() : null;
  }

  @Override
  public boolean deleteUserFolder(String userId)
  {
    try
    {
      folderRepository.delete(FolderId.valueOf(userId));
      return true;
    }
    catch (DMSRepositoryException e)
    {
      LOGGER.error(e.getMessage(), e);
      return false;
    }
  }

  @Override
  public String uploadAttachment(String userId, File file)
  {
    if (file != null)
    {
      try
      {
        FolderId userFolderId = getFolderId(getBaseUserFolderId(), userId);
        documentRepository.create(userFolderId, file.getName(), Files.readAllBytes(file.toPath()));
        return file.getName();
      }
      catch (Exception e)
      {
        LOGGER.error(e.getMessage(), e);
      }
    }
    return null;
  }

  @Override
  public void deleteAttachment(String userId, String fileName)
  {
    try
    {
      FolderId userFolderId = getFolderId(getBaseUserFolderId(), userId);
      if (userFolderId == null)
      {
        return;
      }
      List<Document.Info> infos = documentRepository.listDocuments(userFolderId);
      for (Document.Info info : infos)
      {
        if (info.getName().split(EXTENSION_SPLITTER_REGEX)[0].equals(fileName.split(EXTENSION_SPLITTER_REGEX)[0]))
        {
          documentRepository.delete(DocumentId.valueOf(info.getId()));
        }
      }
    }
    catch (DMSRepositoryException e)
    {
      LOGGER.error("Could not delete or find attachment", e);
    }
  }

  private FolderId getFolderId(FolderId parentId, String folderName)
  {
    try
    {
      Folder folder = folderRepository.getFolder(parentId, folderName);
      return folder.getFolderId();
    }
    catch (DMSRepositoryException e)
    {
      LOGGER.error(e.getMessage(), e);
      return null;
    }
  }

  private FolderId getBaseUserFolderId()
  {
    Folder baseFolder;
    try
    {
      baseFolder = folderRepository.getFolder(ROOT, BASE_USER_FOLDER);
    }
    catch (DMSRepositoryException e)
    {
      try
      {
        baseFolder = folderRepository.create(ROOT, BASE_USER_FOLDER);
      }
      catch (DMSRepositoryException ex)
      {
        LOGGER.error(e.getMessage(), e);
        return null;
      }
    }

    return baseFolder.getFolderId();
  }

}
