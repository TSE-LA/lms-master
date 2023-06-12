/*
 * (C)opyright, 2021, ERIN SYSTEMS LLC
 * The source code is protected by copyright laws and international copyright
 * treaties, as well as other intellectual property laws and treaties.
 * All rights reserved.
 */

package mn.erin.domain.aim.repository;

import java.io.File;

/**
 * @author Temuulen 2021
 */
public interface AimFileSystem
{
  /**
   * Creates the folder by user ID
   *
   * @param userId Folder named by its user ID
   * @param username Folder description
   * @return Folder name
   */
  String createUserFolder(String userId, String username);

  /**
   * Gets the folder ID by user ID
   *
   * @param userId User ID
   * @return User folder ID
   */
  String getUserFolderId(String userId);

  /**
   * Deletes the user folder by user ID
   *
   * @param userId User ID
   * @return If deleted true otherwise false
   */
  boolean deleteUserFolder(String userId);

  /**
   * Uploads file to user folder
   *
   * @param userId User ID
   * @param file Uploading file
   * @return Uploaded file name
   */
  String uploadAttachment(String userId, File file);

  /**
   * Deletes the attachment of user folder
   *
   * @param userId User ID
   * @param fileName Deleting file name
   */
  void deleteAttachment(String userId, String fileName);
}
