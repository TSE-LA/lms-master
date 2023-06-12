/*
 * (C)opyright, 2019, ERIN SYSTEMS LLC
 * The source code is protected by copyright laws and international copyright
 * treaties, as well as other intellectual property laws and treaties.
 * All rights reserved.
 */

package mn.erin.lms.base.scorm.repository;

import java.util.Collection;
import java.util.Set;

import mn.erin.lms.base.scorm.model.AssetId;
import mn.erin.lms.base.scorm.model.ContentAggregation;
import mn.erin.lms.base.scorm.model.SCO;
import mn.erin.lms.base.scorm.model.SCORMContent;
import mn.erin.lms.base.scorm.model.SCORMContentId;
import mn.erin.lms.base.scorm.model.SCORMWrapper;

/**
 * @author Bat-Erdene Tsogoo.
 */
public interface SCORMContentRepository
{
  /**
   * Lists all SCORM contents stored in the repository
   *
   * @return A collection of SCORM contents. If no SCORM content found, then it'll return an empty collection.
   */
  Collection<SCORMContent> listAll();

  /**
   * Creates a new SCORM content from existing assets according to the specified Content Aggregation Model.
   *
   * @param rootFolderId       The ID of the root folder
   * @param scormWrapper       The wrapper used for enabling the Run-Time communication.
   * @param contentAggregation The structure of the SCORM content
   * @return a new SCORM content
   * @throws SCORMRepositoryException If failed to create a SCORM content
   */
  SCORMContent create(String rootFolderId, SCORMWrapper scormWrapper, ContentAggregation contentAggregation) throws SCORMRepositoryException;

  /**
   * Gets all the Shareable Content Objects (SCOs) of a SCORM content
   *
   * @param scormContentId The unique ID of the SCORM content
   * @return A collection of Shareable Content Objects (SCOs)
   * @throws SCORMRepositoryException if the SCORM content does not exist
   */
  Set<SCO> getShareableContentObjects(SCORMContentId scormContentId) throws SCORMRepositoryException;

  /**
   * Gets a SCORM asset by ID
   *
   * @param assetId The unique ID of the Asset
   * @return A representation of the asset as a SCORM resource
   * @throws SCORMRepositoryException If the resource does not exist.
   */
  ContentAggregation.Resource getResource(AssetId assetId) throws SCORMRepositoryException;

  /**
   * Deletes a SCORM content
   *
   * @param scormContentId The unique ID of the SCORM content
   * @throws SCORMRepositoryException if failed to delete the SCORM content
   */
  void delete(SCORMContentId scormContentId) throws SCORMRepositoryException;
}
