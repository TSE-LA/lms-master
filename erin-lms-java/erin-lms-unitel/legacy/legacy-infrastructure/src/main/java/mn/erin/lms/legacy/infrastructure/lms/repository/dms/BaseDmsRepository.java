/*
 * (C)opyright, 2019, ERIN SYSTEMS LLC
 * The source code is protected by copyright laws and international copyright
 * treaties, as well as other intellectual property laws and treaties.
 * All rights reserved.
 */

package mn.erin.lms.legacy.infrastructure.lms.repository.dms;

import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import mn.erin.domain.dms.model.folder.Folder;
import mn.erin.domain.dms.model.folder.FolderId;
import mn.erin.domain.dms.repository.DMSRepositoryException;
import mn.erin.domain.dms.repository.DocumentRepository;
import mn.erin.domain.dms.repository.FolderRepository;
import mn.erin.lms.legacy.domain.lms.repository.LMSRepositoryException;

/**
 * @author Bat-Erdene Tsogoo.
 */
abstract class BaseDmsRepository
{
  private static final Logger LOGGER = LoggerFactory.getLogger(BaseDmsRepository.class);

  private static final String ROOT_FOLDER = "Courses";

  final FolderRepository folderRepository;
  final DocumentRepository documentRepository;

  private Folder rootFolder;

  BaseDmsRepository(DocumentRepository documentRepository, FolderRepository folderRepository)
  {
    this.documentRepository = Objects.requireNonNull(documentRepository, "DocumentRepository cannot be null!");
    this.folderRepository = Objects.requireNonNull(folderRepository, "FolderRepository cannot be null!");
  }

  Folder getRootFolder() throws LMSRepositoryException
  {
    try
    {
      if (this.rootFolder != null)
      {
        return rootFolder;
      }

      rootFolder = folderRepository.getFolder(FolderId.valueOf("-root-"), ROOT_FOLDER);
      return rootFolder;
    }
    catch (DMSRepositoryException e)
    {
      LOGGER.error(e.getMessage(), e);
      throw new LMSRepositoryException("'Courses' root folder was not found!");
    }
  }
}
