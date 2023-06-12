package mn.erin.lms.base.domain.service.impl;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import mn.erin.common.mail.EmailService;
import mn.erin.common.mail.EmailUtil;
import mn.erin.common.sms.SmsMessageFactory;
import mn.erin.common.sms.SmsSender;
import mn.erin.domain.base.model.person.ContactInfo;
import mn.erin.lms.base.aim.LmsUserService;
import mn.erin.lms.base.domain.service.NotificationService;

/**
 * @author Bat-Erdene Tsogoo.
 */
public class NotificationServiceImpl implements NotificationService
{
  private static final Logger LOGGER = LoggerFactory.getLogger(NotificationServiceImpl.class);

  protected final LmsUserService userService;
  protected final EmailService emailService;
  protected final SmsSender smsSender;
  protected final SmsMessageFactory smsMessageFactory;
  protected  static final String DOMAIN_NAME = "domainName";
  protected String domainName = "notDefined";

  public NotificationServiceImpl(LmsUserService userService, EmailService emailService, SmsSender smsSender,
      SmsMessageFactory smsMessageFactory)
  {
    this.userService = userService;
    this.emailService = emailService;
    this.smsSender = smsSender;
    this.smsMessageFactory = smsMessageFactory;
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
    LOGGER.warn("SMS Message: {}", message);

    for (String lmsUser : lmsUsers)
    {
      ContactInfo contactInfo = userService.getContactInfo(lmsUser);
      LOGGER.warn("User: {}", lmsUser);
      LOGGER.warn("Contact info: {}, {}",
          contactInfo != null ? contactInfo.getEmail() : "null",
          contactInfo != null ? contactInfo.getPhone() : "null"
      );

      if(contactInfo == null){
        continue;
      }

      if (sendEmail)
      {
        try
        {
          sendEmail(contactInfo.getEmail(), publishedCourseData);
        }
        catch (RuntimeException e)
        {
          LOGGER.error("failed to send email: {}", e.getMessage());
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
          LOGGER.error("failed to send sms: {}", e.getMessage());
        }
      }
    }
  }

  @Override
  public void notifyCourseConfirmed(Set<String> lmsUsers, Map<String, Object> confirmedCourseData)
  {
    throw new UnsupportedOperationException();
  }

  @Override
  public void notifyCourseUpdated(Set<String> lmsUsers, Map<String, Object> courseData, String subject, String templateName)
  {
    throw new UnsupportedOperationException();
  }

  @Override
  public void notifyClassroomCourseUpdated(Set<String> lmsUsers, Map<String, Object> updatedCourseData, String subject, String templateName)
  {
    throw new UnsupportedOperationException();
  }

  @Override
  public void notifyCourseStateUpdated(Set<String> lmsUsers, Map<String, Object> courseData, String subject, String templateName, String state)
  {
    throw new UnsupportedOperationException();
  }

  @Override
  public void notifyNewlyEnrolledUsers(Set<String> enrolledUsers, Map<String, Object> courseData)
  {
    throw new UnsupportedOperationException();
  }
  @Override
  public void notifyClassroomCourseClosed(Set<String> lmsUsers, Map<String, Object> courseData)
  {
    throw new UnsupportedOperationException();
  }

  @Override
  public void sendEmailFiltered(List<String> recipient, String subject, String templateName, Map<String, Object> data)
  {
    EmailUtil.bulkSendEmailAsync(emailService, recipient, subject, templateName, data);
  }

  @Override
  public void sendEmail(String recipient, String subject, String templateName, Map<String, Object> data)
  {
    EmailUtil.bulkSendEmailAsync(emailService, Collections.singletonList(recipient), subject, templateName, data);
  }

  @Override
  public void notifyPublisher(String instructor, Map<String, Object> publishCourseData, boolean success)
  {
   throw new UnsupportedOperationException();
  }

  private void sendEmail(String email, Map<String, Object> updatedCourseData)
  {
    if (!StringUtils.isBlank(email))
    {
      EmailUtil.sendEmailAsync(emailService, email, updatedCourseData);
    }
  }

  @Override
  public void notifyExamPublished(Set<String> lmsUsers, Map<String, Object> publishedCourseData, boolean sendEmail, boolean sendSms)
  {
    //do nothing
  }

  private void sendSms(String phone, String message)
  {
    if (!StringUtils.isBlank(phone) && !StringUtils.isBlank(message))
    {
      CompletableFuture<Boolean> result = CompletableFuture.supplyAsync(() -> smsSender.sendSms(phone, message));
      result.handle((isSent, exception) -> {
        if (!isSent || exception != null)
        {
          LOGGER.error("Failed to send an SMS to a phone: [{}], cause: [{}]", phone, exception.getMessage());
        }
        return null;
      });
    }
  }
  private void setDomainName(){
    Properties properties = System.getProperties();
    domainName = properties.getProperty(DOMAIN_NAME);
  }

  @Override
  public void notifyAnnouncementPublished(Set<String> lmsUsers, Map<String, Object> publishedData) {
    //do nothing
  }
}
