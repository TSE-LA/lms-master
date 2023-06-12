package mn.erin.lms.base.domain.usecase.certificate;

import java.time.LocalDateTime;

import org.apache.commons.lang3.Validate;

import mn.erin.lms.base.domain.model.Authorized;
import mn.erin.lms.base.domain.model.certificate.CertificateId;
import mn.erin.lms.base.domain.model.certificate.LearnerCertificate;
import mn.erin.lms.base.domain.model.course.CourseId;
import mn.erin.lms.base.aim.user.Author;
import mn.erin.lms.base.aim.user.Instructor;
import mn.erin.lms.base.aim.user.Learner;
import mn.erin.lms.base.aim.user.LearnerId;
import mn.erin.lms.base.aim.user.Manager;
import mn.erin.lms.base.aim.user.Supervisor;
import mn.erin.lms.base.domain.repository.LearnerCertificateRepository;
import mn.erin.lms.base.domain.repository.LmsRepositoryRegistry;
import mn.erin.lms.base.domain.service.LmsFileSystemService;
import mn.erin.lms.base.domain.service.LmsServiceRegistry;
import mn.erin.lms.base.domain.usecase.LmsUseCase;
import mn.erin.lms.base.domain.usecase.certificate.dto.ReceiveCertificateInput;

/**
 * @author Bat-Erdene Tsogoo.
 */
@Authorized(users = { Author.class, Instructor.class, Manager.class, Learner.class, Supervisor.class })
public class ReceiveCertificate extends LmsUseCase<ReceiveCertificateInput, Boolean>
{
  private final LearnerCertificateRepository learnerCertificateRepository;
  private final LmsFileSystemService lmsFileSystemService;

  public ReceiveCertificate(LmsRepositoryRegistry lmsRepositoryRegistry,
      LmsServiceRegistry lmsServiceRegistry)
  {
    super(lmsRepositoryRegistry, lmsServiceRegistry);
    this.learnerCertificateRepository = lmsRepositoryRegistry.getLearnerCertificateRepository();
    this.lmsFileSystemService = lmsServiceRegistry.getLmsFileSystemService();
  }

  @Override
  protected Boolean executeImpl(ReceiveCertificateInput input)
  {
    Validate.notNull(input);

    //Generate image from pdf
    Boolean generated = lmsFileSystemService.createCertificateImage(input.getCertificatePdfId());

    //Save to mongo
    if(generated){
      LearnerId learnerId = LearnerId.valueOf(input.getLearnerId());
      CertificateId certificateId = CertificateId.valueOf(input.getCertificateId());
      CourseId courseId = CourseId.valueOf(input.getCourseId());
      LearnerCertificate learnerCertificate = new LearnerCertificate(learnerId, courseId, certificateId, LocalDateTime.now());
      learnerCertificateRepository.save(learnerCertificate);
    }

    return generated;
  }
}
