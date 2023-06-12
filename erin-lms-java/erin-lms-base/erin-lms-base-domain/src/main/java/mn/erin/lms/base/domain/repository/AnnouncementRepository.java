package mn.erin.lms.base.domain.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

import mn.erin.lms.base.domain.model.announcement.Announcement;
import mn.erin.lms.base.domain.model.course.PublishStatus;

public interface AnnouncementRepository
{
  /**
   * Creates a new announcement
   *
   * @param author        author id
   * @param title         announcement title
   * @param groupIds
   * @param publishStatus
   * @return new announcement
   * @throws LmsRepositoryException If failed to create a course
   */
  Announcement create(String author, String title, String content, Set<String> groupIds, PublishStatus publishStatus) throws LmsRepositoryException;

  /**
   * Updates Announcement by id
   *
   * @param announcement
   * @return true if updated; otherwise, false.
   * @throws LmsRepositoryException If failed to create a course
   */
  boolean update(Announcement announcement) throws LmsRepositoryException;

  /**
   * Removes an announcement by id
   *
   * @param announcementId
   * @return true if it is removed; otherwise, false.
   */
  boolean delete(String announcementId) throws LmsRepositoryException;

  /**
   * Lists announcements
   *
   * @param startDate
   * @param endDate
   * @return list of announcements
   */
  List<Announcement> listAll(LocalDateTime startDate, LocalDateTime endDate) throws LmsRepositoryException;

  /**
   * Get announcements by list of id
   *
   * @param ids       announcement ids
   * @param startDate
   * @param endDate
   * @return list of announcements
   * @throws LmsRepositoryException
   */
  List<Announcement> getAllById(Set<String> ids, LocalDateTime startDate, LocalDateTime endDate) throws LmsRepositoryException;

  /**
   * Get announcement by Id
   *
   * @param announcementId
   * @return
   * @throws LmsRepositoryException
   */
  Announcement getById(String announcementId) throws LmsRepositoryException;
}
