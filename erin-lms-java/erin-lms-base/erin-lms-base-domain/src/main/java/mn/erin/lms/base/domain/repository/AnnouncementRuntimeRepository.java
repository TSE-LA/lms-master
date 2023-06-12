package mn.erin.lms.base.domain.repository;

import java.time.LocalDateTime;
import java.util.List;

import mn.erin.lms.base.domain.model.announcement.AnnouncementRuntime;
import mn.erin.lms.base.domain.model.announcement.AnnouncementRuntimeStatus;

public interface AnnouncementRuntimeRepository
{
  /**
   * Creats a new announcement runtime data
   *
   * @param announcementId
   * @param learnerId
   * @param status         new or viewed
   * @param viewedDate
   * @return new announcement runtime data
   * @throws LmsRepositoryException
   */
  AnnouncementRuntime create(String announcementId, String learnerId, AnnouncementRuntimeStatus status, LocalDateTime viewedDate) throws LmsRepositoryException;

  /**
   * Creates announcemnet runtime data
   *
   * @param announcementRuntimeData list of announcement runtime data
   * @return
   * @throws LmsRepositoryException
   */
  boolean createAnnouncements(List<AnnouncementRuntime> announcementRuntimeData) throws LmsRepositoryException;

  /**
   * Updates announcement runtime data by announcement id and learner id
   *
   * @param announcementId
   * @param learnerId
   * @param status
   * @param viewedDate
   * @return true if updated; otherwise, false.
   * @throws LmsRepositoryException
   */
  boolean update(String announcementId, String learnerId, AnnouncementRuntimeStatus status, LocalDateTime viewedDate) throws LmsRepositoryException;

  /**
   * Deletes announcement runtime data by announcement id and learner id
   *
   * @param announcementId
   * @param learnerId
   * @return true if deleted; otherwise, false.
   * @throws LmsRepositoryException
   */
  boolean delete(String announcementId, String learnerId) throws LmsRepositoryException;

  boolean deleteAllByAnnouncementId(String announcementId) throws LmsRepositoryException;

  /**
   * Get announcement runtime data by announcement id and learner id
   *
   * @param announcementId
   * @param learnerId
   * @return announcement runtime data
   * @throws LmsRepositoryException
   */
  AnnouncementRuntime findById(String announcementId, String learnerId) throws LmsRepositoryException;

  /**
   * Get announcement runtime data by learner id
   *
   * @param learnerId
   * @return
   * @throws LmsRepositoryException
   */
  List<AnnouncementRuntime> findByLearnerId(String learnerId) throws LmsRepositoryException;

  /**
   * Get annouincement runtime data by learner id and status
   *
   * @param learnerId
   * @param status    annucement runtime status
   * @return announcement runtime data
   * @throws LmsRepositoryException
   */
  List<AnnouncementRuntime> findByLearnerIdAndStatus(String learnerId, AnnouncementRuntimeStatus status) throws LmsRepositoryException;

  /**
   * Get announcement runtime data by learner id
   *
   * @param announcementId
   * @return
   * @throws LmsRepositoryException
   */
  List<AnnouncementRuntime> findByAnnouncementId(String announcementId) throws LmsRepositoryException;
}
