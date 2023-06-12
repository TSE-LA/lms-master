package mn.erin.lms.legacy.infrastructure.unitel.rest.notification;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Properties;
import java.util.Set;
import java.util.stream.Collectors;
import javax.inject.Inject;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import mn.erin.common.mail.EmailService;
import mn.erin.common.mail.EmailUtil;
import mn.erin.common.sms.SmsSender;
import mn.erin.domain.base.model.person.ContactInfo;
import mn.erin.domain.base.usecase.UseCaseException;
import mn.erin.infrastucture.rest.common.RestApi;
import mn.erin.infrastucture.rest.common.response.RestResponse;
import mn.erin.infrastucture.rest.common.response.RestResult;
import mn.erin.lms.base.aim.AccessIdentityManagement;
import mn.erin.lms.legacy.domain.lms.model.course.CourseGroup;
import mn.erin.lms.legacy.domain.lms.repository.CourseCategoryRepository;
import mn.erin.lms.legacy.domain.lms.repository.CourseEnrollmentRepository;
import mn.erin.lms.legacy.domain.lms.repository.CourseGroupRepository;
import mn.erin.lms.legacy.domain.lms.repository.CourseRepository;
import mn.erin.lms.legacy.domain.lms.service.EnrollmentStateService;
import mn.erin.lms.legacy.domain.lms.usecase.course.get_course.GetCourse;
import mn.erin.lms.legacy.domain.lms.usecase.course.get_course.GetCourseInput;
import mn.erin.lms.legacy.domain.lms.usecase.course.get_course.GetCourseOutput;
import mn.erin.lms.legacy.domain.lms.usecase.enrollment.get_enrollment_list.GetEnrollmentList;
import mn.erin.lms.legacy.domain.lms.usecase.enrollment.get_enrollment_list.GetEnrollmentListInput;
import mn.erin.lms.legacy.domain.lms.usecase.enrollment.get_enrollment_list.GetEnrollmentOutput;
import mn.erin.lms.legacy.domain.lms.usecase.enrollment.update_enrollment.UpdateEnrollment;
import mn.erin.lms.legacy.domain.lms.usecase.enrollment.update_enrollment.UpdateEnrollmentInput;
import mn.erin.lms.legacy.domain.unitel.PromotionConstants;
import mn.erin.lms.legacy.domain.unitel.usecase.notification.GetPromotionNotification;
import mn.erin.lms.legacy.domain.unitel.usecase.notification.GetPromotionNotificationInput;
import mn.erin.lms.legacy.domain.unitel.usecase.notification.GetPromotionNotificationOutput;

/**
 * @author Oyungerel Chuluunsukh.
 */
@Api("Notification API")
@RequestMapping(value = "/legacy/course-notifications", name = "Provides LMS notification for LMS")
public class NotificationRestApi implements RestApi
{
  private static final String PROMOTION_UPDATE_EMAIL_SUBJECT = "Нийтэлсэн урамшуулалд өөрчлөлт орлоо.";
  private static final String DOMAIN_NAME = "domainName";
  private String domainName;

  @Inject
  private AccessIdentityManagement accessIdentityManagement;

  @Inject
  private EmailService emailService;

  @Inject
  private SmsSender smsSender;

  @Inject
  private EnrollmentStateService enrollmentStateService;

  @Inject
  private CourseGroupRepository courseGroupRepository;

  private static final Logger LOGGER = LoggerFactory.getLogger(NotificationRestApi.class);

  private final CourseCategoryRepository courseCategoryRepository;
  private final CourseRepository courseRepository;
  private final CourseEnrollmentRepository courseEnrollmentRepository;

  public NotificationRestApi(
      CourseCategoryRepository courseCategoryRepository,
      CourseRepository courseRepository,
      CourseEnrollmentRepository courseEnrollmentRepository)
  {
    this.courseCategoryRepository = Objects.requireNonNull(courseCategoryRepository,
        "CourseCategoryRepository cannot be null!");
    this.courseRepository = Objects.requireNonNull(courseRepository, "CourseRepository cannot be null!");
    this.courseEnrollmentRepository = Objects.requireNonNull(courseEnrollmentRepository, "CourseEnrollmentRepository cannot be null!");
    this.setDomainName();
  }

  private void setDomainName()
  {
    Properties properties = System.getProperties();
    this.domainName = properties.getProperty(DOMAIN_NAME);
  }

  @ApiOperation("Get notification on user depending on user role")
  @GetMapping
  public ResponseEntity<RestResult> read()
  {
    return getNotificationResponseEntity();
  }

  @ApiOperation("Update enrollment state")
  @PostMapping
  public ResponseEntity<RestResult> update(@RequestBody String courseId)
  {
    if (courseId == null)
    {
      RestResponse.badRequest("Course id cannot be null!");
    }
    UpdateEnrollmentInput input = new UpdateEnrollmentInput(courseId, accessIdentityManagement.getCurrentUsername());
    UpdateEnrollment updateCourseEnrollment = new UpdateEnrollment(courseEnrollmentRepository);
    try
    {
      updateCourseEnrollment.execute(input);
    }
    catch (UseCaseException e)
    {
      LOGGER.error(e.getMessage(), e);
      return RestResponse.internalError(e.getMessage());
    }
    return getNotificationResponseEntity();
  }

  @ApiOperation("Send a notification about promotion")
  @GetMapping("/send-notification")
  public ResponseEntity<RestResult> sendNotification(@RequestParam String previousCode,
      @RequestParam String previousName, @RequestParam String promotionId)
  {
    Validate.notNull(previousCode, "Previous code cannot be null!");
    Validate.notNull(previousName, "Previous name cannot be null!");
    Validate.notNull(promotionId, "Promotion ID cannot be null!");

    GetCourseInput getCourseInput = new GetCourseInput(promotionId);
    GetCourse getCourse = new GetCourse(courseRepository);

    GetCourseOutput output;

    try
    {
      output = getCourse.execute(getCourseInput);
    }
    catch (UseCaseException e)
    {
      return RestResponse.internalError("Promotion with the ID: [" + promotionId + "] does not exist!");
    }

    Map<String, Object> templateData = new HashMap<>(output.getProperties());
    templateData.put("authorId", accessIdentityManagement.getCurrentUsername());
    templateData.put("promotionName", output.getTitle());
    templateData.put("note", output.getNote());
    templateData.put("courseId", output.getId());
    templateData.put(DOMAIN_NAME, domainName);

    boolean isNameChanged = !output.getTitle().equals(previousName);

    if (isNameChanged)
    {
      templateData.put("previousName", previousName);
    }

    boolean isCodeChanged = !output.getProperties().get("code").equals(previousCode);

    if (isCodeChanged)
    {
      templateData.put("previousCode", previousCode);
    }

    GetEnrollmentList getEnrollmentList = new GetEnrollmentList(courseEnrollmentRepository);
    GetEnrollmentListInput input = new GetEnrollmentListInput(output.getId());
    List<GetEnrollmentOutput> enrollmentList = getEnrollmentList.execute(input);

    Set<String> recipients = getAllRecipients(enrollmentList, output.getId());

    String smsMessage = getUpdatedPromotionSmsMessage(templateData, isNameChanged);

    List<String> emailRecipients = new ArrayList<>();

    LOGGER.info("Sending promotion sms to [{}] recipients.", recipients.size());
    for (String recipient : recipients)
    {
      ContactInfo contactInfo = accessIdentityManagement.getContactInfo(recipient);

      if (contactInfo != null && contactInfo.getPhone() != null)
      {
        LOGGER.info("Sending to [{}]...", contactInfo.getPhone());
        smsSender.sendSms(contactInfo.getPhone(), smsMessage);
      }

      if (contactInfo != null && contactInfo.getEmail() != null)
      {
        emailRecipients.add(contactInfo.getEmail());
      }
    }

    EmailUtil.bulkSendEmailAsync(emailService, emailRecipients, PROMOTION_UPDATE_EMAIL_SUBJECT, "promotion-update-email-template.ftl", templateData);

    return RestResponse.success();
  }

  private Set<String> getAllRecipients(List<GetEnrollmentOutput> enrollmentList, String courseId)
  {
    Set<String> groups = courseGroupRepository.listGroups(courseId).stream().map(CourseGroup::getGroupId)
        .collect(Collectors.toSet());

    Set<String> learnersWithMembership = new HashSet<>();
    Set<String> admins = new HashSet<>();
    for (String group : groups)
    {
      Set<String> learnerOfGroup = accessIdentityManagement.getAllLearners(group);
      learnersWithMembership.addAll(learnerOfGroup);
      Set<String> adminIds = accessIdentityManagement.getInstructors(group);
      admins.addAll(adminIds);
    }

    Set<String> enrolledUsers = enrollmentList.stream().map(GetEnrollmentOutput::getLearnerId)
        .filter(learnersWithMembership::contains)
        .collect(Collectors.toSet());

    Set<String> recipients = new HashSet<>();
    recipients.addAll(admins);
    recipients.addAll(enrolledUsers);

    return recipients;
  }

  private ResponseEntity<RestResult> getNotificationResponseEntity()
  {
    GetPromotionNotification getPromotionNotification = new GetPromotionNotification(courseCategoryRepository,
        courseRepository, enrollmentStateService, courseEnrollmentRepository);
    GetPromotionNotificationInput input = new GetPromotionNotificationInput(
        accessIdentityManagement.getCurrentUsername());
    GetPromotionNotificationOutput output;
    try
    {
      output = getPromotionNotification.execute(input);
    }
    catch (UseCaseException e)
    {
      LOGGER.error(e.getMessage(), e);
      return RestResponse.internalError(e.getMessage());
    }
    return RestResponse.success(output);
  }

  private String getUpdatedPromotionSmsMessage(Map<String, Object> templateData, boolean isNameChanged)
  {
    String code = (String) templateData.get(PromotionConstants.PROPERTY_CODE);
    String promotionName = (String) templateData.get("promotionName");
    String courseId = (String) templateData.get("courseId");

    String note = (String) templateData.get("note");
    String message = "Сайн байна уу?\n";
    if (isNameChanged)
    {
      String previousName = (String) templateData.get("previousName");
      message += String.format("\n %s, %s  урамшууллын нэр \"%s\" болж өөрчлөгдлөө. Та нэмэлт өөрчлөлттэй танилцана уу. ",
          code, previousName, promotionName);
    }
    else
    {
      message += String.format("%s, %s  нэртэй урамшуулалд нэмэлт өөрчлөлт орлоо. Та нэмэлт өөрчлөлттэй танилцана уу. ",
          code,
          promotionName);
    }
    message += domainName + "/#/timed-course/launch/" + courseId + "/true";
    if (!StringUtils.isBlank(note))
    {
      message += " Тэмдэглэл: " + "[" + note + "] ";
    }

    return message;
  }
}
