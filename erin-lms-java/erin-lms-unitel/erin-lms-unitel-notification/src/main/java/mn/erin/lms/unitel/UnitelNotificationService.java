package mn.erin.lms.unitel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
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
import mn.erin.lms.base.aim.AccessIdentityManagement;
import mn.erin.lms.base.aim.LmsUserService;
import mn.erin.lms.base.domain.service.impl.AbstractNotificationService;

/**
 * @author Bat-Erdene Tsogoo.
 */
public class UnitelNotificationService extends AbstractNotificationService
{
  public static final String COURSE_TYPE = "courseType";
  private static final Logger LOGGER = LoggerFactory.getLogger(UnitelNotificationService.class);

  private static final String NEWLY_ENROLLED_USER_SMS = "Сайн байна уу? Танд \"%s\" цахим сургалтын урилга ирлээ.\n" +
      "%s/#/online-course/launch/%s";
  private static final String NEWLY_CLASSROOM_ENROLLED_USER_SMS = "Сайн байна уу?\n"
      + "Танд \"%s\" %s өдрийн %s~%s цагт хамрагдах танхимын сургалтын урилга ирлээ. "
      + "\nСургалт \"%s\" болох тул та цагтаа хамрагдана уу. ";
  private static final String INVITATION_SIGNATURE = "Урилга илгээсэн админ: %s";
  private static final String CLASSROOM_LAUNCH_LINK = "\n %s/#/classroom-course/launch/%s/true";
  private static final String NEWLY_ENROLLED_USER_EMAIL_SUBJECT = "Цахим сургалтын урилга ирлээ.";
  private static final String NEWLY_CLASSROOM_ENROLLED_USER_EMAIL_SUBJECT = "Танхимын сургалтын урилга ирлээ.";
  private static final String CLOSED_CLASSROOM_COURSE_SURVEY_LINK = "Сайн байна уу?\n"
      + "Та \"%s\"-н үнэлгээний хуудсыг дараах холбоосоор хандан бөглөнө үү\n"
      + "Сургалт орсон админ: %s "
      + "\n %s/#/classroom-course/assessment/launch/%s";
  private static final String END_OF_TEXT = "\nАсууж тодруулах зүйл байвал шууд удирдлагадаа хандана уу.";
  public static final String COURSE_ID = "courseId";
  public static final String COURSE_NAME = "courseName";
  public static final String AUTHOR_ID = "authorId";
  public static final String START_TIME = "startTime";
  public static final String END_TIME = "endTime";
  public static final String DATE = "date";
  public static final String COURSE_TITLE = "courseTitle";
  public static final String SENDING_SMS_TO_USERS = "Sending sms to [{}] users.";
  public static final String SENDING_TO = "Sending to [{}]...";
  public static final String USER_HAS_NO_PHONE_NUMBER = "User [{}] has no phone number.";
  public static final String FAILED_TO_SEND_SMS = "Failed to send sms [{}]";
  public static final String FAILED_TO_SEND_EMAIL = "failed to send email: [{}]";

  public UnitelNotificationService(LmsUserService userService, EmailService emailService,
      SmsSender smsSender, SmsMessageFactory smsMessageFactory, AccessIdentityManagement aim)
  {
    super(userService, emailService, smsSender, smsMessageFactory, aim);
  }

  @Override
  public void notifyNewlyEnrolledUsersIntern(Set<String> enrolledUsers, Map<String, Object> courseData)
  {
    Set<String> recipients = new HashSet<>();
    String courseId = (String) courseData.get(COURSE_ID);
    String courseName = (String) courseData.get(COURSE_NAME);
    courseData.put(AUTHOR_ID, userService.getCurrentUser().getId().getId());
    courseData.put(DOMAIN_NAME, domainName);
    LOGGER.info(SENDING_SMS_TO_USERS, enrolledUsers.size());
    for (String enrolledUser : enrolledUsers)
    {
      ContactInfo contactInfo = userService.getContactInfo(enrolledUser);
      if (contactInfo != null)
      {
        if (contactInfo.getEmail() != null)
        {
          recipients.add(contactInfo.getEmail());
        }

        if (contactInfo.getPhone() != null)
        {
          LOGGER.info(SENDING_TO, contactInfo.getPhone());
          sendSms(contactInfo.getPhone(), String.format(NEWLY_ENROLLED_USER_SMS, courseName, domainName, courseId));
        }
        else
        {
          LOGGER.error(USER_HAS_NO_PHONE_NUMBER, enrolledUser);
        }
      }
    }

    EmailUtil.bulkSendEmailAsync(emailService, new ArrayList<>(recipients), NEWLY_ENROLLED_USER_EMAIL_SUBJECT,
        "online-course-enrollment-template.ftl", courseData);
  }

  @Override
  public void notifyClassroomCourseClosedIntern(Set<String> lmsUsers, Map<String, Object> courseData)
  {
    Set<String> recipients = new HashSet<>();
    String courseId = (String) courseData.get(COURSE_ID);
    String courseName = (String) courseData.get(COURSE_TITLE);
    String authorId = userService.getCurrentUser().getId().getId();
    courseData.put(AUTHOR_ID, authorId);
    courseData.put(DOMAIN_NAME, domainName);
    LOGGER.info(SENDING_SMS_TO_USERS, lmsUsers.size());
    for (String enrolledUser : lmsUsers)
    {
      ContactInfo contactInfo = userService.getContactInfo(enrolledUser);
      if (contactInfo != null)
      {
        if (contactInfo.getEmail() != null)
        {
          recipients.add(contactInfo.getEmail());
        }

        if (contactInfo.getPhone() != null)
        {
          LOGGER.info(SENDING_TO, contactInfo.getPhone());
          smsSender.sendSms(contactInfo.getPhone(), String.format(CLOSED_CLASSROOM_COURSE_SURVEY_LINK, courseName, authorId, domainName, courseId));
        }
        else
        {
          LOGGER.error(USER_HAS_NO_PHONE_NUMBER, enrolledUser);
        }
      }
    }

    EmailUtil.bulkSendEmailAsync(emailService, new ArrayList<>(recipients), "Үнэлгээний хуудас бөглөх",
        "classroom-course-survey-link-email-template.ftl", courseData);
  }

  @Override
  public void notifyCourseConfirmedIntern(Set<String> enrolledUsers, Map<String, Object> courseData)
  {
    Set<String> recipients = new HashSet<>();
    String courseName = (String) courseData.get(COURSE_NAME);
    String date = (String) courseData.get(DATE);
    String startTime = (String) courseData.get(START_TIME);
    String endTime = (String) courseData.get(END_TIME);
    String address = (String) courseData.get("address");
    String courseId = (String) courseData.get(COURSE_ID);
    String authorId = userService.getCurrentUser().getId().getId();
    courseData.put(DOMAIN_NAME, domainName);
    courseData.put(AUTHOR_ID, authorId);
    LOGGER.info(SENDING_SMS_TO_USERS, enrolledUsers.size());
    for (String enrolledUser : enrolledUsers)
    {
      ContactInfo contactInfo = userService.getContactInfo(enrolledUser);
      if (contactInfo != null)
      {
        if (contactInfo.getEmail() != null)
        {
          recipients.add(contactInfo.getEmail());
        }

        if (contactInfo.getPhone() != null)
        {
          LOGGER.info(SENDING_TO, contactInfo.getPhone());
          sendSms(contactInfo.getPhone(), String.format(NEWLY_CLASSROOM_ENROLLED_USER_SMS,
              courseName, date, startTime, endTime, address)
              + String.format(INVITATION_SIGNATURE, authorId)
              + END_OF_TEXT
              + String.format(CLASSROOM_LAUNCH_LINK, domainName, courseId));
        }
        else
        {
          LOGGER.error(USER_HAS_NO_PHONE_NUMBER, enrolledUser);
        }
      }
    }

    EmailUtil.bulkSendEmailAsync(emailService, new ArrayList<>(recipients), NEWLY_CLASSROOM_ENROLLED_USER_EMAIL_SUBJECT,
        "classroom-course-confirmation-email-template.ftl", courseData);
  }

  @Override
  public void notifyCourseUpdatedIntern(Set<String> lmsUsers, Map<String, Object> courseData, String subject, String templateName)
  {
    LOGGER.info("Sending course updated sms to [{}] users", lmsUsers.size());
    List<String> emailRecipients = new ArrayList<>();
    courseData.put(DOMAIN_NAME, domainName);
    String smsMessage = getUpdatedCourseSmsMessage(courseData);
    for (String user : lmsUsers)
    {
      ContactInfo contactInfo = userService.getContactInfo(user);

      if (contactInfo != null && contactInfo.getPhone() != null)
      {
        try
        {
          LOGGER.info(SENDING_TO, contactInfo.getPhone());
          smsSender.sendSms(contactInfo.getPhone(), smsMessage);
        }
        catch (NullPointerException e)
        {
          LOGGER.error(FAILED_TO_SEND_SMS, e.getMessage());
        }
      }
      else
      {
        LOGGER.error(USER_HAS_NO_PHONE_NUMBER, user);
      }
      if (contactInfo != null && contactInfo.getEmail() != null)
      {
        emailRecipients.add(contactInfo.getEmail());
      }
    }

    try
    {
      courseData.put(COURSE_TYPE, getCourseTypeName((String) courseData.get(COURSE_TYPE)));
      EmailUtil.bulkSendEmailAsync(emailService, emailRecipients, subject, templateName, courseData);
    }
    catch (Exception e)
    {
      LOGGER.error(FAILED_TO_SEND_EMAIL, e.getMessage());
    }
  }

  @Override
  public void notifyClassroomCourseUpdatedIntern(Set<String> lmsUsers, Map<String, Object> courseData, String subject, String templateName)
  {
    courseData.put(DOMAIN_NAME, domainName);
    List<String> emailRecipients = new ArrayList<>();
    String smsMessage = getLocationUpdatedCourseSmsMessage(courseData);
    LOGGER.info(SENDING_SMS_TO_USERS, lmsUsers.size());
    for (String user : lmsUsers)
    {
      ContactInfo contactInfo = userService.getContactInfo(user);

      if (contactInfo != null && contactInfo.getPhone() != null)
      {
        try
        {
          LOGGER.info(SENDING_TO, contactInfo.getPhone());
          smsSender.sendSms(contactInfo.getPhone(), smsMessage);
        }
        catch (NullPointerException e)
        {
          LOGGER.error(FAILED_TO_SEND_SMS, e.getMessage());
        }
      }
      else
      {
        LOGGER.error(USER_HAS_NO_PHONE_NUMBER, user);
      }
      if (contactInfo != null && contactInfo.getEmail() != null)
      {
        emailRecipients.add(contactInfo.getEmail());
      }
    }

    try
    {
      EmailUtil.bulkSendEmailAsync(emailService, emailRecipients, subject, templateName, courseData);
    }
    catch (Exception e)
    {
      LOGGER.error(FAILED_TO_SEND_EMAIL, e.getMessage());
    }
  }

  @Override
  public void notifyCourseStateUpdatedIntern(Set<String> lmsUsers, Map<String, Object> courseData, String subject, String templateName, String state)
  {
    List<String> emailRecipients = new ArrayList<>();
    String smsMessage = getUpdatedCourseStateSmsMessage(courseData, state);
    LOGGER.error(SENDING_SMS_TO_USERS, lmsUsers.size());
    for (String user : lmsUsers)
    {
      ContactInfo contactInfo = userService.getContactInfo(user);

      if (contactInfo != null && contactInfo.getPhone() != null)
      {
        try
        {
          LOGGER.error(SENDING_TO, contactInfo.getPhone());
          smsSender.sendSms(contactInfo.getPhone(), smsMessage);
        }
        catch (NullPointerException e)
        {
          LOGGER.error(FAILED_TO_SEND_SMS, e.getMessage());
        }
      }
      else
      {
        LOGGER.error(USER_HAS_NO_PHONE_NUMBER, user);
      }
      if (contactInfo != null && contactInfo.getEmail() != null)
      {
        emailRecipients.add(contactInfo.getEmail());
      }
    }

    try
    {
      EmailUtil.bulkSendEmailAsync(emailService, emailRecipients, subject, templateName, courseData);
    }
    catch (Exception e)
    {
      LOGGER.error(FAILED_TO_SEND_EMAIL, e.getMessage());
    }
  }

  private String getUpdatedCourseSmsMessage(Map<String, Object> courseData)
  {
    String courseName = (String) courseData.get(COURSE_NAME);
    String previousName = (String) courseData.get("previousName");
    String authorId = (String) courseData.get(AUTHOR_ID);
    String courseType = (String) courseData.get(COURSE_TYPE);
    String previousCourseType = (String) courseData.get("previousCourseType");

    String message;
    if (previousName != null)
    {
      message = String.format("%s сургалтын нэр “%s”, болж ", previousName, courseName);
    }
    else
    {
      message = String.format("%s нэртэй сургалт ", courseName);
    }

    if (previousCourseType != null)
    {
      message += String.format("%s хамрах хүрээтэй болж өөрчлөгдлөө.", getCourseTypeName(courseType));
    }
    else
    {
      message += "өөрчлөгдлөө.";
    }

    message += (" Өөрчлөлт оруулсан админы нэр: " + authorId);
    return message;
  }

  private String getLocationUpdatedCourseSmsMessage(Map<String, Object> courseData)
  {
    String courseTitle = (String) courseData.get(COURSE_TITLE);
    String date = (String) courseData.get(DATE);
    String startTime = (String) courseData.get(START_TIME);
    String endTime = (String) courseData.get(END_TIME);
    String location = (String) courseData.get("location");

    return String.format("“%s” сургалт нь %s өдрийн %s~%s цагт “%s” орохоор болж өөрчлөгдлөө. Та сургалтандаа цагтаа ирж хамрагдана уу.",
        courseTitle, date, startTime, endTime, location);
  }

  private String getUpdatedCourseStateSmsMessage(Map<String, Object> courseData, String state)
  {
    String courseTitle = (String) courseData.get(COURSE_TITLE);
    String authorId = (String) courseData.get(AUTHOR_ID);
    String date = (String) courseData.get(DATE);
    String startTime = (String) courseData.get(START_TIME);
    String endTime = (String) courseData.get(END_TIME);
    String reason = (String) courseData.get("reason");
    String courseId = (String) courseData.get(COURSE_ID);

    String message;
    message = String.format("“%s“ %s өдөр %s~%s цагт зохион байгуулагдах танхимын сургалт", courseTitle, date, startTime, endTime);

    if (state.equals("POSTPONED"))
    {
      message += " хойшлогдсон тул сургалтын хугацааг эргэн мэдэгдэх болно.";
    }

    if (state.equals("CANCELED"))
    {
      message += " цуцлагдлаа.";
    }

    if (reason != null)
    {
      message += String.format("%n Шалтгаан: %s.", reason);
    }

    if (state.equals("POSTPONED"))
    {
      message += String.format(" Хойшлуулсан админ: %s", authorId) + "\n";
      message += String.format(CLASSROOM_LAUNCH_LINK, domainName, courseId);
    }

    if (state.equals("CANCELED"))
    {
      message += String.format("%n Цуцалсан админ: %s", authorId);
    }
    return message + END_OF_TEXT;
  }

  private String getCourseTypeName(String courseType)
  {
    if (courseType.equals("EMPLOYEE"))
      return "Суралцагч";
    return courseType.equals("SUPERVISOR") ? "Ахлах" : "Менежер";
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

  @Override
  public void notifyPublisher(String instructor, Map<String, Object> publishCourseData, boolean success)
  {
    String message = success ? "амжилттай нийтэллээ." : "нийтлэхэд алдаа гарсан байна, дахин нийтэлнэ үү.";
    String link = success ? domainName + "/#/online-course/launch/" + publishCourseData.get(COURSE_ID).toString() + "/true" :
        domainName + "/#/online-course/create/" + publishCourseData.get(COURSE_ID).toString() + "/structure";

    publishCourseData.put("message", message);
    publishCourseData.put("link", link);

    ContactInfo contactInfo = userService.getContactInfo(instructor);
    LOGGER.warn("User: {}", instructor);
    LOGGER.warn("Contact info: {}, {}",
        contactInfo != null ? contactInfo.getEmail() : "null",
        contactInfo != null ? contactInfo.getPhone() : "null");
    String courseName = publishCourseData.get(COURSE_NAME).toString();
    if (contactInfo == null)
    {
      return;
    }
    if (contactInfo.getEmail() != null)
    {
      try
      {
        EmailUtil.bulkSendEmailAsync(
            emailService,
            Collections.singletonList(contactInfo.getEmail()),
            "[" + courseName + "] сургалт нийтлэгдсэн тухай",
            "course-publish.ftl",
            publishCourseData);
      }
      catch (RuntimeException e)
      {
        LOGGER.error(FAILED_TO_SEND_EMAIL, e.getMessage());
      }
    }
    if (contactInfo.getPhone() != null)
    {
      try
      {
        String smsMessage = "Энэ өдрийн мэнд хүргэе. Таны '" + publishCourseData.get(COURSE_NAME) + "' нэртэй сургалт " + message + " " + link;
        sendSms(contactInfo.getPhone(), smsMessage);
      }
      catch (RuntimeException e)
      {
        LOGGER.error("failed to send sms: {}", e.getMessage());
      }
    }
  }

  @Override
  protected void notifyAnnouncementPublishedIntern(Set<String> lmsUsers, Map<String, Object> data) {
    List<String> emailRecipients = new ArrayList<>();

    for (String user : lmsUsers)
    {
      ContactInfo contactInfo = userService.getContactInfo(user);

      if (contactInfo != null && contactInfo.getEmail() != null)
      {
        emailRecipients.add(contactInfo.getEmail());
      }
    }

    try
    {
      EmailUtil.bulkSendEmailAsync(emailService, emailRecipients, "Цахим сургалтын системд шинэ зарлал нийтлэгдлээ.", "announcement-enrollment-template.ftl", data);
    }
    catch (Exception e)
    {
      LOGGER.error(FAILED_TO_SEND_EMAIL, e);
    }
  }
}
