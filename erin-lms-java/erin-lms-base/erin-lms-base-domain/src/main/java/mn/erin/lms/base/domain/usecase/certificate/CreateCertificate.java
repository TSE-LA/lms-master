package mn.erin.lms.base.domain.usecase.certificate;

import java.io.IOException;
import java.util.Map;

import org.apache.commons.lang3.Validate;

import mn.erin.domain.base.usecase.UseCaseException;
import mn.erin.lms.base.domain.model.Authorized;
import mn.erin.lms.base.domain.model.certificate.Certificate;
import mn.erin.lms.base.aim.user.AuthorId;
import mn.erin.lms.base.aim.user.Instructor;
import mn.erin.lms.base.domain.repository.CertificateRepository;
import mn.erin.lms.base.domain.repository.LmsRepositoryException;
import mn.erin.lms.base.domain.repository.LmsRepositoryRegistry;
import mn.erin.lms.base.aim.AuthorIdProvider;
import mn.erin.lms.base.domain.service.LmsFileSystemService;
import mn.erin.lms.base.domain.service.LmsServiceRegistry;
import mn.erin.lms.base.domain.usecase.LmsUseCase;
import mn.erin.lms.base.domain.usecase.certificate.dto.CreateCertificateInput;
import mn.erin.lms.base.domain.util.ContentUtils;

/**
 * @author Erdenetulga
 */
@Authorized(users = { Instructor.class })
public class CreateCertificate extends LmsUseCase<CreateCertificateInput, String>
{
  private final CertificateRepository certificateRepository;
  private final AuthorIdProvider authorIdProvider;
  private final LmsFileSystemService lmsFileSystemService;

  public CreateCertificate(LmsRepositoryRegistry lmsRepositoryRegistry, LmsServiceRegistry lmsServiceRegistry)
  {
    super(lmsRepositoryRegistry, lmsServiceRegistry);
    this.certificateRepository = lmsRepositoryRegistry.getCertificateRepository();
    this.authorIdProvider = lmsServiceRegistry.getAuthorIdProvider();
    this.lmsFileSystemService = lmsServiceRegistry.getLmsFileSystemService();
  }

  @Override
  public String executeImpl(CreateCertificateInput input) throws UseCaseException
  {
    Validate.notNull(input);
    AuthorId authorId = AuthorId.valueOf(authorIdProvider.getAuthorId());

    try
    {
      Certificate certificate = certificateRepository.createCertificate(input.getName(), authorId);
      String certificateFolderId = lmsFileSystemService.createCertificateFolder(certificate.getCertificateId().getId(), certificate.getName());
      certificate.setCertificateFolderId(certificateFolderId);
      certificate.setUsed(false);
      certificateRepository
          .updateCertificate(certificate.getName(), certificate.getAuthorId(), certificate.getCertificateId(), certificate.getCertificateFolderId(),
              certificate.isUsed());
      Map<String, byte[]> certificateFileMap = ContentUtils.convertCertificateFiles(input);
      lmsFileSystemService.createCertificate(certificate.getCertificateFolderId(), certificate.getName(), certificateFileMap);
      return certificate.getCertificateId().getId();
    }
    catch (LmsRepositoryException | IOException e)
    {
      throw new UseCaseException(e.getMessage(), e);
    }
  }
}
