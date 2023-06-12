/*
 * (C)opyright, 2019, ERIN SYSTEMS LLC
 * The source code is protected by copyright laws and international copyright
 * treaties, as well as other intellectual property laws and treaties.
 * All rights reserved.
 */

package mn.erin.lms.legacy.domain.scorm.repository;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import mn.erin.lms.legacy.domain.scorm.model.DataModel;
import mn.erin.lms.legacy.domain.scorm.model.RuntimeData;
import mn.erin.lms.legacy.domain.scorm.model.RuntimeDataId;
import mn.erin.lms.legacy.domain.scorm.model.SCO;
import mn.erin.lms.legacy.domain.scorm.model.SCORMContentId;

/**
 * @author Bat-Erdene Tsogoo.
 */
public interface RuntimeDataRepository
{
  /**
   * Lists the learner's runtime data of a SCORM content. For example, if a SCORM content has 7 SCOs,
   * then 7 instances of runtime data will be returned.
   *
   * @param scormContentId The unique ID of the SCORM content, of which runtime data will be returned
   * @return A list of runtime data
   * @throws SCORMRepositoryException if the SCORM content does not exist
   */
  List<RuntimeData> listRuntimeData(SCORMContentId scormContentId) throws SCORMRepositoryException;

  List<RuntimeData> listRuntimeData(String learnerId);

  /**
   * Gets the learner's runtime data on a particular SCO
   *
   * @param scormContentId The unique ID of the SCORM content
   * @param scoName        The name of the SCO
   * @return The runtime data of the SCO
   * @throws SCORMRepositoryException if the SCORM content does not exist or there is no SCO with the specified name
   */
  RuntimeData getRuntimeData(SCORMContentId scormContentId, String scoName) throws SCORMRepositoryException;

  /**
   * Creates a new instance of runtime data when a learner launches a SCORM content or
   * a specific SCO of a SCORM content.
   *
   * @param sco The SCO of which runtime data to create
   * @return A new instance of the runtime data
   */
  RuntimeData create(SCO sco);

  /**
   * Updates the learner's runtime data on a particular SCO.
   *
   * @param scormContentId The unique ID of the SCORM content
   * @param scoName        The name of the SCO of which data to update
   * @param data           The runtime data
   * @return Updated runtime data. Some of the runtime data may be unsupported or invalid, in which case they will be ignored.
   * @throws SCORMRepositoryException if the SCORM content does not exist or there is no SCO with the specified name
   */
  Map<DataModel, Serializable> update(SCORMContentId scormContentId, String scoName, Map<String, Object> data)
      throws SCORMRepositoryException;

  /**
   * Clears all the current learner's runtime data associated to a SCORM content
   *
   * @param scormContentId The unique ID of the SCORM content
   * @throws SCORMRepositoryException If the SCORM content does not exist
   */
  void clear(SCORMContentId scormContentId) throws SCORMRepositoryException;

  /**
   * Clears all the learner's runtime data associated to a SCORM content
   *
   * @param scormContentId The unique ID of the SCORM content
   * @param id             The learner id
   * @throws SCORMRepositoryException If the SCORM content does not exist
   */
  void clearLearnersRuntimeData(SCORMContentId scormContentId, String id) throws SCORMRepositoryException;

  /**
   * Clears all the learner's runtime data associated to a particular SCO of a SCORM content
   *
   * @param scormContentId The unique ID of the SCORM content
   * @param scoName        The name of the SCO, of which data to clear
   * @throws SCORMRepositoryException If the SCORM content does not exist or SCO does not exist
   */
  void clear(SCORMContentId scormContentId, String scoName) throws SCORMRepositoryException;

  /**
   * Deletes a run-time data by its ID. Deleting a single run-time data means
   * deleting the data of a single SCO of a SCORM content, not the entire data of the SCORM content.
   *
   * @param runtimeDataId The unique ID of the run-time data
   * @throws SCORMRepositoryException if the run-time data does not exist or invalid.
   */
  void delete(RuntimeDataId runtimeDataId) throws SCORMRepositoryException;

  /**
   * Deletes all run-time data associated to a SCORM content.
   *
   * @param scormContentId The unique ID of the SCORM content
   * @throws SCORMRepositoryException if the SCORM content does not exist.
   */
  void delete(SCORMContentId scormContentId) throws SCORMRepositoryException;
}
