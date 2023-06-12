/*
 * (C)opyright, 2019, ERIN SYSTEMS LLC
 * The source code is protected by copyright laws and international copyright
 * treaties, as well as other intellectual property laws and treaties.
 * All rights reserved.
 */

package mn.erin.lms.base.scorm.repository;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import mn.erin.lms.base.aim.user.LearnerId;
import mn.erin.lms.base.scorm.model.DataModel;
import mn.erin.lms.base.scorm.model.RuntimeData;
import mn.erin.lms.base.scorm.model.SCO;
import mn.erin.lms.base.scorm.model.SCORMContentId;

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

  /**
   * Lists all runtime data associated to a learner.
   *
   * @param learnerId The Id of the learner
   * @return A list of runtime data
   */
  List<RuntimeData> listRuntimeData(String learnerId);

  /**
   * Lists all runtime data associated to a learner.
   *
   * @return A list of runtime data
   */
  Map<String , List<String>> listAllRuntimeData();

  /**
   * Lists all runtime data associated to a learner and its test and initiated date is in given date is month.
   *
   * @param learnerId The Id of the learner
   * @param startDate The before the initiated date
   * @param endDate The after the initiated date
   * @return A list of runtime data
   */
  List<RuntimeData> listRunTimeDataTestCompleted(String learnerId, LocalDate startDate, LocalDate endDate);

  List<Double> listRunTimeDataProgress(String learnerId, LocalDate startDate, LocalDate endDate);

  List<RuntimeData> listRuntimeData(SCORMContentId scormContentId, String learnerId);

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
   * Deletes all the learner's runtime data associated to a SCORM content
   *
   * @param scormContentId The unique ID of the SCORM content
   * @param learnerId      The ID of the learner
   */
  boolean delete(SCORMContentId scormContentId, String learnerId);

  /**
   * Deletes all runtime data associated to a learner
   *
   * @param learnerId The ID of the learner
   */
  boolean delete(String learnerId);

  /**
   * Deletes all run-time data associated to a SCORM content.
   *
   * @param scormContentId The unique ID of the SCORM content
   */
  boolean delete(SCORMContentId scormContentId);

  boolean hasRuntimeData(LearnerId learnerId);
}
