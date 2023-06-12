package mn.erin.lms.base.domain.usecase.certificate;

import mn.erin.domain.base.usecase.UseCaseException;
import mn.erin.lms.base.aim.user.LmsUser;
import mn.erin.lms.base.domain.model.Authorized;
import mn.erin.lms.base.domain.model.certificate.Certificate;
import mn.erin.lms.base.domain.model.certificate.CertificateId;
import mn.erin.lms.base.domain.repository.CertificateRepository;
import mn.erin.lms.base.domain.repository.LmsRepositoryException;
import mn.erin.lms.base.domain.repository.LmsRepositoryRegistry;
import mn.erin.lms.base.domain.service.LmsServiceRegistry;
import mn.erin.lms.base.domain.usecase.certificate.dto.CertificateDto;

/**
 * @author Bat-Erdene Tsogoo.
 */
@Authorized(users = { LmsUser.class })
public class GetCertificateById extends CertificateUseCase<String, CertificateDto>
{
  private final CertificateRepository certificateRepository;

  public GetCertificateById(LmsRepositoryRegistry lmsRepositoryRegistry,
      LmsServiceRegistry lmsServiceRegistry)
  {
    super(lmsRepositoryRegistry, lmsServiceRegistry);
    this.certificateRepository = lmsRepositoryRegistry.getCertificateRepository();
  }

  @Override
  protected CertificateDto executeImpl(String certificateId) throws UseCaseException
  {
    try
    {
      Certificate certificate = certificateRepository.fetchById(CertificateId.valueOf(certificateId));
      return convert(certificate);
    }
    catch (LmsRepositoryException e)
    {
      throw new UseCaseException(e.getMessage(), e);
    }
  }
}
