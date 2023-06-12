package mn.erin.lms.base.domain.service.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import mn.erin.common.mail.EmailService;
import mn.erin.common.mail.EmailUtil;
import mn.erin.common.sms.SmsMessageFactory;
import mn.erin.common.sms.SmsSender;
import mn.erin.domain.base.model.person.ContactInfo;
import mn.erin.lms.base.aim.AccessIdentityManagement;
import mn.erin.lms.base.aim.LmsUserService;
import mn.erin.lms.base.domain.service.NotificationService;

/**
 * @author Bat-Erdene Tsogoo.
 */
public abstract class AbstractNotificationService implements NotificationService
{
  private static final Logger LOGGER = LoggerFactory.getLogger(AbstractNotificationService.class);

  protected final LmsUserService userService;
  protected final EmailService emailService;
  protected final SmsSender smsSender;
  protected final SmsMessageFactory smsMessageFactory;
  private final AccessIdentityManagement aim;
  protected  static final String DOMAIN_NAME = "domainName";
  protected String domainName = "notDefined";

  protected AbstractNotificationService(LmsUserService userService, EmailService emailService,
      SmsSender smsSender, SmsMessageFactory smsMessageFactory, AccessIdentityManagement aim)
  {
    this.userService = userService;
    this.emailService = emailService;
    this.smsSender = smsSender;
    this.smsMessageFactory = smsMessageFactory;
    this.aim = aim;
    this.setDomainName();
  }

  @Override
  public void notifyCoursePublished(Set<String> lmsUsers, Map<String, Object> publishedCourseData, boolean sendEmail, boolean sendSms)
  {
    if (!sendEmail && !sendSms)
    {
      return;
    }

    publishedCourseData.put(DOMAIN_NAME, domainName);
    String message = smsMessageFactory.newMessage(publishedCourseData);
    LOGGER.warn("Notification Message: {}", message);

    lmsUsers = filterNonArchivedUsers(lmsUsers);
    for (String lmsUser : lmsUsers)
    {
      ContactInfo contactInfo = userService.getContactInfo(lmsUser);
      if (contactInfo == null)
      {
        LOGGER.warn("User [{}] has no contact info", lmsUser);
        continue;
      }

      LOGGER.info("User [{}], contact info [{}, {}]", lmsUser, contactInfo.getEmail(), contactInfo.getPhone());
      if (sendEmail)
      {
        try
        {
          sendEmail(contactInfo.getEmail(), publishedCourseData);
        }
        catch (RuntimeException e)
        {
          LOGGER.error("Failed to send email", e);
        }
      }

      if (sendSms)
      {
        try
        {
          sendSms(contactInfo.getPhone(), message);
        }
        catch (RuntimeException e)
        {
          LOGGER.error("Failed to send sms ", e);
        }
      }
    }
  }

  @Override
  public void notifyExamPublished(Set<String> lmsUsers, Map<String, Object> publishedExamData, boolean sendEmail, boolean sendSms)
  {
    if (!sendEmail && !sendSms)
    {
      return;
    }

    publishedExamData.put(DOMAIN_NAME, domainName);
    String message = smsMessageFactory.newMessage(publishedExamData);
    LOGGER.warn("Notification Message: {}", message);

    lmsUsers = filterNonArchivedUsers(lmsUsers);
    for (String lmsUser : lmsUsers)
    {
      ContactInfo contactInfo = userService.getContactInfo(lmsUser);
      if (contactInfo == null)
      {
        LOGGER.warn("User [{}] has no contact info", lmsUser);
        continue;
      }

      LOGGER.info("User [{}], contact info [{}, {}]", lmsUser, contactInfo.getEmail(), contactInfo.getPhone());
      if (sendEmail)
      {
        try
        {
          sendEmail(contactInfo.getEmail(), publishedExamData);
        }
        catch (RuntimeException e)
        {
          LOGGER.error("Failed to send email", e);
        }
      }

      if (sendSms)
      {
        try
        {
          sendSms(contactInfo.getPhone(), message);
        }
        catch (RuntimeException e)
        {
          LOGGER.error("Failed to send sms ", e);
        }
      }
    }
  }

  @Override
  public void sendEmailFiltered(List<String> recipients, String subject, String templateName, Map<String, Object> data)
  {
    EmailUtil.bulkSendEmailAsync(emailService, filterNonArchivedUsers(recipients), subject, templateName, data);
  }

  @Override
  public void sendEmail(String recipient, String subject, String templateName, Map<String, Object> data)
  {
    EmailUtil.bulkSendEmailAsync(emailService, Collections.singletonList(recipient), subject, templateName, data);
  }

  @Override
  public void notifyCourseConfirmed(Set<String> lmsUsers, Map<String, Object> confirmedCourseData)
  {
    notifyCourseConfirmedIntern(filterNonArchivedUsers(lmsUsers), confirmedCourseData);
  }

  @Override
  public void notifyCourseUpdated(Set<String> lmsUsers, Map<String, Object> courseData, String subject, String templateName)
  {
    notifyCourseUpdatedIntern(filterNonArchivedUsers(lmsUsers), courseData, subject, templateName);
  }

  @Override
  public void notifyClassroomCourseUpdated(Set<String> lmsUsers, Map<String, Object> updatedCourseData, String subject, String templateName)
  {
    notifyClassroomCourseUpdatedIntern(filterNonArchivedUsers(lmsUsers), updatedCourseData, subject, templateName);
  }

  @Override
  public void notifyCourseStateUpdated(Set<String> lmsUsers, Map<String, Object> courseData, String subject, String templateName, String state)
  {
    courseData.put(DOMAIN_NAME, domainName);
    notifyCourseStateUpdatedIntern(filterNonArchivedUsers(lmsUsers),courseData, subject, templateName, state);
  }

  @Override
  public void notifyNewlyEnrolledUsers(Set<String> enrolledUsers, Map<String, Object> courseData)
  {
    notifyNewlyEnrolledUsersIntern(filterNonArchivedUsers(enrolledUsers), courseData);
  }

  @Override
  public void notifyClassroomCourseClosed(Set<String> lmsUsers, Map<String, Object> courseData)
  {
    notifyClassroomCourseClosedIntern(filterNonArchivedUsers(lmsUsers), courseData);
  }

  @Override
  public void notifyAnnouncementPublished(Set<String> lmsUsers, Map<String, Object> publishedData) {
    publishedData.put(DOMAIN_NAME, domainName);
    notifyAnnouncementPublishedIntern(filterNonArchivedUsers(lmsUsers), publishedData);
  }

  protected abstract void notifyCourseConfirmedIntern(Set<String> lmsUsers, Map<String, Object> confirmedCourseData);

  protected abstract void notifyCourseUpdatedIntern(Set<String> lmsUsers, Map<String, Object> courseData, String subject, String templateName);

  protected abstract void notifyClassroomCourseUpdatedIntern(Set<String> lmsUsers, Map<String, Object> updatedCourseData, String subject, String templateName);

  protected abstract void notifyCourseStateUpdatedIntern(Set<String> lmsUsers, Map<String, Object> courseData, String subject, String templateName, String state);

  protected abstract void notifyNewlyEnrolledUsersIntern(Set<String> enrolledUsers, Map<String, Object> courseData);

  protected abstract void notifyClassroomCourseClosedIntern(Set<String> lmsUsers, Map<String, Object> courseData);

  protected abstract void notifyAnnouncementPublishedIntern(Set<String> lmsUsers, Map<String, Object> courseData);

  

  private Set<String> filterNonArchivedUsers(Collection<String> lmsUsers)
  {
    return lmsUsers.stream()
        .filter(username -> !aim.isArchived(username))
        .collect(Collectors.toSet());
  }

  private List<String> filterNonArchivedUsers(List<String> lmsUsers)
  {
    return lmsUsers.stream()
        .filter(username -> !aim.isArchived(username))
        .collect(Collectors.toList());
  }

  private void sendEmail(String email, Map<String, Object> updatedCourseData)
  {
    if (!StringUtils.isBlank(email))
    {
      EmailUtil.sendEmailAsync(emailService, email, updatedCourseData);
    }
  }

  private void sendSms(String phone, String message)
  {
    if (StringUtils.isAnyBlank(phone, message))
    {
      return;
    }
    CompletableFuture<Boolean> result = CompletableFuture.supplyAsync(() -> smsSender.sendSms(phone, message));
    result.handle((isSent, exception) -> {
      if (!(isSent == null || isSent))
      {
        LOGGER.error("Failed to send SMS to [{}]", phone, exception);
      }
      return null;
    });
  }

  private void setDomainName(){
    Properties properties = System.getProperties();
    domainName = properties.getProperty(DOMAIN_NAME);
  }
}
