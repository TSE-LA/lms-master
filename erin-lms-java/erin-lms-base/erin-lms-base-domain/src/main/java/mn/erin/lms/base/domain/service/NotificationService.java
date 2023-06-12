package mn.erin.lms.base.domain.service;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author Bat-Erdene Tsogoo.
 */
public interface NotificationService
{
  /**
   * Notifies a course published to specified LMS users
   *  @param lmsUsers            The list of LMS users who will receive notifications
   * @param publishedCourseData Published course data
   * @param sendEmail           Whether to send email or not
   * @param sendSms             Whether to send sms or not
   */
  void notifyCoursePublished(Set<String> lmsUsers, Map<String, Object> publishedCourseData, boolean sendEmail, boolean sendSms);
  /**
   * Notifies a course confirmed to specified LMS users
   *
   * @param lmsUsers            The list of LMS users who will receive notifications
   * @param confirmedCourseData Published course data
   */
  void notifyCourseConfirmed(Set<String> lmsUsers, Map<String, Object> confirmedCourseData);

  /**
   * Notifies a course updated to specified LMS users
   *
   * @param lmsUsers          The list of LMS users who will receive notifications
   * @param updatedCourseData Updated course data
   */
  void notifyCourseUpdated(Set<String> lmsUsers, Map<String, Object> updatedCourseData, String subject, String templateName);

  void notifyClassroomCourseUpdated(Set<String> lmsUsers, Map<String, Object> updatedCourseData, String subject, String templateName);

  /**
   * Notifies a course state updated to specified LMS users
   * @param lmsUsers          The list of LMS users who will receive notifications
   * @param updatedCourseData Updated course data
   */
  void notifyCourseStateUpdated(Set<String> lmsUsers, Map<String, Object> updatedCourseData, String subject, String templateName, String state);

  void notifyNewlyEnrolledUsers(Set<String> enrolledUsers, Map<String, Object> courseData);
  void notifyClassroomCourseClosed(Set<String> lmsUsers, Map<String, Object> courseData);

  /**
   * Sends email with filtered data
   * @param recipients    A list of recipients
   * @param subject   A string subject
   * @param templateName   A template name
   * @param data   A mapped data
   */
  void sendEmailFiltered(List<String> recipients, String subject, String templateName, Map<String, Object> data);

  /**
   * Sends a single email
   * @param recipient    A recipients
   * @param subject   A string subject
   * @param templateName   A template name
   * @param data   A mapped data
   */
  void sendEmail(String recipient, String subject, String templateName, Map<String, Object> data);

  /**
   * Notifies a course published or not to LMS Instructor
   *  @param instructor         User who will receive the notice
   * @param publishCourseData   Course data about course publish
   */
  void notifyPublisher(String instructor, Map<String, Object> publishCourseData, boolean success);

  void notifyExamPublished(Set<String> lmsUsers, Map<String, Object> publishedCourseData, boolean sendEmail, boolean sendSms);

  void notifyAnnouncementPublished(Set<String> lmsUsers, Map<String, Object> publishedData);
}
