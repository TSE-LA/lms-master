package mn.erin.lms.base.domain.usecase.certificate;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import mn.erin.alfresco.connector.model.RestImageValueField;
import mn.erin.alfresco.connector.model.RestTextValueField;
import mn.erin.alfresco.connector.service.transform.AlfrescoRemoteTransformerService;
import mn.erin.alfresco.connector.service.transform.RestTransformDocumentInput;
import mn.erin.alfresco.connector.service.transform.RestTransformDocumentOutput;
import mn.erin.alfresco.connector.service.transform.TransformServiceException;
import mn.erin.alfresco.connector.service.transform.TransformerService;
import mn.erin.domain.aim.model.user.UserInfo;
import mn.erin.domain.base.usecase.UseCase;
import mn.erin.domain.base.usecase.UseCaseException;
import mn.erin.lms.base.aim.role.LmsRole;
import mn.erin.lms.base.aim.user.LearnerId;
import mn.erin.lms.base.domain.model.course.Course;
import mn.erin.lms.base.domain.model.course.CourseId;
import mn.erin.lms.base.domain.repository.LmsRepositoryException;
import mn.erin.lms.base.domain.repository.LmsRepositoryRegistry;
import mn.erin.lms.base.domain.service.LmsServiceRegistry;
import mn.erin.lms.base.domain.usecase.certificate.dto.GenerateCertificateInput;

/**
 * @author Oyungerel Chuluunsukh.
 */
public class GenerateCertificate implements UseCase<GenerateCertificateInput, String>
{
  private static final Logger LOGGER = LoggerFactory.getLogger(GenerateCertificate.class);
  public static final String CERTIFICATE = "Certificate/";
  public static final String RECIPIENT = "recipient";
  public static final String RECIPIENT_LAST_NAME = "lastName";
  public static final String COURSE_NAME = "courseName";
  public static final String CERTIFIED_NUMBER = "certifiedNumber";
  public static final String DATE = "date";

  private final LmsServiceRegistry lmsServiceRegistry;
  private final LmsRepositoryRegistry lmsRepositoryRegistry;

  public GenerateCertificate(LmsServiceRegistry lmsServiceRegistry, LmsRepositoryRegistry lmsRepositoryRegistry)
  {
    this.lmsServiceRegistry = lmsServiceRegistry;
    this.lmsRepositoryRegistry = lmsRepositoryRegistry;
  }

  @Override
  public String execute(GenerateCertificateInput input) throws UseCaseException
  {
    TransformerService transformerService = new AlfrescoRemoteTransformerService();
    List<RestTextValueField> textFields = new ArrayList<>();

    String documentPath = input.getTargetPath() + "/" + input.getFileName();
    String templatePath = CERTIFICATE + input.getCertificateId() + '/' + input.getCertificateName();

    boolean isAdmin = LmsRole.valueOf(lmsServiceRegistry.getAccessIdentityManagement().getRole(input.getRecipient())).equals(LmsRole.LMS_ADMIN);
    String formattedNumber = "001";

    if (!StringUtils.isBlank(input.getCourseId()) && !isAdmin)
    {
      try
      {
        boolean isGenerated = lmsRepositoryRegistry.getLearnerCertificateRepository().exists(CourseId.valueOf(input.getCourseId()), LearnerId.valueOf(input.getRecipient()));
        Course course = lmsRepositoryRegistry.getCourseRepository().fetchById(CourseId.valueOf(input.getCourseId()));
        int certifiedNumber = course.getCourseDetail().getCertifiedNumber();
        if (!isGenerated)
        {
          certifiedNumber++;
          lmsRepositoryRegistry.getCourseRepository().update(CourseId.valueOf(input.getCourseId()), certifiedNumber);
        }
        int digitCount = (int) (Math.log10(certifiedNumber) + 1);
        formattedNumber = digitCount == 1 ? "00" + certifiedNumber : digitCount == 2 ? "0" + certifiedNumber : String.valueOf(certifiedNumber);
      }
      catch (LmsRepositoryException | NullPointerException | IllegalArgumentException e)
      {
        throw new UseCaseException(e.getMessage());
      }
    }

    UserInfo info = lmsServiceRegistry.getAccessIdentityManagement().getUserInfo(input.getRecipient());
    if (!StringUtils.isBlank(info.getLastName()))
    {
      textFields.add(new RestTextValueField(RECIPIENT_LAST_NAME, StringUtils.capitalize(info.getLastName())));
    }

    textFields.add(new RestTextValueField(RECIPIENT, info.getFirstName().toUpperCase()));
    textFields.add(new RestTextValueField(COURSE_NAME, input.getCourseName()));
    textFields.add(new RestTextValueField(DATE, String.valueOf(LocalDate.now())));
    textFields.add(new RestTextValueField(CERTIFIED_NUMBER, formattedNumber));

    RestTransformDocumentInput transformInput = new RestTransformDocumentInput(templatePath, documentPath);
    List<RestImageValueField> imageValueFields = new ArrayList<>();

    transformInput.setTemplatePath(templatePath);
    transformInput.setTextValueField(textFields);
    transformInput.setImageValueField(imageValueFields);

    RestTransformDocumentOutput restTransformDocumentOutput = null;
    try
    {
      restTransformDocumentOutput = transformerService.transform(transformInput);
    }
    catch (TransformServiceException e)
    {
      LOGGER.error("Failed to generate certificate for this = [{}] course!", input.getCertificateName());
    }
    return Objects.requireNonNull(restTransformDocumentOutput).getDocumentId();
  }
}
